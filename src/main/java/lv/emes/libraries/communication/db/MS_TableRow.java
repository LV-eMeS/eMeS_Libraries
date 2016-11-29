package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Override this class to implement row that will contain all the columns of next RecordSet record.
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
 * @version 1.2.
 * @see MS_List
 */
public abstract class MS_TableRow {
    //PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
    //PRIVATE VARIABLES
    private boolean rsHadNextRecord = false;

    //PUBLIC VARIABLES
    //CONSTRUCTORS

    /**
     * This constructor always should be overridden by successors in order to use <b>newTable</b>.
     *
     * @param rs result set of table rows retrieved from database.
     */
    public MS_TableRow(ResultSet rs) {
        try {
            if (rs.next()) {
                rsHadNextRecord = true;
                initColumns(rs);
            }
        } catch (Exception e) {
        }
    }

    //STATIC CONSTRUCTORS
    //PRIVATE METHODS
    //PROTECTED METHODS

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

    //PUBLIC METHODS

    /**
     * Test if RecordSet had a next record in order to create this object.
     *
     * @return true if RecordSet has next record.
     */
    public boolean getIsFilled() {
        return rsHadNextRecord;
    }

    //STATIC METHODS

    /**
     * Retrieves all the rows from result set and stores them in list of rows to make table.
     *
     * @param rs                   result set of table rows retrieved from database.
     * @param specificRowTypeClass a class of object that is successor of <b>MS_TableRow</b> class.
     * @param <T>                  a type of class that is successor of <b>MS_TableRow</b> class to define type of method's return.
     * @return a <b>MS_List</b> filled with new instances of <b>T</b>.
     * <p>Empty list is returned when some error occurs while trying to create objects from <b>rs</b>
     * or simply if <b>rs</b> is empty.
     * @see MS_List
     */
    public static <T extends MS_TableRow> MS_List<T> newTable(ResultSet rs, Class<T> specificRowTypeClass) {
        MS_List<T> table = new MS_List<>();
        T row = null;
        boolean rsHasRows = true;
        while (rsHasRows) {
            MS_TableRow newRow = null;
            try {
                row = specificRowTypeClass.getConstructor(ResultSet.class).newInstance(rs);
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
