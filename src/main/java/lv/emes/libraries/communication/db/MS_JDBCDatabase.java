package lv.emes.libraries.communication.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.compress.utils.IOUtils;

/** 
 * Implements most common DB connection use cases. <p>
 * For successors need to override class method connect(hostname, dbName, port, userName, password) to set up connection <b>conn</b>.
 * <p>Method summary:
 * -connect
 * -prepareQuery
 * -getQueryResult
 * -commitStatement
 * -setBLOB
 * -getBLOB
 * @version 1.3.
 * @author eMeS
 */
public abstract class MS_JDBCDatabase implements MS_IJDBCDatabase {
	public String hostname, dbName, userName, password;
	public int port;
	/**
	 * Set this to handle this kind of error when trying to do some DB operation with DB which connection is lost or other error connection happens!
	 * <p><code>(exception) -> {error handling methods};</code>
	 */
	public IFuncOnSQLException onDBConnectionError = (exception) -> {exception.printStackTrace();}; 
	/**
	 * Set this to handle this kind of error when trying to execute statement incorrectly or DB access error happens!
	 * <p><code>(exception) -> {error handling methods};</code>
	 */
	public IFuncOnSQLException onDBStatementError = (exception) -> {exception.printStackTrace();}; 
	/**
	 * Set this to handle this kind of error when trying to execute statement incorrectly and after that 
	 * trying to access it when trying to get query result or committing statement!
	 * <p><code>(exception) -> {error handling methods};</code>
	 */
	public IFuncOnNullPointerException onDBEmptyStatementError = (exception) -> {exception.printStackTrace();}; 
	
	protected Connection conn;
	
	@Override
	public void connect() throws ClassNotFoundException {
		connect(hostname, dbName, port, userName, password);	
	}
	@Override
	public void connect(String hostname, String dbName, int port, String userName, String password) throws ClassNotFoundException {
		if (dbName == null) 
			dbName = "";
		if (! dbName.equals("")) //ja definēts celsh lidz DB, tad tas sekos aiz hostname
			dbName = "/" + dbName; 
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
	 * PreparedStatement stmt = prepareQuery(Select * from table where id=?);<br>
	 * stmt.setInt(1, id);<br>
	 * ResultSet getQueryResult(stmt);<br>
	 * </code>
	 * OR<br>
	 * <code>commitQuery(stmt); //if making changes in DB</code>
	 * @see lv.emes.libraries.communication.db.MS_JDBCDatabase#prepareQueryWithThrows
	 * @see lv.emes.libraries.communication.db.MS_IJDBCDatabase#prepareQuery(String)
	 */
	@Override
	public MS_PreparedStatement prepareQuery(String sql) {
		try {
			MS_PreparedStatement res = new MS_PreparedStatement(conn.prepareStatement(sql));
			res.onSQLException = onDBStatementError;
			return res;
		} catch (SQLException e) {
			this.onDBConnectionError.doOnError(e);
			return null;
		}	
	}
	
	@Override
	public MS_PreparedStatement prepareQueryWithThrows(String sql) throws SQLException {
		MS_PreparedStatement res = new MS_PreparedStatement(conn.prepareStatement(sql));
		res.onSQLException = onDBStatementError;
		return res;
	}
	
	@Override
	public ResultSet getQueryResult(MS_PreparedStatement statement) {			
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
	
	public boolean commitStatement(MS_PreparedStatement statement) {
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
	public static void setBLOB(MS_PreparedStatement statement, 
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