package lv.emes.libraries.communication.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements MySQL database common operation handling.
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_MySQLDatabase extends MS_AbstractJDBCDatabase {

    public static final int _DEFAULT_PORT = 3306;

    public MS_MySQLDatabase(MS_DBParameters connParams) {
        super(connParams);
    }

    @Override
    public void initialize() throws ClassNotFoundException, NullPointerException {
        super.initialize();
        if (connParams.getPort() == 0)
            connParams.withPort(_DEFAULT_PORT);

        String connStringDatabaseName;
        if (connParams.getDbName().equals("")) {
            connStringDatabaseName = "";
        } else {
            //if path to host is defined then it will be written right after hostname
            connStringDatabaseName = "/" + connParams.getDbName();
        }

        Class.forName("com.mysql.jdbc.Driver");
        // Create connection
        connectionString = String.format("jdbc:mysql://%s:%d%s", connParams.getHostname(), connParams.getPort(), connStringDatabaseName);
    }

    @Override
    protected Connection getConnectionFromDriver() throws SQLException {
        return DriverManager.getConnection(this.connectionString, connParams.getUserName(), connParams.getPassword());
    }
}