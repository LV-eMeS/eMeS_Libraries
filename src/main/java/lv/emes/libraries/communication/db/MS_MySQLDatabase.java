package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_BadSetupException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements MySQL database common operation handling.
 *
 * @author eMeS
 * @version 2.1.
 */
public class MS_MySQLDatabase extends MS_AbstractJDBCDatabase {

    public static final int _DEFAULT_PORT = 3306;

    /**
     * Creates new instance of MySQL database and initializes it immediately with all passed <b>connParams</b>.
     *
     * @param connParams connection parameters and connection pool settings.
     * @throws MS_BadSetupException if JDBC driver is not found due to {@link ClassNotFoundException}.
     * @throws NullPointerException if some of connection variables are still not set for this DB or are invalid,
     *                              which causes connection string to be <i>null</i>.
     * @see MS_AbstractJDBCDatabase#initialize()
     */
    public MS_MySQLDatabase(MS_DBParameters connParams) throws NullPointerException, MS_BadSetupException {
        super(connParams);
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String formConnectionString() {
        if (connParams.getPort() == 0)
            connParams.withPort(_DEFAULT_PORT);

        String connStringDatabaseName;
        if (connParams.getDbName().equals("")) {
            connStringDatabaseName = "";
        } else {
            //if path to host is defined then it will be written right after hostname
            connStringDatabaseName = "/" + connParams.getDbName();
        }

        // Prepare connection string
        return String.format("jdbc:mysql://%s:%d%s", connParams.getHostname(), connParams.getPort(), connStringDatabaseName);
    }

    @Override
    protected Connection getConnectionFromDriver() throws SQLException {
        return DriverManager.getConnection(this.getConnectionString(), connParams.getUserName(), connParams.getPassword());
    }
}