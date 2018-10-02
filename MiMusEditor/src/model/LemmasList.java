package model;

public class LemmasList extends RelationsList {

	private EntitiesList transcriptionEntities;
	
	public LemmasList(EntitiesList regestEntities, EntitiesList transcriptionEntities) {
		super(regestEntities);
		this.setTranscriptionEntities(transcriptionEntities);
	}

	/* Getters and setters */
	/* Overwriting get/setEntities() from RelationsList for naming */
	
	public EntitiesList getRegestEntities() {
		return getEntities();
	}
	public void setRegestEntities(EntitiesList lemmaEntities) {
		setEntities(lemmaEntities);
	}
	public EntitiesList getTranscriptionEntities() {
		return transcriptionEntities;
	}
	public void setTranscriptionEntities(EntitiesList transcriptionEntities) {
		this.transcriptionEntities = transcriptionEntities;
	}
}
