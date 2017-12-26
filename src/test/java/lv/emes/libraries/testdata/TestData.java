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

    public static final String TEMP_DIR = MS_FileSystemTools.getTmpDirectory();
    public static final String TESTING_SERVER_HOSTAME = "emeslv.sytes.net";
    public static final String TESTING_WEB_SERVER_PORT_STRING = ":" + MS_TakenPorts._SPRING_WEB_SERVER_PORT;
    public static final String HTTP_PREFIX = "http://";
}
