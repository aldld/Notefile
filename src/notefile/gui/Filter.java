// Filter.java
// Filters files to only end in .note

package notefile.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class Filter extends FileFilter {

	public boolean accept(File f) {
		// Show directories to let user navigate their file system
		if (f.isDirectory()) {
			return true;
		}
		
		// Get the file extension
		String ext = null;
		String name = f.getName();
		int i = name.lastIndexOf('.');
		
		if (i > 0 && i < name.length() - 1) {
			ext = name.substring(i+1).toLowerCase();
		}
		
		// Check if the filename ends in .note
		if ("note".equals(ext)) {
			return true;
		}
		
		return false;
	}

	public String getDescription() {
		return "Notefile notebooks (.note)";
	}

}
