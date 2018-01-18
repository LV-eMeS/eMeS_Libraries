package lv.emes.libraries.communication.db;

import java.sql.ResultSet;

/**
 * Interface represents one database record / entry, which can be extracted from {@link ResultSet} with
 * {@link MS_ResultSetExtractingUtils#extractRecord(ResultSet, Class)}.
 * <p>For this approach to work, class (which implements this interface) must have default constructor with no arguments.
 * <p>Method to implement:
 * <ul><li>initColumns()</li></ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public interface MS_TableRecord {

    /**
     * Use this method to initialize variables that will hold column data.
     * This method is called by {@link MS_ResultSetExtractingUtils#extractRecord(ResultSet, Class)} if
     * <b>rs</b> has next row.
     * <p><u>Implementation example</u>:
     * <p>id = rs.getInt(1); //assigns first column's value to field <b>id</b>.<br>
     * name = rs.getString(2); //assigns second column's value to field <b>name</b>.
     *
     * @param rs result set of table rows retrieved from database.
     * @throws Exception any exception, which occurs in initialization process and blocks filling fields
     * with values from <b>rs</b>.
     * <p>This also might be {@link java.sql.SQLException} indicating that database access error occurs
     * or this method is called on a closed result set.
     * <p>And also {@link NullPointerException} can occur, if <b>rs</b> is null or any other action in this method
     * raises this kind of exception.
     */
    void initColumns(ResultSet rs) throws Exception;
}
