package lv.emes.libraries.communication.db;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 2.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSSQLQueryTest {

    private MS_SQLQuery sql;
    private final String tableName = "tabula";
    private final String field1 = "?";
    private final String field2 = "kabacis";
    private final String field3 = "soda";

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        sql = new MS_SQLQuery();
    }

    @Test
    public void test01SelectAll() {
        sql.selectFrom().all().table(tableName);
        assertEquals("SELECT * FROM tabula;", sql.toString());
    }

    @Test
    public void test02Select2Fields() {
        sql.selectFrom().field(field3).field(field2).table(tableName);
        assertEquals("SELECT soda, kabacis FROM tabula;", sql.toString());
    }

    @Test
    public void test03Select3Fields() {
        sql.selectFrom().field(field3).field(field2).field(field1).table(tableName);
        assertEquals("SELECT soda, kabacis, ? FROM tabula;", sql.toString());
    }

    @Test
    public void test04Insert2Values() {
        sql.insertInto().table(tableName).field(field2).field(field3);
        assertEquals("INSERT INTO tabula VALUES(kabacis, soda);", sql.toString());
    }

    @Test
    public void test05Insert3Values() {
        sql.insertInto().table(tableName).field(field1).field(field2).field(field3);
        assertEquals("INSERT INTO tabula VALUES(?, kabacis, soda);", sql.toString());
    }

    @Test
    public void test06Replace1Value() {
        sql.replaceInto().table(tableName).field(field3);
        assertEquals("REPLACE INTO tabula VALUES(soda);", sql.toString());
    }

    @Test(expected = MS_BadSQLSyntaxException.class)
    public void test07ReplaceNoValuesNoTable() {
        sql.replaceInto();
        assertNotEquals("REPLACE INTO VALUES();", sql.toString());
    }

    @Test
    public void test08Update2Fields() {
        sql.update().table(tableName).field("field1", field1).field("field2", field2);
        assertEquals("UPDATE tabula SET field1 = ?, field2 = kabacis;", sql.toString());
    }

    @Test
    public void test09Update1Field() {
        sql.update().table(tableName).field("field1", field1);
        assertEquals("UPDATE tabula SET field1 = ?;", sql.toString());
    }

    @Test
    public void test10UpdateNoFields() {
        sql.update().table(tableName);
        assertEquals("UPDATE tabula SET ;", sql.toString());
    }

    @Test
    public void test11DeleteWhere() {
        sql.deleteFrom().table(tableName).where("value > 4");
        assertEquals("DELETE FROM tabula WHERE value > 4;", sql.toString());
    }

    @Test
    public void test12EmptyQuery() {
        sql.table(tableName).where("value > 4"); //some parameters, but no operation
        assertEquals("", sql.toString());
    }

    @Test
    public void test13SelectCountWhere() {
        sql.selectFrom().table(tableName).field("count(*)").where("name = ? or email = ?");
        assertEquals("SELECT count(*) FROM tabula WHERE name = ? or email = ?;", sql.toString());
    }

    @Test
    public void test14InsertWhere() {
        sql.insertInto().table(tableName).field(field1).field(field1).where("name = ?");
        assertEquals("INSERT INTO tabula VALUES(?, ?) WHERE name = ?;", sql.toString());
    }

    @Test
    public void test15OrderBy() {
        sql.selectFrom().table(tableName).field(field2).field(field3).orderBy(field2 + " DESC");
        assertEquals("SELECT kabacis, soda FROM tabula ORDER BY kabacis DESC;", sql.toString());
    }

    @Test
    public void test15AnythingElse() {
        sql.selectFrom().table(tableName).field(field2).field(field3).orderBy(field2).anythingElse("DESC");
        assertEquals("SELECT kabacis, soda FROM tabula ORDER BY kabacis DESC;", sql.toString());
    }

    @Test
    public void test15AscendingOrDescending() {
        sql.selectFrom().table(tableName).field(field2).field(field3).orderBy(field2).descending();
        assertEquals("SELECT kabacis, soda FROM tabula ORDER BY kabacis DESC;", sql.toString());

        sql.resetContent().selectFrom().table(tableName).field(field2).field(field3).orderBy(field2).ascending();
        assertEquals("SELECT kabacis, soda FROM tabula ORDER BY kabacis ASC;", sql.toString());
    }

    @Test
    public void test16Joins() {
        sql.selectFrom().table("users u")
                .field("u.id").field("u.name").field("t.name")
                .join(JoinTypeEnum.INNER, "user_types t", "u.type_id = t.id")
        ;
        assertEquals("SELECT u.id, u.name, t.name FROM users u INNER JOIN user_types t ON (u.type_id = t.id);", sql.toString());

        sql.resetContent().selectFrom().table("users u")
                .field("u.id").field("u.name").field("t.name").field("z.name")
                .join(JoinTypeEnum.LEFT, "user_types t", "u.type_id = t.id")
                .join(JoinTypeEnum.INNER, "user_z_types z", "t.z_type_id = z.id")
        ;
        assertEquals("SELECT u.id, u.name, t.name, z.name FROM users u" +
                " LEFT JOIN user_types t ON (u.type_id = t.id)" +
                " INNER JOIN user_z_types z ON (t.z_type_id = z.id)" +
                ";", sql.toString());
    }

    @Test
    public void test21InsertValues2Parameters() {
        sql.resetContent().insertInto().table(tableName)
                .field("id", null)
                .field("counter", "123")
                .field("text_field3", "check");
        assertEquals("INSERT INTO tabula(id, counter, text_field3) VALUES(null, 123, check);", sql.toString());
    }
}
