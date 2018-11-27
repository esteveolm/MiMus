package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.MiMusWritable;

public class EntityInstance implements MiMusWritable {

	private Entity itsEntity;
	private int id;
	
	public EntityInstance(Entity itsEntity) {
		this(itsEntity, 0);
	}
	
	public EntityInstance(Entity itsEntity, int id) {
		this.itsEntity = itsEntity;
		this.id = id;
	}
	
	public Entity getItsEntity() {
		return itsEntity;
	}
	public void setItsEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
		return "entities";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(getId());
	}

}
