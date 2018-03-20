package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_StringList;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

/**
 * @author eMeS
 * @version 2.0.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_SQLQueryBuilderTest {

    private MS_SQLQueryBuilder sql;
    private final String tableName = "tabula";
    private final String field1 = MS_SQLQueryBuilder._QPARAM;
    private final String field2 = "kabacis";
    private final String field3 = "soda";

    @Before
    //Before every test do initial setup!
    public void setUpForEachTest() {
        sql = new MS_SQLQueryBuilder();
    }

    @Test
    public void test01SelectAll() {
        sql.select().all().from().table(tableName);
        assertEquals("SELECT * FROM tabula;", sql.buildAndToString());
    }

    @Test
    public void test02Select2Fields() {
        sql.select().field(field3).field(field2).from().table(tableName);
        assertEquals("SELECT soda, kabacis FROM tabula;", sql.buildAndToString());
    }

    @Test
    public void test03Select3Fields() {
        sql.select().field(field3).field(field2).field(field1).from().table(tableName);
        assertEquals("SELECT soda, kabacis, ? FROM tabula;", sql.buildAndToString());
    }

    @Test
    public void test04Select4Fields() {
        sql.select().field(field3).field(field2).field(field1).field(field3).from().table(tableName);
        assertEquals("SELECT soda, kabacis, ?, soda\nFROM tabula;", sql.buildAndToString());
    }

    @Test
    public void test05Insert2Values() {
        sql.insertInto().table(tableName).values().bracketOpening().value(field2).value(field3).bracketClosing();
        assertEquals("INSERT INTO tabula\nVALUES(kabacis, soda);", sql.buildAndToString());
    }

    @Test
    public void test06Insert3Values() {
        sql.insertInto(tableName).values().bracketOpening().field(field1).field(field2).field(field3).bracketClosing();
        assertEquals("INSERT INTO tabula\nVALUES(?, kabacis, soda);", sql.buildAndToString());
    }

    @Test
    public void test07Replace1Value() {
        sql.replaceInto().table(tableName).values().bracketOpening().field(field3).bracketClosing();
        assertEquals("REPLACE INTO tabula\nVALUES(soda);", sql.buildAndToString());
    }

    @Test
    public void test07Replace4Values() {
        sql.replaceInto(tableName).values().bracketOpening().field(field1).field(field2).field(field3).field(field1).bracketClosing();
        assertEquals("REPLACE INTO tabula\nVALUES(?, kabacis, soda, ?);", sql.buildAndToString());
    }

    @Test
    public void test08Update2Fields() {
        sql.update().table(tableName).set().value("field1", field1).value("field2", field2);
        assertEquals("UPDATE tabula SET field1 = ?, field2 = kabacis;", sql.buildAndToString());
    }

    @Test
    public void test09Update1Field() {
        sql.update().table(tableName).set().value("field1", field1);
        assertEquals("UPDATE tabula SET field1 = ?;", sql.buildAndToString());
    }

    @Test
    public void test10UpdateOverloadedMethods() {
        String expected = "UPDATE tabula SET field1 = ?;";
        sql.update().table(tableName).set().value("field1", field1);
        assertEquals(expected, sql.buildAndToString());

        sql = new MS_SQLQueryBuilder().update(tableName).value("field1", field1);
        assertEquals(expected, sql.buildAndToString());
    }

    @Test
    public void test11UpdateNoFields() {
        sql.update().table(tableName).set();
        assertEquals("UPDATE tabula SET;", sql.buildAndToString());
    }

    @Test
    public void test12DeleteWhere() {
        sql.deleteFrom().table(tableName).where().condition("value > 4");
        assertEquals("DELETE FROM tabula\nWHERE value > 4;", sql.buildAndToString());
    }

    @Test
    public void test13EmptyQuery() {
        assertEquals(";", sql.buildAndToString());
    }

    @Test
    public void test14SelectCountWhere() {
        sql.select().field("count(*)").from().table(tableName).where().condition("name = ?")
                .or().condition("email = ?")
                .and().condition("t > 3")
        ;
        assertEquals("SELECT count(*) FROM tabula\nWHERE name = ? OR email = ? AND t > 3;", sql.buildAndToString());
    }

    @Test
    public void test15InsertWhere() {
        sql.insertInto().table(tableName).values().bracketOpening().value(field1).field(field1).bracketClosing().where().field("name").condition("= ?");
        assertEquals("INSERT INTO tabula\nVALUES(?, ?)\nWHERE name = ?;", sql.buildAndToString());
    }

    @Test
    public void test16OrderByDesc() {
        sql.select().field(field2).field(field3).from().table(tableName).orderBy("kabacis").descending();
        assertEquals("SELECT kabacis, soda FROM tabula\nORDER BY kabacis DESC;", sql.buildAndToString());
    }

    @Test
    public void test17OrderByAsc() {
        sql.select().field(field2).field(field3).from().table(tableName).orderBy().field("kabacis").ascending();
        assertEquals("SELECT kabacis, soda FROM tabula\nORDER BY kabacis ASC;", sql.buildAndToString());
    }

    @Test
    public void test18Joins() {
        sql.select()
                .field("u.id").field("u.name").field("t.name")
                .from().table("users u")
                .join(MS_JoinTypeEnum.INNER, "user_types t", "u.type_id = t.id")
        ;
        assertEquals("SELECT u.id, u.name, t.name FROM users u\nINNER JOIN user_types t ON (u.type_id = t.id);", sql.buildAndToString());

        sql = new MS_SQLQueryBuilder().select()
                .field("u.id").field("u.name").field("t.name").field("z.name")
                .from().table("users u")
                .join(MS_JoinTypeEnum.LEFT, "user_types t", "u.type_id = t.id")
                .join(MS_JoinTypeEnum.INNER, "user_z_types z", "t.z_type_id = z.id")
        ;
        assertEquals("SELECT u.id, u.name, t.name, z.name" +
                "\nFROM users u" +
                "\nLEFT JOIN user_types t ON (u.type_id = t.id)" +
                "\nINNER JOIN user_z_types z ON (t.z_type_id = z.id)" +
                ";", sql.buildAndToString());
    }

    @Test
    public void test19InsertIntoFieldsAndValues() {
        sql.insertInto(tableName).bracketOpening()
                .field("id")
                .field("counter")
                .field("text_field3")
                .bracketClosing().values().bracketOpening()
                .value(null)
                .value("123")
                .value("check")
                .bracketClosing()
        ;
        assertEquals("INSERT INTO tabula(id, counter, text_field3)\nVALUES(null, 123, check);", sql.buildAndToString());
    }

    @Test
    public void test20InsertIntoFieldsAndValuesAsLists() {
        sql.insertInto(tableName, new MS_StringList("id,counter,text_field3", ','))
                .values(new MS_StringList("null#123#check"));
        assertEquals("INSERT INTO tabula(id, counter, text_field3)\nVALUES(null, 123, check);", sql.buildAndToString());
    }

    @Test
    public void test21Union() {
        sql
                .select()
                .field("i.id_event_time")
                .field("i.owner")
                .field("i.product")
                .field("i.message")
                .field("i.logging_time")
                .field("null as error")
                .from().table("logger_events.info_events i")
                .union()

                .select()
                .field("id_event_time")
                .field("owner")
                .field("product")
                .field("message")
                .field("logging_time")
                .field("null")
                .from().table("logger_events.warning_events")
                .union()

                .select()
                .field("id_event_time")
                .field("owner")
                .field("product")
                .field("message")
                .field("logging_time")
                .field("error")
                .from().table("logger_events.error_events");

        assertEquals("SELECT i.id_event_time, i.owner, i.product, i.message, i.logging_time, null as error\n" +
                "FROM logger_events.info_events i\n" +
                "UNION\n" +
                "SELECT id_event_time, owner, product, message, logging_time, null\n" +
                "FROM logger_events.warning_events\n" +
                "UNION\n" +
                "SELECT id_event_time, owner, product, message, logging_time, error\n" +
                "FROM logger_events.error_events;", sql.buildAndToString());
    }

    @Test
    public void test22ToSQLText() {
        sql.select()
                .field("id_event_time")
                .field("owner")
                .field("product")
                .field("message")
                .field("logging_time")
                .field("null")
                .from().table("logger_events.warning_events")
                .where().condition("id_event_time like " + MS_SQLQueryBuilder.toSQLText("%672000597%"));

        assertEquals("SELECT id_event_time, owner, product, message, logging_time, null" +
                "\nFROM logger_events.warning_events" +
                "\nWHERE id_event_time like '%672000597%';", sql.buildAndToString());
    }

    @Test
    public void test23Append() {
        sql.append("CREATE").append("DATABASE").append("logger_events");
        assertEquals("CREATE DATABASE logger_events;", sql.buildAndToString());
    }

    @Test
    public void test31Copy() {
        sql.append("CREATE").append("DATABASE").append("logger_events");
        assertEquals(sql.makeCopy().buildAndToString(), sql.buildAndToString());
    }

    @Test(expected = NullPointerException.class)
    public void test32CopyNull() {
        new MS_SQLQueryBuilder(null);
    }
}
