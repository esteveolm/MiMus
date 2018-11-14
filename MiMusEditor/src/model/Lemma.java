package model;


public class Lemma extends Relation {

	private EntitiesList transcriptionEntities;
	
	public Lemma(EntitiesList regestEntities, EntitiesList transcriptionEntities) {
		super(regestEntities);
		this.setTranscriptionEntities(transcriptionEntities);
	}
	
	public Lemma(EntitiesList regestEntities, EntitiesList transcriptionEntities, int entityA, int entityB) {
		super(regestEntities, entityA, entityB);
		this.setTranscriptionEntities(transcriptionEntities);
	}
	
	/* Getters and setters */
	/* Overwriting from Relation for naming and existence of two lists*/
	
	public EntitiesList getRegestEntities() {
		return getEntities();
	}
	public void setRegestEntities(EntitiesList entities) {
		setEntities(entities);
	}
	public EntitiesList getTranscriptionEntities() {
		return transcriptionEntities;
	}
	public void setTranscriptionEntities(EntitiesList transcriptionEntities) {
		this.transcriptionEntities = transcriptionEntities;
	}
	public int getRegestEntity() {
		return getEntityA();
	}
	public void setRegestEntity(int ent) {
		setEntityA(ent);
	}
	public String getRegestEntityText() {
		return getRegestEntityObject().getLemma();
	}
	public Entity getRegestEntityObject() {
		return getById(getRegestEntity(), getRegestEntities());
	}
	public int getTranscriptionEntity() {
		return getEntityB();
	}
	public void setTranscriptionEntity(int ent) {
		setEntityB(ent);
	}
	public String getTranscriptionEntityText() {
		return getTranscriptionEntityObject().getLemma();
	}
	public Entity getTranscriptionEntityObject() {
		return getById(getTranscriptionEntity(), getTranscriptionEntities());
	}
	
	public String toString() {
		return "(" + getTranscriptionEntityText() + "<- HAS LEMMA ->" + getRegestEntityText() + ")";
	}
}
