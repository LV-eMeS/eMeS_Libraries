package lv.emes.libraries.communication.db;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements MySQL database common operation handling.
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_MySQLDatabase extends MS_JDBCDatabase {
    public static final int DEFAULT_PORT = 3306;

    @Override
    public void connect() throws ClassNotFoundException, SQLException {
        if (this.isConnectionInitializationNeeded()) {
            if (port == 0) port = DEFAULT_PORT;

            String connStringDatabaseName;
            if (dbName.equals("")) {
                connStringDatabaseName = "";
            } else {
                //if path to host is defined then it will be written right after hostname
                connStringDatabaseName = "/" + dbName;
            }

            Class.forName("com.mysql.jdbc.Driver");
            // Create connection
            connectionString = String.format("jdbc:mysql://%s:%d%s", this.hostname, this.port, connStringDatabaseName);
            conn = DriverManager.getConnection(this.connectionString, this.userName, this.password);
            conn.setAutoCommit(false);
        }
            conn = DriverManager.getConnection(this.connectionString, this.userName, this.password);
    }
}