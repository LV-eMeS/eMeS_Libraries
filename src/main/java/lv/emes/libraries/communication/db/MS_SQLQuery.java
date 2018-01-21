package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_AbstractCompositeText;
import lv.emes.libraries.tools.MS_LineBuilder;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * SQL query to operate with data. Currently only SELECT, INSERT, REPLACE, UPDATE and DELETE statements are supported.
 * <p><u>Warning</u>: Use only one of those 5 statement types at time because thanks to <b>MS_AbstractCompositeText</b> toString method forms text just once.
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_SQLQuery extends MS_AbstractCompositeText {

    //Supported SQL statement examples:
    //"select * from users where name = ?"
    //insert into notes values(null, ?, null, ?, ?, curdate(), curdate(), ?, ?)
    //replace into canvases values(null, ?, ?)
    //update notes set modified = curdate() where id = ?
    //delete from notes where id = ?

    public static final String _QPARAM = "?";
    public static final Character _SINGLE_QUOTE = '\'';
    public static final String _SINGLE_QUOTE_2X = "''";

    /**
     * Extracts parameter value to be inserted directly as field value in {@link MS_SQLQuery#field(String, String)}
     * method's second parameter.
     * Single quotation marks (like this: <b>'</b>) are escaped with extra single quotation mark (like this: <b>''</b>).
     * @param parameterValue parameter value without any quotation.
     * @return parameter value quoted with single quote.
     */
    public static String toSQLText(String parameterValue) {
        parameterValue = MS_StringUtils.replaceInString(parameterValue, _SINGLE_QUOTE.toString(), _SINGLE_QUOTE_2X);
        return _SINGLE_QUOTE + parameterValue + _SINGLE_QUOTE;
    }

    private static final String TABLE_NAME_IS_NOT_PROVIDED = "Table name is not provided";
    private static final String FIELD_AND_VALUE_COUNT_MUST_BE_EQUAL = "Field and corresponding value count for query must be equal";

    private static final String SELECT1 = "SELECT ";
    private static final String INSERT = "INSERT INTO ";
    private static final String REPLACE = "REPLACE INTO ";
    private static final String UPDATE1 = "UPDATE ";
    private static final String DELETE = "DELETE FROM ";

    private static final String SELECT2 = " FROM ";
    private static final String INSERT_REPLACE_2 = "VALUES(";
    private static final String UPDATE2 = "SET ";
    private static final String INSERT_REPLACE_3 = ")";
    private static final String ALL = "*";

    private static final int CASE_SELECT = 1;
    private static final int CASE_INSERT = 2;
    private static final int CASE_REPLACE = 3;
    private static final int CASE_UPDATE = 4;
    private static final int CASE_DELETE = 5;

    private MS_StringList fields = new MS_StringList(',');
    private MS_StringList fieldValues = new MS_StringList(',');
    private MS_StringList joins = new MS_StringList(',');
    private String tableName = "";
    private String whereClause = "";
    private String orderByClause = "";
    private String anythingElseClause = "";
    private String ascOrDesc = "";
    private int operation = 0; //Do nothing(0), SELECT(1), INSERT(2), REPLACE(3), UPDATE(4) or DELETE(5)

    @Override
    protected MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        if (operation == 0)
            return lb;

        switch (operation) {
            case CASE_SELECT:
                lb.append(SELECT1);
                addListToLineBuilder(fields, lb);
                lb.append(SELECT2);
                lb.append(tableName);
                break;
            case CASE_INSERT:
                lb.append(INSERT);
                addInsertReplacePartToLineBuilder(lb);
                break;
            case CASE_REPLACE:
                lb.append(REPLACE);
                addInsertReplacePartToLineBuilder(lb);
                break;
            case CASE_UPDATE:
                lb.append(UPDATE1);
                lb.append(tableName).append(" ");
                lb.append(UPDATE2);
                addFieldsAndValuesToLineBuilderForUpdate(lb);
                break;
            case CASE_DELETE:
                lb.append(DELETE);
                lb.append(tableName);
                break;
            default:
                break;
        }

        //all following clauses are separated with spaces from beginning of clause
        joins.forEachItem((s, i) -> lb.append(s));
        if (!whereClause.isEmpty())
            lb.append(whereClause);
        if (!orderByClause.isEmpty())
            lb.append(orderByClause).append(ascOrDesc);
        if (!anythingElseClause.isEmpty())
            lb.append(anythingElseClause);
        lb.append(";"); //every query ends with semicolon
        return lb;
    }

    /**
     * Indicates that this will be SELECT query.
     * <p>query.selectFrom().field("id").field("counter").field("text_field3").table(myTable);
     * <br>will make query to be like this:
     * <br>SELECT id, counter, text_field3 FROM myTable;
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery selectFrom() {
        operation = 1;
        return this;
    }

    /**
     * Indicates that this will be INSERT query.
     * <p>query.insertInto().table(myTable).field("null").field("123").field("check");
     * <br>will make query to be like this:
     * <br>INSERT INTO myTable VALUES(null, 123, check);
     * <p>
     * query.insertInto().table(myTable).field("id", null).field("counter", "123").field("text_field3", "check");
     * <br>will make query to be like this:
     * <br>INSERT INTO myTable(id, counter, text_field3) VALUES(null, 123, check);
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery insertInto() {
        operation = 2;
        return this;
    }

    /**
     * Indicates that this will be REPLACE query.
     * <p>query.replaceInto().table(myTable).field("null").field("123").field("check");
     * <br>will make query to be like this:
     * <br>REPLACE INTO myTable VALUES(null, 123, check);
     * <p>
     * query.replaceInto().table(myTable).field("id", null).field("counter", "123").field("text_field3", "check");
     * <br>will make query to be like this:
     * <br>REPLACE INTO myTable(id, counter, text_field3) VALUES(null, 123, check);
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery replaceInto() {
        operation = 3;
        return this;
    }

    /**
     * Indicates that this will be UPDATE query.
     * <p>query.update().table(myTable).field("counter", "123").field("text_field3", "check");
     * <br>will make query to be like this:
     * <br>UPDATE myTable SET counter = 123, text_field3 = check;
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery update() {
        operation = 4;
        return this;
    }

    /**
     * Indicates that this will be DELETE query.
     * <p>query.deleteFrom().table(myTable).where("counter &gt; 4");
     * <br>will make query to be like this:
     * <br>DELETE FROM myTable WHERE counter &gt; 4;
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery deleteFrom() {
        operation = 5;
        return this;
    }

    /**
     * Sets table name for any type of query.
     * <br><u>Example</u>: table("users u")
     *
     * @param tableName name of table.
     * @return reference to this query itself.
     */
    public MS_SQLQuery table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Adds 'where' clause to query.
     * <br><u>Note</u>: keyword 'WHERE ' altogether with whitespace is added before <b>conditions</b> automatically.
     * <br><u>Example</u>: where("name = 'test' and size &gt; 150")
     *
     * @param conditions all the conditions for query 'where' clause.
     * @return reference to this query itself.
     */
    public MS_SQLQuery where(String conditions) {
        whereClause = " WHERE " + conditions;
        return this;
    }

    /**
     * Adds 'order by' clause to query.
     * <br><u>Note</u>: keywords 'ORDER BY ' altogether with whitespace is added before <b>conditions</b> automatically.
     * <br><u>Example</u>: orderBy("id, name")
     *
     * @param conditions all the conditions for query 'order by' clause.
     * @return reference to this query itself.
     */
    public MS_SQLQuery orderBy(String conditions) {
        orderByClause = " ORDER BY " + conditions;
        return this;
    }

    /**
     * Reserved for other cases wher we need to write more complicated SQL statement.
     * Simply inserts <b>additionalSQL</b> part after the whole statement.
     *
     * @param additionalSQL additional SQL statement part to be appended at the end of statement (right before ending semicolon)
     * @return reference to this query itself.
     */
    public MS_SQLQuery anythingElse(String additionalSQL) {
        anythingElseClause = " " + additionalSQL;
        return this;
    }

    /**
     * When using 'order by' additionally ordering can be performed ascending or descending.
     * This makes ordering in ascending order.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery ascending() {
        ascOrDesc = " ASC";
        return this;
    }

    /**
     * When using 'order by' additionally ordering can be performed ascending or descending.
     * This makes ordering in descending order.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery descending() {
        ascOrDesc = " DESC";
        return this;
    }

    /**
     * Adds field to query of chosen type.
     * <br><u>Example</u>: field("name")
     *
     * @param fieldName field to select, insert, or replace.
     * @return reference to this query itself.
     */
    public MS_SQLQuery field(String fieldName) {
        if (fieldName == null)
            fieldName = "null";
        this.fields.add(fieldName);
        return this;
    }

    /**
     * Alias for {@link MS_SQLQuery#field(String)}.
     * Adds field value to query of chosen type (INSERT or REPLACE).
     * <br><u>Example</u>: field("name")
     *
     * @param fieldValue field value for insert or replace statement.
     * @return reference to this query itself.
     */
    public MS_SQLQuery value(String fieldValue) {
        return this.field(fieldValue);
    }

    /**
     * For update statement set new value <b>fieldValue</b> for field with name <b>fieldName</b>.
     * <p>For insert or replace statements set value <b>fieldValue</b> for field with name <b>fieldName</b>.
     * <u>Warning</u>: this method with 2 parameters <u>should not</u> be mixed with method {@link MS_SQLQuery#field(String)}.
     * <br><u>Example</u>: field("name", "Maris")
     *
     * @param fieldName  name of table field.
     * @param fieldValue value of field to update, insert or replace.
     * @return reference to this query itself.
     */
    public MS_SQLQuery field(String fieldName, String fieldValue) {
        if (fieldName == null)
            fieldName = "null";
        if (fieldValue == null)
            fieldValue = "null";
        this.fields.add(fieldName);
        this.fieldValues.add(fieldValue);
        return this;
    }

    /**
     * For SELECT type of query this adds asterisk to query fields.
     * <br><u>Warning</u>: do not mix this with other field values!
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery all() {
        this.fields.add(ALL);
        return this;
    }

    /**
     * Adds join clause of type <b>joinType</b> with table <b>tableToJoin</b> on presented condition <b>onCondition</b>.
     * <br><u>Example</u>: join(JoinTypeEnum.LEFT, "user_types t", "u.user_type_id=t.id")
     *
     * @param joinType    one of supported join types.
     * @param tableToJoin table name and preferable alias of table (alias is delimited from table name with space).
     * @param onCondition boolean expression on condition to link tables.
     * @return reference to this query itself.
     * @see JoinTypeEnum
     */
    public MS_SQLQuery join(JoinTypeEnum joinType, String tableToJoin, String onCondition) {
        return join(joinType.name(), tableToJoin, onCondition);
    }

    /**
     * Adds join clause of type <b>joinType</b> with table <b>tableToJoin</b> on presented condition <b>onCondition</b>.
     * <br><u>Example</u>: join("INNER", "user_types t", "u.user_type_id=t.id")
     *
     * @param joinType    one of: LEFT OUTER, RIGHT OUTER, INNER.
     * @param tableToJoin table name and preferable alias of table (alias is delimited from table name with space).
     * @param onCondition boolean expression on condition to link tables.
     * @return reference to this query itself.
     */
    public MS_SQLQuery join(String joinType, String tableToJoin, String onCondition) {
        joins.add(String.format(" %s JOIN %s ON (%s)", joinType, tableToJoin, onCondition));
        return this;
    }

    @Override
    public MS_SQLQuery resetContent() {
        super.resetContent();
        fields.clear();
        fieldValues.clear();
        joins.clear();
        tableName = "";
        whereClause = "";
        orderByClause = "";
        anythingElseClause = "";
        ascOrDesc = "";
        operation = 0;
        return this;
    }

    //*** PRIVATE METHODS ***

    private void addListToLineBuilder(MS_StringList list, MS_LineBuilder lb) {
        String str;
        if (list.size() > 1) {
            for (int i = 0; i < list.size() - 1; i++) { //everything but last element
                str = list.get(i);
                lb.append(str);
                lb.append(", ");
            }
            lb.append(list.get(list.size() - 1)); //appending last element without separator
        } else if (list.size() == 1) { //only last element and no delimiters at all
            lb.append(list.get(list.size() - 1));
        }
    }

    /**
     * Appends line builder with part common for both insert and replace statements.
     *
     * @param lb line builder.
     */
    private void addInsertReplacePartToLineBuilder(MS_LineBuilder lb) {
        if (StringUtils.isEmpty(tableName))
            throw new MS_BadSQLSyntaxException(TABLE_NAME_IS_NOT_PROVIDED);
        lb.append(tableName);
        if (fieldValues.size() > 0) {
            if (fields.size() != fieldValues.size())
                throw new MS_BadSQLSyntaxException(FIELD_AND_VALUE_COUNT_MUST_BE_EQUAL);
            lb.append("(");
            addListToLineBuilder(fields, lb);
            lb.append(")");
        }
        lb.append(" ");
        lb.append(INSERT_REPLACE_2);
        if (fieldValues.size() == 0)
            addListToLineBuilder(fields, lb);
        else
            addListToLineBuilder(fieldValues, lb);
        lb.append(INSERT_REPLACE_3);
    }

    private void addFieldsAndValuesToLineBuilderForUpdate(MS_LineBuilder lb) {
        if (fields.size() != fieldValues.size())
            throw new MS_BadSQLSyntaxException(FIELD_AND_VALUE_COUNT_MUST_BE_EQUAL);
        if (fields.size() > 1) {
            for (int i = 0; i < fields.size() - 1; i++) { //everything but last element
                lb.append(fields.get(i));
                lb.append(" = ");
                lb.append(fieldValues.get(i));
                lb.append(", ");
            }
            //appending last element without separator
            lb.append(fields.get(fields.size() - 1));
            lb.append(" = ");
            lb.append(fieldValues.get(fieldValues.size() - 1));
        } else if (fields.size() == 1) { //only last element and no delimiters at all
            lb.append(fields.get(0));
            lb.append(" = ");
            lb.append(fieldValues.get(0));
        }
    }
}