package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.testutils.TestUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_TcpIPCommandBuilderTest {

    @Test
    public void testNewBuilder() {
        UUID commandId = UUID.randomUUID();
        assertThat(new MS_TcpIPCommandBuilder().withType(MS_ClientServerConstants._CMD_WITH_STRING_DATA).withId(commandId).withData("Test").build())
                .matches(cmd -> cmd.has("id"), "has id")
                .matches(cmd -> cmd.has("data"), "has data")
                .matches(cmd -> cmd.has("type"), "has type")
                .isEqualTo(new MS_JSONObject().put("id", commandId).put("data", "Test").put("type", MS_ClientServerConstants._CMD_WITH_STRING_DATA));
    }

    @Test
    public void testNewBuilderWithTypeNullAndId() {
        MS_JSONObject command = new MS_TcpIPCommandBuilder().withType(MS_ClientServerConstants._CMD_WITH_NO_DATA).withId(null).build();
        assertThat(command.has("id")).isTrue();
        TestUtils.assertThatEqualToOnlyTemplateFields(command, new MS_JSONObject().put("data", JSONObject.NULL).put("type", MS_ClientServerConstants._CMD_WITH_NO_DATA));
    }

    @Test
    public void testGetCode() {
        String code = "Some code";
        assertThat(new MS_TcpIPCommandBuilder().withCode(code).getCmdCode()).isEqualTo(code);
    }
}