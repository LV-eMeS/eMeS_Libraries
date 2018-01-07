package lv.emes.libraries.testdata;

import lv.emes.libraries.communication.MS_TakenPorts;
import lv.emes.libraries.file_system.MS_FileSystemTools;

/**
 * Contains methods and constants that provides different test data and objects.
 *
 * @author eMeS
 * @version 1.2.
 */
public class TestData {

    public static final String TEST_RESOURCES_DIR = "src/test/resources/";
    public static final String TEMP_DIR = MS_FileSystemTools.getTmpDirectory();
    public static final String TESTING_SERVER_HOSTAME = "emeslv.sytes.net";
    public static final String TESTING_WEB_SERVER_PORT_STRING = ":" + MS_TakenPorts._SPRING_WEB_SERVER_PORT;
    public static final String HTTP_PREFIX = "http://";

    public static final String TEST_FILE_IMAGE = "test_pic.png";
    public static final String TEST_FILE_TEXT = "sampleTextFile4Testing.txt";
    public static final String TEST_FILE_BAT = "test.bat";
    public static final String TEST_FILE_CSV = "commaSeparated.csv";
    public static final String TEST_FILE_TEST_WITH_EXE_EXTENSION = "fakeExe.exe";
    public static final String TEST_FILE_EXE = "normalExeFile.exe";
    public static final String TEST_FILE_JAR = "jar.jar";
    public static final String TEST_FILE_ARCHIVE = "archive.rar";
    public static final String TEST_FILE_WITHOUT_EXT_TEXT = "sampleTextFile4Testing";
    public static final String TEST_FILE_WITHOUT_EXT_IMAGE = "test_pic";
}