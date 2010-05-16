// NoteFrame.java
// Frame containing the editor for individual notes

package notefile.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import notefile.db.Notes;
import notefile.data.Note;
import notefile.gui.Frame;

public class NoteFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;

	private Toolkit toolkit;
	
	private Notes notes = new Notes();
	
	private boolean isNew = false;
	private Note content = new Note();

	private Container pane;

	private JTextField tags;
	private JTextArea textArea;
	
	public NoteFrame(String note, String tags, long date, int id) throws Exception {
		content.id = id;
		content.note = note;
		content.parseTags(tags);
		content.date = date;
		content.category = 1;
		
		initComponents();
		
		setVisible(true);
	}
	
	public NoteFrame() throws Exception {
		this("", "", System.currentTimeMillis() / 1000, 0);
		isNew = true;
	}
	
	public void initComponents() {
		setSize(400, 400);
		setTitle("Notefile");
		
		toolkit = getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		setLocation(screenSize.width/2 - getWidth()/2,
				screenSize.height/2 - getHeight()/2);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// The window's layout
		pane = getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.setPreferredSize(new Dimension(400, 400));
		
		
		// Tags panel
		JPanel tagsPanel = new JPanel();
		tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.LINE_AXIS));
		tagsPanel.setMaximumSize(new Dimension(getMaximumSize().width, 25));
		
		JLabel tagsLabel = new JLabel("Tags");
		
		tags = new JTextField(content.tagString(" "));
		tags.setMaximumSize(new Dimension(getMaximumSize().width, 25));
		
		tagsPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		tagsPanel.add(tagsLabel);
		tagsPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		tagsPanel.add(tags);
		tagsPanel.add(Box.createRigidArea(new Dimension(5, 25)));
		
		pane.add(Box.createRigidArea(new Dimension(0, 3)));
		pane.add(tagsPanel);
		pane.add(Box.createRigidArea(new Dimension(0, 3)));
		
		
		// Main text area
		textArea = new JTextArea(content.note);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(new EmptyBorder(5, 5, 5, 5));

		JScrollPane scrollPane = new JScrollPane(textArea);
		
		pane.add(scrollPane);
		
		
		// "Save" button
		JPanel savePanel = new JPanel();
		savePanel.setMaximumSize(new Dimension(getMaximumSize().width, 50));
		savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.X_AXIS));
		
		JButton saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(50, 30));
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveNote();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		savePanel.add(Box.createRigidArea(new Dimension(3, 3)));
		savePanel.add(saveButton);
		
		pane.add(savePanel);
		
		pack();
	}
	
	public void saveNote() throws Exception {
		content.note = textArea.getText();
		content.parseTags(tags.getText());
		
		if (isNew) {
			content.id = this.notes.addNote(content.note, 1, content.tagString(" "));
			isNew = false;
		} else {
			this.notes.updateNote(content.id, content.note, content.tagString(" "));
		}
		
		Frame.notesTableModel.data = this.notes.getByTag(1, Frame.currentSearch.split(" "));
		Frame.notesTableModel.fireTableDataChanged();
	}
	
}
