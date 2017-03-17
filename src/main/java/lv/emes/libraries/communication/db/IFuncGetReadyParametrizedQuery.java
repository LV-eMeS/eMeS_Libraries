package lv.emes.libraries.communication.db;

/**
 * This functional interface is for prepared sql query to set parameters if needed.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncGetReadyParametrizedQuery {
	void setParams(MS_PreparedSQLQuery paramsForQueryToSet);
}