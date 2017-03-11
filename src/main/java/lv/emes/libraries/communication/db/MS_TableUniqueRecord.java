package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Override this class to implement record (row) of database that will contain all the columns of next RecordSet record.
 * <p>If you expect for ResultSet to have more than 1 row you should use method <b>newTable</b> which fills
 * <b>MS_List</b> of row instances to make table.
 * <p>Also you can use method <b>newTableWithUniqueKey</b> which fills
 * <b>Map</b> of row instances to make table with unique identifier ar key for map.
 * <p>Method to override:
 * <ul><li>initColumns()</li></ul>
 * <p>Public method:
 * <ul><li>getIsFilled()</li></ul>
 * <p>Static method:
 * <ul><li>newTable()</li></ul>
 *
 * @author eMeS
 * @version 1.0.
 * @see MS_List
 */
public abstract class MS_TableUniqueRecord extends MS_TableRecord {
    /**
     * This constructor always should be overridden by descendants by calling <code>super()</code> in order to use <b>newTable</b> and <b>newTableWithUniqueKey</b>.
     *
     * @param rs result set of table rows retrieved from database.
     */
    public MS_TableUniqueRecord(ResultSet rs) {
        super(rs);
    }

    /**
     * Implement this in order for method <b>newTableWithUniqueKey()</b> to work! This must return unique field value.
     * @param <TKey> type of key field. This can be any of primitive type wrapper or even an object if such field acts as unique key for this record.
     * @return actual value of unique key field.
     */
    protected abstract <TKey> TKey getUniqueFieldValue();

    /**
     * Retrieves all the rows from result set and stores them in map of rows where key is the one unique field in order to to make table.
     * <p><u>Note</u>: this method is using constructor <b>MS_TableRecord(ResultSet)</b>, so
     * be sure to override it calling super() it in order to successfully use this method!
     * @param rs                   result set of table rows retrieved from database.
     * @param specificRecordTypeClass a class of object that is descendant of <b>MS_TableRecord</b> class.
     * @param <TKey> type of key field. This can be any of primitive type wrapper or even an object if such field acts as unique key for this record.
     * @param <TRecordType>                  a type of class that is descendant of <b>MS_TableUniqueRecord</b> class to define type of method's return.
     * @return a <b>MS_List</b> filled with new instances of <b>T</b>.
     * <p>Empty map is returned when some error occurs while trying to create objects from <b>rs</b>
     * or simply if <b>rs</b> is empty.
     * @see MS_List
     * @see MS_TableRecord
     */
    public static <TKey, TRecordType extends MS_TableUniqueRecord>
    Map<TKey, TRecordType> newTableWithUniqueKey(ResultSet rs, Class<TRecordType> specificRecordTypeClass) {
        Map<TKey, TRecordType> table = new HashMap<>();
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
                table.put(row.getUniqueFieldValue(), row);
        }
        return table;
    }
}
