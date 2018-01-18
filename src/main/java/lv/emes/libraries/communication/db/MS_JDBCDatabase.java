package lv.emes.libraries.communication.db;

import java.sql.SQLException;

/**
 * Describes main functions of JDBC database.
 * <p>Public methods:
 * <ul>
 * <li>initialize</li>
 * <li>getConnectionSession</li>
 * <li>isOnline</li>
 * <li>setDefaultErrorHandler</li>
 * <li>getDefaultErrorHandler</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public interface MS_JDBCDatabase {

    /**
     * Initializes DB, validates all the set connection parameters, forms connection string and loads correct JDBC driver.
     *
     * @throws ClassNotFoundException if JDBC driver not found.
     * @throws NullPointerException   if some of connection variables are still not set for this DB or are invalid,
     *                                which causes connection string to be <i>null</i>.
     */
    void initialize() throws ClassNotFoundException, NullPointerException;

    /**
     * Checks, whether the connection to DB can be established.
     *
     * @return true if database can be connected right now, otherwise - false.
     */
    boolean isOnline();

    /**
     * Establishes connection with database and opens new transaction for this connection.
     * In case of connection failure a connection error is added to this newly created transaction's error list.
     * Depending on database implementation connection might be taken from database connection pool.
     * <p>Recommended approach to work with connection sessions is:<pre><code>
     *     try (MS_ConnectionSession con = database.getConnectionSession()) {
     *         //Work with con.prepareQuery, con.executeQuery and con.getQueryResult
     *         con.finishWork();
     *     } catch (Exception e) {
     *         //handle errors
     *     }</code></pre>
     *
     * @return new connection session object bind to new or existing (in connection pool) connection.
     * @throws SQLException if a database connection or access error occurs or the url is invalid.
     */
    MS_ConnectionSession getConnectionSession() throws SQLException;

    /**
     * Closes all connections active in connection pool and stops all scheduled jobs.
     */
    void unlink();
}