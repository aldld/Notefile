// DB.java
// Handles SQLite JDBC driver, databases

package notefile.db;

import java.sql.*;

public class DB {
	
	// SQLite3 database connection info
	public Connection conn;
	public Statement stat;
	public PreparedStatement prep;
	public ResultSet rs;
	
	public DB(String databasePath) throws Exception {
		Class.forName("org.sqlite.JDBC");
		
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
		this.stat = conn.createStatement();
	}
	
}
