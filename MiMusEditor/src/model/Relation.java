package model;

import java.util.List;

public class Relation extends ConcreteUnit {

	public static final String[] TYPES = 
			{"TeOfici", "TeCasa", "ServeixA", "ResideixADao", "Moviment"};
	
	private EntityInstance itsEntity1;
	private EntityInstance itsEntity2;
	private String type;
	private Document doc;
	
	public Relation() {}

	public Relation(List<Unit> itsConcepts) {
		super(itsConcepts);
	}
	
	public Relation(Document doc,EntityInstance ent1, EntityInstance ent2, 
			String type, int id) {
		this.itsEntity1 = ent1;
		this.itsEntity2 = ent2;
		this.type = type;
		this.doc = doc;
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
			if (r.getItsEntity1().getItsEntity().equals(
					rel.getItsEntity1().getItsEntity()) &&
					(r.getItsEntity2().getItsEntity().equals(
							rel.getItsEntity2().getItsEntity()))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean containsEntity(List<Relation> relations, EntityInstance ent) {
		for (Relation r: relations) {
			if (r.getItsEntity1().getItsEntity()
							.equals(ent.getItsEntity()) ||
					(r.getItsEntity2().getItsEntity()
							.equals(ent.getItsEntity()))) {
				return true;	
			}
		}
		return false;
	}
}
