package model;

import model.Document;

public class EntityInstance extends Unit {
	
	private Entity itsEntity;
	private Document itsDocument;
	
	public EntityInstance() {}
	
	public EntityInstance(Entity itsEntity, Document itsDocument) {
		this(itsEntity, itsDocument, 0);
	}
	
	public EntityInstance(Entity itsEntity, Document itsDocument, int id) {
		this.itsEntity = itsEntity;
		this.itsDocument = itsDocument;
		this.setId(id);
	}
	
	public Entity getItsEntity() {
		return itsEntity;
	}
	public void setItsEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
	}
	
	public Document getItsDocument() {
		return itsDocument;
	}
	public void setItsDocument(Document itsDocument) {
		this.itsDocument = itsDocument;
	}

	@Override
	public String toString() {
		return getItsEntity().getLemma();
	}
}
