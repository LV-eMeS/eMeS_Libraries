package lv.emes.libraries.testutils;

import lv.emes.libraries.communication.json.MS_JSONObject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Meant only for internal unit tests.
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static void assertThatEqualToOnlyTemplateFields(MS_JSONObject objectToAssert, MS_JSONObject template) {
        MS_JSONObject cloneWithSubsetOfFields = new MS_JSONObject(objectToAssert, template.getKeyArray());
        assertThat(cloneWithSubsetOfFields).isEqualTo(template);
    }
}
