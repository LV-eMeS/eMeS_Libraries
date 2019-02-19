package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONBuilder;
import lv.emes.libraries.communication.json.MS_JSONObject;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Common TCP/IP command structure builder for both client and server side.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3
 */
class MS_TcpIPCommandBuilder extends MS_JSONBuilder {

    public MS_TcpIPCommandBuilder withId(UUID commandId) {
        templ().put("id", commandId);
        return this;
    }

    public MS_TcpIPCommandBuilder withType(int commandType) {
        templ().put("type", commandType);
        return this;
    }

    public MS_TcpIPCommandBuilder withCode(String commandCode) {
        templ().put("code", commandCode);
        return this;
    }

    public MS_TcpIPCommandBuilder withData() {
        templ().put("data", JSONObject.NULL);
        return this;
    }

    public MS_TcpIPCommandBuilder withData(String commandData) {
        templ().putOpt("data", commandData);
        return this;
    }

    public MS_TcpIPCommandBuilder withData(MS_JSONObject commandDataAsJson) {
        templ().putOpt("data", commandDataAsJson);
        return this;
    }

    public MS_TcpIPCommandBuilder withData(MS_JSONArray commandDataAsJsonArray) {
        templ().putOpt("data", commandDataAsJsonArray);
        return this;
    }

    public String getCmdCode() {
        return templ().getString("code");
    }
}
