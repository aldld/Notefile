// Notes.java
// Class handling the notes database table
// Takes care of CRUD for the actual notes

package notefile.db;

import java.util.List;
import java.util.ArrayList;

import notefile.db.DB;
import notefile.data.Note;

public class Notes {
	
	// Database handling class
	DB db;
	
	// Connect to the database.
	// Create notes database table if it doesn't already exist.
	public Notes(String databasePath) throws Exception {
		this.db = new DB(databasePath);
		
		// If the notes table doesn't exist, create it.
		this.db.stat.executeUpdate(
				"CREATE TABLE IF NOT EXISTS notes (" +
					"id INTEGER PRIMARY KEY," +
					"note TEXT," +
					"category INTEGER," +
					"tags TEXT," +
					"date INTEGER" +
				")");
	}
	
	// Insert a new record
	public void addNote(String note, int category, String tags) throws Exception {
		long now = System.currentTimeMillis() / 1000;
		
		this.db.prep = this.db.conn.prepareStatement(
				"INSERT INTO notes (note, category, tags, date) VALUES (" +
				"?, ?, ?, ?)");
		
		this.db.prep.setString(1, note);
		this.db.prep.setInt(2, category);
		this.db.prep.setString(3, tags);
		this.db.prep.setLong(4, now);
		
		this.db.conn.setAutoCommit(false);
		this.db.prep.executeUpdate();
		this.db.conn.setAutoCommit(true);
		
		//this.db.prep.clearBatch();
		//this.db.prep.clearParameters();
	}
	
	// Update a record
	public void updateNote(int id, String note, String tags) throws Exception {
		this.db.prep = this.db.conn.prepareStatement(
				"UPDATE notes SET note=?, tags=? WHERE id=?");
		
		this.db.prep.setString(1, note);
		this.db.prep.setString(2, tags);
		this.db.prep.setInt(3, id);
		
		this.db.conn.setAutoCommit(false);
		this.db.prep.executeUpdate();
		this.db.conn.setAutoCommit(true);
		
		//this.db.prep.clearBatch();
		//this.db.prep.clearParameters();
	}
	
	// Delete a record
	public void deleteNote(int id) throws Exception {
		this.db.prep = this.db.conn.prepareStatement(
				"DELETE FROM notes WHERE id=?");
		
		this.db.prep.setInt(1, id);
		
		this.db.conn.setAutoCommit(false);
		this.db.prep.executeUpdate();
		this.db.conn.setAutoCommit(true);
	}
	
	// Get a single record by ID
	public Note getRecord(int id) throws Exception {
		Note note = new Note();
		
		this.db.prep = this.db.conn.prepareStatement(
				"SELECT note, category, tags, date FROM notes WHERE id=?");
		
		this.db.prep.setInt(1, id);
		
		this.db.rs = this.db.prep.executeQuery();
		
		if (this.db.rs.next()) {
			note.id = id;
			note.note = this.db.rs.getString("note");
			note.category = this.db.rs.getInt("category");
			note.date = this.db.rs.getLong("date");
			
			// Parse tags
			note.parseTags(this.db.rs.getString("tags"));
			
			this.db.rs.close();
			
			return note;
		}
		
		this.db.rs.close();
		
		// No row found returns null
		return null;
	}
	
	// Get all records
	public List<Note> getAll() throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		this.db.rs = this.db.stat.executeQuery(
				"SELECT id, note, category, tags, date FROM notes");
		
		// Temporary HashMap for storing individual rows
		while (this.db.rs.next()) {
			Note note = new Note();
			
			note.id = this.db.rs.getInt("id");
			note.note = this.db.rs.getString("note");
			note.category = this.db.rs.getInt("category");
			note.date = this.db.rs.getLong("date");
			
			// Parse tags
			note.parseTags(this.db.rs.getString("tags"));
			
			notes.add(note);
		}
		
		this.db.rs.close();
		this.db.stat.close();
		
		// No row found returns an empty List
		return notes;
	}
	
	// Get records by category
	public List<Note> getFromCategory(int category) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		this.db.prep = this.db.conn.prepareStatement(
				"SELECT id, note, tags, date FROM notes WHERE category=?");
		
		this.db.prep.setInt(1, category);
		
		this.db.rs = this.db.prep.executeQuery();
		
		while (this.db.rs.next()) {
			Note note = new Note();
			
			note.id = this.db.rs.getInt("id");
			note.note = this.db.rs.getString("note");
			note.category = category;
			note.date = this.db.rs.getLong("date");
			
			// Parse tags
			note.parseTags(this.db.rs.getString("tags"));
			
			notes.add(note);
		}
		
		this.db.rs.close();
		
		return notes;
		
	}
	
	// Get records by tag (in category)
	public List<Note> getByTag(int category, String tag) throws Exception {
		List<Note> notes = new ArrayList<Note>();
		
		this.db.prep = this.db.conn.prepareStatement(
				"SELECT id, note, tags, date FROM notes WHERE category=?" +
				"AND tags LIKE (SELECT '%' || ? || '%')");
		
		this.db.prep.setInt(1, category);
		this.db.prep.setString(2, tag);
		
		this.db.rs = this.db.prep.executeQuery();
		
		while (this.db.rs.next()) {
			Note note = new Note();
			
			note.id = this.db.rs.getInt("id");
			note.note = this.db.rs.getString("note");
			note.category = category;
			note.date = this.db.rs.getLong("date");
			
			// Parse tags
			note.parseTags(this.db.rs.getString("tags"));
			
			notes.add(note);
		}
		
		this.db.rs.close();
		
		return notes;
	}
	
	// Check if a record exists
	public boolean noteExists(int id) throws Exception {
		this.db.rs = this.db.stat.executeQuery(
				"SELECT 1 FROM notes WHERE id=" + id);
		
		if (this.db.rs.next()) {
			
			this.db.rs.close();
			this.db.stat.close();
			
			return true;
		}
		
		this.db.rs.close();
		this.db.stat.close();
		
		return false;
	}
	
}
