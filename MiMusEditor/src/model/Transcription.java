package model;

public class Transcription {
	
	private Entity itsEntity;
	private String form;
	
	public Transcription() {
		this.itsEntity = null;
		this.form = "";
	}
	
	public Transcription(Entity itsEntity, String form) {
		this.itsEntity = itsEntity;
		this.form = form;
	}
	
	/* Getters and setters */
	
	public Entity getEntity() {
		return itsEntity;
	}
	public void setEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
}
