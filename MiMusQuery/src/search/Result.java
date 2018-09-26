package search;

public class Result {
	
	private String text;
	private String type;
	private String subtype;
	private String document;
	
	public Result(String text, String type, String subtype, String document) {
		this.setText(text);
		this.setType(type);
		this.setSubtype(subtype);
		this.setDocument(document);
	}
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	
	public String toString() {
		return "Text: " + text + " - Doc: " + document;
	}
}
