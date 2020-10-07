package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;

import java.sql.ResultSet;

/**
 * Interface represents one database record / entry, which can be extracted from {@link ResultSet} with
 * {@link MS_ResultSetExtractingUtils#extractRecord(ResultSet, Class)}.
 * <p>Record must have specific field, which represents unique value, which can be used as identifier.
 * Mostly this field will be extracted from {@link ResultSet} as well.
 * <p>For this approach to work, class (which implements this interface) must have default constructor with no arguments.
 * <p>Methods to implement:
 * <ul>
 *     <li>initColumns()</li>
 *     <li>getUniqueFieldValue()</li>
 * </ul>
 *
 * @param <TKey> type of key field. This can be any of primitive type wrapper or even an object if such field acts as unique key for this record.
 * @author eMeS
 * @version 2.0.
 * @see MS_List
 */
public interface MS_TableUniqueRecord<TKey> extends MS_TableRecord {

    /**
     * Implement this method in order to guarantee that method {@link MS_ResultSetExtractingUtils#extractMap(ResultSet, Class)}
     * will create Map with unique key values!
     * This method must return unique field value, which mostly will be some instance variable.
     * <p><u>Implementation example</u>: <code>return id;</code>
     *
     * @return actual value of unique key field.
     */
    TKey getUniqueFieldValue();
}
