package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Override this class to implement record (row) of database that will contain all the columns of next RecordSet record.
 * <p>If you expect for ResultSet to have more than 1 row you should use method <b>newTable</b> which fills
 * <b>MS_List</b> of row instances to make table.
 * <p>Method to override:
 * <ul><li>initColumns()</li></ul>
 * <p>Public method:
 * <ul><li>getIsFilled()</li></ul>
 * <p>Static method:
 * <ul><li>newTable()</li></ul>
 *
 * @author eMeS
 * @version 1.6.
 * @see MS_List
 */
public abstract class MS_TableRecord {
    private boolean rsHadNextRecord;

    /**
     * Constructor only to enable wider functionality for successors.
     */
    protected MS_TableRecord() {}

    /**
     * This constructor always should be overridden by descendants by calling <code>super()</code> in order to use <b>newTable</b>.
     *
     * @param rs result set of table rows retrieved from database.
     */
    public MS_TableRecord(ResultSet rs) {
        initColumnsFromNextResult(rs);
    }

    /**
     * Fills all class variables from ResultSet using <b>initColumns</b>.
     * @param rs filled ResultSet.
     */
    public final void initColumnsFromNextResult(ResultSet rs) {
        try {
            if (rs.next()) {
                rsHadNextRecord = true;
                initColumns(rs);
            }
        } catch (Exception e) {
            rsHadNextRecord = false;
        }
    }

    /**
     * Use this method to initialize variables that will hold column data. Example:
     * <p>id = rs.getInt(1); //assigns first column's value to variable <b>id</b>.<br>
     * name = rs.getString(2); //assigns second column's value to variable <b>name</b>.
     *
     * @param rs result set of table rows retrieved from database.
     * @throws SQLException this exception is silently caught by constructor, because this exception should occur and should be handled
     *                      when ResultSet is created by <b>MS_JDBCDatabase.getQueryResult</b> by setting property <b>onDBConnectionError</b>.<br>
     *                      Be aware that this object should be used only in such way. No guaranty that it will work in another way!
     * @see lv.emes.libraries.communication.db.MS_JDBCDatabase#getQueryResult
     */
    protected abstract void initColumns(ResultSet rs) throws SQLException;

    /**
     * Test if RecordSet had a next record in order to create this object.
     *
     * @return true if RecordSet has next record.
     */
    public boolean getIsFilled() {
        return rsHadNextRecord;
    }

    /**
     * Test if RecordSet had a next record in order to create this object.
     *
     * @return true if RecordSet has next record.
     */
    public boolean isFilled() {
        return rsHadNextRecord;
    }

    /**
     * Retrieves all the rows from result set and stores them in list of rows to make table.
     * <p><u>Note</u>: this method is using constructor <b>MS_TableRecord(ResultSet)</b>, so
     * be sure to implement it in order to successfully use this method!
     * @param rs                   result set of table rows retrieved from database.
     * @param specificRecordTypeClass a class of object that is descendant of <b>MS_TableRecord</b> class.
     * @param <TRecordType>                  a type of class that is descendant of <b>MS_TableRecord</b> class to define type of method's return.
     * @return a <b>MS_List</b> filled with new instances of <b>TRecordType</b>.
     * <p>Empty list is returned when some error occurs while trying to create objects from <b>rs</b>
     * or simply if <b>rs</b> is empty.
     * @see MS_List
     */
    public static <TRecordType extends MS_TableRecord> MS_List<TRecordType> newTable(ResultSet rs, Class<TRecordType> specificRecordTypeClass) {
        MS_List<TRecordType> table = new MS_List<>();
        TRecordType row = null;
        boolean rsHasRows = true;
        while (rsHasRows) {
            try {
                row = specificRecordTypeClass.getConstructor(ResultSet.class).newInstance(rs);
                rsHasRows = row.getIsFilled();
            } catch (Exception e) {
                //any type of exception including null pointer exception if an object instance
                //is not created successfully leads to braking of loop and returning empty list
                rsHasRows = false;
            }
            if (rsHasRows)
                table.add(row);
        }
        return table;
    }
}
