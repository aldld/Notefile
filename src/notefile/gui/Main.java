// Main.java
// Creates and handles the Notefile main window

package notefile.gui;

import java.util.List;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import notefile.db.Notes;
import notefile.data.Note;

public class Main extends JFrame {
	
	public Notes notes;
	private Toolkit toolkit;
	
	public Main() throws Exception {
		// Get the path to the Notefile database.
		// For now defaults to .notefile.db in the user's home directory
		String database = System.getProperty("user.home") + "/.notefile.db";
		
		this.notes = new Notes(database);
		
		setSize(800, 600);
		setTitle("Notefile");
		
		toolkit = getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setLocation(screenSize.width/2 - getWidth()/2,
				screenSize.height/2 - getHeight()/2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		createMenuBar();
		createTable();
	}
	
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.setMnemonic(KeyEvent.VK_X);
		fileExit.setToolTipText("Exit Notefile");
		fileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		file.add(fileExit);
		
		menuBar.add(file);
		
		setJMenuBar(menuBar);
	}
	
	private void createTable() throws Exception {
		String[] columnNames = {
				"ID",
				"Note",
				"Category",
				"Tags",
				"Date"
		};
		
		List<Note> notes = this.notes.getAll();

		Object[][] data = new Object[notes.size()][5];
		
		for (int i = 0; i < notes.size(); i++) {
			data[i][0] = notes.get(i).id;
			data[i][1] = notes.get(i).note;
			data[i][2] = notes.get(i).category;
			data[i][3] = notes.get(i).tagString(" ");
			data[i][4] = notes.get(i).date;
		};
		
		JTable table = new JTable(data, columnNames);
		
		table.setPreferredScrollableViewportSize(new Dimension(800, 200));
		table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it
		JScrollPane scrollPane = new JScrollPane(table);
		
		add(scrollPane);
	}
	
}
