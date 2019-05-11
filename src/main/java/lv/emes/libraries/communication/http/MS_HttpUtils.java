package lv.emes.libraries.communication.http;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Utilities for HTTP related operations.
 * <p>Static methods:
 * <ul>
 * <li>formUrlQueryParams</li>
 * <li>formMultiUrlQueryParams</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.8.
 */
public final class MS_HttpUtils {

    private MS_HttpUtils() {
    }

    /**
     * Forms query parameters that needs to be added to url (starting with question mark '?').
     *
     * @param parameters key-value parameters.
     * @return query part of URL, for example: <code>?id=12345678&amp;name=test</code>.
     */
    public static String formUrlQueryParams(Map<String, String> parameters) {
        if (MS_CodingUtils.isEmpty(parameters)) return "";
        StringBuilder res = new StringBuilder();
        res.append("?");
        parameters.forEach((name, value) -> {
            if (!StringUtils.isEmpty(value)) {
                res.append(name);
                res.append("=");
                res.append(value);
                res.append("&");
            }
        });
        return res.toString().replaceFirst(".$", "");
    }

    /**
     * Forms query parameters that needs to be added to url (starting with question mark '?').
     *
     * @param parameters key-value parameters supporting multiple values for one key.
     * @return query part of URL, for example: <code>?id=12345678&amp;id=9876542&amp;name=test</code>.
     */
    public static String formMultiUrlQueryParams(Map<String, List<String>> parameters) {
        if (MS_CodingUtils.isEmpty(parameters)) return "";
        StringBuilder res = new StringBuilder();
        res.append("?");
        parameters.forEach((name, listOfValues) -> {
            if (!MS_CodingUtils.isEmpty(listOfValues)) {
                listOfValues.forEach(value -> {
                    if (!StringUtils.isEmpty(value)) {
                        res.append(name);
                        res.append("=");
                        res.append(value);
                        res.append("&");
                    }
                });
            }
        });
        return res.toString().replaceFirst(".$", "");
    }
}
