package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to extract {@link ResultSet} content to Java objects.
 * <p>Static methods:
 * <ul>
 * <li>extractRecord</li>
 * <li>extractList</li>
 * <li>extractMap</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_ResultSetExtractingUtils {

    private MS_ResultSetExtractingUtils() {
    }

    /**
     * This method extracts 1 row from <b>rs</b> and creates new instance of <b>TRecordType</b>.
     *
     * @param rs                      result set of table rows retrieved from database.
     * @param specificRecordTypeClass a class of object that is implements of <b>MS_TableRecord</b> interface.
     * @param <TRecordType>           a type of class that implements <b>MS_TableRecord</b> interface to define type of method's return.
     * @return new instance of <b>TRecordType</b> or null if result set is completely empty (returned 0 records).
     * @throws Exception any exception, which occurs in <b>TRecordType</b> constructing process and blocks filling fields
     *                   with values from <b>rs</b>.
     *                   <p>This also might be {@link java.sql.SQLException} indicating that database access error occurs
     *                   or this method is called on a closed result set.
     *                   <p>And also {@link NullPointerException} can occur, if <b>rs</b> is null or any other action in this method
     *                   raises this kind of exception.
     */
    public static <TRecordType extends MS_TableRecord> TRecordType
    extractRecord(ResultSet rs, Class<TRecordType> specificRecordTypeClass) throws Exception {

        TRecordType record = null;
        if (rs.next()) {
            record = specificRecordTypeClass.getConstructor().newInstance();
            record.initColumns(rs);
        }
        return record;
    }

    /**
     * Retrieves all the rows from result set and stores them in list of rows to imitate table.
     *
     * @param rs                      result set of table rows retrieved from database.
     * @param specificRecordTypeClass a class of object that is implements of <b>MS_TableRecord</b> interface.
     * @param <TRecordType>           a type of class that implements <b>MS_TableRecord</b> interface to define type of method's return.
     * @return a <b>MS_List</b> filled with new instances of <b>TRecordType</b>.
     * <p>Empty list is returned when some error occurs while trying to create objects from <b>rs</b>
     * or simply if <b>rs</b> is empty.
     * @throws Exception any exception, which occurs in <b>TRecordType</b> constructing process and blocks filling fields
     *                   with values from <b>rs</b>.
     *                   <p>This also might be {@link java.sql.SQLException} indicating that database access error occurs
     *                   or this method is called on a closed result set.
     *                   <p>And also {@link NullPointerException} can occur, if <b>rs</b> is null or any other action in this method
     *                   raises this kind of exception.
     */
    public static <TRecordType extends MS_TableRecord>
    MS_List<TRecordType> extractList(ResultSet rs, Class<TRecordType> specificRecordTypeClass) throws Exception {

        MS_List<TRecordType> list = new MS_List<>();
        while (rs.next()) {
            TRecordType record = specificRecordTypeClass.getConstructor().newInstance();
            record.initColumns(rs);
            list.add(record);
        }
        return list;
    }

    /**
     * Retrieves all the rows from result set and stores them in map of rows where key is the one unique field in order to to make table.
     *
     * @param rs                      result set of table rows retrieved from database.
     * @param specificRecordTypeClass a class of object that is implements of <b>MS_TableRecord</b> interface.
     * @param <TKey>                  type of key field. This can be any of primitive type wrapper or even an object if such field acts as unique key for this record.
     * @param <TRecordType>           a type of class that implements <b>MS_TableRecord</b> interface to define type of method's return.
     * @return a <b>MS_List</b> filled with new instances of <b>T</b>.
     * <p>Empty map is returned when some error occurs while trying to create objects from <b>rs</b>
     * or simply if <b>rs</b> is empty.
     * @throws Exception any exception, which occurs in <b>TRecordType</b> constructing process and blocks filling fields
     *                   with values from <b>rs</b>.
     *                   <p>This also might be {@link java.sql.SQLException} indicating that database access error occurs
     *                   or this method is called on a closed result set.
     *                   <p>And also {@link NullPointerException} can occur, if <b>rs</b> is null or any other action in this method
     *                   raises this kind of exception.
     */
    public static <TKey, TRecordType extends MS_TableUniqueRecord<TKey>>
    Map<TKey, TRecordType> extractMap(ResultSet rs, Class<TRecordType> specificRecordTypeClass) throws Exception {

        Map<TKey, TRecordType> map = new HashMap<>();
        while (rs.next()) {
            TRecordType record = specificRecordTypeClass.getConstructor().newInstance();
            record.initColumns(rs);
            map.put(record.getUniqueFieldValue(), record);
        }
        return map;
    }
}
