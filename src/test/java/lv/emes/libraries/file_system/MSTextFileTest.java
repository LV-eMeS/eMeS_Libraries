package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.lists.MS_StringList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.InputStream;
import java.util.List;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSTextFileTest {
    private final static String TEST_DIR = "src/test/resources/";
    private final static String FIRST_FILE = TEST_DIR + "MSTextFileTest.txt";
    private final static String FILE_AS_RESOURCE = "sampleTextFile4Testing.txt";
    private final static String SECOND_FILE = TEST_DIR + "MSTextFileTest2.txt";
    private final static String TEXT1 = "Test string"; //EN
    private final static String TEXT2 = "Test string2"; //EN
    private final static String TEXT3 = "Test string3ā"; //LV
    private final static String TEXT4 = "Pavisam jauns teksts, kuru būs nepieciešams ierakstīt failā."; //LV
    private final static String TEXT5 = "Šī būs otrā rinda."; //LV
    private final static String TEXT6 = "А эта будет третья строка."; //RU
    private final static String TEXT7_AS_RESOURCE = "2 rows";
    private final static String TEXT8_AS_RESOURCE = "in this file.";
    private final static String MISSING_DIRECTORY_NAME = getTmpDirectory() + "test09CreateFileInNonExistingDirectory/Directory/";
    private final static String FILENAME_FOR_MISSING_DIR = "test09CreateFileInNonExistingDirectory.txt";

    @BeforeClass
    public static void initializeTestObjects() {
        //delete both test files if present!
        deleteFile(FIRST_FILE);
        deleteFile(SECOND_FILE);
    }

    @AfterClass
    public static void cleanUp() {
        deleteFile(FIRST_FILE);
        deleteFile(SECOND_FILE);
        assertFalse(fileExists(FIRST_FILE));
        assertFalse(fileExists(SECOND_FILE));

        deleteDirectory(directoryUp(MISSING_DIRECTORY_NAME));
        assertFalse(directoryExists(MISSING_DIRECTORY_NAME));
    }

    @Test
    public void test01CreateFirstFile() {
        MS_TextFile writer = new MS_TextFile(FIRST_FILE);
        writer.writeln(TEXT1, false);
        writer.writeln(TEXT2, true);
        assertTrue(fileExists(FIRST_FILE));
    }

    @Test
    public void test02AppendFirstFile() {
        MS_TextFile appender = new MS_TextFile(FIRST_FILE);
        appender.appendln(TEXT3, true);
        assertTrue(fileExists(FIRST_FILE)); //if it still exists after appending
    }

    @Test
    public void test03ReadFirstFile() {
        MS_TextFile reader = new MS_TextFile(FIRST_FILE);
        String str;
        MS_StringList contentsOfFile = new MS_StringList();
        while ((str = reader.readln()) != null) {
            contentsOfFile.add(str);
        }
        reader.close();
        assertEquals(3, contentsOfFile.size());
        assertEquals(TEXT1, contentsOfFile.get(0));
        assertEquals(TEXT2, contentsOfFile.get(1));
        assertEquals(TEXT3, contentsOfFile.get(2));
        assertTrue(fileExists(FIRST_FILE));
    }

    @Test
    public void test04FromStringListToFile() {
        //now to create a list, which we will use to rewrite MSTextFileTest.txt file with completetly different text
        MS_StringList list = new MS_StringList();
        list.add(TEXT4);
        list.add(TEXT5);
        list.add(TEXT6);

        MS_TextFile writer = new MS_TextFile(FIRST_FILE);
        List<String> sList = list.toList();
        writer.exportStringListToFile(sList); //export to file using instance of MS_TextFile
        MS_TextFile.exportStringListToFile(SECOND_FILE, sList); //export to file using static method of MS_TextFile
        writer.close();
        assertTrue(fileExists(FIRST_FILE));
        assertTrue(fileExists(SECOND_FILE));
    }

    @Test
    public void test05FromFileToStringList() {
        List<String> sList = MS_TextFile.importStringListFromFile(FIRST_FILE);
        assertEquals(3, sList.size());
        assertEquals(TEXT4, sList.get(0));
        assertEquals(TEXT5, sList.get(1));
        assertEquals(TEXT6, sList.get(2));
        sList.clear();

        MS_TextFile reader = new MS_TextFile(SECOND_FILE);
        sList = reader.importStringListFromFile();
        assertEquals(3, sList.size());
        assertEquals(TEXT4, sList.get(0));
        assertEquals(TEXT5, sList.get(1));
        assertEquals(TEXT6, sList.get(2));
    }

    @Test
    public void test06ReadFileFromResource() {
        InputStream iStream = getResourceInputStream(FILE_AS_RESOURCE);
        MS_TextFile text = new MS_TextFile(iStream);

        assertEquals(TEXT7_AS_RESOURCE, text.readln());
        assertEquals(TEXT8_AS_RESOURCE, text.readln());
    }

    @Test
    public void test07ReadingFileAsString() {
        String str = MS_TextFile.getFileTextAsString(FIRST_FILE, "");
        assertEquals(TEXT4 + TEXT5 + TEXT6, str);

        //now to test same file as resource
        assertTrue(fileExists(FIRST_FILE));
        str = MS_TextFile.getResourceFileTextAsString(FILE_AS_RESOURCE);
        assertEquals(TEXT7_AS_RESOURCE + "\r\n" + TEXT8_AS_RESOURCE, str);
    }

    @Test
    public void test08MixedOperations() {
        MS_TextFile mixed = new MS_TextFile(FIRST_FILE);
        mixed.write(TEXT1, false);
        mixed.writeln(TEXT2, true);
        mixed.append(TEXT3);
        mixed.appendln(TEXT4, true);
        assertEquals(TEXT1 + TEXT2, mixed.readln());
        assertEquals(TEXT3 + TEXT4, mixed.readln());

        mixed.writeln(TEXT4, true); //once again new line
        assertEquals(TEXT4, mixed.readln());
        mixed.close();
    }

    @Test
    public void test09CreateFileInNonExistingDirectory() {
        deleteDirectory(directoryUp(MISSING_DIRECTORY_NAME));
        assertFalse(directoryExists(MISSING_DIRECTORY_NAME));

        boolean fileCreated = MS_TextFile.createEmptyFile(MISSING_DIRECTORY_NAME + FILENAME_FOR_MISSING_DIR);
        assertFalse("File shouldn't be created using this method!", fileCreated);

        //but it can be created by overriding a text file manually
        MS_TextFile aFile = new MS_TextFile(MISSING_DIRECTORY_NAME + FILENAME_FOR_MISSING_DIR);
        assertTrue( aFile.writeln("Test") );
        aFile.close();
    }
}