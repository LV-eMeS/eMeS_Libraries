package lv.emes.libraries.communication.http;

import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_HttpUtilsTest {

    @Test
    public void testFormQueryPathParamsSingleParamValueSecond() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", "1234567");
        params.put("parameter", "v6");
        params.put("filter", "ASC");
        String expected = String.format("?id=%s&parameter=%s&filter=%s", params.get("id"), params.get("parameter"), params.get("filter"));
        String actual = MS_HttpUtils.formUrlQueryParams(params);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormQueryPathParamsSingleParamValueThird() {
        String actual = MS_HttpUtils.formUrlQueryParams(null);
        String actual2 = MS_HttpUtils.formUrlQueryParams(new LinkedHashMap<>());
        assertThat(actual).isEmpty();
        assertThat(actual2).isEmpty();
    }

    @Test
    public void testFormQueryPathParamsSingleParamValueFourth() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", null);
        params.put("code", null);

        String actual = MS_HttpUtils.formUrlQueryParams(params);
        assertThat(actual).isEmpty();
    }

    @Test
    public void testFormQueryPathParamsSingleParamValueFifth() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", "12345");
        params.put("code", null);

        String expected = String.format("?id=%s", params.get("id"));
        String actual = MS_HttpUtils.formUrlQueryParams(params);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormQueryPathParamsSingleParamValueSixth() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", "");
        params.put("code", null);

        String actual = MS_HttpUtils.formUrlQueryParams(params);
        assertThat(actual).isEmpty();
    }

    //***********************************************************************************************

    @Test
    public void testFormQueryPathParamsFirst() {
        Map<String, List<String>> params = new LinkedHashMap<>();
        List<String> values = new LinkedList<>();
        values.add("5fg5g48djsks957fjd8u58fufuf4ufidod57jd7jf");
        values.add("b085y0e4cjr345t0340tjh0956g54");
        params.put("id", values);
        String expected = String.format("?id=%s&id=%s", params.get("id").get(0), params.get("id").get(1));
        String actual = MS_HttpUtils.formMultiUrlQueryParams(params);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormQueryPathParamsSecond() {
        Map<String, List<String>> params = new LinkedHashMap<>();
        List<String> ids = new LinkedList<>();
        ids.add("1234567");
        List<String> filters = new LinkedList<>();
        filters.add("ASC");
        params.put("ids", ids);
        params.put("parameter", null);
        params.put("filter", filters);
        String expected = String.format("?ids=%s&filter=%s", params.get("ids").get(0), params.get("filter").get(0));
        String actual = MS_HttpUtils.formMultiUrlQueryParams(params);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testFormQueryPathParamsThird() {
        String actual = MS_HttpUtils.formMultiUrlQueryParams(null);
        String actual2 = MS_HttpUtils.formMultiUrlQueryParams(new LinkedHashMap<>());
        assertThat(actual).isEmpty();
        assertThat(actual2).isEmpty();
    }

    @Test
    public void testFormQueryPathParamsFourth() {
        Map<String, List<String>> params = new LinkedHashMap<>();
        params.put("id", new ArrayList<>());
        params.put("code", null);

        String actual = MS_HttpUtils.formMultiUrlQueryParams(params);
        assertThat(actual).isEmpty();
    }

    @Test
    public void testFormQueryPathParamsFifth() {
        Map<String, List<String>> params = new LinkedHashMap<>();
        params.put("code", Collections.singletonList(""));
        params.put("id", Collections.singletonList("12345"));

        String expected = String.format("?id=%s", params.get("id").get(0));
        String actual = MS_HttpUtils.formMultiUrlQueryParams(params);
        assertThat(actual).isEqualTo(expected);
    }
}
