package lv.emes.libraries.communication.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Example {

	public static void main(String[] args) {
		//piesleegshanaas:
		MS_JDBCDatabase db = new MS_MySQLDatabase();
		db.hostname = "emesserver.ddns.net";
		db.dbName = "test";
		db.userName = "java_user";
		db.password = "RvyXG?(?]'4";
		db.port = 3306;
		db.onDBConnectionError = (e) -> {e.printStackTrace();};
		db.onDBStatementError = (e) -> {/*Do nothing here*/};
		try {
			db.connect();
		} catch (ClassNotFoundException e1) {
			System.out.println("ERROR: could not load JDBC");
			e1.printStackTrace();
		}
		
		//vaicaajumi
//		MS_TextFile file = new MS_TextFile("./src/lv/team3/db/scripts/production/CreateDatabase.sql");
//		String sql = "";
//		String tmp = "";
//		while ( (tmp = file.readln()) != null) {
//			sql = sql.concat(tmp + "\n");
//		}
//		System.out.println(sql);
//		PreparedStatement sta = db.executeQuery(sql);
//		db.commitStatement(sta);
//		System.exit(0);
		
		MS_PreparedStatement st = db.prepareQuery("select * from blobs where id=?;");	
		//parametri
		st.setInt(1, 2);
		
		//rezultats
		ResultSet res = db.getQueryResult(st);
		try {
			while(res.next()) {
				System.out.println(res.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//ja insert, delete vai update, tad izmainju saglabasana DB
		//db.commitQuery(st);
	}

}
