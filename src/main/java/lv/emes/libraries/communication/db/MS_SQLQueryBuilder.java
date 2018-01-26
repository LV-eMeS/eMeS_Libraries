package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.MS_LineBuilder;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_StringUtils;

/**
 * SQL query builder to build SQL syntactic statements and set parameter values for them.
 * This class is mainly takes care of quick and easy to use SELECT, INSERT/REPLACE, UPDATE, DELETE statement
 * building. Other constructions are possible to build by using this builder via {@link MS_SQLQueryBuilder#add(String)}
 * or {@link MS_SQLQueryBuilder#append(String)} methods.
 *
 * @author eMeS
 * @version 2.0.
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
        VALUES, AND, OR, UNION, BRACKET_OPENING, CUSTOM;

        @Override
        public String toString() {
            return MS_StringUtils.replaceInString(name(), "_", " ");
        }
    }

    private Oper previousOperation = null;
    private byte fieldOperationCalledTimes = 0;

    public MS_SQLQueryBuilder select() {
        return beginOperation(true).appendInternal(Oper.SELECT.toString()).endOperation(Oper.SELECT);
    }

    public MS_SQLQueryBuilder insertInto() {
        return beginOperation(true).appendInternal(Oper.INSERT_INTO.toString()).endOperation(Oper.INSERT_INTO);
    }

    public MS_SQLQueryBuilder insertInto(String tableName) {
        return insertInto().table(tableName);
    }

    public MS_SQLQueryBuilder insertInto(String tableName, MS_StringList fieldNames) {
        insertInto(tableName).bracketOpening();
        fieldNames.forEachItem((field, i) -> field(field));
        return bracketClosing();
    }

    public MS_SQLQueryBuilder replaceInto() {
        return beginOperation(true).appendInternal(Oper.REPLACE_INTO.toString()).endOperation(Oper.REPLACE_INTO);
    }

    public MS_SQLQueryBuilder replaceInto(String tableName) {
        return replaceInto().table(tableName);
    }

    public MS_SQLQueryBuilder update() {
        return beginOperation(true).appendInternal(Oper.UPDATE.toString()).endOperation(Oper.UPDATE);
    }

    public MS_SQLQueryBuilder update(String tableName) {
        return update().table(tableName).set();
    }

    public MS_SQLQueryBuilder deleteFrom() {
        return beginOperation(true).appendInternal(Oper.DELETE_FROM.toString()).endOperation(Oper.DELETE_FROM);
    }

    //other operations

    public MS_SQLQueryBuilder all() {
        return beginOperation(false).appendInternal(_ALL).endOperation(Oper.FIELD);
    }

    public MS_SQLQueryBuilder field(String fieldName) {
        if (fieldName == null) fieldName = "null";
        if (Oper.FIELD.equals(previousOperation)) this.appendInternal(_COMMA);
        return beginOperation(false).appendInternal(fieldName).endOperation(Oper.FIELD);
    }

    public MS_SQLQueryBuilder value(String fieldName) {
        return field(fieldName);
    }

    public MS_SQLQueryBuilder value(String fieldName, String fieldValue) {
        if (fieldName == null) fieldName = "null";
        if (fieldValue == null) fieldValue = "null";
        if (Oper.VALUE.equals(previousOperation)) this.appendInternal(_COMMA);
        return beginOperation(false)
                .appendInternal(fieldName).appendInternal(_EQUALS).appendInternal(fieldValue)
                .endOperation(Oper.VALUE);
    }

    public MS_SQLQueryBuilder values() {
        return beginOperation(true).appendInternal(Oper.VALUES.toString()).endOperation(Oper.VALUES);
    }

    public MS_SQLQueryBuilder values(MS_StringList valueList) {
        this.values().bracketOpening();
        valueList.forEachItem((fieldValue, i) -> value(fieldValue));
        return bracketClosing();
    }

    public MS_SQLQueryBuilder table(String tableName) {
        return beginOperation(false).appendInternal(tableName).endOperation(Oper.TABLE);
    }

    public MS_SQLQueryBuilder set() {
        return beginOperation(false).appendInternal(Oper.SET.toString()).endOperation(Oper.SET);
    }

    public MS_SQLQueryBuilder from() {
        return beginOperation(Oper.FIELD.equals(previousOperation) && fieldOperationCalledTimes > 3)
                .appendInternal(Oper.FROM.toString()).endOperation(Oper.FROM);
    }

    public MS_SQLQueryBuilder bracketOpening() {
        return appendInternal(_LB).endOperation(Oper.BRACKET_OPENING);
    }

    public MS_SQLQueryBuilder bracketClosing() {
        return appendInternal(_RB).endOperation(Oper.CUSTOM);
    }

    public MS_SQLQueryBuilder where() {
        return beginOperation(true).appendInternal(Oper.WHERE.toString()).endOperation(Oper.WHERE);
    }

    public MS_SQLQueryBuilder condition(String cond) {
        return beginOperation(false).appendInternal(cond).endOperation(Oper.CONDITION);
    }

    public MS_SQLQueryBuilder union() {
        return beginOperation(true).appendInternal(Oper.UNION.toString()).endOperation(Oper.UNION);
    }

    public MS_SQLQueryBuilder and() {
        return beginOperation(false).appendInternal(Oper.AND.toString()).endOperation(Oper.AND);
    }

    public MS_SQLQueryBuilder or() {
        return beginOperation(false).appendInternal(Oper.OR.toString()).endOperation(Oper.OR);
    }

    public MS_SQLQueryBuilder orderBy() {
        return beginOperation(true).appendInternal(Oper.ORDER_BY.toString()).endOperation(Oper.ORDER_BY);
    }

    public MS_SQLQueryBuilder orderBy(String conditions) {
        orderBy();
        return beginOperation(false).appendInternal(conditions).endOperation(Oper.CONDITION);
    }

    public MS_SQLQueryBuilder ascending() {
        return beginOperation(false).appendInternal("ASC").endOperation(Oper.CUSTOM);
    }

    public MS_SQLQueryBuilder descending() {
        return beginOperation(false).appendInternal("DESC").endOperation(Oper.CUSTOM);
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
    public MS_SQLQueryBuilder join(JoinTypeEnum joinType, String tableToJoin, String onCondition) {
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

    public String build() {
        return this.appendInternal(";").toString();
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
            else if (!Oper.BRACKET_OPENING.equals(previousOperation))
                this.appendInternal(_SP);
        }
        return this;
    }

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