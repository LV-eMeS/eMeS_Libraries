package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_EqualityCheckBuilder;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;
import java.util.Objects;

/**
 * Useful methods to write unit and integration tests easier.
 *
 * @author eMeS
 * @version 1.2.
 */
public class MS_TestUtils {

    public static class MS_CheckedException extends Exception {
        private static final long serialVersionUID = 1886903532036673010L;

        public MS_CheckedException() {
            super();
        }

        public MS_CheckedException(String msg) {
            super(msg);
        }

        public MS_CheckedException(String message, Throwable cause) {
            super(message, cause);
        }

        public MS_CheckedException(Throwable cause) {
            super(cause);
        }
    }

    public static class MS_UnCheckedException1 extends RuntimeException {
        private static final long serialVersionUID = 1886903532036673011L;

        public MS_UnCheckedException1() {
            super();
        }

        public MS_UnCheckedException1(String msg) {
            super(msg);
        }

        public MS_UnCheckedException1(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Runtime exception with custom equals method that checks for message equality and cause in 1 further level of deepness.
     */
    public static class MS_UnCheckedException2 extends RuntimeException {
        static final long serialVersionUID = 1886903532036673012L;

        public MS_UnCheckedException2() {
            super();
        }

        public MS_UnCheckedException2(String msg) {
            super(msg);
        }

        public MS_UnCheckedException2(Throwable cause) {
            super(cause);
        }

        @Override
        public boolean equals(Object another) {
            return this == another || another != null
                    && getClass() == another.getClass()
                    && new MS_EqualityCheckBuilder()
                    .append(this,
                            another, (thi, ano) -> {
                                MS_UnCheckedException2 anoth = (MS_UnCheckedException2) ano;
                                return new MS_EqualityCheckBuilder()
                                        .append(thi.getMessage(), anoth.getMessage())
                                        .append(thi.getCause(), anoth.getCause(),
                                                (thiCause, anoCause) -> new MS_EqualityCheckBuilder()
                                                        .append(thiCause.getMessage(), anoCause.getMessage())
                                                        .areEqual())
                                        .areEqual();
                            }).areEqual();

        }
    }

    /**
     * Creates mocked ResultSet that sequentially returns values for getX method calls according to presented <b>tableToMockResults</b>, where X is
     * data type supported by ResultSet (for example, getString, getBytes, etc.).
     * It supports most common data types for columns like:
     * <ul>
     * <li>String</li>
     * <li>Integer</li>
     * <li>Long</li>
     * <li>Time (from java.sql)</li>
     * <li>Date (from java.sql)</li>
     * <li>Boolean</li>
     * <li>Timestamp</li>
     * <li>BigDecimal</li>
     * <li>byte[]</li>
     * <li>byte</li>
     * <li>InputStream</li>
     * <li>Blob</li>
     * </ul>
     * It also supports <i>null</i> values for objects other than <i>Integer</i>, <i>Long</i> and <i>Boolean</i>.
     * <br><u>Example of use</u>: <pre><code>private Map&lt;String, Object[]&gt; table = new HashMap&lt;&gt;();
     * public void setUp() {
     * table.put("id", new Object[] {1, 2, 3});
     * table.put("name", new Object[] {"test1", String.format("test for id [%d]", 2), "test3"});
     * rs = TestUtils.mockResultSetForTable(table, 3);
     * }
     * //Check:
     * assertTrue(rs.next()); assertTrue(rs.next()); assertTrue(rs.next()); //first 3 calls will pass
     * assertFalse(rs.next()); //4th call will fail to get next record
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
     * <br><u>Note</u>: As addition some specific Exception, which is allowed to be thrown on rs.getX method call,
     * can be put into one of expected table cells in order to expect this kind of exception to be thrown on
     * some column's data retrieval step.
     * <br><u>Concrete example</u>: <pre><code>private Map&lt;String, Object[]&gt; table = new HashMap&lt;&gt;();
     * public void setUp() {
     * Blob normalBlob = new SerialBlob(new byte[]{3});
     * table.put("blobFail", new Object[] {normalBlob, new SQLFeatureNotSupportedException("JDBC driver doesn't support BLOBs")});
     * rs = TestUtils.mockResultSetForTable(table, 2);
     * }
     * //Check:
     * assertNotNull(rs.getBlob("blobFail")); //first BLOB is ok
     * boolean exceptionCaught = false;
     * try {
     * rs.getBlob("blobFail");
     * } catch (SQLFeatureNotSupportedException e) {
     * exceptionCaught = true;
     * }
     * assertTrue(exceptionCaught); //second BLOB failed
     * </code></pre>
     *
     * @param tableToMockResults map that holds column names as keys and array of values for each column to imitate rows.
     * @param recordCount        count of records that need to be returned by mocked ResultSet, it should also be the size of any list of map's value.
     * @return mocked result set.
     */
    public static ResultSet mockResultSetForTable(Map<String, Object[]> tableToMockResults, int recordCount) {
        Objects.requireNonNull(tableToMockResults);
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
                        Class<?> aClass = null;
                        //determine class type of object collection by at least type of first element that is not null
                        for (Object cellValue : rowValues) {
                            if (cellValue != null) {
                                aClass = cellValue.getClass();
                                //don't count throwables here as classes, because those will be caught on
                                //real class method call as specific condition
                                if (!(cellValue instanceof Throwable)) {
                                    break;
                                }
                            }
                        }

                        mockAllValuesForSingleColumn(recordCount, rs, colName, rowValues, aClass);
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

    //*** Private methods ***

    private static void mockAllValuesForSingleColumn(int recordCount, ResultSet rs, String colName, Object[] rowValues, Class<?> aClass) throws SQLException {
        if (aClass != null) { //note: in case when all array elements are nulls we do nothing (unmocked methods will return null)
            if (aClass.isAssignableFrom(String.class)) {
                OngoingStubbing<String> stubString = Mockito.when(rs.getString(colName));
                mockAllStubRecords(recordCount, rowValues, stubString, null);
            } else if (Integer.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Integer> stubInt = Mockito.when(rs.getInt(colName));
                mockAllStubRecords(recordCount, rowValues, stubInt, 0);
            } else if (Long.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Long> stubLong = Mockito.when(rs.getLong(colName));
                mockAllStubRecords(recordCount, rowValues, stubLong, 0L);
            } else if (Time.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Time> stubTime = Mockito.when(rs.getTime(colName));
                mockAllStubRecords(recordCount, rowValues, stubTime, null);
            } else if (Date.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Date> stubDate = Mockito.when(rs.getDate(colName));
                mockAllStubRecords(recordCount, rowValues, stubDate, null);
            } else if (Boolean.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Boolean> stubBool = Mockito.when(rs.getBoolean(colName));
                mockAllStubRecords(recordCount, rowValues, stubBool, false);
            } else if (Timestamp.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Timestamp> stubTimestamp = Mockito.when(rs.getTimestamp(colName));
                mockAllStubRecords(recordCount, rowValues, stubTimestamp, null);
            } else if (BigDecimal.class.isAssignableFrom(aClass)) {
                OngoingStubbing<BigDecimal> stubBigDecimal = Mockito.when(rs.getBigDecimal(colName));
                mockAllStubRecords(recordCount, rowValues, stubBigDecimal, null);
            } else if (byte[].class.isAssignableFrom(aClass)) {
                OngoingStubbing<byte[]> stubByteArray = Mockito.when(rs.getBytes(colName));
                mockAllStubRecords(recordCount, rowValues, stubByteArray, null);
            } else if (Byte.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Byte> stubByte = Mockito.when(rs.getByte(colName));
                mockAllStubRecords(recordCount, rowValues, stubByte, (byte) 0);
            } else if (InputStream.class.isAssignableFrom(aClass)) {
                OngoingStubbing<InputStream> stubStream = Mockito.when(rs.getBinaryStream(colName));
                mockAllStubRecords(recordCount, rowValues, stubStream, null);
            } else if (Blob.class.isAssignableFrom(aClass)) {
                OngoingStubbing<Blob> stubBlob = Mockito.when(rs.getBlob(colName));
                mockAllStubRecords(recordCount, rowValues, stubBlob, null);
            } else {
                OngoingStubbing<Object> stubObject = Mockito.when(rs.getObject(colName));
                mockAllStubRecords(recordCount, rowValues, stubObject, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void mockAllStubRecords(int recordCount, Object[] rowValues, OngoingStubbing<T> ongStub, T valueIfNull) {
        for (int i = 0; i < recordCount; i++) {
            try {
                Object cell = rowValues[i];
                if (cell != null) {
                    if (cell instanceof Throwable) {
                        ongStub = ongStub.thenThrow((Throwable) cell);
                    } else {
                        ongStub = ongStub.thenReturn((T) cell);
                    }
                } else {
                    ongStub = ongStub.thenReturn(valueIfNull);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                ongStub = ongStub.thenReturn(valueIfNull);
            }
        }
    }
}
