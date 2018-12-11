package model;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.Persistable;

public class EntityInstance extends ConcreteUnit implements Persistable {
	
	private int id;
	private Entity itsEntity;
	
	public EntityInstance(List<Entity> allEntities) {
		super(allEntities);
	}
	
	public EntityInstance(Entity itsEntity) {
		this(itsEntity, 0);
	}
	
	public EntityInstance(Entity itsEntity, int id) {
		this.itsEntity = itsEntity;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Entity getItsEntity() {
		return itsEntity;
	}
	public void setItsEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
	}
	
	@Override
	public String toString() {
		return getItsEntity().getLemma();
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public Persistable fromXMLElement(Element elem) {
		int entId = Integer.parseInt(
				elem.getElementsByTagName("entity_id")
				.item(0).getTextContent());
		Entity ent = null;
		for (int i=0; i<getItsConcepts().size(); i++) {
			Unit u = getItsConcepts().get(i);
			if (u instanceof Entity) {
				Entity thisEnt = ((Entity) u);
				if (thisEnt.getId() == entId) {
					ent = thisEnt;
					break;
				}
			}
		}
		if (ent != null) {
			int id = Integer.parseInt(
					elem.getElementsByTagName("id")
					.item(0).getTextContent());
			return new EntityInstance(ent, id);
		}
		return null;
	}
	
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
	public static boolean containsEntity(List<Unit> list, EntityInstance ent) {
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
