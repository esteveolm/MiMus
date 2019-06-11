package model;

import java.util.List;

public class Relation extends ConcreteUnit {

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
