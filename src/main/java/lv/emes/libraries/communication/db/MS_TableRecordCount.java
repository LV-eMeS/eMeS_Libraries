package lv.emes.libraries.communication.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A record count returned by result set using <code>select count(*)...</code> query. This query must return count in first column.
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_TableRecordCount implements MS_TableRecord {

    private int count;

    public int getCount() {
        return count;
    }

    @Override
    public void initColumns(ResultSet rs) throws SQLException {
        count = rs.getInt(1);
    }
}
