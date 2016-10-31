package lv.emes.libraries.communication.db;

import java.sql.*;

/**
 * Describes main functions of JDBC database.
 *
 * @author eMeS
 * @version 1.1.
 */
public interface MS_IJDBCDatabase {
    /**
     * Connects to DB using public connection variables.
     *
     * @throws SQLException           if couldn't connect to DB.
     * @throws ClassNotFoundException if JDBC driver not found.
     */
    public void connect() throws ClassNotFoundException, SQLException;

    /**
     * Connects to given DB.
     *
     * @param hostname "localhost", "http://emesserver.ddns.net", "128.156.244.123", etc.
     * @param dbName   if not used, leave null or "", if used - database name.
     * @param port     number of port to DB connection.
     * @param userName DB user name.
     * @param password DB password.
     * @throws ClassNotFoundException if JDBC driver failed to load.
     * @throws SQLException           if connecting to given DB failed.
     */
    void connect(String hostname, String dbName, int port, String userName, String password) throws ClassNotFoundException, SQLException;

    /**
     * Closing connecting to DB.
     *
     * @throws SQLException if couldn't do disconnect.
     */
    void disconnect() throws SQLException;

    /**
     * Checks, if the connection to DB is established.
     *
     * @return true if connected.
     */
    boolean getIsConnected();

    /**
     * Prepares statement for given SQL query. Exception handling is done silently (you don't need to handle those exceptions every time,
     * just set common error handling operations of JDBCDatabase)
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @return a new default <code>MS_PreparedStatement</code> object containing the pre-compiled SQL statement
     * @see lv.emes.libraries.communication.db.MS_JDBCDatabase
     */
    MS_PreparedStatement prepareQuery(String sql);

    /**
     * Just like <code>executeQuery</code>, but if exception occurs, it isn't handled silently, using "onError" methods.
     * So use this method if you want to instantly handle errors using individual approach for some case!
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @return a new default <code>MS_PreparedStatement</code> object containing the pre-compiled SQL statement
     * @throws SQLException if a database access error occurs or this method is called on a closed connection
     * @see lv.emes.libraries.communication.db.MS_IJDBCDatabase#prepareQuery
     */
    MS_PreparedStatement prepareQueryWithThrows(String sql) throws SQLException;

    /**
     * Gets the result of executed statement. Example of use (executes given statement and prints out first column of result as String):
     * <code><pre>{@code
     * ResultSet ress = db.getQueryResult(statement);
     * while (ress.next()) {
     *  System.out.println(ress.getString(1)); //columns are starting with 1
     * }}</pre></code>
     * @param statement proper SQL statement which have been executed as SELECT type statement.
     * @return ResultSet
     */

    ResultSet getQueryResult(MS_PreparedStatement statement);

    /**
     * Commits executed statement.
     *
     * @param statement a already executed statement to commit.
     * @return true, if success, false - if failed to commit for some reason.
     */
    boolean commitStatement(MS_PreparedStatement statement);

    /**
     * @return reference to current connection.
     */
    Connection getConnection();
}