package lv.emes.libraries.file_system;

/*
Copyright [2016] [Maris Salenieks]

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

import org.apache.commons.io.FileUtils;
import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.lists.MS_StringList;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Different transformations and actions related with OS file system.
 * <p>Static methods:
 * -executeApplication
 * -getPathToPackage
 * -getPathToSourcePackage
 * -getTmpDirectory
 * -getProjectDirectory
 * -fileExists
 * -directoryExists
 * -createEmptyFile
 * -createNewDirectory
 * -deleteFile
 * -deleteDirectory
 * -getFilenameWithoutExtension
 * -getFileExtensionWithDot
 * -getFileExtensionWithoutDot
 * -getDirectoryOfFile
 * -getShortFilename
 * -replaceBackslash
 *
 * @version 1.1.
 */
public class MS_FileSystemTools {
    public static final String CURRENT_DIRECTORY = "./";

    /**
     * Opens link in default web browser or runs an application in OS. Do not use spaces in links!
     *
     * @param aLink path to resource.
     * @return true if successfully opened, false, if not.
     * @throws MalformedURLException
     */
    private static boolean _openLinkInWebBrowser(String aLink) {
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
     * <p>If it fails to execute application, it tries to execute it from command line.
     *
     * @param processName full path (including directory) to executable file. Also relative path is fully supported.
     * @return true if successfully sent command to OS (doesn't mean that process actually executed successfully).
     */
    private static boolean _executeRuntimeEXE(String processName) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(processName);
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
        MS_StringList commandLineParameters = new MS_StringList();
        commandLineParameters.add("cmd");
        commandLineParameters.add("/c");
        commandLineParameters.add("Start");
        String batchFilename = getShortFilename(fileName);

        //if pathToBach have spaces, use exec application
        if (MS_StringTools.textContains(batchFilename, ' ')) {
            if (params.equals(""))
                return _executeRuntimeEXE(fileName);
            else
                return _executeRuntimeEXE(fileName + " " + params);
        }

        commandLineParameters.add(batchFilename);
        commandLineParameters.concatenate(inputParameters);
        List<String> cmdAndArgs = commandLineParameters.toList();
        String directory = getDirectoryOfFile(fileName);
        File dir = new File(directory);
        ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
        if (!dir.equals(CURRENT_DIRECTORY))
            pb.directory(dir);
        try {
            pb.start();
            return true;
        } catch (Exception e1) {
            //if won't execute as batch then use Open Link method that uses Desktop
            return _openLinkInWebBrowser(fileName);
        }
    }

    /**
     * Replaces all the backslashes to slashes.
     *
     * @return text with all existing backslash symbols replaced with slash symbols.
     */
    public static String replaceBackslash(String textWithBackslashes) {
        return textWithBackslashes.replace("\\", "/");
    }

    /**
     * @return path to system default temporary folder.
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
        return System.getProperty("user.dir") + "/";
    }

    /**
     * @param aFileName path to file.
     * @return true if file exists (not directory).
     */
    public static boolean fileExists(String aFileName) {
        File file = new File(aFileName);
        return file.exists();
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
     * Atomically creates a new, empty file named by this abstract pathname if and only if a file with this name does not yet exist. The check for the existence of the file and the creation of the file if it does not exist are a single operation that is atomic with respect to all other filesystem activities that might affect the file.
     * Note: this method should not be used for file-locking, as the resulting protocol cannot be made to work reliably. The FileLock facility should be used instead.
     *
     * @param aFileName - path + short filename, in other words: full path to file.
     * @return true if the named file does not exist and was successfully created; false if the named file already exists.
     */

    public static boolean createEmptyFile(String aFileName) {
        File file = new File(aFileName);
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates a new directory with given path. Also creates subdirectories if necessary.
     *
     * @param aPathToDir full path to a directory we are about to create.
     * @return true if the directory does not exist and was successfully created; false if the named directory already exists.
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
        MS_StringList tmp = new MS_StringList(aFilename, '.');
        if (tmp.count() > 1) {
            tmp.remove(tmp.count() - 1); //delete last element
            tmp.first();
            String res = "";
            while (tmp.currentIndexInsideTheList()) {
                res = res.concat(tmp.current());
                tmp.next();
                if (tmp.currentIndexInsideTheList())
                    res = res.concat("."); //add dot, if there will be more elements in the list (case when there is more than one dot in filename)
            }
            return res;
        } else
            return aFilename;
    }

    /**
     * @param aFilename name of file. For example, "test.file"
     * @return only extension of file with dot. Like ".file".
     */
    public static String getFileExtensionWithDot(String aFilename) {
        String inversedFilename = MS_StringTools.getInversedText(aFilename);
        int positionOfDot = MS_StringTools.pos(inversedFilename, ".");
        String res = MS_StringTools.substring(inversedFilename, 0, positionOfDot+1);
        res = MS_StringTools.getInversedText(res);
        return res;
    }

    /**
     * @param aFilename name of file. For example, "test.file"
     * @return only extension of file with<u>out</u> dot. Like "file".
     */
    public static String getFileExtensionWithoutDot(String aFilename) {
        String res = getFileExtensionWithDot(aFilename);
        return MS_StringTools.getSubstring(res, 1, aFilename.length() - 1);
    }

    /**
     * From full filename gets directory in which file is found.
     *
     * @param aFilename directory + path to a file.
     * @return path to directory or "./" if only short filename given as parameter <b>aFilename</b>.
     */
    public static String getDirectoryOfFile(String aFilename) {
        try {
            Path p = Paths.get(aFilename);
            return replaceBackslash(p.getParent().toString()) + "/";
        } catch (Exception r) {
            return replaceBackslash(CURRENT_DIRECTORY) + "/";
        }
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
            return aFilename;
        }
    }

    /**
     * Silently deletes file from file system. If file doesn't exist, nothing happens.
     *
     * @param filename name of file to be deleted.
     * @return true if delete successful, false if file couldn't be found or deleted.
     */
    public static boolean deleteFile(String filename) {
        Path path = FileSystems.getDefault().getPath(filename);
        try {
            Files.delete(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Silently deletes directory from file system. If directory doesn't exist, nothing happens.
     *
     * @param dirName name of directory to be deleted.
     * @return true if delete successful, false if couldn't delete directory.
     */
    public static boolean deleteDirectory(String dirName) {
        try {
            FileUtils.deleteDirectory(new File(dirName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns 1 level upper parent directory of passed directory.
     * If passed directory was already a root directory then returns empty string.
     * @param pathToDir current directory path.
     * @return path of directory 1 level up.
     */
    public static String directoryUp(String pathToDir) {
        pathToDir = replaceBackslash(pathToDir);
        MS_StringList directoryBranch = new MS_StringList(pathToDir, '/');
        directoryBranch.removeLast();
        return directoryBranch.toString();
    }

    public static MS_StringList getDirectoryFileList(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                res.add(replaceBackslash(file.getAbsolutePath()));
            }
        }
        return res;
    }

    public static MS_StringList getDirectoryFileList_Shortnames(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                res.add(replaceBackslash(file.getName()));
            }
        }
        return res;
    }

    public static MS_StringList getDirectoryFileList_Directories(String pathToDir) {
        MS_StringList res = new MS_StringList();
        File dir = new File(pathToDir);
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isDirectory()) {
                res.add(replaceBackslash(file.getName()));
            }
        }
        return res;
    }

    //TODO: rewrite as static methods
//	  procedure ForceForegroundNoActivate(hWnd : THandle);
//	  function ForceForegroundWindow(hwnd: THandle): Boolean;
}