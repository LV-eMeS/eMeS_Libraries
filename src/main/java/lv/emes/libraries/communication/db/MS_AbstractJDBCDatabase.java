package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.logging.MS_Log4Java;
import lv.emes.libraries.tools.threading.MS_Scheduler;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for database that can be accessed by using JDBC driver.
 * Constructor as well as fields <b>connParams</b> and <b>isLinked</b> acts as a placeholders
 * and should be used in successors of this class to implement necessary abstract methods.
 *
 * @author eMeS
 * @version 2.1.
 */
public abstract class MS_AbstractJDBCDatabase implements MS_JDBCDatabase {

    protected MS_DBParameters connParams;
    private String connectionString;
    protected boolean isLinked = false;

    private Map<Long, MS_ConnectionSession> connectionPool;
    private MS_Scheduler cleaningJobScheduler;
    private Long sessionIdCounter = 0L;

    /**
     * Creates new instance of database and initializes it immediately with all passed <b>connParams</b>.
     * @param connParams connection parameters and connection pool settings.
     * @throws MS_BadSetupException if JDBC driver is not found due to {@link ClassNotFoundException}.
     * @throws NullPointerException if some of connection variables are still not set for this DB or are invalid,
     *                              which causes connection string to be <i>null</i>.
     * @see MS_AbstractJDBCDatabase#initialize()
     */
    public MS_AbstractJDBCDatabase(MS_DBParameters connParams) throws NullPointerException, MS_BadSetupException {
        this.connParams = connParams;
        this.connectionPool = new ConcurrentHashMap<>();
        initialize();
    }

    @Override
    public final void initialize() throws NullPointerException, MS_BadSetupException {
        if (isLinked) return;
        if (connParams.getHostname() == null || connParams.getDbName() == null
                || connParams.getUserName() == null || connParams.getPassword() == null)
            throw new NullPointerException("Some of connection variables are null");

        try {
            Class.forName(getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new MS_BadSetupException(e);
        }

        connectionString = formConnectionString();

        this.isLinked = true;
        scheduleCleanupJob(); //setup connection pool cleaning job scheduler
    }

    @Override
    public void disconnect() {
        isLinked = false;
        if (cleaningJobScheduler != null) {
            cleaningJobScheduler.terminate();
            cleaningJobScheduler = null;
        }
        Iterator<Map.Entry<Long, MS_ConnectionSession>> iterator = connectionPool.entrySet().iterator();
        while (iterator.hasNext()) {
            MS_ConnectionSession session = iterator.next().getValue();
            try {
                session.getConnection().close();
                iterator.remove();
            } catch (SQLException ignored) {
            }
        }
    }

    public boolean isOnline() {
        try {
            MS_ConnectionSession sess = getConnectionSession();
            boolean res = !sess.conn.isClosed();
            sess.setWorkInProgress(false);
            return res;
        } catch (Exception e) {
            return false;
        }
    }

    public final MS_ConnectionSession getConnectionSession() throws SQLException {
        MS_ConnectionSession session = getConnectionFromPool();
        if (session == null) {
            Connection conn = getConnectionFromDriver();
            conn.setAutoCommit(false);
            session = registerNewConnection(conn);
        }
        return session;
    }

    //*** PROTECTED METHODS ***

    /**
     * Used in {@link MS_AbstractJDBCDatabase#initialize()} method after basic connection parameter null checks
     * and before connection string formation.
     * This method should return valid path to {@link Driver} class.
     *
     * @return specific DB implementation full class name (including package)
     * e.g. "foo.bah.Driver", "oracle.jdbc.driver.OracleDriver", "com.mysql.jdbc.Driver".
     */
    protected abstract String getDriverClassName();

    /**
     * Used in {@link MS_AbstractJDBCDatabase#initialize()} method after Driver class loading
     * and before cleaning job scheduling.
     * Forms Driver-specific connection string from connection parameters in order to use it in
     * {@link MS_AbstractJDBCDatabase#getConnectionFromDriver()}.
     * @return string that is applicable to {@link DriverManager} and allows to establish connection via
     * Driver manager's <b>getConnection</b> method.
     */
    protected abstract String formConnectionString();

    /**
     * Retrieve new connection from connection driver. <p><u>Example</u>:
     * <code>
     * DriverManager.getConnection(this.connectionString, connParams.getUserName(), connParams.getPassword());
     * </code>
     *
     * @return new connection object.
     * @throws SQLTimeoutException when the driver has determined that the timeout value specified by the setLoginTimeout method has been exceeded and has at least tried to cancel the current database connection attempt.
     * @throws SQLException        if a database access error occurs or the url is null.
     */
    protected abstract Connection getConnectionFromDriver() throws SQLException;

    protected String getConnectionString() {
        return connectionString;
    }

    //*** PRIVATE METHODS ***

    /**
     * Registers new connection to connection pool to ensure efficiency and management of active connections.
     *
     * @param connection new connection provided by driver.
     * @return new connection session created from passed <b>connection</b>.
     */
    private synchronized MS_ConnectionSession registerNewConnection(Connection connection) {
        MS_ConnectionSession session = new MS_ConnectionSession(connection, ++sessionIdCounter, LocalDateTime.now());
        connectionPool.put(sessionIdCounter, session);
        return session;
    }

    /**
     * Looks for existing connection sessions, checks, if they already finished their work and in that case
     * takes connection objects from first free connection session and creates new session on top of this connection
     * object, preserving it's session ID.
     *
     * @return existing and ready for use connection from connection pool or null if all connections are unavailable.
     */
    private synchronized MS_ConnectionSession getConnectionFromPool() {
        MS_ConnectionSession session = null;
        if (!connectionPool.isEmpty()) {
            Iterator<Map.Entry<Long, MS_ConnectionSession>> iterator = connectionPool.entrySet().iterator();
            MS_ConnectionSession existingSession = null;
            while (iterator.hasNext() && existingSession == null) {
                existingSession = iterator.next().getValue();
                if (isConnectionClosed(existingSession)) {
                    iterator.remove();
                    existingSession = null;
                } else if (existingSession.isWorkInProgress())
                    existingSession = null;
            }

            if (existingSession != null) {
                session = new MS_ConnectionSession(existingSession.getConnection(), existingSession.getSessionId(), LocalDateTime.now());
                connectionPool.put(session.getSessionId(), session);
            }
        }
        return session;
    }

    private boolean isConnectionClosed(MS_ConnectionSession session) {
        try {
            boolean res = session.conn.isClosed();
            session.setWorkInProgress(false);
            return res;
        } catch (Exception e) {
            return true;
        }
    }

    private void scheduleCleanupJob() {
        cleaningJobScheduler = new MS_Scheduler()
                .withTriggerTime(ZonedDateTime.now().plus(connParams.getConnPoolCleanupFrequency()))
                .withAction(this::runConnectionCleanup)
                .withActionOnException((exception, eventExecutionTime) -> {
                    MS_Log4Java.getLogger("MS_AbstractJDBCDatabase.scheduleCleanupJob")
                            .error(String.format("Database connection pool cleanup job failed at [%s] due to exception", eventExecutionTime), exception);
                    scheduleCleanupJob();
                })
                .withActionOnInterruptedException((eventExecutionTime) -> {
                    MS_Log4Java.getLogger("MS_AbstractJDBCDatabase.scheduleCleanupJob")
                            .info(String.format("Database connection pool cleanup job terminated at [%s]", eventExecutionTime));
                })
                .schedule();
    }

    /**
     * Should be called by connection pool cleanup scheduler.
     * Checks for connections, whose TTL is expired and removes them from pool.
     */
    private void runConnectionCleanup(ZonedDateTime execTime) {
        Duration ttl = connParams.getSessionTTL();
        if (ttl != null && !connectionPool.isEmpty()) {
            for (Iterator<Map.Entry<Long, MS_ConnectionSession>> it = connectionPool.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Long, MS_ConnectionSession> entry = it.next();
                MS_ConnectionSession session = entry.getValue();
                LocalDateTime sessCreationTime = session.getSessionCreated();
                if (sessCreationTime.plus(ttl).isBefore(execTime.toLocalDateTime())) {
                    if (!session.isWorkInProgress()) {
                        try {
                            session.getConnection().close();
                            it.remove();
                        } catch (SQLException ignored) {
                        }
                    }
                }
            }
        }
        if (this.isLinked) scheduleCleanupJob(); //schedule next job if database is still linked
    }
}