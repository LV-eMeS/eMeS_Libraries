package lv.emes.libraries.communication.db;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.utilities.MS_AbstractCompositeText;
import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * SQL query to operate with data. Currently only SELECT, INSERT, REPLACE, UPDATE and DELETE statements are supported.
 * <p><u>Note</u>: Use only one of those 5 statement types at time!
 *
 * @author eMeS
 * @version 1.1.
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
    private String tableName = "";
    private String whereClause = "";
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
        if (!whereClause.isEmpty())
            lb.append(whereClause);
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
     *
     * @param tableName name of table.
     * @return reference to this query itself.
     */
    public MS_SQLQuery table(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Adds where clause to query.
     * <br><u>Note</u>: keyword 'WHERE ' altogether with whitespace is added before <b>conditions</b> automatically.
     *
     * @param conditions all the conditions for query where clause.
     * @return reference to this query itself.
     */
    public MS_SQLQuery where(String conditions) {
        whereClause = " WHERE " + conditions;
        return this;
    }

    /**
     * Adds field to query of chosen type.
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
}
