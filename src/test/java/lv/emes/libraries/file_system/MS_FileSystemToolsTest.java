package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_FileSystemToolsTest {

    private final static String TEST_RESOURCES_DIR = "src/test/resources/";
    private final static String CSV_COMMA_SEPARATED = "commaSeparated.csv";
    private final static String CSV_CHAMELEON_SEPARATED = "chameleonSeparated.csv";
    private static final String tmpFileName = "eMeS_Testing_File_System_Tools.txt";
    private static final String tmpFileName2 = "eMeS_Testing_File_System_Tools2.txt";
    private static final String tmpFilePath = TestData.TEMP_DIR + tmpFileName;
    private static final String tmpFilePath2 = TestData.TEMP_DIR + tmpFileName2;
    private static final String tmpDirName = "eMeS_Testing_File_System_Tools/";
    private static final String tmpDirPath = TestData.TEMP_DIR + tmpDirName;
    private static final String childDirectory = "child directory";
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
        assertThat(replaceBackslash("\\test\\")).isEqualTo("/test/");
    }

    @Test
    public void test02FileExists() {
        //after next line file should exist with 100% guaranty
        assertThat(MS_TextFile.createEmptyFile(tmpFilePath)).as("File couldn't be created, that's why is not possible to test if file exists").isTrue();
        assertThat(fileExists(tmpFilePath)).isTrue();
        File createdFile = new File(tmpFilePath);
        assertThat(fileExists(createdFile)).isTrue();
        tmpFileStillExists = true;
    }

    @Test
    public void test03DeleteFile() {
        assertThat(tmpFileStillExists).as("Previous test failed").isTrue();
        deleteFile(tmpFilePath);
        assertThat(fileExists(tmpFilePath)).isFalse();
    }

    @Test
    public void test04Resources() throws IOException {
        InputStream testSubject = getResourceInputStream("sampleTextFile4Testing.txt");
        MS_BinaryTools.writeFile(testSubject, tmpFilePath);
        assertThat(fileExists(tmpFilePath)).isTrue();
        tmpFileStillExists = true;
    }

    @Test
    public void test05DirectoryOfFile() {
        assertThat(tmpFileStillExists).as("Previous test failed").isTrue();
        assertThat(getDirectoryOfFile(tmpFilePath)).isEqualTo(getTmpDirectory());
    }

    @Test
    public void test06DirectoryCreating() {
        assertThat(directoryExists(getTmpDirectory())).isTrue();
        assertThat(directoryExists(new File(getTmpDirectory()))).isTrue();

        assertThat(directoryExists(tmpDirPath)).isFalse();
        assertThat(createNewDirectory(tmpDirPath + childDirectory)).isTrue();
        assertThat(directoryExists(tmpDirPath)).isTrue();
        assertThat(directoryExists(tmpDirPath + childDirectory)).isTrue();
    }

    @Test
    public void test07FileNamesAndExtensions() {
        final String EXTENSION_WITH_DOT = ".txt";
        String withoutExtension = MS_StringUtils.replaceInString(tmpFilePath, EXTENSION_WITH_DOT, "");

        assertThat(getShortFilename(tmpFilePath)).isEqualTo(tmpFileName);
        assertThat(getFilenameWithoutExtension(tmpFilePath)).isEqualTo(withoutExtension);
        assertThat(getFileExtensionWithDot(tmpFilePath)).isEqualTo(EXTENSION_WITH_DOT);
        assertThat(getFileExtensionWithoutDot(tmpFilePath)).isEqualTo("txt");

        assertThat(getShortFilename("")).isEqualTo("");
        assertThat(getFilenameWithoutExtension("abc..txt")).isEqualTo("abc.");
    }

    @Test
    public void test08DirectoryUp() {
        String directoryPath;
        String directoryUpPath;
        directoryPath = "C:\\Program Files (x86)\\AGEIA Technologies";
        directoryUpPath = directoryUp(directoryPath);
        assertThat(directoryUpPath).isEqualTo("C:/Program Files (x86)/");
        directoryUpPath = directoryUp(directoryUpPath);
        assertThat(directoryUpPath).isEqualTo("C:/");
        directoryUpPath = directoryUp(directoryUpPath);
        assertThat(directoryUpPath).isEqualTo("");

        directoryPath = "C:/Program Files (x86)/AGEIA Technologies";
        directoryUpPath = directoryUp(directoryPath);
        assertThat(directoryUpPath).isEqualTo("C:/Program Files (x86)/");
        directoryUpPath = directoryUp(directoryUpPath);
        assertThat(directoryUpPath).isEqualTo("C:/");
        directoryUpPath = directoryUp(directoryUpPath);
        assertThat(directoryUpPath).isEqualTo("");
    }

    @Test
    public void test09DirectoryFileList() {
        assertThat(directoryExists(tmpDirPath)).as("Directory doesn't exist").isTrue();
        String file1 = tmpDirPath + "file1";
        String file2 = tmpDirPath + "file2";
        String file3 = tmpDirPath + "file3";
        MS_TextFile.createEmptyFile(file1);
        MS_TextFile.createEmptyFile(file2);
        MS_TextFile.createEmptyFile(file3);

        MS_StringList test;
        test = getDirectoryFileList(tmpDirPath);
        assertThat(test.count()).isEqualTo(3);
        assertThat(test.get(0)).isEqualTo(file1);
        assertThat(test.get(1)).isEqualTo(file2);
        assertThat(test.get(2)).isEqualTo(file3);

        test = getDirectoryFileList_Shortnames(tmpDirPath);
        assertThat(test.count()).isEqualTo(3);
        assertThat(test.get(0)).isEqualTo(getShortFilename(file1));
        assertThat(test.get(1)).isEqualTo(getShortFilename(file2));
        assertThat(test.get(2)).isEqualTo(getShortFilename(file3));

        test = getDirectoryFileList_Directories(tmpDirPath);
        assertThat(test.count()).isEqualTo(1);
        assertThat(test.get(0)).isEqualTo(childDirectory);

        String invalidDirName = "not a directory";
        test = getDirectoryFileList(invalidDirName);
        assertThat(test.count()).isEqualTo(0);

        test = getDirectoryFileList_Shortnames(invalidDirName);
        assertThat(test.count()).isEqualTo(0);

        test = getDirectoryFileList_Directories(invalidDirName);
        assertThat(test.count()).isEqualTo(0);
    }

    @Test
    public void test10ResourceExtractingToTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(_NIRCMD_FILE_FOR_WINDOWS, null, true);
        String shortName = getShortFilename(tmpFile);
        assertThat(fileExists(getTmpDirectory() + shortName)).isTrue();
    }

    @Test
    public void test11ResourceExtractingToParticularFolderInsideTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(_NIRCMD_FILE_FOR_WINDOWS, "test/", true);
        String shortName = getShortFilename(tmpFile);
        assertThat(fileExists(getTmpDirectory() + "test/" + shortName)).isTrue();
    }

    @Test
    public void test12ResourceExtractingToParticularSubFolderInsideTmpFolder() {
        String tmpFile = extractResourceToTmpFolder(_NIRCMD_FILE_FOR_WINDOWS, "test/subTest", true);
        String shortName = getShortFilename(tmpFile);
        assertThat(fileExists(getTmpDirectory() + "test/subTest/" + shortName)).isTrue();
    }

    @Test
    public void test13FileRenaming() {
        assertThat(tmpFileStillExists).isTrue();
        String thisFilename = tmpFilePath;
        String anotherFilename = tmpFilePath2;

        //move file to this same folder
        tmpFileStillExists = !moveFileOrDirectory(thisFilename, anotherFilename);
        assertThat(tmpFileStillExists).isFalse();

        //create another file with first filename, so now we got 2 files
        File createdFile = new File(tmpFilePath);
        assertThat(MS_TextFile.createEmptyFile(tmpFilePath)).isTrue();
        assertThat(fileExists(anotherFilename)).isTrue();

        //move renamed file back using method that doesn't overwrite it
        tmpFileStillExists = !moveFileOrDirectory(anotherFilename, thisFilename, false);
        assertThat(tmpFileStillExists).isTrue();
        assertThat(fileExists(createdFile)).isTrue();
        assertThat(fileExists(anotherFilename)).isTrue();

        //this time move second file to first file dest with overwriting it
        tmpFileStillExists = moveFileOrDirectory(anotherFilename, thisFilename, true);
        assertThat(tmpFileStillExists).isTrue();
        assertThat(fileExists(anotherFilename)).isFalse();

        //now to test, if dest directory is created by renaming process
        String childDirectoryUnique = "new_child_directory_6397/";
        String anotherFileInDiffDir = getTmpDirectory() + childDirectoryUnique + tmpFileName2;
        tmpFileStillExists = !moveFileOrDirectory(thisFilename, anotherFileInDiffDir);
        assertThat(tmpFileStillExists).isFalse();
    }

    @Test
    public void test14NewFileCreation() {
        String filename = tmpDirPath + RandomStringUtils.randomAlphanumeric(9) + _SLASH + "test14NewFileCreation.txt";
        assertThat(fileExists(filename)).as("Error: file already exists").isFalse();
        assertThat(createEmptyFile(filename)).as("New file creation failed").isTrue();
        assertThat(fileExists(filename)).as("File doesn't exist after creation").isTrue();
    }

    /**
     * Tests file with content (numbers are corresponding line numbers):<pre>
     * 1
     * 2 x
     * 3 DE,4,
     * 4 ABC,2,@
     * 5
     * 6
     * </pre>
     *
     * @throws IOException if there are some problem during file read, which shouldn't normally happen.
     */
    @Test
    public void test20CSVCommaSeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_COMMA_SEPARATED);
        assertThat(content.size()).isEqualTo(5); //out of 6 lines of file last empty line isn't counted

        assertThat(content.get(0).length).isEqualTo(1); //empty line still is with 1 element
        assertThat(content.get(0)[0]).isEqualTo(""); //even though this element is empty string

        assertThat(content.get(1).length).isEqualTo(1);
        assertThat(content.get(1)[0]).isEqualTo("x");

        assertThat(content.get(2).length).isEqualTo(3);
        assertThat(content.get(2)[0]).isEqualTo("DE");
        assertThat(content.get(2)[1]).isEqualTo("4");
        assertThat(content.get(2)[2]).isEqualTo("");

        assertThat(content.get(3).length).isEqualTo(3);
        assertThat(content.get(3)[0]).isEqualTo("ABC");
        assertThat(content.get(3)[1]).isEqualTo("2");
        assertThat(content.get(3)[2]).isEqualTo("@");

        assertThat(content.get(4).length).isEqualTo(1); //empty line still is with 1 element
        assertThat(content.get(4)[0]).isEqualTo(""); //even though this element is empty string
    }

    @Test
    public void test21CSVDotSeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '.');
        assertThat(content.size()).isEqualTo(3);

        assertThat(content.get(0).length).isEqualTo(5);
        assertThat(content.get(0)[0]).isEqualTo("This");
        assertThat(content.get(0)[1]).isEqualTo("is");
        assertThat(content.get(0)[2]).isEqualTo("Chameleon");
        assertThat(content.get(0)[3]).isEqualTo("Separated");
        assertThat(content.get(0)[4]).isEqualTo("File");

        assertThat(content.get(1).length).isEqualTo(6);
        assertThat(content.get(1)[0]).isEqualTo("We");
        assertThat(content.get(1)[1]).isEqualTo("will");
        assertThat(content.get(1)[2]).isEqualTo("use");
        assertThat(content.get(1)[3]).isEqualTo("both");
        assertThat(content.get(1)[4]).isEqualTo("dot");
        assertThat(content.get(1)[5]).isEqualTo("and");

        assertThat(content.get(2).length).isEqualTo(3);
        assertThat(content.get(2)[0]).isEqualTo("'i'");
        assertThat(content.get(2)[1]).isEqualTo("letter");
        assertThat(content.get(2)[2]).isEqualTo("separations");
    }

    @Test
    public void test22CSVLetterISeparated() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, 'i');
        assertThat(content.size()).isEqualTo(3);

        assertThat(content.get(0)[0]).isEqualTo("Th");
        assertThat(content.get(0)[1]).isEqualTo("s.");
        assertThat(content.get(0)[2]).isEqualTo("s.Chameleon.Separated.F");
        assertThat(content.get(0)[3]).isEqualTo("le");

        assertThat(content.get(1)[0]).isEqualTo("We.w");
        assertThat(content.get(1)[1]).isEqualTo("ll.use.both.dot.and");

        assertThat(content.get(2)[0]).isEqualTo("'");
        assertThat(content.get(2)[1]).isEqualTo("'.letter.separat");
        assertThat(content.get(2)[2]).isEqualTo("ons");
    }

    @Test
    public void test23CSVNotExistingCharSeparated() throws IOException {
        //all lines are taken as they are
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '_');
        assertThat(content.size()).isEqualTo(3);
        assertThat(content.get(0)[0]).isEqualTo("This.is.Chameleon.Separated.File");
        assertThat(content.get(1)[0]).isEqualTo("We.will.use.both.dot.and");
    }

    @Test
    public void test24CSVQuoteCharacters() throws IOException {
        List<String[]> content = loadCSVFile(TEST_RESOURCES_DIR + CSV_CHAMELEON_SEPARATED, '.', '\'');
        //As a difference from test21CSVDotSeparated
        assertThat(content.get(2)[0]).isEqualTo("i"); //here we do not get quotes
    }

    @Test(expected = FileNotFoundException.class)
    public void test25CSVFileDoesntExist() throws IOException {
        loadCSVFile(TEST_RESOURCES_DIR + "anything unreal");
    }

    @Test
    public void test30MoveDirectory() {
        String newDirPath = tmpDirPath + childDirectory;
        String destDirPath = newDirPath + "30";
        String newFileInDirName = "test30MoveDirectory.txt";
        createNewDirectory(newDirPath);
        assertThat(createEmptyFile(destDirPath + _SLASH + newFileInDirName)).isTrue();
        moveFileOrDirectory(newDirPath, destDirPath);
        assertThat(fileExists(destDirPath + _SLASH + newFileInDirName)).isTrue();
    }

    @Test
    public void test31CopyDirectory() {
        String newDirPath = tmpDirPath + childDirectory + "31";
        String copiedDirPath = newDirPath + "31";
        String newFileInDirName = "test31CopyDirectory.txt";
        createNewDirectory(newDirPath);
        assertThat(createEmptyFile(newDirPath + _SLASH + newFileInDirName)).isTrue();
        assertThat(copyFileOrDirectory(newDirPath, copiedDirPath)).isTrue();
        //files in both directories should exist if copied successfully
        assertThat(fileExists(copiedDirPath + _SLASH + newFileInDirName)).as("File copy failure").isTrue();
        assertThat(fileExists(newDirPath + _SLASH + newFileInDirName)).as("Created file doesn't exist after copying").isTrue();
    }

    @Test
    public void test32CopyFileWhenDestAlreadyExists() {
        String dir1 = tmpDirPath + childDirectory + "32";
        String dir2 = dir1 + "32";
        String fileInBothDirsName = "test32CopyFileWhenDestAlreadyExists.txt";
        String fileInDir1Path = dir1 + _SLASH + fileInBothDirsName;
        String fileInDir2Path = dir2 + _SLASH + fileInBothDirsName;

        createNewDirectory(dir1);
        createNewDirectory(dir2);
        assertThat(createEmptyFile(fileInDir2Path)).isTrue(); //empty file in second directory
        //some text in file, which is located in first directory
        final String stringToCheckAgainst = "test32CopyFileWhenDestAlreadyExists";
        MS_TextFile fileInDir1 = new MS_TextFile(fileInDir1Path);
        fileInDir1.writeln(stringToCheckAgainst);
        fileInDir1.close();

        //check that content of files differs
        assertThat(MS_TextFile.getFileTextAsString(fileInDir2Path, null)).isNotEqualTo(stringToCheckAgainst);
        assertThat(MS_TextFile.getFileTextAsString(fileInDir1Path, null)).isEqualTo(stringToCheckAgainst);

        //copy file with text to empty file destination
        assertThat(copyFileOrDirectory(fileInDir1Path, fileInDir2Path)).isTrue();

        //now both files should have same content
        assertThat(MS_TextFile.getFileTextAsString(fileInDir1Path, null)).isEqualTo(stringToCheckAgainst);
        assertThat(MS_TextFile.getFileTextAsString(fileInDir2Path, null)).isEqualTo(stringToCheckAgainst);
    }

    @Test
    public void test33GetFileOrFolderSize() {
        String dir = tmpDirPath + childDirectory + "33/";

        final String fileWithSize0Bytes = "fileWithSize0Bytes";
        createEmptyFile(dir + fileWithSize0Bytes);

        final String fileWithSize2Bytes = "fileWithSize2Bytes";
        MS_TextFile fileWriter;
        fileWriter = new MS_TextFile(dir + fileWithSize2Bytes);
        fileWriter.writeln("");
        fileWriter.close();

        final String fileWithSize6Bytes = "fileWithSize6Bytes";
        fileWriter = new MS_TextFile(dir + fileWithSize6Bytes);
        fileWriter.writeln("abcd");
        fileWriter.close();

        final String fileWithSize12Bytes = "fileWithSize12Bytes";
        fileWriter = new MS_TextFile(dir + fileWithSize12Bytes);
        fileWriter.writeln("1234567890");
        fileWriter.close();

        assertThat(getFileOrDirectorySize(dir + fileWithSize0Bytes)).as("File '" + fileWithSize0Bytes + "' size should be 1+2").isEqualTo(0);
        assertThat(getFileOrDirectorySize(dir + fileWithSize2Bytes)).as("File '" + fileWithSize2Bytes + "' size should be 1+2").isEqualTo(2);
        assertThat(getFileOrDirectorySize(dir + fileWithSize6Bytes)).as("File '" + fileWithSize6Bytes + "' size should be 3+2").isEqualTo(6);
        assertThat(getFileOrDirectorySize(dir + fileWithSize12Bytes)).as("File '" + fileWithSize12Bytes + "' size should be 10+2").isEqualTo(12);
        //now to try to determine folder's size. It should weight 20 bytes.
        assertThat(getFileOrDirectorySize(dir)).as("Folder's '" + dir + "' (containing all previous files) size should be 0+2+6+12").isEqualTo(20);
    }

    @Test
    public void test34GetNotExistingPathSize() {
        assertThat(getFileOrDirectorySize("ugabuga")).isEqualTo(0);
    }
}