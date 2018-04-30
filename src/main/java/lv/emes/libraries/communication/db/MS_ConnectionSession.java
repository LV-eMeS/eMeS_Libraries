package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * An eMeS JDBC connection session object, which binds {@link Connection} object with it and provides methods
 * to operate with statements and execute them in order to either write something to DB or read something from it.
 * Connection related transactions in one session must be finished as soon as possible (do not keep references to this session!).
 * This is done by using <b>finishWork</b> method in order to either commit all work, either rollback transactions in case of failures.
 * When <b>finishWork</b> method is called in case of failure, return value is <i>false</i>.
 * When <b>finishWorkWithDefaultErrorHandling</b> method is called in case of failure, a default error handler is raised
 * with most crucial exception. This handler is defined by {@link MS_JDBCDatabase}.
 * <p>At any time <b>hasErrors</b> can be called to check, if there was some error during statement execution.
 * Also <b>getErrors</b> can be called to get list with specific errors gathered in same order as they happened during
 * statement calls and execution.
 * <p><u>Warning</u>: This Connection session is not thread safe, so each session should be established in separate thread and used
 * in bounds of that thread.
 * <p>Public methods:
 * <ul>
 * <li>prepareQuery</li>
 * <li>executeQuery</li>
 * <li>getQueryResult</li>
 * <li>finishWork</li>
 * <li>finishWorkWithDefaultErrorHandling</li>
 * </ul>
 * <p>Getters:
 * <ul>
 * <li>getConnection</li>
 * <li>hasErrors</li>
 * <li>getErrors</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.1.
 */
public class MS_ConnectionSession implements AutoCloseable {

    protected Connection conn;
    protected Long sessionId;
    protected LocalDateTime sessionCreated;

    private MS_List<Exception> errors;
    private boolean workInProgress;

    /**
     * Creates new connection session with presented connection object <b>conn</b>.
     *
     * @param conn                new or existing connection to specified database <b>db</b>.
     * @param sessionId           identifier defined by {@link MS_JDBCDatabase}.
     * @param sessionCreationTime time when session was created or refreshed. Typically <code>LocalDateTime.now()</code>.
     */
    public MS_ConnectionSession(Connection conn, Long sessionId, LocalDateTime sessionCreationTime) {
        this.conn = conn;
        this.sessionId = sessionId;
        errors = new MS_List<>();
        workInProgress = true;
        sessionCreated = sessionCreationTime;
    }

    /**
     * Prepares statement for given SQL query.
     * just set common error handling operations of {@link MS_JDBCDatabase}).
     * <p><code>
     * PreparedStatement stmt = conn.prepareQuery(Select * from table where id=?);<br>
     * stmt.setInt(1, id);<br>
     * ResultSet rs = getQueryResult(stmt);<br>
     * </code>
     * OR<br>
     * <code>conn.executeQuery(stmt); //if making changes in DB</code>
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders.
     * @return a new <code>MS_PreparedSQLQuery</code> object containing the pre-compiled SQL statement.
     */
    public MS_PreparedSQLQuery prepareQuery(String sql) {
        try {
            MS_PreparedSQLQuery res = new MS_PreparedSQLQuery(conn.prepareStatement(sql));
            res.onSQLException = (e -> errors.add(e));
            return res;
        } catch (SQLException e) {
            errors.add(e);
            return null;
        }
    }

    /**
     * Prepares statement for given SQL query builder.
     * <p><code>
     * PreparedStatement stmt = conn.prepareQuery(new MS_SQLQueryBuilder().select().all().from("table").where().fieldEquals("id", _QPARAM));<br>
     * stmt.setInt(1, id);<br>
     * ResultSet rs = getQueryResult(stmt);<br>
     * </code>
     * OR<br>
     * <code>conn.executeQuery(stmt); //if making changes in DB</code>
     *
     * @param query a SQL builder that may contain one or more '?' IN parameter placeholders.
     * @return a new <code>MS_PreparedSQLQuery</code> object containing the pre-compiled SQL statement.
     */
    public MS_PreparedSQLQuery prepareQuery(MS_SQLQueryBuilder query) {
        return prepareQuery(query.buildAndToString());
    }

    /**
     * Gets the result of executed statement. Example of use (executes given statement and prints out first column of result as String):
     * <pre><code>
     * ResultSet res = conn.getQueryResult(statement);
     * while (res.next()) {
     *      System.out.println(res.getString(1)); //columns are starting with 1
     * }</code></pre>
     *
     * @param statement proper SQL statement which have been executed as SELECT type statement.
     * @return ResultSet.
     */
    public ResultSet getQueryResult(MS_PreparedSQLQuery statement) {
        try {
            return statement.executeQuery();
        } catch (SQLException | NullPointerException e) {
            //most probably null pointer exception here happens due to wrong SQL
            errors.add(e);
            return null;
        }
    }

    /**
     * Immediately (without statement preparations) executes SQL query and returns result.
     *
     * @param sql an SQL statement, which most often will be of INSERT type.
     * @return ResultSet.
     */
    public ResultSet getQueryResult(String sql) {
        try {
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            errors.add(e);
            return null;
        }
    }

    /**
     * Executes statement and stores errors occurred in error lists.
     *
     * @param statement statement to execute and commit on session finalizing (<b>finish</b> method).
     */
    public void executeQuery(MS_PreparedSQLQuery statement) {
        try {
            statement.executeUpdate();
            statement.close();
        } catch (SQLException | NullPointerException e) {
            //most probably null pointer exception here happens due to wrong SQL
            errors.add(e);
        }
    }

    /**
     * @return reference to current connection.
     */
    public Connection getConnection() {
        return conn;
    }

    //*** Error handling related methods ***

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public MS_List<Exception> getErrors() {
        return errors;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public LocalDateTime getSessionCreated() {
        return sessionCreated;
    }

    boolean isWorkInProgress() {
        return workInProgress;
    }

    void setWorkInProgress(boolean workInProgress) {
        this.workInProgress = workInProgress;
    }

    //*** Connection finalizing related methods ***

    /**
     * Commits all the statements used in this connection session.
     * In case of failure rollbacks changes made and throws first occurred exception.
     *
     * @throws Exception SQLException, NullPointerException or any other exception happened during
     *                   SQL statement execution during this connection session.
     */
    public void finishWork() throws Exception {
        try {
            workInProgress = false;
            if (errors.isEmpty()) conn.commit();
            else conn.rollback();
        } catch (SQLException e) {
            conn.rollback(); //TODO check this case
        }
        if (hasErrors()) throw errors.get(0);
    }

    @Override
    public void close() throws Exception {
        if (workInProgress) finishWork();
    }
}
