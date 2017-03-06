package lv.emes.libraries.communication.db;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements MySQL database common operation handling. Reduced to 4 simple methods + BLOB handling:
 * <p>Method summary:
 * <ul>
 * <li>connect</li>
 * <li>prepareSQLQuery</li>
 * <li>getQueryResult</li>
 * <li>commitStatement</li>
 * <li>setBLOB</li>
 * <li>getBLOB</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.4.
 */
public class MS_MySQLDatabase extends MS_JDBCDatabase {
    public static final int DEFAULT_PORT = 3306;

    private Boolean autoReconnect = false;

    @Override
    public void connect(String hostname, String dbName, int port, String userName, String password)
            throws ClassNotFoundException, SQLException {
        if (port == 0)
            port = DEFAULT_PORT;
        if (! dbName.equals("")) //if path to host is defined then it will be written right after hostname
            dbName = "/" + dbName;

        super.connect(hostname, dbName, port, userName, password); //simply saving variable values
        Class.forName("com.mysql.jdbc.Driver");
        // Create connection
        String connStr = String.format("jdbc:mysql://%s:%d%s?autoReconnect=%s", this.hostname, this.port, this.dbName, this.autoReconnect.toString());
        conn = DriverManager.getConnection(connStr, this.userName, this.password);
        conn.setAutoCommit(false);
    }

    public Boolean getDoAutoReconnect() {
        return autoReconnect;
    }

    /**
     * Call this method with true if auto reconnecting is needed to perform to avoid connection loss due to client timeout.
     * @param autoReconnect parameter to perform automatic reconnecting to DB. By default it's false.
     * @return reference to database object itself.
     */
    public MS_MySQLDatabase withAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
        return this;
    }
}