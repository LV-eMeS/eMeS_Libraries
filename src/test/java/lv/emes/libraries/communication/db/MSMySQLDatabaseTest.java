package lv.emes.libraries.communication.db;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSMySQLDatabaseTest {
    private static MS_JDBCDatabase db = new MS_MySQLDatabase();

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() throws SQLException, ClassNotFoundException {
        db.hostname = TestData.TESTING_SERVER_HOSTAME;
        db.dbName = "test";
        db.userName = "test_user";
        db.password = "test_user";
        db.port = 3306;
        db.onDBConnectionError = Exception::printStackTrace;
        db.onDBStatementError = Exception::printStackTrace;
        db.connect();
    }

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {
        db.disconnect();
    }

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        MS_PreparedSQLQuery st = db.prepareSQLQuery("insert into tests(id, name, count) values (1, 'test1', 33)");
        db.commitStatement(st);
        st = db.prepareSQLQuery("insert into tests(id, name, count) values (2, 'test2', 22)");
        db.commitStatement(st);
        st = db.prepareSQLQuery("insert into tests(id, name, count) values (3, 'test3', 11)");
        db.commitStatement(st);
    }

    @After
    //After every test tear down this mess!
    public void tearDownForEachTest() {
        MS_PreparedSQLQuery st = db.prepareSQLQuery("delete from tests");
        db.commitStatement(st);
    }

    @Test
    public void test01GetCellValuesByName() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests";
        st = db.prepareSQLQuery(query);
        ResultSet rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("1", rs.getString("id"));
        assertEquals(1, rs.getInt("id"));
        assertEquals("test1", rs.getString("name"));
        assertEquals(33, rs.getInt("count"));

        assertTrue(rs.next()); //second record
        assertEquals(22, rs.getInt("count"));
        assertEquals("test2", rs.getString("name"));
        assertEquals(2, rs.getInt("id"));

        assertTrue(rs.next()); //third record
        assertEquals(11, rs.getInt(3));
        assertEquals("test3", rs.getString(2));
        assertEquals(3, rs.getInt(1));
    }

    @Test
    public void test02GetSecondRecord() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests where id=2";
        st = db.prepareSQLQuery(query);
        ResultSet rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals(2, rs.getInt(1));
        assertEquals("test2", rs.getString(2));
        assertEquals("22", rs.getString(3));
        assertEquals(22, rs.getInt(3));
    }

    @Test
    public void test03QueryWithParams() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests where id=?";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        st.setInt(1, 3);
        rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("3", rs.getString(1));
        assertEquals("test3", rs.getString(2));
        assertEquals(11, rs.getInt(3));
    }

    @Test
    public void test04Editing() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "update tests set name='Osvald' where id=1";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        db.commitStatement(st);

        //now to look at the changes!
        query = "select * from tests where id=1";
        st = db.prepareSQLQuery(query);
        rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("Osvald", rs.getString(2));
        assertEquals(33, rs.getInt(3));
    }

    @Test
    public void test05SelectAll() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        rs = db.getQueryResult(st);
        int i = 0;
        while (rs.next()) {
            i++;
            assertEquals(i, rs.getInt(1));
            assertEquals("test" + i, rs.getString(2));
        }
        assertEquals(3, i);
    }

    @Test
    public void test06TableRecordTest() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        rs = db.getQueryResult(st);

        MS_List<Table_tests_Row> testsTable = Table_tests_Row.newTable(rs, Table_tests_Row.class);
        assertEquals(3, testsTable.count());
        for (int i = 0; i < testsTable.count(); i++) {
            assertEquals(i+1, testsTable.get(i).id);
            assertEquals("test" + (i+1), testsTable.get(i).name);
        }
    }

    @Test
    public void test07TableUniqueRecordTest() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select * from tests";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        rs = db.getQueryResult(st);

        Map<Integer, Table_tests_Row> testsTable = Table_tests_Row.newTableWithUniqueKey(rs, Table_tests_Row.class);
        assertEquals(3, testsTable.size());
        assertEquals("test1", testsTable.get(1).name);
        assertEquals("test2", testsTable.get(2).name);
        assertEquals("test3", testsTable.get(3).name);
        assertEquals(33, testsTable.get(1).count);
        assertEquals(2, testsTable.get(2).id);
        assertEquals(11, testsTable.get(3).count);
    }

    @Test
    public void test08GetCount() throws SQLException {
        MS_PreparedSQLQuery st;
        String query = "select count(*) from tests";
        ResultSet rs;

        st = db.prepareSQLQuery(query);
        rs = db.getQueryResult(st);

        assertEquals(3, new MS_TableRecordCount(rs).getCount());
    }

    private static class Table_tests_Row extends MS_TableUniqueRecord {
        int id;
        String name;
        int count;

        public Table_tests_Row(ResultSet rs) {
            super(rs);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Integer getUniqueFieldValue() {
            return id;
        }

        @Override
        protected void initColumns(ResultSet rs) throws SQLException {
            id = rs.getInt("id");
            name = rs.getString("name");
            count = rs.getInt("count");
        }
    }
}