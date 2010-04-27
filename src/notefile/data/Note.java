package notefile.data;

public class Note {
	
	public int id;
	public String note;
	public int category;
	public String[] tags;
	public long date;
	
	// Parses tags from a space-separated string
	public void parseTags(String tags) {
		this.tags = tags.split(" ");
	}
	
	// Convert array of tags to a string
	public String tagString(String separator) {
		StringBuffer result = new StringBuffer();
		
		if (this.tags.length > 0) {
			result.append(this.tags[0]);
			
			for (int i = 1; i < this.tags.length; i++) {
				result.append(separator);
				result.append(this.tags[i]);
			}
		}
		
		return result.toString();
	}
	
}
