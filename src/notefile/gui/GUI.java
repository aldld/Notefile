// GUI.java
// Graphical interface for Notefile using Swing

package notefile.gui;

//import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import notefile.gui.Frame;

public class GUI {
	
	public GUI() throws Exception {
		// Set a native look and feel for Windows / Mac OS X
		/*try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}*/
		
		new Frame();
	}
	
}
