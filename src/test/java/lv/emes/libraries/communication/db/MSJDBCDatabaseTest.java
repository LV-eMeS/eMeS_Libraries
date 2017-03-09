package lv.emes.libraries.communication.db;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;

/**
 * This test doesn't do actual connection to some database. Everything is mocked just to test reconnect functionality.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSJDBCDatabaseTest {
    private final static String SQL = "SELECT * FROM TEST";

    private static MS_JDBCDatabase db;
    private static Boolean exceptionRaised = false;
    private static Connection connMock;
    private static PreparedStatement prepStmntMock;
    private static ResultSet rsMock;

    @BeforeClass
    //Before even start testing do some preparations!
    public static void initTestPreConditions() throws SQLException, ClassNotFoundException {
        connMock = Mockito.mock(Connection.class);
        prepStmntMock = Mockito.mock(PreparedStatement.class);
        rsMock = Mockito.mock(ResultSet.class);

        db = new JDBCDatabase(connMock);
        db.onDBConnectionError = (exception -> exceptionRaised = true);
        db.onDBStatementError = (exception -> exceptionRaised = true);
        db.onDBEmptyStatementError = (exception -> exceptionRaised = true);
        db.connect();
    }

    @Test
    public void test01PrepareSQLQueryWithReconnectSuccess() throws Exception {
        Mockito.when(connMock.prepareStatement(any(String.class))).thenThrow(SQLException.class).thenReturn(prepStmntMock);
        MS_PreparedSQLQuery query = db.prepareSQLQuery(SQL);
        assertThatExceptionOccuredByLambda();
        assertNotNull(query);
    }

    @Test(expected = Exception.class)
    public void test02PrepareSQLQueryWithReconnectFailure() throws Exception {
        Mockito.when(connMock.prepareStatement(any(String.class))).thenThrow(SQLException.class);
        db.prepareSQLQuery(SQL);
        assertThatExceptionOccuredByLambda();
    }

    @Test
    public void test03GetQueryResultWithReconnectSuccess() throws Exception {
        Mockito.reset(connMock);
        Mockito.when(connMock.prepareStatement(any(String.class))).thenReturn(prepStmntMock);
        Mockito.when(prepStmntMock.executeQuery()).thenReturn(rsMock);
        MS_PreparedSQLQuery query = db.prepareSQLQuery(SQL);
        assertNotNull(query);

        ResultSet rs = db.getQueryResult(query);
        assertThatExceptionOccuredByLambda();
        assertEquals(rsMock, rs);
    }

    @Test(expected = Exception.class)
    public void test04GetQueryResultWithReconnectFailure() throws Exception {
        Mockito.reset(connMock);
        Mockito.when(connMock.prepareStatement(any(String.class))).thenReturn(prepStmntMock);
        Mockito.when(prepStmntMock.executeQuery()).thenThrow(SQLException.class);
        MS_PreparedSQLQuery query = db.prepareSQLQuery(SQL);
        assertNotNull(query);

        db.getQueryResult(query);
        assertThatExceptionOccuredByLambda();
    }

    @Test
    public void test05CommitStatementWithReconnectSuccess() throws Exception {
        Mockito.reset(connMock);
        Mockito.when(connMock.prepareStatement(any(String.class))).thenReturn(prepStmntMock);
        Mockito.when(prepStmntMock.executeUpdate()).thenReturn(1);
        MS_PreparedSQLQuery query = db.prepareSQLQuery(SQL);
        assertNotNull(query);

        Boolean res = db.commitStatement(query);
        assertThatExceptionOccuredByLambda();
        assertEquals(true, res);
    }

    @Test(expected = Exception.class)
    public void test06CommitStatementWithReconnectFailure() throws Exception {
        Mockito.reset(connMock);
        Mockito.when(connMock.prepareStatement(any(String.class))).thenReturn(prepStmntMock);
        Mockito.when(prepStmntMock.executeUpdate()).thenThrow(SQLException.class);
        MS_PreparedSQLQuery query = db.prepareSQLQuery(SQL);
        assertNotNull(query);

        db.commitStatement(query);
        assertThatExceptionOccuredByLambda();
    }

    private static class JDBCDatabase extends MS_JDBCDatabase {
        public JDBCDatabase(Connection con) {
            this.conn = con;
        }
    }

    private static void assertThatExceptionOccuredByLambda() throws Exception {
        if (exceptionRaised) {
            exceptionRaised = false;
            throw new Exception();
        }
    }
}