// NotesTableModel.java
// Model for 

package notefile.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import notefile.db.Notes;
import notefile.data.Note;

public class NotesTableModel extends AbstractTableModel {

	Notes notes;
	
	// Column names
	protected String[] columnNames = new String[] {
			"ID", "Note", "Tags", "Date"
	};
	
	// Column classes
	protected Class[] columnClasses = new Class[] {
			Integer.class, String.class, String.class, Integer.class
	};
	
	public List<Note> data;
	
	public NotesTableModel(List<Note> data) {
		this.data = data;
	}

	// The easiest methods of this whole model
	public int getColumnCount() { return 4; } // A constant for this model
	public int getRowCount() { return data.size(); } // # of notes in table
	
	// Info for each column
	public String getColumnName(int col) { return columnNames[col]; }
	public Class getColumnClass(int col) { return columnClasses[col]; }

	public Object getValueAt(int rowIndex, int columnIndex) {
		Note row = data.get(rowIndex);
		
		switch (columnIndex) {
		case 0: // ID
			return row.id;
		case 1: // Note
			return row.note;
		case 2: // Tags
			return row.tagString(" ");
		case 3: // Date
			return row.date;
		}
		
		return null;
	}

}
