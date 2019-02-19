package lv.emes.libraries.communication.tcp_ip;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.file_system.MS_BinaryTools;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable TCP/IP command structure for both client and server side to be sent to opposite
 * side (correspondingly - server or client).
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3
 */
@Immutable
public class MS_TcpIPCommand {

    private MS_TcpIPCommandBuilder cmdBuilder;

    /**
     * Creates new TCP/IP command with specified <b>code</b> and no data to send to receiver.
     *
     * @param code unique command code between this type of command that receiver is registered as known command.
     */
    public MS_TcpIPCommand(String code) {
        this.cmdBuilder = newCmdBuilder(code).withData().withType(MS_ClientServerConstants._CMD_WITH_NO_DATA);
    }

    /**
     * Creates new TCP/IP command with specified <b>code</b> and data of {@link String} type to send to receiver.
     *
     * @param code unique command code between this type of command that receiver is registered as known command.
     * @param data nullable string data that receiver will be operating with.
     *             Receiver should handle <tt>null</tt>s himself if those are necessary by contract between sender and receiver.
     */
    public MS_TcpIPCommand(String code, String data) {
        this.cmdBuilder = newCmdBuilder(code).withData(data).withType(MS_ClientServerConstants._CMD_WITH_STRING_DATA);
    }

    /**
     * Creates new TCP/IP command with specified <b>code</b> and data of {@link MS_JSONObject} type to send to receiver.
     *
     * @param code unique command code between this type of command that receiver is registered as known command.
     * @param data nullable JSON object data that receiver will be operating with.
     *             Receiver should handle <tt>null</tt>s himself if those are necessary by contract between sender and receiver.
     */
    public MS_TcpIPCommand(String code, MS_JSONObject data) {
        this.cmdBuilder = newCmdBuilder(code).withData(data).withType(MS_ClientServerConstants._CMD_WITH_JSON_OBJECT_DATA);
    }

    /**
     * Creates new TCP/IP command with specified <b>code</b> and data of {@link MS_JSONArray} type to send to receiver.
     *
     * @param code unique command code between this type of command that receiver is registered as known command.
     * @param data nullable JSON array data that receiver will be operating with.
     *             Receiver should handle <tt>null</tt>s himself if those are necessary by contract between sender and receiver.
     */
    public MS_TcpIPCommand(String code, MS_JSONArray data) {
        this.cmdBuilder = newCmdBuilder(code).withData(data).withType(MS_ClientServerConstants._CMD_WITH_JSON_ARRAY_DATA);
    }

    /**
     * Creates new TCP/IP command with specified <b>code</b> and data of byte array type to send to receiver.
     *
     * @param code unique command code between this type of command that receiver is registered as known command.
     * @param data nullable binary data that receiver will be operating with.
     *             Receiver should handle <tt>null</tt>s himself if those are necessary by contract between sender and receiver.
     */
    public MS_TcpIPCommand(String code, byte[] data) {
        this.cmdBuilder = newCmdBuilder(code).withData(MS_BinaryTools.bytesToString(data)).withType(MS_ClientServerConstants._CMD_WITH_BINARY_DATA);
    }

    /**
     * Method to be used by client or server exactly when method to send command is called.
     */
    public MS_JSONObject buildCommand() {
        return cmdBuilder.withId(UUID.randomUUID()).build(); //generate new ID every time command is sent
    }

    public String getCmdCode() {
        return cmdBuilder.getCmdCode();
    }

    private MS_TcpIPCommandBuilder newCmdBuilder(String code) {
        Objects.requireNonNull(code, "Command code must not be null");
        return new MS_TcpIPCommandBuilder().withCode(code);
    }
}
