package lv.emes.libraries.file_system;

/*
Copyright [2016] [Māris Salenieks]

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

import au.com.bytecode.opencsv.CSVReader;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.logging.MS_Log4Java;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Different transformations and actions related with OS file system.
 * <p>Static methods:
 * <ul>
 * <li>executeApplication</li>
 * <li>getTmpDirectory</li>
 * <li>getProjectDirectory</li>
 * <li>fileExists</li>
 * <li>directoryExists</li>
 * <li>createEmptyFile</li>
 * <li>createNewDirectory</li>
 * <li>deleteFile</li>
 * <li>deleteDirectory</li>
 * <li>directoryUp</li>
 * <li>getDirectoryFileList_Shortnames</li>
 * <li>getDirectoryFileList_Directories</li>
 * <li>getFilenameWithoutExtension</li>
 * <li>getFileExtensionWithDot</li>
 * <li>getFileExtensionWithoutDot</li>
 * <li>getDirectoryOfFile</li>
 * <li>getShortFilename</li>
 * <li>replaceBackslash</li>
 * <li>extractResourceToTmpFolder</li>
 * </ul>
 *
 * @version 2.0.
 * @since 1.1.1
 */
public class MS_FileSystemTools {

    public static final String _CURRENT_DIRECTORY = "./";
    public static final String _SLASH = "/";
    public static final String _NIRCMD_FILE_FOR_WINDOWS = "tools/nircmd.exe";

    /**
     * Opens link in default web browser or runs an application in OS. Do not use spaces in links!
     *
     * @param aLink path to resource.
     * @return true if successfully opened, false, if not.
     */
    public static boolean openLinkInWebBrowser(String aLink) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                URI uri = new URI(aLink);
                desktop.browse(uri);
                return true;
            } catch (IOException | URISyntaxException e) {
//                e.printStackTrace(); 
                return false;
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + aLink);
                return true;
            } catch (IOException e) {
//                e.printStackTrace(); 
                return false;
            }
        }
    }

    /**
     * Launches process or application with parameters (if any) included in <b>processName</b>. It uses Runtime object.
     * Also supports paths and filename with spaces.
     *
     * @param processName full path (including directory) to executable file. Also relative path is fully supported.
     * @return true if successfully sent command to OS (doesn't mean that process actually executed successfully).
     */
    private static boolean _executeRuntimeEXE(String processName) {
        try {
            Runtime.getRuntime().exec(processName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Launches application named <b>fileName</b> using OS <b>Runtime</b> process runner, <b>ProcessBuilder</b> for batch handling or <b>Desktop</b> to open hyperlinks.
     * Capabilities of this method are described in lv.emes.libraries.file_system.MS_FileSystemTools.executeApplication capabilities.xls.
     * <p><i><b>WARNING: </b></i>Currently there is problems with filenames or folders with spaces, so try not to use such paths!
     * <p><i><b>Note: </b></i>After batch file execution command line or terminal window is remaining open.
     * To close this window after execution, just add line "exit" in your batch script file!
     *
     * @param fileName full path (including directory) to executable file, directory or application. Also relative path without spaces is fully supported.
     * @param params   command line or application parameters delimited with spaces for process to execute. Leave empty if application don't have any parameter!
     * @return true if successfully sent command to OS (in some cases that doesn't mean that process actually executed, but OS definitely received command to execute application).
     * <br>It returns false in cases when cannot find application, process, file or folder with passed <b>fileName</b> or when some other error occurs.
     * @throws NullPointerException when <b>fileName</b> or <b>params</b> is null.
     * @see java.lang.Runtime
     * @see java.lang.ProcessBuilder
     * @see java.awt.Desktop
     */
    public static boolean executeApplication(String fileName, String params) {
        //trying to execute it as batch
        MS_StringList inputParameters = new MS_StringList(params, ' ');
        String batchFilename = getShortFilename(fileName);

        //if pathToBach have spaces, use exec application
        if (MS_StringUtils.textContains(batchFilename, ' ')) {
            if (params.equals(""))
                return _executeRuntimeEXE(fileName);
            else
                return _executeRuntimeEXE(fileName + " " + params);
        }

        MS_StringList commandLineParameters = new MS_StringList();
        commandLineParameters.add("cmd");
        commandLineParameters.add("/c");
        commandLineParameters.add("Start");
        commandLineParameters.add(batchFilename);
        commandLineParameters.concatenate(inputParameters);
        List<String> cmdAndArgs = commandLineParameters.toList();

        ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
        String directory = getDirectoryOfFile(fileName);
        if (!directory.equals(_CURRENT_DIRECTORY)) {
            File dir = new File(directory);
            pb.directory(dir);
        }
        try {
            pb.start();
            return true;
        } catch (Exception e1) {
            //if won't execute as batch then use Open Link method that uses Desktop
            return openLinkInWebBrowser(fileName);
        }
    }

    /**
     * Replaces all the backslashes to slashes.
     *
     * @param textWithBackslashes text that includes backslashes.
     * @return text with all existing backslash symbols replaced with slash symbols.
     */
    public static String replaceBackslash(String textWithBackslashes) {
        return textWithBackslashes.replace("\\", _SLASH);
    }

    /**
     * @return path to system default temporary folder.
     * <br><u>Note</u>: path ends with slash "/".
     */
    public static String getTmpDirectory() {
        String res = System.getProperty("java.io.tmpdir");
        return replaceBackslash(res);
    }

    /**
     * When using project resources folder this is best way to get access to files stored there.
     * By using this method you can get direct path to specified resource through it's short path (searching starts from folder "resources").
     * <br><u>Note</u>: resource must be for read only and it must be prepared before launching any script that gets it.
     *
     * @param resourceFilename "folder/file.txt"
     * @return full path to a file "file.txt" located in "src/main/resources/" or in "resources/" (depending on project config). <p>
     * If resource cannot be found, returns empty String.
     */
    public static InputStream getResourceInputStream(String resourceFilename) {
        return MS_FileSystemTools.class.getClassLoader().getResourceAsStream(resourceFilename);
    }

    /**
     * @return path to a directory where application is launched.
     */
    public static String getProjectDirectory() {
        return replaceBackslash(System.getProperty("user.dir")) + _SLASH;
    }

    /**
     * @param aFileName path to file.
     * @return true if file exists (not directory).
     */
    public static boolean fileExists(String aFileName) {
        if (aFileName == null) return false;
        File file = new File(aFileName);
        return file.isFile();
    }

    /**
     * @param aFile path to file.
     * @return true if file exists (not directory).
     */
    public static boolean fileExists(File aFile) {
        return aFile.exists();
    }

    /**
     * @param path path to directory.
     * @return true if directory exists (not file).
     */
    public static boolean directoryExists(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * @param aDirectory path to directory.
     * @return true if directory exists (not file).
     */
    public static boolean directoryExists(File aDirectory) {
        return aDirectory.isDirectory();
    }

    /**
     * Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist.
     * If directories (subdirectories) in path to this file doesn't exist, all necessary directories are created
     * The check for the existence of the file and the creation of the file if it does not exist are a single operation
     * that is atomic with respect to all other filesystem activities that might affect the file.
     * Note: this method should not be used for file-locking, as the resulting protocol cannot be made to work reliably.
     * The FileLock facility should be used instead.
     *
     * @param fileName directory path + short filename, in other words: full path to file.
     * @return true if the named file did not exist and was successfully created; false if the named file already exists.
     */
    public static boolean createEmptyFile(String fileName) {
        createNewDirectory(directoryUp(fileName));
        File file = new File(fileName);
        try {
            return file.createNewFile();
        } catch (IOException e) { //shouldn't happen if directory exists
            return false;
        }
    }

    /**
     * Creates a new directory with given path. Also creates subdirectories if necessary.
     *
     * @param aPathToDir full path to a directory we are about to create.
     * @return true if the directory does not exist and was successfully created;
     * false if the named directory already exists.
     */
    public static boolean createNewDirectory(String aPathToDir) {
        File file = new File(aPathToDir);
        return file.mkdirs();
    }

    /**
     * @param aFilename = "test.txt"; "without extension"; "text.txt."
     * @return "test"; "without extension"; "text".
     */
    public static String getFilenameWithoutExtension(String aFilename) {
        MS_StringList filenameParts = new MS_StringList(aFilename, '.');
        if (filenameParts.count() > 1) {
            filenameParts.removeLast(); //delete last element
            //add dot, if there will be more elements in the list (case when there is more than one dot in filename)
            return filenameParts.toStringWithNoLastDelimiter();
        } else
            return aFilename;
    }

    /**
     * @param aFilename name of file. For example, "test.file"
     * @return only extension of file with dot. Like ".file".
     */
    public static String getFileExtensionWithDot(String aFilename) {
        String inversedFilename = MS_StringUtils.getInversedText(aFilename);
        int positionOfDot = MS_StringUtils.pos(inversedFilename, ".");
        String res = MS_StringUtils.substring(inversedFilename, 0, positionOfDot + 1);
        res = MS_StringUtils.getInversedText(res);
        return res;
    }

    /**
     * @param aFilename name of file. For example, "test.file"
     * @return only extension of file with<u>out</u> dot. Like "file".
     */
    public static String getFileExtensionWithoutDot(String aFilename) {
        String res = getFileExtensionWithDot(aFilename);
        return MS_StringUtils.getSubstring(res, 1, aFilename.length() - 1);
    }

    /**
     * From full filename gets directory in which file is found.
     *
     * @param aFilename directory + path to a file.
     * @return path to directory or "./" if only short filename given as parameter <b>aFilename</b>.
     */
    public static String getDirectoryOfFile(String aFilename) {
        aFilename = replaceBackslash(aFilename);
        String res = aFilename.endsWith(_SLASH) ? aFilename : directoryUp(aFilename);
        return res.equals("") ? _CURRENT_DIRECTORY : res;
    }

    /**
     * From full filename gets filename without path to directory.
     *
     * @param aFilename directory + path to a file.
     * @return just filename (with extension).
     */
    public static String getShortFilename(String aFilename) {
        try {
            Path p = Paths.get(aFilename);
            return p.getFileName().toString();
        } catch (Exception r) {
            aFilename = replaceBackslash(aFilename);
            MS_StringList path = new MS_StringList(aFilename, '/');
            return path.get(path.size() - 1);
        }
    }

    /**
     * Silently deletes file from file system. If file doesn't exist result is <code>false</code>.
     *
     * @param filename name of file to be deleted.
     * @return true if delete successful, false if file couldn't be found or deleted.
     */
    public static boolean deleteFile(String filename) {
        return deleteFile(new File(filename));
    }

    /**
     * Silently deletes file from file system. If file doesn't exist result is <code>false</code>.
     *
     * @param file file to be deleted.
     * @return true if delete successful, false if file is null or couldn't be found or deleted.
     */
    public static boolean deleteFile(File file) {
        if (file == null) return false;
        else return file.delete();
    }

    /**
     * Silently deletes directory from file system. If directory doesn't exist, nothing happens.
     *
     * @param dirName name of directory to be deleted.
     * @return true if delete successful, false if couldn't delete directory.
     */
    public static boolean deleteDirectory(String dirName) {
        return deleteDirectory(new File(dirName));
    }

    /**
     * Silently deletes directory from file system. If directory doesn't exist, nothing happens.
     *
     * @param directory directory to be deleted.
     * @return true if delete successful, false if directory is null or couldn't delete directory.
     */
    public static boolean deleteDirectory(File directory) {
        try {
            FileUtils.deleteDirectory(directory);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Moves file or directory to another location or simply renames it.
     * It overwrites file or directory existing in destination.
     * If destination directory doesn't exist, creates it.
     *
     * @param sourcePath path and name of file or directory that needs to be moved.
     * @param destPath   new path of file or directory.
     * @return true if moved successfully, false, if error in moving process or destination file or directory exists.
     */
    public static boolean moveFileOrDirectory(String sourcePath, String destPath) {
        return moveFileOrDirectory(sourcePath, destPath, true);
    }

    /**
     * Moves file or directory to another location or simply renames it.
     * If destination directory doesn't exist, creates it.
     *
     * @param sourcePath    path and name of file or directory that needs to be moved.
     * @param destPath      new path of file or directory.
     * @param overwriteDest if true then attempts to overwrite existing file or directory in destination path.
     *                      If false then obviously it will rename file only if destination file doesn't exist.
     * @return true if moved successfully, false, if error in moving process or destination file or directory exists.
     */
    public static boolean moveFileOrDirectory(String sourcePath, String destPath, boolean overwriteDest) {
        if (overwriteDest)
            deleteFile(destPath);
        File fileToMove = new File(sourcePath);
        File fileToMoveTo = new File(destPath);
        //check, if dest dir exists, if not, create it
        if (!directoryExists(destPath))
            createNewDirectory(getDirectoryOfFile(destPath));
        return fileToMove.renameTo(fileToMoveTo);
    }

    /**
     * Copies file or directory to desired <b>destPath</b>.
     * It overwrites everything that already exists in destination with same name.
     * If destination directory doesn't exist, creates it.
     *
     * @param sourcePath path and name of file or directory that needs to be copied.
     * @param destPath   new path of file or directory.
     * @return true if copied successfully, false, if error in copying process or destination file or directory exists.
     */
    public static boolean copyFileOrDirectory(String sourcePath, String destPath) {
        try {
            File filePathToCopyFrom = new File(sourcePath);
            File filePathToCopyTo = new File(destPath);
            //do cleanup - remove existing files or directories in destination path
            if (filePathToCopyTo.isFile()) deleteFile(filePathToCopyTo);
            if (filePathToCopyTo.isDirectory()) deleteDirectory(filePathToCopyTo);
            //now do actual copying depending on what we are copying - file or directory
            if (filePathToCopyFrom.isFile()) {
                //check, if dest dir for file exists, if not, create it
                String destFileDir = getDirectoryOfFile(destPath);
                if (!directoryExists(destFileDir)) createNewDirectory(destFileDir);
                FileUtils.copyFile(filePathToCopyFrom, filePathToCopyTo);
            }
            if (filePathToCopyFrom.isDirectory()) {
                FileUtils.copyDirectory(filePathToCopyFrom, filePathToCopyTo);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns 1 level upper parent directory of passed directory.
     * If passed directory was already a root directory then returns empty string.
     *
     * @param pathToDir current directory path.
     * @return path of directory 1 level up.
     */
    public static String directoryUp(String pathToDir) {
        pathToDir = replaceBackslash(pathToDir);
        MS_StringList directoryBranch = new MS_StringList(pathToDir, '/');
        directoryBranch.removeLast();
        return directoryBranch.toString();
    }

    /**
     * Retrieves all the files and folders located in directory <b>pathToDir</b>.
     *
     * @param pathToDir path to directory in which files are located.
     * @return list of filename with full file paths.
     */
    public static MS_StringList getDirectoryFileList(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        if (filesList != null)
            for (File file : filesList) {
                if (file.isFile()) {
                    res.add(replaceBackslash(file.getAbsolutePath()));
                }
            }
        return res;
    }

    /**
     * Retrieves all the files located in directory <b>pathToDir</b>.
     *
     * @param pathToDir path to directory in which files are located.
     * @return list of short filenames.
     */
    public static MS_StringList getDirectoryFileList_Shortnames(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        if (filesList != null)
            for (File file : filesList) {
                if (file.isFile()) {
                    res.add(replaceBackslash(file.getName()));
                }
            }
        return res;
    }

    /**
     * Calculates total size of file or directory in bytes.
     * <p><u>Warning</u>: this method doesn't throw any exception in case it couldn't enter some directory
     * or had trouble traversing. Instead it's logging to Log4J failing file or directory name.
     * In this failing case result might be inaccurate.
     * <p>Inspired from:
     * <a href="https://stackoverflow.com/questions/2149785/get-size-of-folder-or-file/19877372#19877372">Aksel Willgert @Stackoverflow</a>.
     *
     * @param path path to file or directory.
     * @return file size in bytes or <code>0L</code> if the file or directory does not exist.
     */
    public static long getFileOrDirectorySize(String path) {
        final AtomicLong res = new AtomicLong(0);
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    res.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    MS_Log4Java.getLogger(MS_FileSystemTools.class)
                            .warn(String.format("While getting '%s' size, skipped: '%s' (%s)", path, file, exc));
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc != null)
                        MS_Log4Java.getLogger(MS_FileSystemTools.class)
                                .warn(String.format("While getting '%s' size, had trouble traversing: '%s' (%s)", path, dir, exc));
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }
        return res.get();
    }

    /**
     * Retrieves all the directories located in directory <b>pathToDir</b>.
     *
     * @param pathToDir path to directory in which files are located.
     * @return list of short directory names.
     */
    public static MS_StringList getDirectoryFileList_Directories(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        if (filesList != null)
            for (File file : filesList) {
                if (file.isDirectory()) {
                    res.add(replaceBackslash(file.getName()));
                }
            }
        return res;
    }

    /**
     * Looks for resource in JAR file, extracts it in temporary directory if resource exists and
     * returns the full path to a file.
     *
     * @param pathToResource            path to resource file looking from resources folder root (but not including it in path name).
     *                                  <b>Example:</b> File can be found in classpath src/main/resources/texts/file.txt
     *                                  <br>in this case <b>pathToResource = texts/file.txt</b>.
     * @param dirInTempDirectory        in which directory of temporary folder file should be extracted.
     *                                  <br><u>Note</u>: Folder can be presented also as: "folder/subFolder/subSubFolder/".
     *                                  <br><u>Note</u>: Also can be null.
     *                                  If this argument is null then file will be extracted directly in temporary folder.
     * @param alwaysExtractResourceFile if true then resource is always extracted, but
     *                                  if false then looking if resource file already exists in temporary folder and
     *                                  if so then simply return full path of existing resource file.
     *                                  <br><u>Warning</u>: resource is checked only for filename not contents of resource,
     *                                  which means that in temporary folder might be already existing file with same filename
     *                                  that actually isn't this particular resource.
     * @return full path to extracted file. Empty string if resource not found.
     */
    public static String extractResourceToTmpFolder(String pathToResource,
                                                    String dirInTempDirectory,
                                                    boolean alwaysExtractResourceFile) {
        final File tempFile;
        final InputStream resourceStream = getResourceInputStream(pathToResource);
        String fileName = getShortFilename(pathToResource);

        String tmpDir;
        if (dirInTempDirectory == null) {
            tmpDir = getTmpDirectory();
        } else {
            tmpDir = dirInTempDirectory.endsWith(_SLASH) ?
                    getTmpDirectory() + dirInTempDirectory :
                    getTmpDirectory() + dirInTempDirectory + _SLASH;
        }
        String fullFilename = tmpDir + fileName;

        //now the caching part - looking for
        if (!alwaysExtractResourceFile) {
            if (fileExists(fullFilename))
                return fullFilename;
        }

        createNewDirectory(tmpDir); //creates if folder doesn't exist
        tempFile = new File(fullFilename);

        try {
            OutputStream fileStream = new FileOutputStream(tempFile);
            MS_BinaryTools.copyStream(resourceStream, fileStream);
        } catch (Exception e) {
            return "";
        }

        return (tempFile.toString());
    }

    /**
     * Opens <b>elementSeparator</b> separated UTF-8 encoded file, reads its content and stores in list of String array with element size matching
     * element count in each of read lines.
     *
     * @param pathToFile       full CSV filename.
     * @param elementSeparator the delimiter to use for separating entries (elements).
     * @param quoteChar        the character to use for quoted elements (mostly apostrophe ' is used).
     *                         Those quotes are removed when reading entry value, leaving element plain without quotes
     * @return list of String array or empty list if file is empty.
     * @throws IOException <ul>
     *                     <li>if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading;</li>
     *                     <li>if bad things happen during the read;</li>
     *                     <li>if the file closing fails.</li>
     *                     </ul>
     */
    public static List<String[]> loadCSVFile(String pathToFile, char elementSeparator, char quoteChar) throws IOException {
        List<String[]> fileContent = new ArrayList<>();
        File readerFile;
        CSVReader reader = null;
        String[] line; //all the CSV values for single line of file

        try {
            readerFile = new File(pathToFile);
            reader = new CSVReader(
                    new InputStreamReader(new FileInputStream(readerFile.getAbsolutePath()), StandardCharsets.UTF_8),
                    elementSeparator, quoteChar, 0);
            while ((line = reader.readNext()) != null) fileContent.add(line);
            return fileContent;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Opens <b>elementSeparator</b> separated UTF-8 encoded file, reads its content and stores in list of String array with element size matching
     * element count in each of read lines.
     *
     * @param pathToFile       full CSV filename.
     * @param elementSeparator the delimiter to use for separating entries (elements).
     * @return list of String array or empty list if file is empty.
     * @throws IOException <ul>
     *                     <li>if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading;</li>
     *                     <li>if bad things happen during the read;</li>
     *                     <li>if the file closing fails.</li>
     *                     </ul>
     */
    public static List<String[]> loadCSVFile(String pathToFile, char elementSeparator) throws IOException {
        return loadCSVFile(pathToFile, elementSeparator, '\0');
    }

    /**
     * Opens comma separated UTF-8 encoded file, reads its content and stores in list of String array with element size matching
     * element count in each of read lines.
     *
     * @param pathToFile full CSV filename.
     * @return list of String array or empty list if file is empty.
     * @throws IOException <ul>
     *                     <li>if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading;</li>
     *                     <li>if bad things happen during the read;</li>
     *                     <li>if the file closing fails.</li>
     *                     </ul>
     */
    public static List<String[]> loadCSVFile(String pathToFile) throws IOException {
        return loadCSVFile(pathToFile, ',', '\0');
    }
}