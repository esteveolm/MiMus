package model;

public class MiMusEntry {
	
	private String regest;
	private String transcription;
	
	public MiMusEntry() {
		this.regest = null;
		this.transcription = null;
	}
	
	public MiMusEntry(String regest, String body) {
		this.regest = regest;
		this.transcription = body;
	}
	
	public String toString() {
		return "Regest: " + regest + "\n\nTranscription: " + transcription;
	}
	
	public String getRegest() {
		return regest;
	}
	public void setRegest(String regest) {
		this.regest = regest;
	}
	public String getTranscription() {
		return transcription;
	}
	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}
}
