package model;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.Persistable;

public class EntityInstance extends Entity implements Persistable {

	private Entity itsEntity;
	
	public EntityInstance(Entity itsEntity) {
		this(itsEntity, 0);
	}
	
	public EntityInstance(Entity itsEntity, int id) {
		/* ID is set after super() for the instance, not the concept */
		super(-1, itsEntity.getType());
		this.itsEntity = itsEntity;
		this.setId(id);
	}
	
	public Entity getItsEntity() {
		return itsEntity;
	}
	public void setItsEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
	}

	@Override
	public String getLemma() {
		return itsEntity.getLemma();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}
	
	/* Implementation of MiMusWritable */

	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntity = doc.createElement(getWritableName());
		Element tagItsEntity = doc.createElement("entity_id");
		tagItsEntity.appendChild(doc.createTextNode(
				String.valueOf(itsEntity.getId())));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(
				String.valueOf(getId())));
		tagEntity.appendChild(tagItsEntity);
		tagEntity.appendChild(tagId);
		return tagEntity;
	}

	@Override
	public String getWritableName() {
		return "entity";
	}

	@Override
	public String getWritableCategory() {
		System.out.println("category is entities");
		return "entities";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(getId());
	}
	
	/**
	 * Compares fields <itsEntity> of an EntitiesList <list> of EntityInstance
	 * to check if any equals <ent>.
	 */
	public static boolean containsEntity(Vector<? extends Unit> list, Entity ent) {
		for (Unit u: list) {
			if (u instanceof EntityInstance) {
				if (((EntityInstance) u).getItsEntity().equals(ent)) {
					return true;
				}
			}
		}
		return false;
	}
}
