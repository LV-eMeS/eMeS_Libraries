package lv.emes.libraries.utilities;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Useful methods to write unit and integration tests easier.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_TestUtils {
    /**
     * Creates mocked ResultSet that sequentially returns values for getX method calls according to presented <b>tableToMockResults</b>, where X is
     * data type supported by ResultSet (for example, getString, getBytes, etc.).
     * It supports most common data types for columns like:
     * <ul>
     * <li>String</li>
     * <li>Integer</li>
     * <li>Long</li>
     * <li>Date (from java.sql)</li>
     * <li>Boolean</li>
     * <li>Timestamp</li>
     * <li>BigDecimal</li>
     * <li>byte[]</li>
     * </ul>
     * It also supports <i>null</i> values for objects other than <i>Integer</i>, <i>Long</i> and <i>Boolean</i>.
     * <br><u>Example of use</u>: <pre><code>private Map&lt;String, Object[]&gt; table = new HashMap&lt;&gt;();
     * public void setUp() {
     * table.put("id", new Object[] {1, 2, 3});
     * table.put("name", new Object[] {"test1", String.format("test for id [%d]", 2), "test3"});
     * rs = TestUtils.mockResultSetForTable(table, 3);
     * }
     * //Check:
     * rs.getInt("id") == 1;
     * rs.getString("name").equals("test1");
     * rs.getInt("id") == 2;
     * rs.getString("name").equals("test for id [2]");
     * rs.getInt("id") == 3;
     * rs.getString("name").equals("test3");
     * </code></pre>
     * <br><u>Note</u>: size of array of objects can be different for different keys in order to support cases when get method for some column
     * is performed more often than get method for another.
     * <br><u>Concrete example</u>: <pre><code>private Map&lt;String, Object[]&gt; table = new HashMap&lt;&gt;();
     * public void setUp() {
     * table.put("id", new Object[] {1, 2, 3});
     * table.put("name", new Object[] {"test1");
     * rs = TestUtils.mockResultSetForTable(table, 3);
     * }
     * //Check:
     * rs.getInt("id") == 1;
     * rs.getString("name").equals("test1");
     * rs.getInt("id") == 2;
     * rs.getString("name").equals("test1");
     * rs.getInt("id") == 3;
     * </code></pre>
     *
     * @param tableToMockResults map that holds column names as keys and array of values for each column to imitate rows.
     * @param recordCount        count of records that need to be returned by mocked ResultSet, it should also be the size of any list of map's value.
     * @return mocked result set.
     */
    public static ResultSet mockResultSetForTable(Map<String, Object[]> tableToMockResults, int recordCount) {
        ResultSet rs = Mockito.mock(ResultSet.class);
        try {
            if (tableToMockResults.size() > 0 && recordCount > 0) {
                //mock result set record count
                OngoingStubbing<Boolean> stub = Mockito.when(rs.next());
                for (int i = 0; i < recordCount; i++) {
                    stub = stub.thenReturn(true);
                }
                stub.thenReturn(false);

                //mock column return values
                for (Map.Entry<String, Object[]> entry : tableToMockResults.entrySet()) {
                    String colName = entry.getKey();
                    Object[] rowValues = entry.getValue();
                    try {
                        Class<?> aClass = null;
                        //determine class type of object collection by at least type of first element that is not null
                        for (Object rowValue : rowValues) {
                            if (rowValue != null) {
                                aClass = rowValue.getClass();
                                break;
                            }
                        }

                        if (aClass != null) { //note: in case when all array elements are nulls we do nothing (unmocked methods will return null)
                            if (aClass.equals(String.class)) {
                                OngoingStubbing<String> ongStub = Mockito.when(rs.getString(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(Integer.class)) {
                                OngoingStubbing<Integer> ongStub = Mockito.when(rs.getInt(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(Long.class)) {
                                OngoingStubbing<Long> ongStub = Mockito.when(rs.getLong(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(Date.class)) {
                                OngoingStubbing<Date> ongStub = Mockito.when(rs.getDate(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(Boolean.class)) {
                                OngoingStubbing<Boolean> ongStub = Mockito.when(rs.getBoolean(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(Timestamp.class)) {
                                OngoingStubbing<Timestamp> ongStub = Mockito.when(rs.getTimestamp(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(BigDecimal.class)) {
                                OngoingStubbing<BigDecimal> ongStub = Mockito.when(rs.getBigDecimal(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else if (aClass.equals(byte[].class)) {
                                OngoingStubbing<byte[]> ongStub = Mockito.when(rs.getBytes(colName));
                                mockAllStubRecords(recordCount, rowValues, ongStub);
                            } else {
                                throw new RuntimeException(String.format("Object of type [%s] is not supported for this kind of mocking.", aClass.getCanonicalName()));
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        //ignored on purpose because array sizes for different columns may differ
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }

            } else { //when nothing to mock
                Mockito.when(rs.next()).thenReturn(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }

    @SuppressWarnings("unchecked")
    private static <T> void mockAllStubRecords(int recordCount, Object[] rowValues, OngoingStubbing<T> ongStub) {
        for (int i = 0; i < recordCount; i++) {
            ongStub = ongStub.thenReturn((T) rowValues[i]);
        }
    }
}
