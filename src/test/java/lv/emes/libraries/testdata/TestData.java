package lv.emes.libraries.testdata;

import com.google.common.collect.ImmutableMap;
import lv.emes.libraries.communication.MS_TakenPorts;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.file_system.MS_FileSystemTools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains methods and constants that provides different test data and objects.
 *
 * @author eMeS
 * @version 2.0.
 */
public final class TestData {

    private TestData() {
    }

    public static final Map<String, Object> OBJECT_MAP = newObjectMap();

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

    public static Map<String, Object> newObjectMap() {
        Map<String, Object> aMap = new HashMap<>();
        aMap.put("null", null);
        aMap.put("primitive", 123);
        aMap.put("bool", true);
        aMap.put("object", new Object());
        aMap.put("bean", new TestObjectWithGettersOnly());
        aMap.put("beanJSONRepresentation", new MS_JSONObject().put("field1", "fieldValue").put("field2", 4));
        aMap.put("list", Arrays.asList(false, 2L, new Object()));
        aMap.put("listOfLists", Arrays.asList(Collections.singletonList(999), Arrays.asList(998, 997)));
        aMap.put("json", new MS_JSONObject().put("key1", "value"));
        aMap.put("orgJson", new org.json.JSONObject().put("key1", "value"));
        aMap.put("map", ImmutableMap.<String, Double>builder().put("double", 3.14d).build());
        aMap.put("mapJSONRepresentation", new MS_JSONObject().put("double", 3.14d));
        aMap.put("array", new MS_JSONArray().put(1));
        aMap.put("orgArray", new org.json.JSONArray().put(1));
        return aMap;
    }
}