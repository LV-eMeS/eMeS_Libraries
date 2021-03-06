package lv.emes.libraries.file_system;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_StringList;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.InputStream;
import java.util.List;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_TextFileTest {

    private final static String TEST_DIR = "src/test/resources/";
    private final static String FIRST_FILE = TEST_DIR + "MS_TextFileTest.txt";
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
    private final static String MISSING_DIRECTORY_NAME = TestData.TEMP_DIR + "test09CreateFileInNonExistingDirectory/Directory/";

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
        assertThat(fileExists(FIRST_FILE)).isFalse();
        assertThat(fileExists(SECOND_FILE)).isFalse();

        deleteDirectory(directoryUp(MISSING_DIRECTORY_NAME));
        assertThat(directoryExists(MISSING_DIRECTORY_NAME)).isFalse();
    }

    @Test
    public void test01CreateFirstFile() {
        MS_TextFile writer = new MS_TextFile(FIRST_FILE);
        writer.writeln(TEXT1, false);
        writer.writeln(TEXT2, true);
        assertThat(fileExists(FIRST_FILE)).isTrue();
    }

    @Test
    public void test02AppendFirstFile() {
        MS_TextFile appender = new MS_TextFile(FIRST_FILE);
        appender.appendln(TEXT3, true);
        assertThat(fileExists(FIRST_FILE)).isTrue(); //if it still exists after appending
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
        assertThat(contentsOfFile.size()).isEqualTo(3);
        assertThat(contentsOfFile.get(0)).isEqualTo(TEXT1);
        assertThat(contentsOfFile.get(1)).isEqualTo(TEXT2);
        assertThat(contentsOfFile.get(2)).isEqualTo(TEXT3);
        assertThat(fileExists(FIRST_FILE)).isTrue();
    }

    @Test
    public void test04FromStringListToFile() {
        //now to create a list, which we will use to rewrite MS_TextFileTest.txt file with completetly different text
        MS_StringList list = new MS_StringList();
        list.add(TEXT4);
        list.add(TEXT5);
        list.add(TEXT6);

        MS_TextFile writer = new MS_TextFile(FIRST_FILE);
        List<String> sList = list.toList();
        writer.exportStringListToFile(sList); //export to file using instance of MS_TextFile
        MS_TextFile.exportStringListToFile(SECOND_FILE, sList); //export to file using static method of MS_TextFile
        writer.close();
        assertThat(fileExists(FIRST_FILE)).isTrue();
        assertThat(fileExists(SECOND_FILE)).isTrue();
    }

    @Test
    public void test05FromFileToStringList() {
        List<String> sList = MS_TextFile.importStringListFromFile(FIRST_FILE);
        assertThat(sList.size()).isEqualTo(3);
        assertThat(sList.get(0)).isEqualTo(TEXT4);
        assertThat(sList.get(1)).isEqualTo(TEXT5);
        assertThat(sList.get(2)).isEqualTo(TEXT6);
        sList.clear();

        MS_TextFile reader = new MS_TextFile(SECOND_FILE);
        sList = reader.importStringListFromFile();
        assertThat(sList.size()).isEqualTo(3);
        assertThat(sList.get(0)).isEqualTo(TEXT4);
        assertThat(sList.get(1)).isEqualTo(TEXT5);
        assertThat(sList.get(2)).isEqualTo(TEXT6);
    }

    @Test
    public void test06ReadFileFromResource() {
        InputStream iStream = getResourceInputStream(FILE_AS_RESOURCE);
        MS_TextFile text = new MS_TextFile(iStream);

        assertThat(text.readln()).isEqualTo(TEXT7_AS_RESOURCE);
        assertThat(text.readln()).isEqualTo(TEXT8_AS_RESOURCE);
    }

    @Test
    public void test07ReadingFileAsString() {
        String str = MS_TextFile.getFileTextAsString(FIRST_FILE, "");
        assertThat(str).isEqualTo(TEXT4 + TEXT5 + TEXT6);

        //now to test same file as resource
        assertThat(fileExists(FIRST_FILE)).isTrue();
        str = MS_TextFile.getResourceFileTextAsString(FILE_AS_RESOURCE);
        assertThat(str).isEqualTo(TEXT7_AS_RESOURCE + "\r\n" + TEXT8_AS_RESOURCE);
    }

    @Test
    public void test08MixedOperations() throws MagicMatchNotFoundException {
        MS_TextFile mixed = new MS_TextFile(FIRST_FILE);
        mixed.write(TEXT1, false);
        mixed.writeln(TEXT2, true);
        //additional test for static method
        assertThat(MS_TextFile.isTextFile(FIRST_FILE)).isTrue();
        mixed.append(TEXT3);
        mixed.appendln(TEXT4, true);
        assertThat(mixed.readln()).isEqualTo(TEXT1 + TEXT2);
        assertThat(mixed.readln()).isEqualTo(TEXT3 + TEXT4);

        mixed.writeln(TEXT4, true); //once again new line
        assertThat(mixed.readln()).isEqualTo(TEXT4);
        mixed.close();
    }
}