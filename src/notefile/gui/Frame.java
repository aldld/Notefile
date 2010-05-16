// Frame.java
// Creates and handles the Notefile main window

package notefile.gui;

//import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Container;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;

import notefile.data.Note;
import notefile.db.DB;
import notefile.db.Notes;
import notefile.gui.NotesTableModel;

public class Frame extends JFrame {

	/**
	 * Why do I need this?
	 */
	private static final long serialVersionUID = 3998390858253262741L;
	
	private Toolkit toolkit;
	public static NotesTableModel notesTableModel;
	private Notes notes = new Notes();
	
	private Container pane;
	
	private JTable notesTable;
	private JScrollPane notesScrollPane;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	
	private JTextField tags;
	private JButton go;
	private JButton reset;
	
	private JButton addNote;
	
	final JFileChooser fileChooser = new JFileChooser();
	private Filter filter;

	public static String currentSearch = "";
	
	public Frame() throws Exception {
		// Get the path to the Notefile database.
		// For now defaults to .notefile.db in the user's home directory
		//String database = null;
		
		//this.notes = new Notes();
		
		initComponents();
		
		setVisible(true);
	}
	
	private void initComponents() throws Exception {
		// Stuff related to the window itself
		setSize(800, 600);
		setTitle("Notefile");
		
		toolkit = getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setLocation(screenSize.width/2 - getWidth()/2,
				screenSize.height/2 - getHeight()/2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		// Menu bar
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem fileOpen = new JMenuItem("Open");
		fileOpen.setMnemonic(KeyEvent.VK_O);
		fileOpen.setToolTipText("Open a file");
		fileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					openFile();
				} catch (Exception e) { } // Will I ever really need this? (probably)
			}
		});
		
		JMenuItem fileExit = new JMenuItem("Exit");
		fileExit.setMnemonic(KeyEvent.VK_X);
		fileExit.setToolTipText("Exit Notefile");
		fileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		
		fileMenu.add(fileOpen);
		fileMenu.add(fileExit);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		
		// The window's layout
		pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.setPreferredSize(new Dimension(800, 600));
		
		// Search panel
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		searchPanel.setMaximumSize(new Dimension(getMaximumSize().width, 25));
		
		JLabel tagsLabel = new JLabel("Tags");
		
		tags = new JTextField();
		tags.setMaximumSize(new Dimension(getMaximumSize().width, 25));
		// Submit the form when user presses enter
		tags.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Enter pressed");
		tags.getActionMap().put("Enter pressed", new Action() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					updateTagList(tags.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			public Object getValue(String key) {
				return null;
			}
			public boolean isEnabled() {
				return true;
			}
			public void addPropertyChangeListener(
					PropertyChangeListener listener) { }
			public void putValue(String key, Object value) { }
			public void removePropertyChangeListener(
					PropertyChangeListener listener) { }
			public void setEnabled(boolean b) { }
		});
		
		go = new JButton("Go");
		go.setEnabled(false);
		go.setMaximumSize(new Dimension(35, 25));
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					updateTagList(tags.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		reset = new JButton("Reset");
		reset.setEnabled(false);
		reset.setMaximumSize(new Dimension(35, 25));
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tags.setText("");
				try {
					updateTagList("");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		searchPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		searchPanel.add(tagsLabel);
		searchPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		searchPanel.add(tags);
		searchPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		searchPanel.add(go);
		searchPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		searchPanel.add(reset);
		searchPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		
		pane.add(Box.createRigidArea(new Dimension(0, 3)));
		pane.add(searchPanel);
		pane.add(Box.createRigidArea(new Dimension(0, 3)));
		
		
		// Create notesTableModel with all notes in the database
		notesTableModel = new NotesTableModel(this.notes.getAll());
		
		notesTable = new JTable(notesTableModel);
		notesTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = notesTable.getSelectedRow();
					int id = (Integer) Frame.notesTableModel.getValueAt(row, 0);
					
					try {
						Note note = notes.getRecord(id);
						new NoteFrame(note.note, note.tagString(" "), note.date, note.id);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		notesTable.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int c = e.getKeyCode();
				
				if (c == KeyEvent.VK_DELETE) {
					int[] rows = notesTable.getSelectedRows();
					
					for (int row = rows.length-1; row >= 0; row--) {
						try {
							notes.deleteNote((Integer) notesTableModel.getValueAt(row, 0));
							
							notesTableModel.data.remove(row);
							notesTableModel.fireTableRowsDeleted(row, row);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		notesScrollPane = new JScrollPane(notesTable);
		notesScrollPane.setPreferredSize(new Dimension(800, 0));
		
		notesTable.setRowSelectionAllowed(true);
		notesTable.setColumnSelectionAllowed(false);
		
		// Set preferred column widths
		TableColumn column = null;
		for (int i = 0; i < notesTable.getColumnCount(); i++) {
		    column = notesTable.getColumnModel().getColumn(i);
		    
		    switch (i) {
		    case 0: // ID
		    	column.setPreferredWidth(15);
		    	break;
		    case 1: // Note
		    	column.setPreferredWidth(475);
		    	break;
		    case 2: // Tags
		    	column.setPreferredWidth(150);
		    	break;
		    case 3: // Date
		    	// TODO: Choose a width for the date column.
		    	// Also, get around to converting the timestamp to a string.
		    	break;
		    }
		}
		
		// "Add note" button
		JPanel addPanel = new JPanel();
		addPanel.setMaximumSize(new Dimension(getMaximumSize().width, 50));
		addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.X_AXIS));
		
		addNote = new JButton("Add note");
		addNote.setPreferredSize(new Dimension(50, 40));
		addNote.setEnabled(false);
		addNote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new NoteFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		addPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		addPanel.add(addNote);
		
		
		// Finally, add all of the stuff to the window
		pane.add(notesScrollPane);
		pane.add(addPanel);
		
		pack();
	}
	
	private void updateTagList(String tags) throws Exception {
		currentSearch = tags;
		
		if (tags.equals("")) {
			notesTableModel.data = this.notes.getAll();
		} else {
			notesTableModel.data = this.notes.getByTag(1, tags.split(" "));
		}
		
		notesTableModel.fireTableDataChanged();
	}
	
	private void openFile() throws Exception {
		if (filter == null) {
			filter = new Filter();
			fileChooser.addChoosableFileFilter(filter);
		}
		
		int returnVal = fileChooser.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				DB.closeDB();
				DB.path = fileChooser.getSelectedFile().toString();
				DB.initDB();
				
				// Check if the selected file is a properly configured SQLite database
				if (notes.notesTableExists()) {			
					// Reset the notes table
					notesTableModel.data = this.notes.getAll();
					notesTableModel.fireTableDataChanged();
				} else {
					System.err.println("Error: Notefile cannot understand this file.");
				}
				
				tags.setText("");
				
				go.setEnabled(true);
				reset.setEnabled(true);
				addNote.setEnabled(true);
			} catch (SQLException e) {
				System.err.println("Error: Selected file could not be opened.");
			}
		}
	}

}
