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
        conn = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + this.dbName,
                this.userName, this.password);
        conn.setAutoCommit(true);
    }
}