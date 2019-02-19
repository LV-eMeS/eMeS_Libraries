package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.testutils.TestUtils;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_TcpIPCommandTest {

    private static final String CODE = "Test";

    @Test
    public void testBuiltCommandHasUniqueIdOnMultipleCalls() {
        MS_TcpIPCommand command = new MS_TcpIPCommand(CODE);
        String firstBuildId = command.buildCommand().getString("id");
        assertThat(command.buildCommand().getString("id")).isNotEqualTo(firstBuildId);
    }

    @Test
    public void testCommandWithNoData() {
        TestUtils.assertThatEqualToOnlyTemplateFields(new MS_TcpIPCommand(CODE).buildCommand(),
                new MS_JSONObject()
                        .put("code", CODE)
                        .put("data", JSONObject.NULL)
                        .put("type", MS_ClientServerConstants._CMD_WITH_NO_DATA)
        );
    }

    @Test
    public void testCommandWithStringData() {
        String data = "data";
        TestUtils.assertThatEqualToOnlyTemplateFields(new MS_TcpIPCommand(CODE, data).buildCommand(),
                new MS_JSONObject()
                        .put("code", CODE)
                        .put("data", data)
                        .put("type", MS_ClientServerConstants._CMD_WITH_STRING_DATA)
        );
    }

    @Test
    public void testCommandWithJsonObjectData() {
        MS_JSONObject data = new MS_JSONObject()
                .put("item", "value")
                .put("int", 3)
                .put("obj", new MS_JSONObject())
                .put("arr", new MS_JSONArray().put(new JSONObject().put("sub", 1)));
        TestUtils.assertThatEqualToOnlyTemplateFields(new MS_TcpIPCommand(CODE, data).buildCommand(),
                new MS_JSONObject()
                        .put("code", CODE)
                        .put("data", data)
                        .put("type", MS_ClientServerConstants._CMD_WITH_JSON_OBJECT_DATA)
        );
    }

    @Test
    public void testCommandWithJsonArrayData() {
        MS_JSONArray data = new MS_JSONArray()
                .put("str")
                .put(3)
                .put(new MS_JSONObject())
                .put(new MS_JSONArray().put(new JSONObject().put("sub", 1)));
        TestUtils.assertThatEqualToOnlyTemplateFields(new MS_TcpIPCommand(CODE, data).buildCommand(),
                new MS_JSONObject()
                        .put("code", CODE)
                        .put("data", data)
                        .put("type", MS_ClientServerConstants._CMD_WITH_JSON_ARRAY_DATA)
        );
    }

    @Test
    public void testCommandWithBinaryData() {
        byte[] data = new byte[]{1, 2, 3};
        TestUtils.assertThatEqualToOnlyTemplateFields(new MS_TcpIPCommand(CODE, data).buildCommand(),
                new MS_JSONObject()
                        .put("code", CODE)
                        .put("data", MS_BinaryTools.bytesToString(data))
                        .put("type", MS_ClientServerConstants._CMD_WITH_BINARY_DATA)
        );
    }

    @Test
    public void testCommandWithEmptyBinaryDataConvertsBackNormally() {
        byte[] data = new byte[]{};
        String commandBinaryData = new MS_TcpIPCommand(CODE, data).buildCommand().getString("data");
        assertThat(MS_BinaryTools.stringToBytes(commandBinaryData)).isEqualTo(data);

        data = new byte[]{4, 5, 6, 7};
        commandBinaryData = new MS_TcpIPCommand(CODE, data).buildCommand().getString("data");
        assertThat(MS_BinaryTools.stringToBytes(commandBinaryData)).isEqualTo(data);
    }

    //*** Null data value tests ***

    @Test
    public void testCommandWithStringDataNull() {
        assertThat(new MS_TcpIPCommand(CODE, (String) null).buildCommand().isNull("data")).isTrue();
    }

    @Test
    public void testCommandWithJsonObjectDataNull() {
        assertThat(new MS_TcpIPCommand(CODE, (MS_JSONObject) null).buildCommand().isNull("data")).isTrue();
    }

    @Test
    public void testCommandWithJsonArrayDataNull() {
        assertThat(new MS_TcpIPCommand(CODE, (MS_JSONArray) null).buildCommand().isNull("data")).isTrue();
    }

    @Test
    public void testCommandWithEmptyBinaryDataNull() {
        assertThat(new MS_TcpIPCommand(CODE, (byte[]) null).buildCommand().isNull("data")).isTrue();
    }
}