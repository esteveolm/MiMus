package model;

public class NoteType extends Unit {
	
	private String text;
	
	public NoteType(int id, String text) {
		this.text = text;
		this.setId(id);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getText();
	}

}
