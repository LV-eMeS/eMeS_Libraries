package lv.emes.libraries.communication.db;

import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(db.isOnline()).isTrue();
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
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("id")).isEqualTo("1");
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("test1");
            assertThat(rs.getInt("count")).isEqualTo(33);
        }
    }

    @Test
    public void test01GetCellValuesByName() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests";
            st = con.prepareQuery(query);
            ResultSet rs = con.getQueryResult(st);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("id")).isEqualTo("1");
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("test1");
            assertThat(rs.getInt("count")).isEqualTo(33);

            assertThat(rs.next()).isTrue(); //second record
            assertThat(rs.getInt("count")).isEqualTo(22);
            assertThat(rs.getString("name")).isEqualTo("test2");
            assertThat(rs.getInt("id")).isEqualTo(2);

            assertThat(rs.next()).isTrue(); //third record
            assertThat(rs.getInt(3)).isEqualTo(11);
            assertThat(rs.getString(2)).isEqualTo("test3");
            assertThat(rs.getInt(1)).isEqualTo(3);
        }
    }

    @Test
    public void test02GetSecondRecord() throws Exception {
        try (MS_ConnectionSession con = db.getConnectionSession()) {
            MS_PreparedSQLQuery st;
            String query = "select * from tests where id=2";
            st = con.prepareQuery(query);
            ResultSet rs = con.getQueryResult(st);
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(2);
            assertThat(rs.getString(2)).isEqualTo("test2");
            assertThat(rs.getString(3)).isEqualTo("22");
            assertThat(rs.getInt(3)).isEqualTo(22);
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
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("3");
            assertThat(rs.getString(2)).isEqualTo("test3");
            assertThat(rs.getInt(3)).isEqualTo(11);
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
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(2)).isEqualTo("Oswald");
            assertThat(rs.getInt(3)).isEqualTo(33);
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
                assertThat(rs.getInt(1)).isEqualTo(i);
                assertThat(rs.getString(2)).isEqualTo("test" + i);
            }
            assertThat(i).isEqualTo(3);
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
            assertThat(testsTable.count()).isEqualTo(3);
            for (int i = 0; i < testsTable.count(); i++) {
                assertThat(testsTable.get(i).id).isEqualTo(i + 1);
                assertThat(testsTable.get(i).name).isEqualTo("test" + (i + 1));
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
            assertThat(testsTable.size()).isEqualTo(3);
            assertThat(testsTable.get(1).name).isEqualTo("test1");
            assertThat(testsTable.get(2).name).isEqualTo("test2");
            assertThat(testsTable.get(3).name).isEqualTo("test3");
            assertThat(testsTable.get(1).count).isEqualTo(33);
            assertThat(testsTable.get(2).id).isEqualTo(2);
            assertThat(testsTable.get(3).count).isEqualTo(11);
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

            assertThat(MS_ResultSetExtractingUtils.extractRecord(rs, MS_TableRecordCount.class).getCount()).isEqualTo(3);
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