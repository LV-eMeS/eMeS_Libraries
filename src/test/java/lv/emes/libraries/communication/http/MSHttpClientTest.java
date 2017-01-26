package lv.emes.libraries.communication.http;

import lv.emes.libraries.communication.CommunicationConstants;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static lv.emes.libraries.communication.http.MS_HttpClient.get;
import static lv.emes.libraries.communication.http.MS_HttpClient.post;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSHttpClientTest {
    private static final String URL_STRING_GET = "http://"+ CommunicationConstants.TESTING_SERVER_HOSTAME +"/Test/test_get.php";
    private static final String URL_STRING_POST = "http://"+CommunicationConstants.TESTING_SERVER_HOSTAME+"/Test/test_post.php";
    private static final String URL_STRING_NO_PARAMS = "http://"+CommunicationConstants.TESTING_SERVER_HOSTAME+"/Test/test_no_params.php";
    private static final String URL_STRING_WRONG_URL = "http://"+ CommunicationConstants.TESTING_SERVER_HOSTAME +"/Test/no_file_is_added.php";
    private static final String URL_STRING_UNREACHABLE_HOST = "http://111.111.111.111/Test/no_file_is_added.php";
    private static final String TEST_PARAMETER_NAME = "test";
    private static final String TEST_PARAMETER_VALUE = "vards";
    private static final String TEST_NO_PARAMETER_VALUE = TEST_PARAMETER_NAME;

    private static Map<String, String> params;
    private static MS_RequestResult response;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        params = new HashMap<>();
        params.put(TEST_PARAMETER_NAME, TEST_PARAMETER_VALUE);
    }

    @Test
    public void test01GetWithTestVariable() {
        response = get(URL_STRING_GET, params);
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test02PostWithTestVariable() {
        response = post(URL_STRING_POST, params);
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test03GetWithoutParameters() {
        response = get(URL_STRING_NO_PARAMS, params);
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_NO_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test04PostWithoutParameters() {
        response = post(URL_STRING_NO_PARAMS, params);
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_NO_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test05GetWithEmptyParameters() {
        response = get(URL_STRING_GET, null);
        assertNotEquals(TEST_NO_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test06PostWithEmptyParameters() {
        response = post(URL_STRING_GET, null);
        assertNotEquals(TEST_NO_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test07GetWrongURLScriptFilename() {
        response = get(URL_STRING_WRONG_URL, null);
        assertEquals(404, response.reponseCode);
        assertEquals("", response.message);
        assertEquals(FileNotFoundException.class, response.exception.getClass());
    }

    @Test
    public void test08PostWrongURLScriptFilename() {
        response = post(URL_STRING_WRONG_URL, null);
        assertEquals(404, response.reponseCode);
        assertEquals("", response.message);
    }

    @Test
    public void test09Timeout() {
        IFuncConnectionConfig config = (cn) -> {
            cn.setConnectTimeout(1);
            cn.setReadTimeout(1);
        };
        response = get(URL_STRING_UNREACHABLE_HOST, null, config);
        assertEquals(java.net.SocketTimeoutException.class, response.exception.getClass());

        config = (cn) -> {
            cn.setConnectTimeout(1);
            cn.setReadTimeout(1);
            cn.setDoOutput(true);
        };
        response = post(URL_STRING_UNREACHABLE_HOST, null, config);
        assertEquals(java.net.SocketTimeoutException.class, response.exception.getClass());
    }
}