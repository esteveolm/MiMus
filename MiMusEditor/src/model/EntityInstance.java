package model;

/**
 * An EntityInstance is an instance of an Entity object in the
 * MiMus corpus. That is, Entities are the concepts and Instances
 * are the appearances of these concepts in the corpus. EntityInstance
 * corresponds with elements stored in EntityInstance table on MiMus
 * database.
 * 
 * An EntityInstance represents an association between an Entity and
 * a Document, hence the model object is only a pair of foreign keys for
 * both elements.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class EntityInstance extends Unit {
	
	private Entity itsEntity;
	private Document itsDocument;
	
	
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

	/**
	 * The String representation of an Instance is the lemma
	 * of the Entity associated.
	 */
	@Override
	public String toString() {
		return getItsEntity().getLemma();
	}
}
