package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.SharedResources;
import util.xml.MiMusXML;
import util.xml.Persistable;

public class EntityInstance extends ConcreteUnit implements Persistable {
	
	private int id;
	private Entity itsEntity;
	
	public EntityInstance() {}
	
	public EntityInstance(List<Unit> allEntities) {
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
	public String getType() {
		return itsEntity.getWritableName();
	}

	@Override
	public String toString() {
		return getItsEntity().getLemma();
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public EntityInstance fromXMLElement(Element elem) {
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
		return "entities";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(getId());
	}
	
	public static List<Unit> read(String docIdStr) {
		List<Unit> arts = SharedResources.getInstance().getArtistas();
		List<Unit> insts = SharedResources.getInstance().getInstruments();
		List<Unit> cases = SharedResources.getInstance().getCases();
		List<Unit> proms = SharedResources.getInstance().getPromotors();
		List<Unit> all = Stream.of(arts, insts, cases, proms)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openDoc(docIdStr).getDoc();
		NodeList nl = doc.getElementsByTagName("entity");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new EntityInstance(all).fromXMLElement(elem));
			}
		}
		return entries;
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
