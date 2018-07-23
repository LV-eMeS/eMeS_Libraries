package lv.emes.libraries.communication.http;

import okhttp3.OkHttpClient;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static lv.emes.libraries.testdata.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_HttpClientTest {

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
    private static final OkHttpClient CLIENT = MS_HTTPConnectionConfigurations.DEFAULT_HTTP_CONFIG_FOR_CONNECTION.build();

    private static Map<String, String> params;
    private static Map<String, String> headers;
    private MS_HttpRequest request = new MS_HttpRequest().withClientConfigurations(CLIENT).withMethod(MS_HttpRequestMethod.GET).withParameters(params);

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        params = new HashMap<>();
        params.put(TEST_PARAMETER_NAME, TEST_PARAMETER_VALUE);

        headers = new HashMap<>();
        headers.put(TEST_HEADER_NAME, TEST_HEADER_VALUE);
    }

    @Test
    public void test01GetWithTestVariable() throws IOException {
        request.withUrl(URL_STRING_GET);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(200, response.getStatusCode());
        assertEquals(TEST_PARAMETER_VALUE, response.getBodyAsString());

        //test that header passing will not break the request itself
        response = MS_HttpCallHandler.call(request.withHeaders(headers));
        assertEquals(200, response.getStatusCode());
        assertEquals(TEST_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test02PostWithTestVariable() throws IOException {
        request.withMethod(MS_HttpRequestMethod.POST).withUrl(URL_STRING_POST);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(200, response.getStatusCode());
        assertEquals(TEST_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test03GetWithoutParameters() throws IOException {
        request.withMethod(MS_HttpRequestMethod.GET).withUrl(URL_STRING_NO_PARAMS);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(200, response.getStatusCode());
        assertEquals(TEST_NO_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test04PostWithoutParameters() throws IOException {
        request.withMethod(MS_HttpRequestMethod.POST).withUrl(URL_STRING_NO_PARAMS);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(200, response.getStatusCode());
        assertEquals(TEST_NO_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test05GetWithEmptyParameters() throws IOException {
        request.withMethod(MS_HttpRequestMethod.GET).withUrl(URL_STRING_GET).withParameters(null);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertNotEquals(TEST_NO_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test06PostWithEmptyParameters() throws IOException {
        request.withMethod(MS_HttpRequestMethod.POST).withUrl(URL_STRING_GET).withParameters(null);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertNotEquals(TEST_NO_PARAMETER_VALUE, response.getBodyAsString());
    }

    @Test
    public void test07GetWrongURL() throws IOException {
        request.withMethod(MS_HttpRequestMethod.GET).withUrl(URL_STRING_WRONG_URL);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void test08PostWrongURL() throws IOException {
        request.withMethod(MS_HttpRequestMethod.POST).withUrl(URL_STRING_WRONG_URL);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void test09PutWrongURL() throws IOException {
        request.withMethod(MS_HttpRequestMethod.PUT).withUrl(URL_STRING_WRONG_URL).withParameters(null);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void test10DeleteWrongURL() throws IOException {
        request.withMethod(MS_HttpRequestMethod.DELETE).withUrl(URL_STRING_WRONG_URL);
        MS_HttpResponse response = MS_HttpCallHandler.call(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test(expected = IOException.class)
    public void test00Timeout() throws IOException {
        OkHttpClient httpClientConfig = new OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MILLISECONDS)
                .readTimeout(1, TimeUnit.MILLISECONDS)
                .writeTimeout(1, TimeUnit.MILLISECONDS)
                .build();
        request.withClientConfigurations(httpClientConfig)
                .withMethod(MS_HttpRequestMethod.GET).withParameters(null).withUrl(URL_STRING_UNREACHABLE_HOST);
        MS_HttpCallHandler.call(request);
    }
}