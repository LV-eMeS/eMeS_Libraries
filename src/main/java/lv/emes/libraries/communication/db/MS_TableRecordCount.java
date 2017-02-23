package lv.emes.libraries.communication.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A record count returned by result set using <code>select count(*)...</code> query. This query must return count in first column.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_TableRecordCount extends MS_TableRecord {
    private int count;

    public int getCount() {
        return count;
    }

    /**
     * @param rs result set of table rows retrieved from database.
     */
    public MS_TableRecordCount(ResultSet rs) {
        super(rs);
    }

    @Override
    protected void initColumns(ResultSet rs) throws SQLException {
        count = rs.getInt(1);
    }
}
