package lv.emes.libraries.communication.db;

import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author eMeS
 * @version 1.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SQLQueryTest {
	private SQLQuery sql = null;
	private final String tableName = "tabula";
	private final String field1 = "?";
	private final String field2 = "kabacis";
	private final String field3 = "soda";

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        sql = new SQLQuery();
    }

    @Test
    public void test01SelectAll() {
        sql.selectFrom().all().table(tableName);
        assertEquals("SELECT * FROM tabula;", sql.toString());
    }

    @Test
    public void test02Insert3Values() {
        sql.insertInto().table(tableName).field(field1).field(field2).field(field3);
        assertEquals("INSERT INTO tabula VALUES(?, kabacis, soda);", sql.toString());
    }

    @Test
    public void test03Replace1Value() {
        sql.replaceInto().table(tableName).field(field3);
        assertEquals("REPLACE INTO tabula VALUES(soda);", sql.toString());
    }

    @Test
    public void test04Update2Fields() {
        sql.update().table(tableName).setNewValue("field1", field1).setNewValue("field2", field2);
        assertEquals("UPDATE tabula SET field1 = ?, field2 = kabacis;", sql.toString());
    }

    @Test
    public void test05Update1Field() {
        sql.update().table(tableName).setNewValue("field1", field1);
        assertEquals("UPDATE tabula SET field1 = ?;", sql.toString());
    }

    @Test
    public void test06UpdateNoFields() {
        sql.update().table(tableName);
        assertEquals("UPDATE tabula SET ;", sql.toString());
    }

    @Test
    public void test07DeleteWhere() {
        sql.deleteFrom().table(tableName).where("value > 4");
        assertEquals("DELETE FROM tabula WHERE value > 4;", sql.toString());
    }

    @Test
    public void test08EmptyQuery() {
        sql.table(tableName).where("value > 4"); //some parameters, but no operation
        assertEquals("", sql.toString());
    }
}
