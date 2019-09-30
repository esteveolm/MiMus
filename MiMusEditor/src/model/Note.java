package model;

/**
 * A Note is a footnote appearing in a MiMus Document. Hence, it
 * represents a foreign key in its associated table in the MiMus
 * database, as well as the text of the note and the type of note.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Note extends Unit {
	
	private String type;
	private String text;
	private Document doc;
	
	public Note(int id, String type, String text, Document doc) {
		this.type = type;
		this.text = text;
		this.setDoc(doc);
		this.setId(id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
}
