package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.lists.MS_StringList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;
import static lv.emes.libraries.file_system.MS_FileSystemTools.getTmpDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSFileSystemToolsTest {
    public static String tmpFileName = "eMeS_Testing_File_System_Tools.txt";
    public static String tmpFilePath = getTmpDirectory() + tmpFileName;
    public static String tmpDirName = "eMeS_Testing_File_System_Tools/";
    public static String tmpDirPath = getTmpDirectory() + tmpDirName;
    public static String childDirectory = "child directory";
    public static Boolean tmpFileStillExists = false;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        deleteFile(tmpFilePath);
        deleteDirectory(tmpDirPath);
    }

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {
        deleteFile(tmpFilePath);
        deleteDirectory(tmpDirPath);
    }

    @Test
    public void test01ManualTest() {
        System.out.println(getTmpDirectory());
        System.out.println(getProjectDirectory());
        assertEquals("/test/", replaceBackslash("\\test\\"));
    }

    @Test
    public void test02FileExists() {
        //after next line file should exist with 100% guaranty
        assertTrue("File couldn't be created, that's why is not possible to test if file exists", MS_TextFile.createEmptyFile(tmpFilePath));
        assertTrue(fileExists(tmpFilePath));
        File createdFile = new File(tmpFilePath);
        assertTrue(fileExists(createdFile));
        tmpFileStillExists = true;
    }

    @Test
    public void test03DeleteFile() {
        assertTrue("Previous test failed", tmpFileStillExists);
        deleteFile(tmpFilePath);
        assertFalse(fileExists(tmpFilePath));
    }

    @Test
    public void test04Resources() throws IOException {
        InputStream testSubject = getResourceInputStream("sampleTextFile4Testing.txt");
        MS_BinaryTools.writeFile(testSubject, tmpFilePath);
        assertTrue(fileExists(tmpFilePath));
        tmpFileStillExists = true;
    }

    @Test
    public void test05DirectoryOfFile() throws IOException {
        assertTrue("Previous test failed", tmpFileStillExists);
        assertEquals(getTmpDirectory(), getDirectoryOfFile(tmpFilePath));
    }

    @Test
    public void test06DirectoryCreating() throws IOException {
        assertTrue(directoryExists(getTmpDirectory()));
        assertTrue(directoryExists(new File(getTmpDirectory())));

        assertFalse(directoryExists(tmpDirPath));
        assertTrue(createNewDirectory(tmpDirPath + childDirectory));
        assertTrue(directoryExists(tmpDirPath));
        assertTrue(directoryExists(tmpDirPath + childDirectory));
    }

    @Test
    public void test07FileNamesAndExtensions() {
        String extensionWithDot = ".txt";
        String withoutExtension = MS_StringTools.replaceInString(tmpFilePath, extensionWithDot, "");

        assertEquals(tmpFileName, getShortFilename(tmpFilePath));
        assertEquals(withoutExtension, getFilenameWithoutExtension(tmpFilePath));
        assertEquals(extensionWithDot, getFileExtensionWithDot(tmpFilePath));
        assertEquals("txt", getFileExtensionWithoutDot(tmpFilePath));
    }

    @Test
    public void test08DirectoryUp() {
        String directoryPath;
        String directoryUpPath;
        directoryPath = "C:\\Program Files (x86)\\AGEIA Technologies";
        directoryUpPath = directoryUp(directoryPath);
        assertEquals("C:/Program Files (x86)/", directoryUpPath);
        directoryUpPath = directoryUp(directoryUpPath);
        assertEquals("C:/", directoryUpPath);
        directoryUpPath = directoryUp(directoryUpPath);
        assertEquals("", directoryUpPath);

        directoryPath = "C:/Program Files (x86)/AGEIA Technologies";
        directoryUpPath = directoryUp(directoryPath);
        assertEquals("C:/Program Files (x86)/", directoryUpPath);
        directoryUpPath = directoryUp(directoryUpPath);
        assertEquals("C:/", directoryUpPath);
        directoryUpPath = directoryUp(directoryUpPath);
        assertEquals("", directoryUpPath);
    }

    @Test
    public void test09DirectoryFileList() {
        assertTrue("Directory doesn't exist", directoryExists(tmpDirPath));
        String file1 = tmpDirPath + "file1";
        String file2 = tmpDirPath + "file2";
        String file3 = tmpDirPath + "file3";
        MS_TextFile.createEmptyFile(file1);
        MS_TextFile.createEmptyFile(file2);
        MS_TextFile.createEmptyFile(file3);

        MS_StringList test = getDirectoryFileList(tmpDirPath);
        assertEquals(3, test.count());
        assertEquals(file1, test.get(0));
        assertEquals(file2, test.get(1));
        assertEquals(file3, test.get(2));

        test.clear();
        test = getDirectoryFileList_Shortnames(tmpDirPath);
        assertEquals(3, test.count());
        assertEquals(getShortFilename(file1), test.get(0));
        assertEquals(getShortFilename(file2), test.get(1));
        assertEquals(getShortFilename(file3), test.get(2));

        test = getDirectoryFileList_Directories(tmpDirPath);
        assertEquals(1, test.count());
        assertEquals(childDirectory, test.get(0));
    }
}