package lv.emes.libraries.communication.db;

import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 
 * Implements most common DB connection use cases. <p>
 * For successors need to override class method connect(hostname, dbName, port, userName, password) to set up connection <b>conn</b>.
 * <p>Method summary:
 * <ul>
 * <li>connect</li>
 * <li>prepareSQLQuery</li>
 * <li>getQueryResult</li>
 * <li>commitStatement</li>
 * <li>setBLOB</li>
 * <li>getBLOB</li>
 * </ul>
 * @version 1.5.
 * @author eMeS
 */
public abstract class MS_JDBCDatabase implements MS_IJDBCDatabase {
	public String hostname, dbName, userName, password;
	public int port;
	/**
	 * Set this to handle this kind of error when trying to do some DB operation with DB which connection is lost or other error connection happens!
	 * <p><code>(exception) -&gt; {error handling methods};</code>
	 */
	public IFuncOnSQLException onDBConnectionError = (exception) -> {exception.printStackTrace();}; 
	/**
	 * Set this to handle this kind of error when trying to execute statement incorrectly or DB access error happens!
	 * <p><code>(exception) -&gt; {error handling methods};</code>
	 */
	public IFuncOnSQLException onDBStatementError = (exception) -> {exception.printStackTrace();}; 
	/**
	 * Set this to handle this kind of error when trying to execute statement incorrectly and after that 
	 * trying to access it when trying to get query result or committing statement!
	 * <br><u>Hint</u>: this kind of exception should be controlled only if outcome of SQL query result is user-dependent.
	 * <p><code>(exception) -&gt; {error handling methods};</code>
	 */
	public IFuncOnNullPointerException onDBEmptyStatementError = (exception) -> {exception.printStackTrace();}; 
	
	protected Connection conn;
	
	@Override
	public void connect() throws ClassNotFoundException, SQLException {
		connect(hostname, dbName, port, userName, password);	
	}
	@Override
	public void connect(String hostname, String dbName, int port, String userName, String password) throws ClassNotFoundException, SQLException {
		if (dbName == null) 
			dbName = "";

		this.hostname = hostname;
		this.dbName = dbName;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	@Override
	public void disconnect() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	@Override
	public boolean getIsConnected() {
		try {
			return ! conn.isClosed();
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}<p>
	 * <code>
	 * PreparedStatement stmt = prepareSQLQuery(Select * from table where id=?);<br>
	 * stmt.setInt(1, id);<br>
	 * ResultSet getQueryResult(stmt);<br>
	 * </code>
	 * OR<br>
	 * <code>commitQuery(stmt); //if making changes in DB</code>
	 * @see lv.emes.libraries.communication.db.MS_JDBCDatabase#prepareSQLQueryWithThrows
	 * @see lv.emes.libraries.communication.db.MS_IJDBCDatabase#prepareSQLQuery(String)
	 */
	@Override
	public MS_PreparedSQLQuery prepareSQLQuery(String sql) {
		try {
			MS_PreparedSQLQuery res = new MS_PreparedSQLQuery(conn.prepareStatement(sql));
			res.onSQLException = onDBStatementError;
			return res;
		} catch (SQLException e) {
			this.onDBConnectionError.doOnError(e);
			return null;
		}	
	}
	
	@Override
	public MS_PreparedSQLQuery prepareSQLQueryWithThrows(String sql) throws SQLException {
		MS_PreparedSQLQuery res = new MS_PreparedSQLQuery(conn.prepareStatement(sql));
		res.onSQLException = onDBStatementError;
		return res;
	}
	
	@Override
	public ResultSet getQueryResult(MS_PreparedSQLQuery statement) {
		try {
			return statement.executeQuery();
		} catch (SQLException e) {
			onDBConnectionError.doOnError(e);
			return null;
		} catch (NullPointerException e) { //most probably null pointer exception
			onDBEmptyStatementError.doOnError(e);
			return null;
		}
	}
	
	public boolean commitStatement(MS_PreparedSQLQuery statement) {
		try {
			if (statement.executeUpdate() > 0) {
				conn.commit();
				return true;
			}
			else 
				return false;
		} catch (SQLException e) {
			this.onDBConnectionError.doOnError(e);
			return false;
		} catch (NullPointerException e) { //most probably null pointer exception happens of wrong statement
			onDBEmptyStatementError.doOnError(e);
			return false;
		}
	}
	
	@Override
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Loads binary file into statement as parameter.
	 * @param statement prepared statement.
	 * @param paramNumber number of i-th "?" (starting with 1) which should hold binary object.
	 * @param filename name of the file which will be loaded.
	 * @throws FileNotFoundException if file doesn't exist.
	 * @throws SQLException if something went wrong with assigning correct param to statement.
	 */
	public static void setBLOB(MS_PreparedSQLQuery statement,
			int paramNumber, String filename) throws FileNotFoundException, SQLException {
		FileInputStream inputStream = new FileInputStream(new File(filename));
		statement.setBinaryStream(paramNumber, (InputStream) inputStream);
	}

	/**
	 * Gets binary stream as InputStream from database ResultSet.
	 * @param result ResultSet
	 * @param paramNumber Number of i-th return parameter of select query starting from 1!
	 * @throws SQLException if something went wrong during stream obtaining
	 * @return binary stream filled with contents of some column in database.
	 */

	public static InputStream getBLOB(ResultSet result, int paramNumber) throws SQLException {
		InputStream inputStream = result.getBinaryStream(paramNumber);
		return inputStream;
	}
	
	/**
	 * Gets binary stream from database ResultSet and saves it to file `filename.
	 * @param result ResultSet
	 * @param paramNumber Number of i-th return parameter of select query starting from 1!
	 * @param filename name of external file that will be created
	 * @throws SQLException if something went wrong during stream obtaining
	 * @throws IOException if cannot convert streams
	 */
	public static void getBLOB(ResultSet result, 
			int paramNumber, String filename) throws SQLException, IOException {
		InputStream in = getBLOB(result, paramNumber);
		OutputStream out = new FileOutputStream(filename);
		IOUtils.copy(in, out);
		in.close();
		out.close();				
	}	
}