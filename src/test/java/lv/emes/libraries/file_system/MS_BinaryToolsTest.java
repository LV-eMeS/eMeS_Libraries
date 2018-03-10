package lv.emes.libraries.file_system;

import lv.emes.libraries.patches.jmimemagic.MimeMagic;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import org.junit.Test;

import static lv.emes.libraries.testdata.TestData.*;
import static org.junit.Assert.*;

/**
 * Tests part of binary tool features.
 * This test specifically created to test patched {@link MimeMagic}.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_BinaryToolsTest {

    private static String testFilePath;

    @Test
    public void isNotBinaryFileTextWithoutExtension() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_WITHOUT_EXT_TEXT;
        assertFalse(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isBinaryFileImageWithoutExtension() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_WITHOUT_EXT_IMAGE;
        assertTrue(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isNotBinaryFileText() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_TEXT;
        assertFalse(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isNotBinaryFileCsv() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_CSV;
        assertFalse(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isBinaryFileTestWithExeExtension() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_TEST_WITH_EXE_EXTENSION;
        assertFalse(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    /**
     * This is an exceptional case, as actual return of {@link MimeMagic} is "???", which means that it is not determined.
     * Although in this case it is more safe to threat .bat files as binary ones.
     */
    @Test
    public void isBinaryFileBat() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_BAT;
        assertTrue(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isBinaryFileExecutable() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_EXE;
        assertTrue(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isBinaryFileImage() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_IMAGE;
        assertTrue(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test
    public void isBinaryFileJar() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_JAR;
        assertTrue(MS_BinaryTools.isBinaryFile(testFilePath));
    }

    @Test(expected = MagicMatchNotFoundException.class)
    public void undeterminedFileArchive() throws MagicMatchNotFoundException {
        testFilePath = TEST_RESOURCES_DIR + TEST_FILE_ARCHIVE;
        MS_BinaryTools.isBinaryFile(testFilePath);
    }

    @Test
    public void testBase64FromAndToString() {
        final String TEXT = "A b 8 �,語āЮ";
        final String CORRESPONDING_BASE64_TEXT = "QSBiIDgg77+9LOiqnsSB0K4="; //hardcoded Base64 representation of TEXT

        String base64 = MS_BinaryTools.stringToBase64String(TEXT);
        assertEquals(CORRESPONDING_BASE64_TEXT, base64);
        String text = MS_BinaryTools.base64StringToString(base64);
        assertEquals(TEXT, text);

        assertNull(MS_BinaryTools.stringToBase64String(null));
        assertNull(MS_BinaryTools.base64StringToString(null));
    }
}