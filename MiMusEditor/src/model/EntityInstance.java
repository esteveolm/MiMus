package model;

import java.util.List;
import model.Document;

public class EntityInstance extends ConcreteUnit {
	
	private Entity itsEntity;
	private Document itsDocument;
	
	public EntityInstance() {}
	
	public EntityInstance(List<Unit> allEntities) {
		super(allEntities);
	}
	
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
	
	/**
	 * Compares fields <itsEntity> of an EntitiesList <list> of EntityInstance
	 * to check if any equals <ent>.
	 */
	public static boolean containsEntity(List<Unit> list, Entity ent) {
		for (Unit u: list) {
			if (u instanceof EntityInstance) {
				if (((EntityInstance) u).getItsEntity().equals(ent)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static EntityInstance getInstanceWithEntity(List<Unit> list, Entity ent) {
		for (Unit u: list) {
			if (u instanceof EntityInstance) {
				if (((EntityInstance) u).getItsEntity().equals(ent)) {
					return (EntityInstance) u;
				}
			}
		}
		return null;
	}
}
