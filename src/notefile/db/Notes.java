// Notes.java
// Class handling the notes database table
// Takes care of CRUD for the actual notes

package notefile.db;

import java.util.List;
import java.util.ArrayList;

import notefile.db.DB;
import notefile.data.Note;

public class Notes {
	
	// Connect to the database.
	// Create notes database table if it doesn't already exist.
	public Notes() throws Exception {
		//DB.path = databasePath;
		if (!DB.initialized) {
			DB.initDB();
		}
		
		// If the notes table doesn't exist, create it.
		DB.stat.executeUpdate(
				"CREATE TABLE IF NOT EXISTS notes (" +
					"id INTEGER PRIMARY KEY," +
					"note TEXT," +
					"category INTEGER," +
					"tags TEXT," +
					"date INTEGER" +
				")");
	}
	
	// Check if the database contains a table called "notes"
	public boolean notesTableExists() throws Exception {
		DB.rs = DB.stat.executeQuery(
				"SELECT name FROM sqlite_master WHERE type='table' AND name='notes';");
		
		if (DB.rs.next()) {
			DB.rs.close();
			DB.stat.close();
			
			return true;
		}
		
		DB.rs.close();
		DB.stat.close();
		
		return false;
	}
	
	// Insert a new record
	// Returns ID of added note
	public int addNote(String note, int category, String tags) throws Exception {
		long now = System.currentTimeMillis() / 1000;
		
		DB.prep = DB.conn.prepareStatement(
				"INSERT INTO notes (note, category, tags, date) VALUES (" +
				"?, ?, ?, ?)");
		
		DB.prep.setString(1, note);
		DB.prep.setInt(2, category);
		DB.prep.setString(3, tags);
		DB.prep.setLong(4, now);
		
		DB.conn.setAutoCommit(false);
		DB.prep.executeUpdate();
		DB.conn.setAutoCommit(true);
		
		DB.rs = DB.prep.getGeneratedKeys();
		
		// Get the ID of the inserted row
		if (DB.rs.next()) {
			int id = DB.rs.getInt(1);
			
			DB.rs.close();
			return id;
		}
		DB.rs.close();
		
		return 0;
	}
	
	// Update a record
	public void updateNote(int id, String note, String tags) throws Exception {
		DB.prep = DB.conn.prepareStatement(
				"UPDATE notes SET note=?, tags=? WHERE id=?");
		
		DB.prep.setString(1, note);
		DB.prep.setString(2, tags);
		DB.prep.setInt(3, id);
		
		DB.conn.setAutoCommit(false);
		DB.prep.executeUpdate();
		DB.conn.setAutoCommit(true);
		
		//DB.prep.clearBatch();
		//DB.prep.clearParameters();
	}
	
	// Delete a record
	public void deleteNote(int id) throws Exception {
		DB.prep = DB.conn.prepareStatement(
				"DELETE FROM notes WHERE id=?");
		
		DB.prep.setInt(1, id);
		
		DB.conn.setAutoCommit(false);
		DB.prep.executeUpdate();
		DB.conn.setAutoCommit(true);
	}
	
	// Get a single record by ID
	public Note getRecord(int id) throws Exception {
		Note note = new Note();
		
		DB.prep = DB.conn.prepareStatement(
				"SELECT note, category, tags, date FROM notes WHERE id=?");
		
		DB.prep.setInt(1, id);
		
		DB.rs = DB.prep.executeQuery();
		
		if (DB.rs.next()) {
			note.id = id;
			note.note = DB.rs.getString("note");
			note.category = DB.rs.getInt("category");
			note.date = DB.rs.getLong("date");
			
			// Parse tags
			note.parseTags(DB.rs.getString("tags"));
			
			DB.rs.close();
			
			return note;
		}
		
		DB.rs.close();
		
		// No row found returns null
		return null;
	}
	
	// Get all records
	public List<Note> getAll() throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		DB.rs = DB.stat.executeQuery(
				"SELECT id, note, category, tags, date FROM notes");
		
		// Temporary HashMap for storing individual rows
		while (DB.rs.next()) {
			Note note = new Note();
			
			note.id = DB.rs.getInt("id");
			note.note = DB.rs.getString("note");
			note.category = DB.rs.getInt("category");
			note.date = DB.rs.getLong("date");
			
			// Parse tags
			note.parseTags(DB.rs.getString("tags"));
			
			notes.add(note);
		}
		
		DB.rs.close();
		DB.stat.close();
		
		// No row found returns an empty List
		return notes;
	}
	
	// Get records by category
	public List<Note> getFromCategory(int category) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		DB.prep = DB.conn.prepareStatement(
				"SELECT id, note, tags, date FROM notes WHERE category=?");
		
		DB.prep.setInt(1, category);
		
		DB.rs = DB.prep.executeQuery();
		
		while (DB.rs.next()) {
			Note note = new Note();
			
			note.id = DB.rs.getInt("id");
			note.note = DB.rs.getString("note");
			note.category = category;
			note.date = DB.rs.getLong("date");
			
			// Parse tags
			note.parseTags(DB.rs.getString("tags"));
			
			notes.add(note);
		}
		
		DB.rs.close();
		
		return notes;
		
	}
	
	// Get records by tag (in category)
	public List<Note> getByTag(int category, String tag) throws Exception {
		String[] tags = { tag };
		return getByTag(category, tags);
	}
	
	public List<Note> getByTag(int category, String[] tags) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		String sql = "SELECT id, note, tags, date FROM notes WHERE category=? ";
		
		for (int i = 0; i < tags.length; i++) {
			if (i == 0) {
				sql = sql + "AND tags LIKE (SELECT '%' || ? || '%') ";
			} else {
				sql = sql + "OR tags LIKE (SELECT '%' || ? || '%') ";
			}
		}
		
		DB.prep = DB.conn.prepareStatement(sql);
		
		DB.prep.setInt(1, category);
		
		int i = 1;
		for (String tag : tags) {
			i++;
			DB.prep.setString(i, tag);
		}
		
		DB.rs = DB.prep.executeQuery();
		
		while (DB.rs.next()) {
			Note note = new Note();
			
			note.id = DB.rs.getInt("id");
			note.note = DB.rs.getString("note");
			note.category = category;
			note.date = DB.rs.getLong("date");
			
			// Parse tags
			note.parseTags(DB.rs.getString("tags"));
			
			notes.add(note);
		}
		
		DB.rs.close();
		
		return notes;
	}
	
	// Check if a record exists
	public boolean noteExists(int id) throws Exception {
		DB.rs = DB.stat.executeQuery(
				"SELECT 1 FROM notes WHERE id=" + id);
		
		if (DB.rs.next()) {
			
			DB.rs.close();
			DB.stat.close();
			
			return true;
		}
		
		DB.rs.close();
		DB.stat.close();
		
		return false;
	}
	
}
