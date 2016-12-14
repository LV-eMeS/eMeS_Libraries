package lv.emes.libraries.communication.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/** 
 * This is a mock prepared statement.
 * <p>Public methods are just like java.sql.PreparedStatement.
 * <p>Properties:
 * -onSQLException
 * @version 1.0.
 * @author eMeS
 */
public class MS_PreparedStatement implements PreparedStatement {
	/**
	 * Set this to handle this kind of error when trying to do DB operations such as:<br>
	 * * Trying to communicate with DB which connection is lost;<br>
	 * * If DB cannot be accessed;<br>
	 * * If Trying to set wrong parameter of statement.
	 * <p><code>(exception) -&gt; {error handling methods};</code>
	 */
	public IFuncOnSQLException onSQLException = (exception) -> {};  
	
	private PreparedStatement actualStatement;

	/**
	 * Mocks PreparedStatement to act like class not interface.
	 * @param actualStatement a original PreparedStatement.
	 */
	public MS_PreparedStatement(PreparedStatement actualStatement) {
		this.actualStatement = actualStatement;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return actualStatement.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) {
		try {
			return actualStatement.executeUpdate(sql);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void close() {
		try {
			actualStatement.close();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int getMaxFieldSize() {
		try {
			return actualStatement.getMaxFieldSize();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void setMaxFieldSize(int max) {
		try {
			actualStatement.setMaxFieldSize(max);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int getMaxRows() {
		try {
			return actualStatement.getMaxRows();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void setMaxRows(int max) {
		try {
			actualStatement.setMaxRows(max);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setEscapeProcessing(boolean enable) {
		try {
			actualStatement.setEscapeProcessing(enable);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int getQueryTimeout() {
		try {
			return actualStatement.getQueryTimeout();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void setQueryTimeout(int seconds) {
		try {
			actualStatement.setQueryTimeout(seconds);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void cancel() {
		try {
			actualStatement.cancel();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public SQLWarning getWarnings() {
		try {
			return actualStatement.getWarnings();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public void clearWarnings() {
		try {
			actualStatement.clearWarnings();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setCursorName(String name) {
		try {
			actualStatement.setCursorName(name);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public boolean execute(String sql) {
		try {
			return actualStatement.execute(sql);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public ResultSet getResultSet() {
		try {
			return actualStatement.getResultSet();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public int getUpdateCount() {
		try {
			return actualStatement.getUpdateCount();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public boolean getMoreResults() {
		try {
			return actualStatement.getMoreResults();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public void setFetchDirection(int direction) {
		try {
			actualStatement.setFetchDirection(direction);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int getFetchDirection() {
		try {
			return actualStatement.getFetchDirection();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void setFetchSize(int rows) {
		try {
			actualStatement.setFetchSize(rows);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int getFetchSize() {
		try {
			return actualStatement.getFetchSize();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public int getResultSetConcurrency() {
		try {
			return actualStatement.getResultSetConcurrency();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public int getResultSetType() {
		try {
			return actualStatement.getResultSetType();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public void addBatch(String sql) {
		try {
			actualStatement.addBatch(sql);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void clearBatch() {
		try {
			actualStatement.clearBatch();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public int[] executeBatch() {
		try {
			return actualStatement.executeBatch();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public Connection getConnection() {
		try {
			return actualStatement.getConnection();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public boolean getMoreResults(int current) {
		try {
			return actualStatement.getMoreResults(current);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public ResultSet getGeneratedKeys() {
		try {
			return actualStatement.getGeneratedKeys();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) {
		try {
			return actualStatement.executeUpdate(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) {
		try {
			return actualStatement.executeUpdate(sql, columnIndexes);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) {
		try {
			return actualStatement.executeUpdate(sql, columnNames);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) {
		try {
			return actualStatement.execute(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) {
		try {
			return actualStatement.execute(sql, columnIndexes);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public boolean execute(String sql, String[] columnNames) {
		try {
			return actualStatement.execute(sql, columnNames);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public int getResultSetHoldability() {
		try {
			return actualStatement.getResultSetHoldability();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return 0;
		}
	}

	@Override
	public boolean isClosed() {
		try {
			return actualStatement.isClosed();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public void setPoolable(boolean poolable) {
		try {
			actualStatement.setPoolable(poolable);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public boolean isPoolable() {
		try {
			return actualStatement.isPoolable();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public void closeOnCompletion() {
		try {
			actualStatement.closeOnCompletion();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public boolean isCloseOnCompletion() {
		try {
			return actualStatement.isCloseOnCompletion();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) {
		try {
			return actualStatement.unwrap(iface);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
		try {
			return actualStatement.isWrapperFor(iface);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public ResultSet executeQuery() throws SQLException  {
		//these should actually throw SQLException
		return actualStatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException  {
		//these should actually throw SQLException
		return actualStatement.executeUpdate();
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) {
		try {
			actualStatement.setNull(parameterIndex, sqlType);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) {
		try {
			actualStatement.setBoolean(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setByte(int parameterIndex, byte x) {
		try {
			actualStatement.setByte(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setShort(int parameterIndex, short x) {
		try {
			actualStatement.setShort(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setInt(int parameterIndex, int x) {
		try {
			this.actualStatement.setInt(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setLong(int parameterIndex, long x) {
		try {
			actualStatement.setLong(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setFloat(int parameterIndex, float x) {
		try {
			actualStatement.setFloat(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setDouble(int parameterIndex, double x) {
		try {
			actualStatement.setDouble(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		try {
			actualStatement.setBigDecimal(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setString(int parameterIndex, String x) {
		try {
			this.actualStatement.setString(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) {
		try {
			actualStatement.setBytes(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setDate(int parameterIndex, Date x) {
		try {
			actualStatement.setDate(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setTime(int parameterIndex, Time x) {
		try {
			actualStatement.setTime(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) {
		try {
			actualStatement.setTimestamp(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) {
		try {
			actualStatement.setAsciiStream(parameterIndex, x, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) {
		try {
			actualStatement.setUnicodeStream(parameterIndex, x, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) {
		try {
			actualStatement.setBinaryStream(parameterIndex, x, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void clearParameters() {
		try {
			actualStatement.clearParameters();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) {
		try {
			actualStatement.setObject(parameterIndex, x, targetSqlType);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setObject(int parameterIndex, Object x) {
		try {
			actualStatement.setObject(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public boolean execute() {
		try {
			return actualStatement.execute();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return false;
		}
	}

	@Override
	public void addBatch() {
		try {
			actualStatement.addBatch();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) {
		try {
			actualStatement.setCharacterStream(parameterIndex, reader, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setRef(int parameterIndex, Ref x) {
		try {
			actualStatement.setRef(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) {
		try {
			actualStatement.setBlob(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setClob(int parameterIndex, Clob x) {
		try {
			actualStatement.setClob(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setArray(int parameterIndex, Array x) {
		try {
			actualStatement.setArray(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public ResultSetMetaData getMetaData() {
		try {
			return actualStatement.getMetaData();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) {
		try {
			actualStatement.setDate(parameterIndex, x, cal);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) {
		try {
			actualStatement.setTime(parameterIndex, x, cal);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) {
		try {
			actualStatement.setTimestamp(parameterIndex, x, cal);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) {
		try {
			actualStatement.setNull(parameterIndex, sqlType, typeName);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setURL(int parameterIndex, URL x) {
		try {
			actualStatement.setURL(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public ParameterMetaData getParameterMetaData() {
		try {
			return actualStatement.getParameterMetaData();
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
			return null;
		}
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) {
		try {
			actualStatement.setRowId(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNString(int parameterIndex, String value) {
		try {
			actualStatement.setNString(parameterIndex, value);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) {
		try {
			actualStatement.setNCharacterStream(parameterIndex, value, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) {
		try {
			actualStatement.setNClob(parameterIndex, value);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) {
		try {
			actualStatement.setClob(parameterIndex, reader, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) {
		try {
			actualStatement.setBlob(parameterIndex, inputStream, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) {
		try {
			actualStatement.setNClob(parameterIndex, reader, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) {
		try {
			actualStatement.setSQLXML(parameterIndex, xmlObject);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) {
		try {
			actualStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) {
		try {
			actualStatement.setAsciiStream(parameterIndex, x, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) {
		try {
			actualStatement.setBinaryStream(parameterIndex, x, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) {
		try {
			actualStatement.setCharacterStream(parameterIndex, reader, length);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) {
		try {
			actualStatement.setAsciiStream(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) {
		try {
			actualStatement.setBinaryStream(parameterIndex, x);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) {
		try {
			actualStatement.setCharacterStream(parameterIndex, reader);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) {
		try {
			actualStatement.setNCharacterStream(parameterIndex, value);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) {
		try {
			actualStatement.setClob(parameterIndex, reader);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) {
		try {
			actualStatement.setBlob(parameterIndex, inputStream);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) {
		try {
			actualStatement.setNClob(parameterIndex, reader);
		} catch (SQLException e) {
			this.onSQLException.doOnError(e);
		}
	}
}