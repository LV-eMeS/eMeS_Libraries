package lv.emes.libraries.communication.db;

import java.sql.SQLException;

/**
 * This functional interface is for database exception handling purposes. 
 * Set it to define behavior of after trying to do some DB operation when connection is lost or other error happens.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnSQLException {
	void doOnError(SQLException exception);
}