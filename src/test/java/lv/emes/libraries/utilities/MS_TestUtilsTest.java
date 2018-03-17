package lv.emes.libraries.utilities;

import lv.emes.libraries.communication.db.MS_ResultSetExtractingUtils;
import lv.emes.libraries.communication.db.MS_TableRecord;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
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
