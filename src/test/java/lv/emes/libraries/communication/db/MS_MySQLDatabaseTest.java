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
public class MS_MySQLDatabaseTest {

    private static MS_JDBCDatabase db;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() {
        db = new MS_MySQLDatabase(new MS_DBParameters()
                .withHostname(TestData.TESTING_SERVER_HOSTAME)
                .withDbName("test")
                .withUserName("test_user")
                .withPassword("test_user")
                .withPort(3306)
        );
        db.initialize();

        assertTrue(db.isOnline());
    }

    @AfterClass
    //After all tests perform actions that cleans everything up!
    public static void finalizeTestConditions() {
        db.disconnect();
    }

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st = con.prepareQuery("insert into tests(id, name, count) values (1, 'test1', 33)");
            con.executeQuery(st);
            st = con.prepareQuery("insert into tests(id, name, count) values (2, 'test2', 22)");
            con.executeQuery(st);
            st = con.prepareQuery("insert into tests(id, name, count) values (3, 'test3', 11)");
            con.executeQuery(st);

            con.finishWork();
        }
    }

    @After
    //After every test tear down this mess!
    public void tearDownForEachTest() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st = con.prepareQuery("delete from tests");
            con.executeQuery(st);
        }
    }

    @Test
    public void test00GetQueryResultWithoutStmtPreparation() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            String query = "select * from tests";
            ResultSet rs = con.getQueryResult(query);
            assertTrue(rs.next());
            assertEquals("1", rs.getString("id"));
            assertEquals(1, rs.getInt("id"));
            assertEquals("test1", rs.getString("name"));
            assertEquals(33, rs.getInt("count"));
        }
    }

    @Test
    public void test01GetCellValuesByName() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests";
            st = con.prepareQuery(query);
            ResultSet rs = con.getQueryResult(st);
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
    }

    @Test
    public void test02GetSecondRecord() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests where id=2";
            st = con.prepareQuery(query);
            ResultSet rs = con.getQueryResult(st);
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
            assertEquals("test2", rs.getString(2));
            assertEquals("22", rs.getString(3));
            assertEquals(22, rs.getInt(3));
        }
    }

    @Test
    public void test03QueryWithParams() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests where id=?";
            ResultSet rs;

            st = con.prepareQuery(query);
            st.setInt(1, 3);
            rs = con.getQueryResult(st);
            assertTrue(rs.next());
            assertEquals("3", rs.getString(1));
            assertEquals("test3", rs.getString(2));
            assertEquals(11, rs.getInt(3));
        }
    }

    @Test
    public void test04Editing() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "update tests set name='Oswald' where id=1";
            ResultSet rs;

            st = con.prepareQuery(query);
            con.executeQuery(st);

            //now to look at the changes!
            query = "select * from tests where id=1";
            st = con.prepareQuery(query);
            rs = con.getQueryResult(st);
            assertTrue(rs.next());
            assertEquals("Oswald", rs.getString(2));
            assertEquals(33, rs.getInt(3));
        }
    }

    @Test
    public void test05SelectAll() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests";
            ResultSet rs;

            st = con.prepareQuery(query);
            rs = con.getQueryResult(st);
            int i = 0;
            while (rs.next()) {
                i++;
                assertEquals(i, rs.getInt(1));
                assertEquals("test" + i, rs.getString(2));
            }
            assertEquals(3, i);
        }
    }

    @Test
    public void test06TableRecordTest() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests";
            ResultSet rs;

            st = con.prepareQuery(query);
            rs = con.getQueryResult(st);

            MS_List<Table_tests_Row> testsTable = MS_ResultSetExtractingUtils.extractList(rs, Table_tests_Row.class);
            assertEquals(3, testsTable.count());
            for (int i = 0; i < testsTable.count(); i++) {
                assertEquals(i + 1, testsTable.get(i).id);
                assertEquals("test" + (i + 1), testsTable.get(i).name);
            }
        }
    }

    @Test
    public void test07TableUniqueRecordTest() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests";
            ResultSet rs;

            st = con.prepareQuery(query);
            rs = con.getQueryResult(st);


            Map<Integer, Table_tests_Row> testsTable = MS_ResultSetExtractingUtils.extractMap(rs, Table_tests_Row.class);
            assertEquals(3, testsTable.size());
            assertEquals("test1", testsTable.get(1).name);
            assertEquals("test2", testsTable.get(2).name);
            assertEquals("test3", testsTable.get(3).name);
            assertEquals(33, testsTable.get(1).count);
            assertEquals(2, testsTable.get(2).id);
            assertEquals(11, testsTable.get(3).count);
        }
    }

    @Test
    public void test08GetCount() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select count(*) from tests";
            ResultSet rs;

            st = con.prepareQuery(query);
            rs = con.getQueryResult(st);

            assertEquals(3, MS_ResultSetExtractingUtils.extractRecord(rs, MS_TableRecordCount.class).getCount());
        }
    }

    @Test(expected = NullPointerException.class)
    public void test11FailToInitializeWithNullParams() {
        MS_MySQLDatabase database = new MS_MySQLDatabase(new MS_DBParameters()
                .withHostname(TestData.TESTING_SERVER_HOSTAME)
                .withDbName(null)
                .withUserName(null)
                .withPassword(null)
                .withPort(0)
        );
        database.initialize();
    }

    private static class Table_tests_Row implements MS_TableUniqueRecord<Integer> {
        int id;
        String name;
        int count;

        public Table_tests_Row() {
        }

        @Override
        public Integer getUniqueFieldValue() {
            return id;
        }

        @Override
        public void initColumns(ResultSet rs) throws SQLException {
            id = rs.getInt("id");
            name = rs.getString("name");
            count = rs.getInt("count");
        }
    }
}