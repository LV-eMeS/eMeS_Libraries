package lv.emes.libraries.communication.db;

import java.time.Duration;

/**
 * Connection and configuration parameters for {@link MS_JDBCDatabase}.
 * This also includes default error handler setup.
 * <p>Setters and getters:
 * <ul>
 * <li>withHostname</li>
 * <li>withDbName</li>
 * <li>withUserName</li>
 * <li>withPassword</li>
 * <li>withPort</li>
 * </ul><ul>
 * <li>getHostname</li>
 * <li>getDbName</li>
 * <li>getUserName</li>
 * <li>getPassword</li>
 * <li>getPort</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_DBParameters {

    /**
     * Name of the database host "localhost", "http://hostname.com", "128.156.244.123", etc.
     */
    private String hostname = "";
    /**
     * Database name.
     */
    private String dbName = "";
    /**
     * Database user name.
     */
    private String userName = "";
    /**
     * Password to access database for user with username <b>userName</b>.
     */
    private String password = "";
    /**
     * Port, through which database can be reached to establish connection.
     */
    private int port;

    /**
     * Time to live for each database connection session made.
     * This parameter is used by database connection pool when cleanup job is being run.
     * All sessions, which time to live is expired are removed from connection pool.
     * <p>No value set (e.g. <i>null</i> value) means that connection sessions are kept and reused inside DB connection pool forever.
     * Default value is <b>30 minutes</b>.
     */
    private Duration sessionTTL = Duration.ofMinutes(30L);

    /**
     * Value describing, how often connection pool cleanup job will be run.
     * By default it's being ran every <b>10 minutes</b>.
     * Recommended value is at least few seconds just to let connection pool to fill up with some amount of connections.
     * This parameter cannot be <i>null</i>.
     */
    private Duration connPoolCleanupFrequency = Duration.ofMinutes(10L);

    //*** Getters ***

    /**
     * @return {@link MS_DBParameters#hostname}
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return {@link MS_DBParameters#dbName}
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return {@link MS_DBParameters#userName}
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return {@link MS_DBParameters#password}
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return {@link MS_DBParameters#port}
     */
    public int getPort() {
        return port;
    }

    /**
     * @return {@link MS_DBParameters#sessionTTL}
     */
    public Duration getSessionTTL() {
        return sessionTTL;
    }

    /**
     * @return {@link MS_DBParameters#connPoolCleanupFrequency}
     */
    public Duration getConnPoolCleanupFrequency() {
        return connPoolCleanupFrequency;
    }
    //*** Setters ***

    /**
     * Sets {@link MS_DBParameters#hostname}.
     *
     * @param hostname {@link MS_DBParameters#hostname}
     * @return reference to parameters.
     */
    public MS_DBParameters withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#dbName}.
     *
     * @param dbName {@link MS_DBParameters#dbName}
     * @return reference to parameters.
     */
    public MS_DBParameters withDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#userName}.
     *
     * @param userName {@link MS_DBParameters#userName}
     * @return reference to parameters.
     */
    public MS_DBParameters withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#password}.
     *
     * @param password {@link MS_DBParameters#password}
     * @return reference to parameters.
     */
    public MS_DBParameters withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#port}.
     *
     * @param port {@link MS_DBParameters#port}
     * @return reference to parameters.
     */
    public MS_DBParameters withPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#sessionTTL}.
     *
     * @param sessionTTL {@link MS_DBParameters#sessionTTL}
     * @return reference to parameters.
     */
    public MS_DBParameters withSessionTTL(Duration sessionTTL) {
        this.sessionTTL = sessionTTL;
        return this;
    }

    /**
     * Sets {@link MS_DBParameters#connPoolCleanupFrequency}.
     *
     * @param connPoolCleanupFrequency {@link MS_DBParameters#connPoolCleanupFrequency}
     * @return reference to parameters.
     */
    public MS_DBParameters withConnPoolCleanupFrequency(Duration connPoolCleanupFrequency) {
        if (connPoolCleanupFrequency != null)
            this.connPoolCleanupFrequency = connPoolCleanupFrequency;
        return this;
    }
}
