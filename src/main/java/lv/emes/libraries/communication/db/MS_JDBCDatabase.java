package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_BadSetupException;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

/**
 * Describes main operations with JDBC database, which supports connection sessions.
 * <p>Public methods:
 * <ul>
 * <li>initialize</li>
 * <li>isOnline</li>
 * <li>getConnectionSession</li>
 * <li>disconnect</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.1.
 */
public interface MS_JDBCDatabase {

    /**
     * Initializes DB, validates all the set connection parameters, forms connection string, loads correct JDBC driver
     * and schedules cleanup job for connection pool if supported by implementation.
     *
     * @throws MS_BadSetupException if JDBC driver is not found due to {@link ClassNotFoundException}.
     * @throws NullPointerException if some of connection variables are still not set for this DB or are invalid,
     *                              which causes connection string to be <i>null</i>.
     */
    void initialize() throws NullPointerException, MS_BadSetupException;

    /**
     * Checks, whether the initialization is successful and connection to DB can be established.
     * Basically, if this method returns false, {@link MS_JDBCDatabase#getConnectionSession()} will not work either.
     *
     * @return true if database can be connected right now, otherwise - false.
     */
    boolean isOnline();

    /**
     * Establishes connection with database and opens new transaction for this connection.
     * In case of connection failure a connection error is added to this newly created transaction's error list and
     * on session closing {@link SQLException} will arise.
     * Depending on database implementation connection might be taken from database connection pool.
     * <p>Recommended approach to work with connection sessions is:<pre><code>
     *     try (MS_ConnectionSession con = database.getConnectionSession()) {
     *         //Work with con.prepareQuery, con.executeQuery and con.getQueryResult
     *         con.finishWork();
     *     } catch (Exception e) { //though if not further errors expected in Try block, catching SQLException will be enough
     *         //handle errors
     *     }</code></pre>
     *
     * @return new connection session object bind to new or existing (in connection pool) connection.
     * @throws SQLTimeoutException when the driver has determined that the timeout value specified by the
     *                             setLoginTimeout method has been exceeded and has at least tried to cancel
     *                             the current database connection attempt.
     * @throws SQLException        if a database access error occurs or the url is null.
     */
    MS_ConnectionSession getConnectionSession() throws SQLException;

    /**
     * Disconnects from database.
     * Depending on implementation might also close all active connections in connection pool and stop all scheduled jobs.
     */
    void disconnect();
}