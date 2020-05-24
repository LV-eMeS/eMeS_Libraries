package lv.emes.libraries.communication.tcp_ip;

import com.google.common.collect.ImmutableMap;
import lv.emes.libraries.communication.MS_TakenPorts;

import java.util.Map;

public final class MS_ClientServerConstants {

    private MS_ClientServerConstants() {
    }

    public static final String _DEFAULT_HOST = "localhost";
    public static final String _DC_NOTIFY_MESSAGE = "_DC_NOTIFY_MESSAGE";
    public static final String _NEW_CLIENT_ID_NOTIFY_MESSAGE = "_NEW_CLIENT_ID_NOTIFY_MESSAGE";
    public static final String _INFO_ABOUT_NEW_CLIENT = "_INFO_ABOUT_NEW_CLIENT";
    public static final String _CLIENT_DISCONNECTS_NOTIFY_MESSAGE = "_CLIENT_DISCONNECTS_NOTIFY_MESSAGE";
    public static final String _CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE = "_CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE";
    public static final String _SERVER_ACKNOWLEDGEMENT = "_SERVER_ACKNOWLEDGEMENT";
    public static final String _REFRESH_CLIENT_CURRENT_TIME = "_REFRESH_CLIENT_CURRENT_TIME";
    public static final String _CURRENT_CLIENT_TIME = "_CURRENT_CLIENT_TIME";
    public static final String _CLIENT_TIMEOUT = "_CLIENT_TIMEOUT";

    public static final int _CMD_WITH_NO_DATA = 1;
    public static final int _CMD_WITH_STRING_DATA = 2;
    public static final int _CMD_WITH_JSON_OBJECT_DATA = 3;
    public static final int _CMD_WITH_JSON_ARRAY_DATA = 4;
    public static final int _CMD_WITH_BINARY_DATA = 5;

    public static final Map<Integer, String> _CMD_DATA_TYPE_DESCRIPTIONS = ImmutableMap.<Integer, String>builder()
            .put(_CMD_WITH_NO_DATA, "_CMD_WITH_NO_DATA")
            .put(_CMD_WITH_STRING_DATA, "_CMD_WITH_STRING_DATA")
            .put(_CMD_WITH_JSON_OBJECT_DATA, "_CMD_WITH_JSON_OBJECT_DATA")
            .put(_CMD_WITH_JSON_ARRAY_DATA, "_CMD_WITH_JSON_ARRAY_DATA")
            .put(_CMD_WITH_BINARY_DATA, "_CMD_WITH_BINARY_DATA")
            .build();

    public static final String _CMD_DATA_KEY_OS = "os";
    public static final String _CMD_DATA_KEY_USER_NAME = "userName";
    public static final String _CMD_DATA_KEY_WORKING_DIR = "workingDir";
    public static final String _CMD_DATA_KEY_HOME_DIR = "homeDir";

    public static final Integer _DEFAULT_PORT_FOR_TESTING = MS_TakenPorts._DEFAULT_PORT_FOR_TESTING;
    public static final int _DEFAULT_CONNECT_TIMEOUT = 5000;
    public static final int _DEFAULT_WRITE_TIMEOUT = 3000;
}
