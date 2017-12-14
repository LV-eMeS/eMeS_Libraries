package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSFileSystemToolsTest {

    private final static String TEST_RESOURCES_DIR = "src/test/resources/";
    private final static String CSV_COMMA_SEPARATED = "commaSeparated.csv";
    private final static String CSV_CHAMELEON_SEPARATED = "chameleonSeparated.csv";
    private static String tmpFileName = "eMeS_Testing_File_System_Tools.txt";
    private static String tmpFileName2 = "eMeS_Testing_File_System_Tools2.txt";
    private static String tmpFilePath = TestData.TEMP_DIR + tmpFileName;
    private static String tmpFilePath2 = TestData.TEMP_DIR + tmpFileName2;
    private static String tmpDirName = "eMeS_Testing_File_System_Tools/";
    private static String tmpDirPath = TestData.TEMP_DIR + tmpDirName;
    private static String childDirectory = "child directory";
    private static Boolean tmpFileStillExists = false;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        deleteFile(tmpFilePath);
        deleteFile(tmpFilePath2);
        deleteDirectory(tmpDirPath);
    }

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {
        deleteFile(tmpFilePath);
        deleteFile(tmpFilePath2);
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
        assertTrue("File couldn't be created, that's why is not possible to test if file exists",
                MS_TextFile.createEmptyFile(tmpFilePath));
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
        String withoutExtension = MS_StringUtils.replaceInString(tmpFilePath, extensionWithDot, "");

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

        MS_StringList test;
        test = getDirectoryFileList(tmpDirPath);
        assertEquals(3, test.count());
        assertEquals(file1, test.get(0));
        assertEquals(file2, test.get(1));
        assertEquals(file3, test.get(2));

        test = getDirectoryFileList_Shortnames(tmpDirPath);
        assertEquals(3, test.count());
        assertEquals(getShortFilename(file1), test.get(0));
        assertEquals(getShortFilename(file2), test.get(1));
        assertEquals(getShortFilename(file3), test.get(2));

        test = getDirectoryFileList_Directories(tmpDirPath);
        assertEquals(1, test.count());
        assertEquals(childDirectory, test.get(0));

        String invalidDirName = "not a directory";
        test = getDirectoryFileList(invalidDirName);
        assertEquals(0, test.count());

        test = getDirectoryFileList_Shortnames(invalidDirName);
        assertEquals(0, test.count());

        test = getDirectoryFileList_Directories(invalidDirName);
        assertEquals(0, test.count());
    }

    @Test
    public void test10ResourceExtractingToTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(NIRCMD_FILE_FOR_WINDOWS, null, true);
        String shortName = getShortFilename(tmpFile);
        assertTrue(fileExists(getTmpDirectory() + shortName));
    }

    @Test
    public void test11ResourceExtractingToParticularFolderInsideTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(NIRCMD_FILE_FOR_WINDOWS, "test/", true);
        String shortName = getShortFilename(tmpFile);
        assertTrue(fileExists(getTmpDirectory() + "test/" + shortName));
    }

    @Test
    public void test12ResourceExtractingToParticularSubFolderInsideTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(NIRCMD_FILE_FOR_WINDOWS, "test/subTest", true);
        String shortName = getShortFilename(tmpFile);
        assertTrue(fileExists(getTmpDirectory() + "test/subTest/" + shortName));
    }

    @Test
    public void test13FileRenaming() {
        assertTrue(tmpFileStillExists);
        String thisFilename = tmpFilePath;
        String anotherFilename = tmpFilePath2;

        //move file to this same folder
        tmpFileStillExists = !moveFile(thisFilename, anotherFilename);
        assertFalse(tmpFileStillExists);

        //create another file with first filename, so now we got 2 files
        File createdFile = new File(tmpFilePath);
        assertTrue(MS_TextFile.createEmptyFile(tmpFilePath));
        assertTrue(fileExists(anotherFilename));

        //move renamed file back using method that doesn't overwrite it
        tmpFileStillExists = !moveFile(anotherFilename, thisFilename, false);
        assertTrue(tmpFileStillExists);
        assertTrue(fileExists(createdFile));
        assertTrue(fileExists(anotherFilename));

        //this time move second file to first file dest with overwriting it
        tmpFileStillExists = moveFile(anotherFilename, thisFilename, true);
        assertTrue(tmpFileStillExists);
        assertFalse(fileExists(anotherFilename));

        //now to test, if dest directory is created by renaming process
        String childDirectoryUnique = "new_child_directory_6397/";
        String anotherFileInDiffDir = getTmpDirectory() + childDirectoryUnique + tmpFileName2;
        tmpFileStillExists = !moveFile(thisFilename, anotherFileInDiffDir);
        assertFalse(tmpFileStillExists);
    }

    /**
     * Tests file with content (numbers are corresponding line numbers):<pre>
     1
     2 x
     3 DE,4,
     4 ABC,2,@
     5
     6
     * </pre>
     * @throws IOException if there are some problem during file read.
     */
    @Test
    public void test20CSVCommaSeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_COMMA_SEPARATED);
        assertEquals(5, content.size()); //out of 6 lines of file last empty line isn't counted

        assertEquals(1, content.get(0).length); //empty line still is with 1 element
        assertEquals("", content.get(0)[0]); //even though this element is empty string

        assertEquals(1, content.get(1).length);
        assertEquals("x", content.get(1)[0]);

        assertEquals(3, content.get(2).length);
        assertEquals("DE", content.get(2)[0]);
        assertEquals("4", content.get(2)[1]);
        assertEquals("", content.get(2)[2]);

        assertEquals(3, content.get(3).length);
        assertEquals("ABC", content.get(3)[0]);
        assertEquals("2", content.get(3)[1]);
        assertEquals("@", content.get(3)[2]);

        assertEquals(1, content.get(4).length); //empty line still is with 1 element
        assertEquals("", content.get(4)[0]); //even though this element is empty string
    }

    @Test
    public void test21CSVDotSeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '.');
        assertEquals(3, content.size());

        assertEquals(5, content.get(0).length);
        assertEquals("This", content.get(0)[0]);
        assertEquals("is", content.get(0)[1]);
        assertEquals("Chameleon", content.get(0)[2]);
        assertEquals("Separated", content.get(0)[3]);
        assertEquals("File", content.get(0)[4]);

        assertEquals(6, content.get(1).length);
        assertEquals("We", content.get(1)[0]);
        assertEquals("will", content.get(1)[1]);
        assertEquals("use", content.get(1)[2]);
        assertEquals("both", content.get(1)[3]);
        assertEquals("dot", content.get(1)[4]);
        assertEquals("and", content.get(1)[5]);

        assertEquals(3, content.get(2).length);
        assertEquals("'i'", content.get(2)[0]);
        assertEquals("letter", content.get(2)[1]);
        assertEquals("separations", content.get(2)[2]);
    }

    @Test
    public void test22CSVLetterISeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, 'i');
        assertEquals(3, content.size());

        assertEquals("Th", content.get(0)[0]);
        assertEquals("s.", content.get(0)[1]);
        assertEquals("s.Chameleon.Separated.F", content.get(0)[2]);
        assertEquals("le", content.get(0)[3]);

        assertEquals("We.w", content.get(1)[0]);
        assertEquals("ll.use.both.dot.and", content.get(1)[1]);

        assertEquals("'", content.get(2)[0]);
        assertEquals("'.letter.separat", content.get(2)[1]);
        assertEquals("ons", content.get(2)[2]);
    }

    @Test
    public void test23CSVNotExistingCharSeparated() throws IOException {
        //all lines are taken as they are
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '_');
        assertEquals(3, content.size());
        assertEquals("This.is.Chameleon.Separated.File", content.get(0)[0]);
        assertEquals("We.will.use.both.dot.and", content.get(1)[0]);
    }

    @Test
    public void test24CSVQuoteCharacters() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '.', '\'');
        //As a difference from test21CSVDotSeparated
        assertEquals("i", content.get(2)[0]); //here we do not get quotes
    }

    @Test(expected = FileNotFoundException.class)
    public void test25CSVFileDoesntExist() throws IOException {
        loadCSVFile(TEST_RESOURCES_DIR + "anything unreal");
    }
}