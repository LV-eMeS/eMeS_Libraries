package lv.emes.libraries.communication.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static lv.emes.libraries.communication.http.MS_HttpClient.*;
import static lv.emes.libraries.testdata.TestData.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSHttpClientTest {
    private static final String URL_STRING_GET = HTTP_PREFIX + TESTING_SERVER_HOSTAME + TESTING_WEB_SERVER_PORT_STRING + "/Test/test_get.php";
    private static final String URL_STRING_POST = HTTP_PREFIX + TESTING_SERVER_HOSTAME + TESTING_WEB_SERVER_PORT_STRING + "/Test/test_post.php";
    private static final String URL_STRING_NO_PARAMS = HTTP_PREFIX + TESTING_SERVER_HOSTAME + TESTING_WEB_SERVER_PORT_STRING + "/Test/test_no_params.php";
    private static final String URL_STRING_WRONG_URL = HTTP_PREFIX + TESTING_SERVER_HOSTAME + TESTING_WEB_SERVER_PORT_STRING + "/Test/no_file_is_added.php";
    private static final String URL_STRING_UNREACHABLE_HOST = "http://111.111.111.111/Test/no_file_is_added.php";
    private static final String TEST_PARAMETER_NAME = "test";
    private static final String TEST_PARAMETER_VALUE = "vards";
    private static final String TEST_NO_PARAMETER_VALUE = TEST_PARAMETER_NAME;
    private static final String TEST_HEADER_NAME = "header1";
    private static final String TEST_HEADER_VALUE = "header value";

    private static Map<String, String> params;
    private static Map<String, String> headers;
    private static MS_HttpRequestResult response;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        params = new HashMap<>();
        params.put(TEST_PARAMETER_NAME, TEST_PARAMETER_VALUE);

        headers = new HashMap<>();
        headers.put(TEST_HEADER_NAME, TEST_HEADER_VALUE);
    }

    @Test
    public void test01GetWithTestVariable() {
        response = get(URL_STRING_GET, params);
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_PARAMETER_VALUE, response.message);

        response = get(URL_STRING_GET, params, headers); //test that header passing will not break the request itself
        assertEquals(200, response.reponseCode);
        assertEquals(TEST_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test02PostWithTestVariable() {
        response = post(URL_STRING_POST, params, null);
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
        response = post(URL_STRING_NO_PARAMS, params, null);
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
        response = post(URL_STRING_GET, null, null);
        assertNotEquals(TEST_NO_PARAMETER_VALUE, response.message);
    }

    @Test
    public void test07GetWrongURLScriptFilename() {
        response = get(URL_STRING_WRONG_URL, null);
        assertEquals(404, response.reponseCode);
    }

    @Test
    public void test08PostWrongURLScriptFilename() {
        response = post(URL_STRING_WRONG_URL, null, null);
        assertEquals(404, response.reponseCode);
    }

    @Test
    public void test09PutWrongURLScriptFilename() {
        response = put(URL_STRING_WRONG_URL, null, null);
        assertEquals(404, response.reponseCode);
    }

    @Test
    public void test10DeleteWrongURLScriptFilename() {
        response = delete(URL_STRING_WRONG_URL, null, null);
        assertEquals(404, response.reponseCode);
    }

    @Test
    public void test00Timeout() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1).setConnectionRequestTimeout(1).setSocketTimeout(1)
                .build();
        response = get(URL_STRING_UNREACHABLE_HOST, null, null, config);
        assertEquals(ConnectTimeoutException.class, response.exception.getClass());

        response = post(URL_STRING_UNREACHABLE_HOST, (Map<String, String>) null, null, config);
        assertEquals(ConnectTimeoutException.class, response.exception.getClass());
    }
}