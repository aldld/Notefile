// DB.java
// Handles SQLite JDBC driver, databases

package notefile.db;

import java.sql.*;

public class DB {
	
	// SQLite3 database connection info
	public static String path;
	public static Connection conn;
	public static Statement stat;
	public static PreparedStatement prep;
	public static ResultSet rs;
	
	public static boolean initialized = false;
	
	public static void initDB() throws Exception {
		if (!initialized) {
			Class.forName("org.sqlite.JDBC");
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + path);
			stat = conn.createStatement();
			
			initialized = true;
		}
	}
	
	public static void closeDB() throws Exception {
		if (initialized) {
			rs.close();
			conn.close();
			
			initialized = false;
		}
	}
	
}
