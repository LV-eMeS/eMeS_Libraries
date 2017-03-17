package lv.emes.libraries.communication.db;

import java.sql.ResultSet;

/**
 * This class represents an entity for JDBC database.
 * It can do most common operations with DB table record like SELECT, INSERT, UPDATE and DELETE.
 * It also can be used to select many records of same type from DB.
 * <p>Public methods:
 * <ul>
 * <li>selectFromDB</li>
 * <li>insertIntoDB</li>
 * <li>updateTheDB</li>
 * <li>deleteFromDB</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class MS_Entity extends MS_TableUniqueRecord {
    private MS_JDBCDatabase db;

    /**
     * This constructor always should be overridden by descendants by calling <code>super()</code> in order to use <b>newTable</b> and <b>newTableWithUniqueKey</b>.
     *
     * @param rs result set of table rows retrieved from database.
     */
    public MS_Entity(ResultSet rs) {
        super(rs);
    }

    /**
     * Constructs new entity in order to use it for easy SELECT, INSERT, UPDATE and DELETE operations.
     * @param db already configured, connected and ready for use database.
     */
    public MS_Entity(MS_JDBCDatabase db) {
        super();
        this.db = db;
    }

    /**
     * Does default SELECT statement and fills class variables with values from database.
     * In case of success <b>isFilled()</b> returns true.
     * @throws NullPointerException if default SQL query is null.
     */
    public final void selectFromDB() {
        MS_SQLQuery sql = prepareSelectSQL();
        if (sql == null) throw new NullPointerException("SQL query cannot be null.");
        IFuncGetReadyParametrizedQuery parameters = prepareSelectSQLParams();
        readFromDB(sql, parameters);
    }

    /**
     * Does default INSERT statement in order to add this record to database.
     * @throws NullPointerException if default SQL query is null.
     * @return true if record successfully inserted.
     */
    public final boolean insertIntoDB() {
        MS_SQLQuery sql = prepareInsertSQL();
        if (sql == null) throw new NullPointerException("SQL query cannot be null.");
        IFuncGetReadyParametrizedQuery parameters = prepareInsertSQLParams();
        return writeIntoDB(sql, parameters);
    }

    /**
     * Does default UPDATE statement in order to change this record in database.
     * @throws NullPointerException if default SQL query is null.
     * @return true if record successfully inserted.
     */
    public final boolean updateTheDB() {
        MS_SQLQuery sql = prepareUpdateSQL();
        if (sql == null) throw new NullPointerException("SQL query cannot be null.");
        IFuncGetReadyParametrizedQuery parameters = prepareUpdateSQLParams();
        return writeIntoDB(sql, parameters);
    }

    /**
     * Does default DELETE statement in order to remove this record from database.
     * @throws NullPointerException if default SQL query is null.
     * @return true if record successfully inserted.
     */
    public final boolean deleteFromDB() {
        MS_SQLQuery sql = prepareDeleteSQL();
        if (sql == null) throw new NullPointerException("SQL query cannot be null.");
        IFuncGetReadyParametrizedQuery parameters = prepareDeleteSQLParams();
        return writeIntoDB(sql, parameters);
    }

    /**
     * Create new SQL query and fill its body.
     * @return new filled SQL query for SELECT statement.
     */
    protected abstract MS_SQLQuery prepareSelectSQL();

    /**
     * Implements the way, how the prepared query parameter filling is done for SELECT statement.
     * @return new parameter setter (as lambda function) or null if no parameters is required.
     */
    protected abstract IFuncGetReadyParametrizedQuery prepareSelectSQLParams();

    /**
     * Create new SQL query and fill its body.
     * @return new filled SQL query for INSERT statement.
     */
    protected abstract MS_SQLQuery prepareInsertSQL();

    /**
     * Implements the way, how the prepared query parameter filling is done for INSERT statement.
     * @return new parameter setter (as lambda function) or null if no parameters is required.
     */
    protected abstract IFuncGetReadyParametrizedQuery prepareInsertSQLParams();

    /**
     * Create new SQL query and fill its body.
     * @return new filled SQL query for UPDATE statement.
     */
    protected abstract MS_SQLQuery prepareUpdateSQL();

    /**
     * Implements the way, how the prepared query parameter filling is done for UPDATE statement.
     * @return new parameter setter (as lambda function) or null if no parameters is required.
     */
    protected abstract IFuncGetReadyParametrizedQuery prepareUpdateSQLParams();

    /**
     * Create new SQL query and fill its body.
     * @return new filled SQL query for DELETE statement.
     */
    protected abstract MS_SQLQuery prepareDeleteSQL();

    /**
     * Implements the way, how the prepared query parameter filling is done for DELETE statement.
     * @return new parameter setter (as lambda function) or null if no parameters is required.
     */
    protected abstract IFuncGetReadyParametrizedQuery prepareDeleteSQLParams();

    protected final  void readFromDB(MS_SQLQuery sql, IFuncGetReadyParametrizedQuery queryParameterSetter) {
        MS_PreparedSQLQuery query = db.prepareSQLQuery(sql.toString());
        if (queryParameterSetter != null) queryParameterSetter.setParams(query);
        ResultSet rs = db.getQueryResult(query);
        initColumnsFromNextResult(rs);
    }

    protected final boolean writeIntoDB(MS_SQLQuery sql, IFuncGetReadyParametrizedQuery queryParameterSetter) {
        MS_PreparedSQLQuery query = db.prepareSQLQuery(sql.toString());
        if (queryParameterSetter != null) queryParameterSetter.setParams(query);
        return db.commitStatement(query);
    }
}
