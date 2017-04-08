package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.utilities.MS_AbstractCompositeText;
import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * SQL query to operate with data. Currently only SELECT, INSERT, REPLACE, UPDATE and DELETE statements are supported.
 * <p><u>Warning</u>: Use only one of those 5 statement types at time because thanks to <b>MS_AbstractCompositeText</b> toString method forms text just once.
 *
 * @author eMeS
 * @version 1.3.
 */
public class MS_SQLQuery extends MS_AbstractCompositeText {
    //Supported SQL statement examples:
    //"select * from users where name = ?"
    //insert into notes values(null, ?, null, ?, ?, curdate(), curdate(), ?, ?)
    //replace into canvases values(null, ?, ?)
    //update notes set modified = curdate() where id = ?
    //delete from notes where id = ?

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

    private MS_List<String> fields = new MS_List<>();
    private MS_List<String> joins = new MS_List<>();
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
                addFieldsToLineBuilder(lb);
                lb.append(SELECT2);
                lb.append(tableName);
                break;
            case CASE_INSERT:
                lb.append(INSERT);
                lb.append(tableName).append(" ");
                lb.append(INSERT_REPLACE_2);
                addFieldsToLineBuilder(lb);
                lb.append(INSERT_REPLACE_3);
                break;
            case CASE_REPLACE:
                lb.append(REPLACE);
                if (!tableName.isEmpty())
                    lb.append(tableName).append(" ");
                lb.append(INSERT_REPLACE_2);
                addFieldsToLineBuilder(lb);
                lb.append(INSERT_REPLACE_3);
                break;
            case CASE_UPDATE:
                lb.append(UPDATE1);
                lb.append(tableName).append(" ");
                lb.append(UPDATE2);
                addFieldsToLineBuilder(lb);
                break;
            case CASE_DELETE:
                lb.append(DELETE);
                lb.append(tableName);
                break;
            default:
                break;
        }

        //all following clauses are separated with spaces from beginning of clause
        joins.forEach(lb::append);//FIXME looping through all elements without lambda
        if (!whereClause.isEmpty())
            lb.append(whereClause);
        if (!orderByClause.isEmpty())
            lb.append(orderByClause).append(ascOrDesc);
        if (!anythingElseClause.isEmpty())
            lb.append(anythingElseClause);
        lb.append(";"); //every query ends with semicolon
        return lb;
    }

    private void addFieldsToLineBuilder(MS_LineBuilder lb) {
        String str;
        if (fields.size() > 1) {
            for (int i = 0; i < fields.size() - 1; i++) { //everything but last element
                str = fields.get(i);
                lb.append(str);
                lb.append(", ");
            }
            lb.append(fields.get(fields.size() - 1)); //appending last element without separator
        } else if (fields.size() == 1) { //only last element and no delimiters at all
            lb.append(fields.get(fields.size() - 1));
        }
    }

    /**
     * Indicates that this will be SELECT query.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery selectFrom() {
        operation = 1;
        return this;
    }

    /**
     * Indicates that this will be INSERT query.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery insertInto() {
        operation = 2;
        return this;
    }

    /**
     * Indicates that this will be REPLACE query.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery replaceInto() {
        operation = 3;
        return this;
    }

    /**
     * Indicates that this will be UPDATE query.
     *
     * @return reference to this query itself.
     */
    public MS_SQLQuery update() {
        operation = 4;
        return this;
    }

    /**
     * Indicates that this will be DELETE query.
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
        this.fields.add(fieldName);
        return this;
    }

    /**
     * For update statement set new value <b>newValue</b> for field with name <b>fieldName</b>.
     * <br><u>Example</u>: setNewValue("name", "Maris")
     *
     * @param fieldName name of table field.
     * @param newValue  value of field to update.
     * @return reference to this query itself.
     */
    public MS_SQLQuery setNewValue(String fieldName, String newValue) {
        this.fields.add(fieldName + " = " + newValue);
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
        joins.clear();
        tableName = "";
        whereClause = "";
        orderByClause = "";
        anythingElseClause = "";
        ascOrDesc = "";
        operation = 0;
        return this;
    }
}
