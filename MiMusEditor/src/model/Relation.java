package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;
import util.xml.Persistable;

public class Relation extends ConcreteUnit implements Persistable {

	private EntityInstance itsEntity1;
	private EntityInstance itsEntity2;
	private String type;
	
	public Relation() {}

	public Relation(List<Unit> itsConcepts) {
		super(itsConcepts);
	}
	
	public Relation(EntityInstance ent1, EntityInstance ent2, String type, int id) {
		this.itsEntity1 = ent1;
		this.itsEntity2 = ent2;
		this.type = type;
		this.setId(id);
 	}

	public EntityInstance getItsEntity1() {
		return itsEntity1;
	}
	public void setItsEntity1(EntityInstance itsEntity1) {
		this.itsEntity1 = itsEntity1;
	}
	public EntityInstance getItsEntity2() {
		return itsEntity2;
	}
	public void setItsEntity2(EntityInstance itsEntity2) {
		this.itsEntity2 = itsEntity2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getItsEntity1().toString() + " - " + getItsEntity2().toString();
	}
	
	@Override
	public Persistable fromXMLElement(Element elem) {
		/* Find 1st EntityInstance with certain id from xml */
		int ent1Id = Integer.parseInt(
				elem.getElementsByTagName("entity1_id")
				.item(0).getTextContent());
		EntityInstance ent1 = null;
		for (int i=0; i<getItsConcepts().size(); i++) {
			Unit u = getItsConcepts().get(i);
			if (u instanceof EntityInstance) {
				EntityInstance thisEnt = ((EntityInstance) u);
				if (thisEnt.getId() == ent1Id) {
					ent1 = thisEnt;
					break;
				}
			}
		}
		/* Find 2nd EntityInstance with certain id from xml */
		int ent2Id = Integer.parseInt(
				elem.getElementsByTagName("entity2_id")
				.item(0).getTextContent());
		EntityInstance ent2 = null;
		for (int i=0; i<getItsConcepts().size(); i++) {
			Unit u = getItsConcepts().get(i);
			if (u instanceof EntityInstance) {
				EntityInstance thisEnt = ((EntityInstance) u);
				if (thisEnt.getId() == ent2Id) {
					ent2 = thisEnt;
					break;
				}
			}
		}
		
		/* Create Relation object from references to Entities */
		if (ent1 != null && ent2 != null) {
			int id = Integer.parseInt(
					elem.getElementsByTagName("id")
					.item(0).getTextContent());
			String type = elem.getElementsByTagName("type")
					.item(0).getTextContent();
			return new Relation(ent1, ent2, type, id);
		}
		return null;
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element tagRelation = doc.createElement(getWritableName());
		Element tagItsEntity1 = doc.createElement("entity1_id");
		tagItsEntity1.appendChild(doc.createTextNode(
				String.valueOf(getItsEntity1().getId())));
		Element tagItsEntity2 = doc.createElement("entity2_id");
		tagItsEntity2.appendChild(doc.createTextNode(
				String.valueOf(getItsEntity2().getId())));
		Element tagType = doc.createElement("type");
		tagType.appendChild(doc.createTextNode(getType()));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(
				String.valueOf(getId())));
		tagRelation.appendChild(tagItsEntity1);
		tagRelation.appendChild(tagItsEntity2);
		tagRelation.appendChild(tagType);
		tagRelation.appendChild(tagId);
		return tagRelation;
	}

	@Override
	public String getWritableName() {
		return "relation";
	}

	@Override
	public String getWritableCategory() {
		return "relations";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(this.getId());
	}
	
	public static List<Unit> read(String docIdStr) {
		/* Retrieve EntityInstances at this document */
		List<Unit> instances = EntityInstance.read(docIdStr);
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openDoc(docIdStr).getDoc();
		NodeList nl = doc.getElementsByTagName("relation");
		for (int i=0; i<nl.getLength(); i++) {
			/* Iterate all relations */
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				/* Retrieve all fields: id, type, entity1_id, entity2_id */
				int id = Integer.parseInt(
						elem.getElementsByTagName("id")
						.item(0).getTextContent());
				String type = elem.getElementsByTagName("type")
						.item(0).getTextContent();
				int ent1Id = Integer.parseInt(
						elem.getElementsByTagName("entity1_id")
						.item(0).getTextContent());
				int ent2Id = Integer.parseInt(
						elem.getElementsByTagName("entity2_id")
						.item(0).getTextContent());
				EntityInstance ent1 = null;
				EntityInstance ent2 = null;
				for (int j=0; j<instances.size(); j++) {
					/* Use entity ids to find the EntityInstance objects */
					if (((EntityInstance) instances.get(j)).getId() == ent1Id) {
						ent1 = (EntityInstance) instances.get(j);
					} else if (((EntityInstance) instances.get(j)).getId() == ent2Id) {
						ent2 = (EntityInstance) instances.get(j);
					}
				}
				if (ent1 != null && ent2 != null)
					/* Should always hold true */
					entries.add(new Relation(ent1, ent2, type, id));
			}
		}
		return entries;
	}
	
	public static boolean containsRelation(List<Unit> relations, Relation rel) {
		for (Unit u: relations) {
			if (u instanceof Relation) {
				if (((Relation) u).getItsEntity1().getItsEntity().equals(
						rel.getItsEntity1().getItsEntity()) &&
						((Relation) u).getItsEntity2().getItsEntity().equals(
								rel.getItsEntity2().getItsEntity())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean containsEntity(List<Unit> relations, EntityInstance ent) {
		for (Unit u: relations) {
			if (u instanceof Relation) {
				if (((Relation) u).getItsEntity1().getItsEntity()
								.equals(ent.getItsEntity()) ||
						((Relation) u).getItsEntity2().getItsEntity()
								.equals(ent.getItsEntity())) {
					return true;	
				}
			}
		}
		return false;
	}
}
