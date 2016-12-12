package lv.emes.libraries.communication.db;

import lv.emes.libraries.communication.CommunicationConstants;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSMySQLDatabaseTest {
    public static MS_JDBCDatabase db = new MS_MySQLDatabase();

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() throws SQLException, ClassNotFoundException {
        db.hostname = CommunicationConstants.TESTING_SERVER_HOSTAME;
        db.dbName = "test";
        db.userName = "test_user";
        db.password = "test_user";
        db.port = 3306;
        db.onDBConnectionError = (e) -> {
            e.printStackTrace();
        };
        db.onDBStatementError = (e) -> {
            e.printStackTrace();
        };
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
        MS_PreparedStatement st = db.prepareQuery("insert into tests(id, name, count) values (1, 'test1', 1)");
        db.commitStatement(st);
        st = db.prepareQuery("insert into tests(id, name, count) values (2, 'test2', 2)");
        db.commitStatement(st);
        st = db.prepareQuery("insert into tests(id, name, count) values (3, 'test3', 3)");
        db.commitStatement(st);
    }

    @After
    //After every test tear down this mess!
    public void tearDownForEachTest() {
        MS_PreparedStatement st = db.prepareQuery("delete from tests");
        db.commitStatement(st);
    }

    @Test
    public void test01GetSecondRecord() throws SQLException {
        MS_PreparedStatement st;
        String query = "select * from tests where id=2";
        st = db.prepareQuery(query);
        ResultSet rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("test2", rs.getString(2));
        assertEquals("2", rs.getString(3));
        assertEquals(2, rs.getInt(3));
    }

    @Test
    public void test02QueryWithParams() throws SQLException {
        MS_PreparedStatement st;
        String query = "select * from tests where id=?";
        ResultSet rs;

        st = db.prepareQuery(query);
        st.setInt(1, 2);
        rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("test2", rs.getString(2));
        assertEquals("2", rs.getString(3));
        assertEquals(2, rs.getInt(3));
    }

    @Test
    public void test03Editing() throws SQLException {
        MS_PreparedStatement st;
        String query = "update tests set name='Osvald' where id=2";
        ResultSet rs;

        st = db.prepareQuery(query);
        db.commitStatement(st);

        //now to look at the changes!
        query = "select * from tests where id=2";
        st = db.prepareQuery(query);
        rs = db.getQueryResult(st);
        assertTrue(rs.next());
        assertEquals("Osvald", rs.getString(2));
        assertEquals(2, rs.getInt(3));
    }

    @Test
    public void test04SelectAll() throws SQLException {
        MS_PreparedStatement st;
        String query = "select * from tests";
        ResultSet rs;

        st = db.prepareQuery(query);
        rs = db.getQueryResult(st);
        int i = 0;
        while (rs.next()) {
            i++;
            assertEquals(i, rs.getInt(1));
            assertEquals("test" + i, rs.getString(2));
        }
        assertEquals(3, i);
    }
}