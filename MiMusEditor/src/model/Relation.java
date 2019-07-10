package model;

import java.util.List;

public class Relation extends HierarchicalUnit {

	public static final String[] TYPES = 
			{"TeOfici", "TeCasa", "ServeixA", "ResideixA", "Moviment"};
	
	private Entity itsEntity1;
	private Entity itsEntity2;
	private String type;
	private Document doc;
	
	public Relation() {}

	public Relation(Document doc, Entity ent1, Entity ent2, 
			String type, int id, int specId) {
		this.itsEntity1 = ent1;
		this.itsEntity2 = ent2;
		this.type = type;
		this.doc = doc;
		this.setId(id);
		this.setSpecificId(specId);
 	}

	public Entity getItsEntity1() {
		return itsEntity1;
	}
	public void setItsEntity1(Entity itsEntity1) {
		this.itsEntity1 = itsEntity1;
	}
	public Entity getItsEntity2() {
		return itsEntity2;
	}
	public void setItsEntity2(Entity itsEntity2) {
		this.itsEntity2 = itsEntity2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	@Override
	public String toString() {
		return getItsEntity1().toString() + " - " + getItsEntity2().toString();
	}
	
	public static boolean containsRelation(List<Relation> relations, Relation rel) {
		for (Relation r: relations) {
			if (r.getItsEntity1().equals(rel.getItsEntity1()) &&
					(r.getItsEntity2().equals(rel.getItsEntity2()))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsEntity(List<Relation> relations, 
			EntityInstance ent) {
		for (Relation r: relations) {
			if (r.getItsEntity1().equals(ent.getItsEntity()) ||
					(r.getItsEntity2().equals(ent.getItsEntity()))) {
				return true;	
			}
		}
		return false;
	}
}
