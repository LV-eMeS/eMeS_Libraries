package lv.emes.libraries.file_system;

import lv.emes.libraries.storage.BatchAddOperation;
import lv.emes.libraries.storage.MS_Repository;
import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static lv.emes.libraries.file_system.MS_FileSystemTools._SLASH;

/**
 * Text file repository to store texts categorized by identifiers in text file.
 * File content might be something like: <br>
 *     <b>Line 1</b>: identifier1#Some data#<br>
 *     <b>Line 2</b>: identifier2#Some other data#<br>
 * Recommended to store only few texts, because every operation requires reading whole file.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.2.
 */
public class MS_TextToFileRepository extends MS_Repository<String, String> implements BatchAddOperation<String, String> {

    private static final char DELIMITER = '®';
    private String pathToFile;
    private MS_TextFile file;
    private boolean fileLocked = false;
    private Map<String, String> itemsToBatchAdd = new HashMap<>();

    /**
     * Constructs new text file repository for texts.
     *
     * @param fileRootPath full path (without ending slash) to folder, where file will be created.
     * @param fileName     short filename (without any slashes) of text file, where text entries will be cached.
     */
    public MS_TextToFileRepository(String fileRootPath, String fileName) {
        super(fileRootPath, fileName);
    }

    /**
     * Constructs new text file repository for texts.
     *
     * @param fileRootPath   full path (without ending slash) to folder, where file will be created.
     * @param fileName       short filename (without any slashes) of text file, where text entries will be cached.
     * @param autoInitialize if initialization needs to be performed right after successful construction.
     */
    public MS_TextToFileRepository(String fileRootPath, String fileName, boolean autoInitialize) {
        super(fileRootPath, fileName, autoInitialize);
    }

    @Override
    public boolean isInitialized() {
        return MS_FileSystemTools.fileExists(pathToFile);
    }

    @Override
    protected void doInitialize() {
        pathToFile = getRepositoryRoot() + _SLASH + getRepositoryCategoryName();
        MS_TextFile.createEmptyFile(pathToFile);
        file = new MS_TextFile(pathToFile);
    }

    @Override
    protected Map<String, String> doFindAll() {
        Map<String, String> res = new LinkedHashMap<>();
        String line = "";

        waitAndLockFile();
        try {
            while ((line = file.readln()) != null) {
                MS_StringList parts = new MS_StringList(line, DELIMITER);
                res.put(parts.get(0), parts.get(1));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MS_RepositoryDataExchangeException("Failed to parse corrupted line in file:\n" + line, e);
        } catch (Exception e) {
            throw new MS_RepositoryDataExchangeException("Failed to read from file:\n" + pathToFile, e);
        } finally {
            file.close();
            releaseFile();
        }

        return res;
    }

    @Override
    protected void doRemoveAll() {
        waitAndLockFile();
        //just recreate file
        if (!(MS_FileSystemTools.deleteFile(pathToFile) & MS_TextFile.createEmptyFile(pathToFile))) {
            releaseFile();
            throw new MS_RepositoryDataExchangeException("Failed to remove all cached items. " +
                    "Cannot delete corresponding file:\n" + pathToFile);
        } else {
            releaseFile();
        }
    }

    @Override
    protected int doGetSize() {
        waitAndLockFile();
        int fileSize = 0;
        while (file.readln() != null) fileSize++;
        file.close();
        releaseFile();
        return fileSize;
    }

    @Override
    public String get(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        return find(identifier);
    }

    //Following 4 methods are overridden completely

    @Override
    public String put(String identifier, String item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, String> all = doFindAll();
        String previous = all.put(identifier, item);
        rewriteFile(all);
        return previous;
    }

    @Override
    public void add(String identifier, String item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, String> all = doFindAll();
        String previous = all.get(identifier);
        if (previous == null) {
            all.put(identifier, item);
            rewriteFile(all);
        }
    }

    @Override
    public void batchAdd(String identifier, String item) {
        itemsToBatchAdd.put(identifier, item);
    }

    @Override
    public void commitAddition(boolean forceReplace) throws MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, String> all = doFindAll();
        if (forceReplace) {
            for (Map.Entry<String, String> itemToBatchAdd : itemsToBatchAdd.entrySet()) {
                all.put(itemToBatchAdd.getKey(), itemToBatchAdd.getValue());
            }
        } else {
            for (Map.Entry<String, String> itemToBatchAdd : itemsToBatchAdd.entrySet()) {
                all.computeIfAbsent(itemToBatchAdd.getKey(), k -> itemToBatchAdd.getValue());
            }
        }
        rewriteFile(all);
        itemsToBatchAdd.clear();
    }

    @Override
    public void remove(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, String> all = doFindAll();
        if (all.remove(identifier) != null) //only if some item was removed from cache then changes in file are needed
            rewriteFile(all);
    }

    @Override
    public String find(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, String> all = doFindAll();
        return all.get(identifier);
    }

    //following 3 methods are disabled because main methods are overridden (above) to improve performance of this repository

    @Override
    protected void doAdd(String identifier, String object) {
        throw new UnsupportedOperationException("Cannot perform doAdd operation. This repository doesn't support it.");
    }

    @Override
    protected String doFind(String identifier) {
        throw new UnsupportedOperationException("Cannot perform doFind operation. This repository doesn't support it.");
    }

    @Override
    protected void doRemove(String identifier) {
        throw new UnsupportedOperationException("Cannot perform doRemove operation. This repository doesn't support it.");
    }

    //*** Private methods and classes ***

    /**
     * Locks file for caching from other threads.
     */
    private void waitAndLockFile() {
        waitUntilFileReleased();
        lockFile();
    }

    private synchronized void lockFile() {
        this.fileLocked = true;
    }

    private synchronized void releaseFile() {
        fileLocked = false;
    }

    private synchronized boolean isFileLocked() {
        return fileLocked;
    }

    private void waitUntilFileReleased() {
        try {
            MS_CodingUtils.executeWithRetry(100, () -> {
                if (isFileLocked())
                    throw new MS_ExecutionFailureException();
            }, () -> MS_CodingUtils.sleep(10));
        } catch (MS_ExecutionFailureException e) {
            throw new MS_RepositoryDataExchangeException("Thread failed to execute within ~1 second lasting attempt of " +
                    "waiting until file is released by another thread", e);
        }
    }

    private void rewriteFile(Map<String, String> cachedObjects) {
        doRemoveAll();
        waitAndLockFile();
        for (Map.Entry<String, String> entry : cachedObjects.entrySet()) {
            String id = entry.getKey();
            String objToCache = entry.getValue();

            MS_StringList parts = new MS_StringList(DELIMITER);
            parts.add(id);
            parts.add(objToCache);
            file.appendln(parts.toStringWithNoLastDelimiter(), false);
        }
        file.close();
        releaseFile();
    }
}
