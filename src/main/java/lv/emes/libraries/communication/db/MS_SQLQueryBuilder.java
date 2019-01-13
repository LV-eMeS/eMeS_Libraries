package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_LineBuilder;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.Objects;

/**
 * SQL query builder to build SQL syntactic statements and set parameter values for them.
 * This class is mainly takes care of quick and easy to use SELECT, INSERT/REPLACE, UPDATE, DELETE statement
 * building. Other constructions are possible to build by using this builder via {@link MS_SQLQueryBuilder#add(String)}
 * or {@link MS_SQLQueryBuilder#append(String)} methods.
 * <p><u>Example 1</u>:
 * <pre><code>
 * public static void main(String[] args) {
 *     String tableName = "users";
 *     MS_SQLQueryBuilder sql = new MS_SQLQueryBuilder().select().all().from().table(tableName);
 *     System.out.println(sql.buildAndToString()); //prints: SELECT * FROM users;
 * }
 * </code></pre>
 * <u>Example 2</u>:
 * <pre><code>
 * public static void main(String[] args) {
 *     String tableName = "users";
 *     MS_SQLQueryBuilder sql = new MS_SQLQueryBuilder().update().table(tableName).set().value("age", "?");
 *     System.out.println(sql.buildAndToString()); //prints: UPDATE users SET age = ?;
 * }
 * </code></pre>
 * <u>Example 3</u>:
 * <pre><code>
 * public static void main(String[] args) {
 *     String tableName = "users";
 *     MS_SQLQueryBuilder sql = new MS_SQLQueryBuilder()
 *         .insertInto(tableName, new MS_StringList("id,counter,text_field3", ','))
 *         .values(new MS_StringList("null#123#check"));
 *     System.out.println(sql.buildAndToString()); //prints: INSERT INTO users(id, counter, text_field3)
 *     //and in new line: VALUES(null, 123, check);
 * }
 * </code></pre>
 * <u>Example 4</u>:
 * <pre><code>
 * public static void main(String[] args) {
 *     String tableName = "users";
 *     MS_SQLQueryBuilder sql = new MS_SQLQueryBuilder().deleteFrom().table(tableName).where().condition("age &gt; 104");
 *     System.out.println(sql.buildAndToString()); //prints: DELETE FROM users
 *     //and in new line: WHERE age &gt; 104;
 * }
 * </code></pre>
 *
 * @author eMeS
 * @version 2.4.
 */
public class MS_SQLQueryBuilder extends MS_LineBuilder {

    public static final String _QPARAM = "?";
    public static final String _SP = " "; //space
    public static final String _LB = "("; //left bracket
    public static final String _RB = ")"; //right bracket
    public static final String _COMMA = ",";
    public static final String _EQUALS = " = ";
    public static final String _ALL = "*";

    /**
     * No arg constructor.
     */
    public MS_SQLQueryBuilder() {
    }

    /**
     * Creates new instance of {@link MS_SQLQueryBuilder}, which copies from <b>otherBuilder</b> all till this moment
     * built query constructions.
     *
     * @param otherBuilder another SQL builder.
     * @throws NullPointerException if <b>otherBuilder</b> is null.
     */
    public MS_SQLQueryBuilder(MS_SQLQueryBuilder otherBuilder) throws NullPointerException {
        appendInternal(otherBuilder.getStringBuilder().toString());
    }

    /**
     * Extracts parameter value to be inserted directly as field value in
     * {@link MS_SQLQueryBuilder#value(String)} method's parameter
     * or in {@link MS_SQLQueryBuilder#value(String, String)}
     * method's second parameter.
     * Single quotation marks (like this: <b>'</b>) are escaped with extra single quotation mark (like this: <b>''</b>).
     *
     * @param parameterValue parameter value without any quotation.
     * @return parameter value quoted with single quote.
     */
    public static String toSQLText(String parameterValue) {
        return MS_StringUtils.toQuotedText(parameterValue);
    }

    private enum Oper {
        SELECT, INSERT_INTO, REPLACE_INTO, UPDATE, SET, FROM, DELETE_FROM,
        FIELD, VALUE, TABLE, WHERE, CONDITION, ORDER_BY, JOIN,
        VALUES, AND, OR, IN, UNION, BRACKET_OPENING, CUSTOM;

        @Override
        public String toString() {
            return MS_StringUtils.replaceInString(name(), "_", " ");
        }
    }

    private Oper previousOperation = null;
    private byte fieldOperationCalledTimes = 0;

    /**
     * Appends query with "SELECT".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder select() {
        return beginOperation(true).appendInternal(Oper.SELECT.toString()).endOperation(Oper.SELECT);
    }

    /**
     * Appends query with "INSERT INTO".
     *
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#insertInto(String)
     * @see MS_SQLQueryBuilder#insertInto(String, MS_StringList)
     */
    public MS_SQLQueryBuilder insertInto() {
        return beginOperation(true).appendInternal(Oper.INSERT_INTO.toString()).endOperation(Oper.INSERT_INTO);
    }

    /**
     * Appends query with "INSERT INTO <b>tableName</b>".
     *
     * @param tableName table name to append to query.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#insertInto()
     * @see MS_SQLQueryBuilder#insertInto(String, MS_StringList)
     */
    public MS_SQLQueryBuilder insertInto(String tableName) {
        return insertInto().table(tableName);
    }

    /**
     * Appends query with "INSERT INTO <b>tableName</b>(<b>fieldNames.1</b>, <b>fieldNames.2</b>...)".
     * Parameter <b>fieldNames</b> should be a string list containing 1 or more field names.
     * No null checks or list length checks are performed here, so make sure list is correct before calling this method!
     * <p><u>Example of use</u>:
     * <code>sqlBuilder.insertInto("products", new MS_StringList("id,name,price", ','))</code>
     *
     * @param tableName  table name to append to query.
     * @param fieldNames eMeS string list of field names.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#insertInto()
     * @see MS_SQLQueryBuilder#insertInto(String)
     */
    public MS_SQLQueryBuilder insertInto(String tableName, MS_StringList fieldNames) {
        insertInto(tableName).bracketOpening();
        fieldNames.forEachItem((field, i) -> field(field));
        return bracketClosing();
    }

    /**
     * Appends query with "REPLACE INTO".
     *
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#replaceInto(String)
     * @see MS_SQLQueryBuilder#replaceInto(String, MS_StringList)
     */
    public MS_SQLQueryBuilder replaceInto() {
        return beginOperation(true).appendInternal(Oper.REPLACE_INTO.toString()).endOperation(Oper.REPLACE_INTO);
    }

    /**
     * Appends query with "REPLACE INTO <b>tableName</b>".
     *
     * @param tableName table name to append to query.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#replaceInto()
     * @see MS_SQLQueryBuilder#replaceInto(String, MS_StringList)
     */
    public MS_SQLQueryBuilder replaceInto(String tableName) {
        return replaceInto().table(tableName);
    }

    /**
     * Appends query with "REPLACE INTO <b>tableName</b>(<b>fieldNames.1</b>, <b>fieldNames.2</b>...)".
     * Parameter <b>fieldNames</b> should be a string list containing 1 or more field names.
     * No null checks or list length checks are performed here, so make sure list is correct before calling this method!
     * <p><u>Example of use</u>:
     * <code>sqlBuilder.replaceInto("products", new MS_StringList("id,name,price", ','))</code>
     *
     * @param tableName  table name to append to query.
     * @param fieldNames eMeS string list of field names.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#replaceInto()
     * @see MS_SQLQueryBuilder#replaceInto(String)
     */
    public MS_SQLQueryBuilder replaceInto(String tableName, MS_StringList fieldNames) {
        replaceInto(tableName).bracketOpening();
        fieldNames.forEachItem((field, i) -> field(field));
        return bracketClosing();
    }

    /**
     * Appends query with "UPDATE".
     *
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#update(String)
     */
    public MS_SQLQueryBuilder update() {
        return beginOperation(true).appendInternal(Oper.UPDATE.toString()).endOperation(Oper.UPDATE);
    }

    /**
     * Appends query with "UPDATE <b>tableName</b>".
     *
     * @param tableName table name to append to query.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#update()
     */
    public MS_SQLQueryBuilder update(String tableName) {
        return update().table(tableName).set();
    }

    /**
     * Appends query with "DELETE FROM".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder deleteFrom() {
        return beginOperation(true).appendInternal(Oper.DELETE_FROM.toString()).endOperation(Oper.DELETE_FROM);
    }

    //other operations

    /**
     * Appends query with "*".
     * Should be used in SELECT constructions after calling {@link MS_SQLQueryBuilder#select()}.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder all() {
        return beginOperation(false).appendInternal(_ALL).endOperation(Oper.FIELD);
    }

    /**
     * Appends query with field name <b>fieldName</b> and if previous call was <b>field(String fieldName)</b> as well,
     * appends query with comma ", " before this <b>fieldName</b>.
     * Also null check is performed and, in case <b>fieldName</b> = <code>null</code>, query is appended with "null" instead.
     *
     * @param fieldName field name to append to query.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder field(String fieldName) {
        if (fieldName == null) fieldName = "null";
        if (previousOperation == Oper.FIELD) this.appendInternal(_COMMA);
        return beginOperation(false).appendInternal(fieldName).endOperation(Oper.FIELD);
    }

    /**
     * Appends query with "VALUES".
     *
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#values(MS_StringList)
     */
    public MS_SQLQueryBuilder values() {
        return beginOperation(true).appendInternal(Oper.VALUES.toString()).endOperation(Oper.VALUES);
    }

    /**
     * Appends query with "VALUES (<b>valueList.1</b>, <b>valueList.2</b>...)".
     * Parameter <b>valueList</b> should be a string list containing 1 or more field values.
     * No null checks or list length checks are performed here, so make sure list is correct before calling this method!
     * <p><u>Example of use</u>:
     * <code>sqlBuilder.values(new MS_StringList("null,orange,0.75", ','))</code>
     *
     * @param valueList eMeS string list of field values.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#values()
     */
    public MS_SQLQueryBuilder values(MS_StringList valueList) {
        this.values().bracketOpening();
        valueList.forEachItem((fieldValue, i) -> value(fieldValue));
        return bracketClosing();
    }

    /**
     * Appends query with field value <b>value</b>.
     * Should be used in INSERT INTO or REPLACE INTO constructions after calling {@link MS_SQLQueryBuilder#values()}
     * and {@link MS_SQLQueryBuilder#bracketOpening()}.
     *
     * @param value value of field to be inserted or replaced.
     * @return reference to this query itself.
     * @see MS_SQLQueryBuilder#field(String)
     */
    public MS_SQLQueryBuilder value(String value) {
        return field(value);
    }

    /**
     * Appends query with field name <b>fieldName</b> and value <b>fieldValue</b> like "<b>fieldName</b> = <b>fieldValue</b>".
     * Should be used in UPDATE ... SET constructions after calling {@link MS_SQLQueryBuilder#set()}.
     *
     * @param fieldName  name of field which value will be updated.
     * @param fieldValue new value for field to be updated.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder value(String fieldName, String fieldValue) {
        if (fieldName == null) fieldName = "null";
        if (fieldValue == null) fieldValue = "null";
        if (previousOperation == Oper.VALUE) this.appendInternal(_COMMA);
        return beginOperation(false)
                .appendInternal(fieldName).appendInternal(_EQUALS).appendInternal(fieldValue)
                .endOperation(Oper.VALUE);
    }

    /**
     * Appends query with table name <b>tableName</b>.
     *
     * @param tableName table name.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder table(String tableName) {
        return beginOperation(false).appendInternal(tableName).endOperation(Oper.TABLE);
    }

    /**
     * Appends query with "SET".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder set() {
        return beginOperation(false).appendInternal(Oper.SET.toString()).endOperation(Oper.SET);
    }

    /**
     * Appends query with "FROM".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder from() {
        return beginOperation(previousOperation == Oper.FIELD && fieldOperationCalledTimes > 3)
                .appendInternal(Oper.FROM.toString()).endOperation(Oper.FROM);
    }

    /**
     * Appends query with "FROM <b>tableName</b>".
     *
     * @param tableName table name.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder from(String tableName) {
        return from().table(tableName);
    }

    /**
     * Appends query with "(".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder bracketOpening() {
        return appendInternal(_LB).endOperation(Oper.BRACKET_OPENING);
    }

    /**
     * Appends query with ")".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder bracketClosing() {
        return appendInternal(_RB).endOperation(Oper.CUSTOM);
    }

    /**
     * Appends query with "WHERE".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder where() {
        return beginOperation(true).appendInternal(Oper.WHERE.toString()).endOperation(Oper.WHERE);
    }

    /**
     * Appends query with what ever condition <b>cond</b> is expected after WHERE clause.
     *
     * @param cond expression that should be used in WHERE expression possibly in combination with
     *             {@link MS_SQLQueryBuilder#and()} and/or {@link MS_SQLQueryBuilder#or()}
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder condition(String cond) {
        return beginOperation(false).appendInternal(cond).endOperation(Oper.CONDITION);
    }

    /**
     * Appends query with "UNION".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder union() {
        return beginOperation(true).appendInternal(Oper.UNION.toString()).endOperation(Oper.UNION);
    }

    /**
     * Appends query with "AND".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder and() {
        return beginOperation(false).appendInternal(Oper.AND.toString()).endOperation(Oper.AND);
    }

    /**
     * Appends query with "OR".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder or() {
        return beginOperation(false).appendInternal(Oper.OR.toString()).endOperation(Oper.OR);
    }

    /**
     * Appends query with "IN('String1', 'String2'...)".
     *
     * @param valuesAsStrings values of String type to be inserted comma-separated and enclosed by
     *                        apostrophes inside braces of IN clause.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder in(String... valuesAsStrings) {
        this.beginOperation(false).appendInternal(Oper.IN.toString()).appendInternal(_SP).bracketOpening();
        int i = 0;
        for (String value : valuesAsStrings) {
            this.appendInternal("'" + value + "'");
            if (++i < valuesAsStrings.length) this.appendInternal(_COMMA);
        }
        return this.bracketClosing().endOperation(Oper.IN);
    }

    /**
     * Appends query with "IN(ObjectValue1, ObjectValue2...)". Should be used for any object that is applicable without
     * adding apostrophes.
     *
     * @param valuesAsObjects values of any type ({@link Object#toString} is called) to be inserted
     *                        comma-separated inside braces of IN clause.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder in(Object... valuesAsObjects) {
        this.beginOperation(false).appendInternal(Oper.IN.toString()).appendInternal(_SP).bracketOpening();
        int i = 0;
        for (Object value : valuesAsObjects) {
            this.appendInternal(Objects.toString(value));
            if (++i < valuesAsObjects.length) this.appendInternal(_COMMA);
        }
        return this.bracketClosing().endOperation(Oper.IN);
    }

    /**
     * Appends query with "ORDER BY".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder orderBy() {
        return beginOperation(true).appendInternal(Oper.ORDER_BY.toString()).endOperation(Oper.ORDER_BY);
    }

    /**
     * Appends query with "ORDER BY <b>conditions</b>".
     *
     * @param conditions usually some single or comma delimited field name or expression collection.
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder orderBy(String conditions) {
        orderBy();
        return beginOperation(false).appendInternal(conditions).endOperation(Oper.CONDITION);
    }

    /**
     * Appends query with "ASC".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder ascending() {
        return beginOperation(false).appendInternal("ASC").endOperation(Oper.CUSTOM);
    }

    /**
     * Appends query with "DESC".
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder descending() {
        return beginOperation(false).appendInternal("DESC").endOperation(Oper.CUSTOM);
    }

    /**
     * Adds join clause of type <b>joinType</b> with table <b>tableToJoin</b> on presented condition <b>onCondition</b>.
     * <br><u>Example</u>: join(MS_JoinTypeEnum.LEFT, "user_types t", "u.user_type_id=t.id")
     *
     * @param joinType    one of supported join types.
     * @param tableToJoin table name and preferable alias of table (alias is delimited from table name with space).
     * @param onCondition boolean expression on condition to link tables.
     * @return reference to this query itself.
     * @see MS_JoinTypeEnum
     */
    public MS_SQLQueryBuilder join(MS_JoinTypeEnum joinType, String tableToJoin, String onCondition) {
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
    public MS_SQLQueryBuilder join(String joinType, String tableToJoin, String onCondition) {
        String joinText = String.format("%s JOIN %s ON (%s)", joinType, tableToJoin, onCondition);
        return beginOperation(true).appendInternal(joinText).endOperation(Oper.JOIN);
    }

    /**
     * In order to finalize and build this SQL query appends semicolon at the end of statement.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQueryBuilder build() {
        return this.appendInternal(";");
    }

    /**
     * Appends semicolon at the end of statement and returns prepared SQL query string.
     *
     * @return SQL string representing all the parts set for this query in building process.
     */
    public String buildAndToString() {
        return this.build().toString();
    }

    /**
     * This method basically clones this SQL builder.
     *
     * @return new instance of SQL builder with same SQL constructions as this instance already has.
     */
    public MS_SQLQueryBuilder makeCopy() {
        return new MS_SQLQueryBuilder(this);
    }

    @Override
    public MS_SQLQueryBuilder append(String str) {
        return beginOperation(false).appendInternal(str).endOperation(Oper.CUSTOM);
    }

    //*** PRIVATE METHODS ***

    private MS_SQLQueryBuilder beginOperation(boolean newLineOperation) {
        if (previousOperation != null) {
            if (newLineOperation)
                this.appendInternal(MS_StringUtils._LINE_FEED);
            else if (previousOperation != Oper.BRACKET_OPENING)
                this.appendInternal(_SP);
        }
        return this;
    }

    /**
     * Saves passed operation as previous executed operation.
     * @param operationId operation.
     * @return reference to this query itself.
     */
    private MS_SQLQueryBuilder endOperation(Oper operationId) {
        if (Oper.FIELD.equals(operationId)) {
            fieldOperationCalledTimes++;
        } else {
            fieldOperationCalledTimes = 0;
        }
        previousOperation = operationId;
        return this;
    }

    private MS_SQLQueryBuilder appendInternal(String str) {
        super.append(str);
        return this;
    }
}
