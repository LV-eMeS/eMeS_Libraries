package lv.emes.libraries.utilities;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Useful methods to write unit and integration tests easier.
 * @author eMeS
 * @version 1.0.
 */
public class MS_TestUtils {
    /**
     * Creates mocked ResultSet that returns values according to presented <b>tableToMockResults</b>.
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
     * All lists in map (table rows) must be same size that equals to <b>recordCount</b>.
     *
     * @param tableToMockResults map that holds column names as keys and list of values for each column to imitate rows.
     * @param recordCount        count of records that need to be returned by mocked ResultSet, it should also be the size of any list of map's value.
     * @return mocked result set.
     */
    public static ResultSet mockResultSetForTable(Map<String, List<Object>> tableToMockResults, int recordCount) {
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
                tableToMockResults.forEach((colName, rowValues) -> {
                    try {
                        if (rowValues.get(0).getClass().equals(String.class)) {
                            OngoingStubbing<String> ongStub = Mockito.when(rs.getString(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((String) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(Integer.class)) {
                            OngoingStubbing<Integer> ongStub = Mockito.when(rs.getInt(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((Integer) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(Long.class)) {
                            OngoingStubbing<Long> ongStub = Mockito.when(rs.getLong(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((Long) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(Date.class)) {
                            OngoingStubbing<Date> ongStub = Mockito.when(rs.getDate(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((Date) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(Boolean.class)) {
                            OngoingStubbing<Boolean> ongStub = Mockito.when(rs.getBoolean(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((Boolean) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(Timestamp.class)) {
                            OngoingStubbing<Timestamp> ongStub = Mockito.when(rs.getTimestamp(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((Timestamp) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(BigDecimal.class)) {
                            OngoingStubbing<BigDecimal> ongStub = Mockito.when(rs.getBigDecimal(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((BigDecimal) rowValues.get(i));
                            }
                        } else if (rowValues.get(0).getClass().equals(byte[].class)) {
                            OngoingStubbing<byte[]> ongStub = Mockito.when(rs.getBytes(colName));
                            for (int i = 0; i < recordCount; i++) {
                                ongStub = ongStub.thenReturn((byte[]) rowValues.get(i));
                            }
                        } else {
                            throw new RuntimeException(String.format("Object of type [%s] is not supported for this kind of mocking.", rowValues.get(0).getClass().getCanonicalName()));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else { //when nothing to mock
                Mockito.when(rs.next()).thenReturn(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rs;
    }
}
