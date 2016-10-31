package lv.emes.libraries.communication.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/** 
 * Override this class to implement object that will contain all the columns of next RecordSet record.
 * <p>If you expect for ResultSet to have more than 1 row you should do design pattern like this:<br>
 * ResultSet rs = ...<br>
 * MS_TableRow row;
 * boolean rsHasRows = true;<br>
 * while (rsHasRows) {<br>
 * row = new MS_TableRow(rs);<br>
 * rsHasRows = row.getIsFilled();<br>
 * if (rsHasRows)<br>
 * ...store row in list or do something else with it...<br>
 * }
 * <p>Method to override:
 * -initColumns
 * <p>Public method:
 * -getIsFilled
 * @version 1.0.
 * @author eMeS
 */
public abstract class MS_TableRow {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
	//PRIVATE VARIABLES
	private boolean rsHadNextRecord = false;

	//PUBLIC VARIABLES
	//CONSTRUCTORS
	public MS_TableRow(ResultSet rs) {
		try {
			if (rs.next()) {
				rsHadNextRecord = true;
				initColumns(rs);
			}
		} catch (Exception e) {		}
	}

	//STATIC CONSTRUCTORS
	//PRIVATE METHODS
	//PROTECTED METHODS
	/**
	 * Use this method to initialize variables that will hold column data. Example:
	 * <p>id = rs.getInt(1); //assigns first column's value to variable <b>id</b>.<br>
	 * name = rs.getString(2); //assigns second column's value to variable <b>name</b>.
	 * @param rs table after select SQL as result set.
	 * @throws SQLException this exception is silently caught by constructor, because this exception should occur and should be handled 
	 * when ResultSet is created by <b>MS_JDBCDatabase.getQueryResult</b> by setting property <b>onDBConnectionError</b>.<br>
	 * Be aware that this object should be used only in such way. No guaranty that it will work in another way!
	 * @see lv.emes.libraries.communication.db.MS_JDBCDatabase#getQueryResult
	 */
	protected abstract void initColumns(ResultSet rs) throws SQLException;

	//PUBLIC METHODS
	/**
	 * Test if RecordSet had a next record in order to create this object.
	 * @return true if RecordSet has next record.
	 */
	public boolean getIsFilled() {
		return rsHadNextRecord;
	}
	//STATIC METHODS
}
