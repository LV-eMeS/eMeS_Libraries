package lv.emes.libraries.communication.db;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements MySQL database common operation handling. Reduced to 4 simple methods + BLOB handling: 
 * <p>Method summary:
 * -connect
 * -prepareQuery
 * -getQueryResult
 * -commitStatement
 * -setBLOB
 * -getBLOB
 * @version 1.2.
 * @author eMeS
 */
public class MS_MySQLDatabase extends MS_JDBCDatabase {
	public static final int DEFAULT_PORT = 3306;
	
	@Override
	public void connect(String hostname, String dbName, int port, String userName, String password)
			throws ClassNotFoundException {
		if (port == 0)
			port = DEFAULT_PORT;

		super.connect(hostname, dbName, port, userName, password); //simply saving variable values
		Class.forName("com.mysql.jdbc.Driver");
		// Create connection
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + this.dbName,
					this.userName, this.password);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			onDBConnectionError.doOnError(e);
		}
	}
}