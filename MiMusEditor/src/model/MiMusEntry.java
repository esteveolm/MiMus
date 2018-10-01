package model;

public class MiMusEntry {
	
	private String regest;
	private String body;
	
	public MiMusEntry() {
		this.regest = null;
		this.body = null;
	}
	
	public MiMusEntry(String regest, String body) {
		this.regest = regest;
		this.body = body;
	}
	
	public String toString() {
		return "Regest: " + regest + "\n\nBody: " + body;
	}
	
	public String getRegest() {
		return regest;
	}
	public void setRegest(String regest) {
		this.regest = regest;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
