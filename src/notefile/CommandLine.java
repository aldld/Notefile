// CommandLine.java
// Class handling Notefile's command-line interface
// Includes standard CLI and interactive environment

package notefile;

import java.io.*;
import java.util.List;
import java.text.SimpleDateFormat;

import notefile.db.Notes;
import notefile.data.Note;

public class CommandLine {
	
	Notes notes;
	BufferedReader br;
	
	public CommandLine(boolean interactive) throws Exception {
		// Get the path to the Notefile database.
		// For now defaults to .notefile.db in the user's home directory
		// TODO: Allow user to set custom database or "notebook"
		String database = System.getProperty("user.home") + "/.notefile.db";
		
		this.notes = new Notes(database);
		this.br = new BufferedReader(new InputStreamReader(System.in));
		
		if (interactive) {
			cmdPrompt();
		}
	}
	
	// Command-line prompt for interactive environment
	public void cmdPrompt() throws Exception {
		String cmd = "";
		
		System.out.println("Notefile is ready. Have fun!");
		
		// Internal scope for displaying the date in the welcome message
		{
			long now = System.currentTimeMillis();
			String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now);
			
			System.out.println("It is currently " + date);
		}
		
		System.out.println("\nType h for a list of commands.\n");
		
		while (!cmd.equals("q")) {
			// Get the user's command
			try {
				System.out.print("> ");
				cmd = br.readLine();
			} catch (IOException ioe) {
				System.err.println("Error reading input " + ioe);
				System.exit(1);
			}
			
			// Run the command
			runCommand(cmd);
		}
	}
	
	public int getID() throws Exception {
		int id;
		
		do {
			System.out.print("ID: ");
			id = Integer.parseInt(this.br.readLine());
			
			if (!this.notes.noteExists(id)) {
				System.out.println("The note with ID " + id + " does not exist");
			}
		} while (!this.notes.noteExists(id));
		
		return id;
	}
	
	public boolean confirm() {
		String input = null; 
		
		try {
			input = br.readLine();
		} catch (IOException ioe) {
			System.err.println("Error reading input " + ioe);
			System.exit(1);
		}
		
		if (input.toLowerCase().equals("y") || input.toLowerCase().equals("yes")) {
			return true;
		}
		
		return false;
	}
	
	public void displayNote(Note note) {
		System.out.println("ID: " + note.id);
		System.out.println(note.note + "\n");
		
		System.out.println("Date: " +
				new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(note.date*1000));
		
		System.out.print(note.tags.length + " tag(s): ");
		for (String tag : note.tags) {
			System.out.print(tag + " ");
		}
	}
	
	public void runCommand(String cmd) throws Exception {
		if (cmd.equals("h")) { // h: List commands
			listCommands();
		} else if (cmd.equals("i")) { // i: Insert new record
			insertRecord(1);
		} else if (cmd.equals("u")) { // u: Update record
			updateRecord();
		} else if (cmd.equals("d")) { // d: Delete record
			deleteRecord();
		} else if (cmd.equals("a")) { // a: List all records
			listAllRecords();
		} else if (cmd.equals("st")) {  // st: Search tags
			searchTags();
		} else if (cmd.equals("q")) { // q: Quit
			System.out.println("Bye");
			System.exit(0);
		} else { // Command not found
			System.out.println(cmd + ": Command not found");
		}
	}
	
	public void listCommands() {
		System.out.println(
				"Notefile Commands\n\n" +
				"  i   Insert new record\n" +
				"  u   Update record\n" +
				"  d   Delete record\n" +
				"  a   Show all records\n" +
				"  st  Search all records by tag\n" +
				"  q   quit");
	}
	
	public void insertRecord(int category) {
		String note = null;
		String tags = null;
		
		System.out.println("Insert new record");
		
		try {
			// Read in the user's note
			System.out.println("Note:");
			note = br.readLine();
			
			// Read in the tags
			System.out.println("Tags (separated by spaces):");
			tags = br.readLine();
		} catch (IOException ioe) {
			System.err.println("Error reading input" + ioe);
			System.exit(1);
		}
		
		try {
			this.notes.addNote(note, category, tags);
		} catch (Exception e) {
			System.err.println("Error: " + e);
			System.exit(1);
		}
	}
	
	public void updateRecord() throws Exception {
		int id;
		String note = null;
		String tags = null;
		
		System.out.println("Update record");
		
		id = getID();
		
		System.out.print("Continue? [y/n] ");
		if (confirm()) {
			System.out.println("Note:");
			note = br.readLine();
			
			System.out.println("Tags:");
			tags = br.readLine();
			
			this.notes.updateNote(id, note, tags);
		}
	}
	
	public void deleteRecord() throws Exception {
		int id;
		
		System.out.println("Delete record");
		
		id = getID();
		
		System.out.print("Continue? [y/n] ");
		if (confirm()) {
			this.notes.deleteNote(id);
		}
	}
	
	public void listAllRecords() throws Exception {
		List<Note> notes = this.notes.getAll();
		
		if (!notes.isEmpty()) {
			for (Note note : notes) {
				displayNote(note);
				
				System.out.println("\n---------------");
			}
		} else {
			System.out.println("No notes found");
		}
	}
	
	public void searchTags() throws Exception {
		String tag = null;
		List<Note> notes;
		
		System.out.println("Search records by tag");
		
		// Get the tag to search for
		try {
			System.out.print("Tag: ");
			tag = br.readLine();
		} catch (IOException ioe) {
			System.err.println("Error reading input: " + ioe);
			System.exit(1);
		}
		
		notes = this.notes.getByTag(1, tag);
		
		if (!notes.isEmpty()) {
			for (Note note : notes) {
				displayNote(note);
				
				System.out.println("\n---------------");
			}
		} else {
			System.out.println("No notes found");
		}
	}

}
