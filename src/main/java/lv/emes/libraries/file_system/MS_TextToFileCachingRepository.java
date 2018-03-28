package lv.emes.libraries.file_system;

import lv.emes.libraries.storage.MS_CachingRepository;
import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static lv.emes.libraries.file_system.MS_FileSystemTools.SLASH;
import static lv.emes.libraries.utilities.MS_DateTimeUtils.*;

/**
 * Cache repository to cache texts in file system.
 * Recommended to store only few texts, because every operation requires reading whole file.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_TextToFileCachingRepository extends MS_CachingRepository<String, String> {

    private String pathToFile;
    private MS_TextFile file;
    private boolean fileLocked = false;

    /**
     * Constructs file system new caching repository for texts.
     *
     * @param fileRootPath full path (without ending slash) to folder, where file will be created.
     * @param fileName     short filename (without any slashes) of text file, where text entries will be cached.
     */
    public MS_TextToFileCachingRepository(String fileRootPath, String fileName) {
        super(fileRootPath, fileName);
    }

    /**
     * Constructs file system new caching repository for texts.
     *
     * @param fileRootPath   full path (without ending slash) to folder, where file will be created.
     * @param fileName       short filename (without any slashes) of text file, where text entries will be cached.
     * @param autoInitialize - if initialization needs to be performed right after successful construction.
     */
    public MS_TextToFileCachingRepository(String fileRootPath, String fileName, boolean autoInitialize) {
        super(fileRootPath, fileName, autoInitialize);
    }

    @Override
    public boolean isInitialized() {
        return MS_FileSystemTools.fileExists(pathToFile);
    }

    @Override
    protected void doInitialize() {
        pathToFile = getRepositoryRoot() + SLASH + getRepositoryCategoryName();
        MS_TextFile.createEmptyFile(pathToFile);
        file = new MS_TextFile(pathToFile);
    }

    @Override
    protected Map<String, Pair<String, LocalDateTime>> doFindAll() {
        Map<String, Pair<String, LocalDateTime>> res = new LinkedHashMap<>();
        String line = "";

        waitAndLockFile();
        try {
            while ((line = file.readln()) != null) {
                CachedText text = CachedText.newInstance(line);
                res.put(text.getId(), Pair.of(text.getText(), text.getExpirationTime()));
            }
        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
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
    public Pair<String, LocalDateTime> get(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        return find(identifier);
    }

    //Following 4 methods are overridden completely

    @Override
    public Pair<String, LocalDateTime> put(String identifier, Pair<String, LocalDateTime> item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, Pair<String, LocalDateTime>> all = doFindAll();
        Pair<String, LocalDateTime> previous = all.put(identifier, item);
        rewriteFile(all);
        return previous;
    }

    @Override
    public void add(String identifier, Pair<String, LocalDateTime> item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, Pair<String, LocalDateTime>> all = doFindAll();
        Pair<String, LocalDateTime> previous = all.get(identifier);
        if (previous == null) {
            all.put(identifier, item);
            rewriteFile(all);
        }
    }

    @Override
    public void remove(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, Pair<String, LocalDateTime>> all = doFindAll();
        if (all.remove(identifier) != null) //only if some item was removed from cache then changes in file are needed
            rewriteFile(all);
    }

    @Override
    public Pair<String, LocalDateTime> find(String identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        checkAndThrowNotInitializedException();
        Map<String, Pair<String, LocalDateTime>> all = doFindAll();
        return all.get(identifier);
    }

    //following 3 methods are disabled because main methods are overridden (above) to improve performance of this repository

    @Override
    protected void doAdd(String identifier, Pair<String, LocalDateTime> object) {
        throw new UnsupportedOperationException("Cannot perform doAdd operation. This repository doesn't support it.");
    }

    @Override
    protected Pair<String, LocalDateTime> doFind(String identifier) {
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

    private void rewriteFile(Map<String, Pair<String, LocalDateTime>> cachedObjects) {
        doRemoveAll();
        waitAndLockFile();
        cachedObjects.forEach((id, objToCache) -> {
            MS_StringList parts = new MS_StringList();
            parts.add(id);
            String expDatePart = objToCache.getRight() == null ? "null" :
                    dateTimeToStr(objToCache.getRight(), _CUSTOM_DATE_TIME_FORMAT_LV);
            parts.add(expDatePart);
            parts.add(objToCache.getLeft());
            file.appendln(parts.toStringWithNoLastDelimiter(), false);
        });
        file.close();
        releaseFile();
    }

    private static class CachedText {

        private String id;
        private String text;
        private LocalDateTime expirationTime;

        public String getId() {
            return id;
        }

        public CachedText withId(String id) {
            this.id = id;
            return this;
        }

        public String getText() {
            return text;
        }

        public CachedText withText(String text) {
            this.text = text;
            return this;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

        public CachedText withExpirationTime(LocalDateTime expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public static CachedText newInstance(String line) {
            MS_StringList parts = new MS_StringList(line);
            String expDatePart = parts.get(1);
            LocalDateTime expDate = "null".equals(expDatePart) ? null :
                    formatDateTime(expDatePart, _CUSTOM_DATE_TIME_FORMAT_LV).toLocalDateTime();

            return new CachedText()
                    .withId(parts.get(0))
                    .withExpirationTime(expDate)
                    .withText(parts.get(2))
                    ;
        }
    }
}
