// Categories.java
// Class handling the categories database table

package notefile.db;

import notefile.db.DB;

public class Categories {
	
	DB db;
	
	public Categories(String databasePath) throws Exception {
		this.db = new DB(databasePath);
	}
	
}
