package lv.emes.libraries.utilities;

import lv.emes.libraries.communication.db.MS_ResultSetExtractingUtils;
import lv.emes.libraries.communication.db.MS_TableRecord;
import lv.emes.libraries.tools.MS_EqualityCheckBuilder;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Test;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * ResultSetExtractor implementation test for barcode key pair.
 * Created by maris.salenieks on 07.03.2017.
 */
public class MS_TestUtilsTest {

    @Test
    public void testResultSetMocking() throws Exception {
        ResultSet rs;
        Map<String, Object[]> table = new HashMap<>();

        TableToMock firstRecord = new TableToMock().withAge(25).withName("Māris").withSurname("Salenieks");
        TableToMock secondRecord = new TableToMock().withAge(62).withName("Bill").withSurname("Gates");
        TableToMock thirdRecord = new TableToMock().withAge(44).withName("Gwyneth").withSurname("Paltrow");
        TableToMock fourthRecord = new TableToMock().withName("Unknown");

        //sets data for all mocked database records
        table.put("name", new Object[] {firstRecord.name, secondRecord.name, thirdRecord.name, fourthRecord.name});
        table.put("surname", new Object[] {firstRecord.surname, secondRecord.surname, thirdRecord.surname});
        table.put("age", new Object[] {firstRecord.age, secondRecord.age, thirdRecord.age});

        //Mock result set so that it will return 1 record
        rs = MS_TestUtils.mockResultSetForTable(table, 0);
        TableToMock extractedObject = MS_ResultSetExtractingUtils.extractRecord(rs, TableToMock.class);
        assertNull(extractedObject);

        //Mock result set so that it will return 1 record
        rs = MS_TestUtils.mockResultSetForTable(table, 1);
        extractedObject = MS_ResultSetExtractingUtils.extractRecord(rs, TableToMock.class);

        assertNotNull(extractedObject);
        assertEquals(firstRecord, extractedObject);

        //Mock result set so that it will return 3 records
        MS_List<TableToMock> extractedObjects;
        rs = MS_TestUtils.mockResultSetForTable(table, 3);
        extractedObjects = MS_ResultSetExtractingUtils.extractList(rs, TableToMock.class);
        assertEquals(3, extractedObjects.count());
        assertEquals(firstRecord, extractedObjects.get(0));
        assertEquals(secondRecord, extractedObjects.get(1));
        assertEquals(thirdRecord, extractedObjects.get(2));

        //test fourth record that has only one field
        rs = MS_TestUtils.mockResultSetForTable(table, 4);
        extractedObjects = MS_ResultSetExtractingUtils.extractList(rs, TableToMock.class);
        assertEquals(4, extractedObjects.count());
        assertEquals(fourthRecord.name, extractedObjects.get(3).name);
    }

    @Test
    public void testMockResultSetForTable() throws SQLException {
        Map<String, Object[]> emptyTable = new HashMap<>();
        ResultSet emptyRs = MS_TestUtils.mockResultSetForTable(emptyTable, 2);
        assertFalse("Result set for empty table must not have next record", emptyRs.next());

        Map<String, Object[]> table = new HashMap<>();
        table.put("id", new Object[] {1, 2});
        table.put("name", new Object[] {null, "John"});
        table.put("longNumber", new Object[] {1234567890987654321L, null});
        table.put("birthDate", new Object[] {new Date(12345)});
        table.put("lastLoggedInAt", new Object[] {new Time(987654321)});
        table.put("isEmployee", new Object[] {null, true});
        table.put("someTimeAgo", new Object[] {new Timestamp(564576)});
        table.put("avgSalary", new Object[] {null, new BigDecimal("2540.76")});
        table.put("zero", new Object[] {new byte[]{0}});
        table.put("someWeirdObject", new Object[] {new MS_EqualityCheckBuilder()});
        table.put("b", new Object[] {(byte) 1});
        Blob fakeBlob = new SerialBlob(new byte[]{3});
        table.put("blobFail", new Object[] {fakeBlob, new SQLFeatureNotSupportedException("JDBC driver doesn't support BLOBs")});
        table.put("stream", new Object[] {new SQLException("getBinaryStream usually throws only SQLException"),
                new ByteArrayInputStream(new byte[] {0})});

        ResultSet rsWithoutAnyRecords = MS_TestUtils.mockResultSetForTable(table, 0);
        assertFalse(rsWithoutAnyRecords.next());

        ResultSet rsWith1RecordOnly = MS_TestUtils.mockResultSetForTable(table, 1);
        assertTrue(rsWith1RecordOnly.next());
        assertFalse(rsWith1RecordOnly.next());

        ResultSet rs = MS_TestUtils.mockResultSetForTable(table, 2);
        assertTrue(rs.next());
        assertTrue(rs.next());
        assertFalse(rs.next());

        Object obj;
        int id;
        String name;
        long longNumber;
        Date birthDate;
        Time lastLoggedInAt;
        Boolean isEmployee;
        Timestamp someTimeAgo;
        BigDecimal avgSalary;
        byte[] zero;
        Object someWeirdObject;
        byte b;
        Blob blobFail;

        id = rs.getInt("id");
        assertEquals(1, id);
        name = rs.getString("name");
        assertNull(name);
        longNumber = rs.getLong("longNumber");
        assertEquals(1234567890987654321L, longNumber);
        birthDate = rs.getDate("birthDate");
        assertEquals(new Date(12345), birthDate);
        lastLoggedInAt = rs.getTime("lastLoggedInAt");
        assertEquals(new Time(987654321), lastLoggedInAt);
        isEmployee = rs.getBoolean("isEmployee");
        assertEquals(false, isEmployee);
        someTimeAgo = rs.getTimestamp("someTimeAgo");
        assertNotNull(someTimeAgo);
        avgSalary = rs.getBigDecimal("avgSalary");
        assertNull(avgSalary);
        zero = rs.getBytes("zero");
        assertEquals(1, zero.length);
        someWeirdObject = rs.getObject("someWeirdObject");
        assertTrue(someWeirdObject instanceof MS_EqualityCheckBuilder);
        b = rs.getByte("b");
        assertEquals(1, b);
        blobFail = rs.getBlob("blobFail");
        assertNotNull(blobFail);

        id = rs.getInt("id");
        assertEquals(2, id);
        name = rs.getString("name");
        assertEquals("John", name);
        obj = rs.getLong("longNumber");
        assertEquals(0L, obj);
        birthDate = rs.getDate("birthDate");
        assertNull(birthDate);
        assertNull(rs.getTime("lastLoggedInAt"));
        isEmployee = rs.getBoolean("isEmployee");
        assertEquals(true, isEmployee);
        avgSalary = rs.getBigDecimal("avgSalary");
        assertEquals(new BigDecimal("2540.76"), avgSalary);

        //check, if throwables are thrown as intended
        boolean exceptionCaught;
        exceptionCaught = false;
        try {
            rs.getBlob("blobFail");
        } catch (SQLFeatureNotSupportedException e) {
            exceptionCaught = true;
        }
        assertTrue("For this cell rs.getBlob should've thrown a SQLFeatureNotSupportedException", exceptionCaught);

        exceptionCaught = false;
        try {
            rs.getBinaryStream("stream");
        } catch (SQLException e) {
            exceptionCaught = true;
        }
        assertTrue("For this cell rs.getBinaryStream should've thrown a SQLException ", exceptionCaught);
        //but on second attempt we can get actual stream object
        assertNotNull("On second attempt we should've been able to get actual stream object", rs.getBinaryStream("stream"));
    }

    public static class TableToMock implements MS_TableRecord {
        String name;
        String surname;
        int age;

        public TableToMock withName(String name) {
            this.name = name;
            return this;
        }

        public TableToMock withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public TableToMock withAge(int age) {
            this.age = age;
            return this;
        }

        @Override
        public void initColumns(ResultSet rs) throws SQLException {
            name = rs.getString("name");
            surname = rs.getString("surname");
            age = rs.getInt("age");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TableToMock that = (TableToMock) o;

            if (age != that.age) return false;
            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            return surname != null ? surname.equals(that.surname) : that.surname == null;
        }
    }
}
