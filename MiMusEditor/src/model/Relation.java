package model;

public class Relation extends Unit {

	private EntitiesList entities;
	private int entityA;
	private int entityB;
	private int type;
	
	public Relation(EntitiesList entities) {
		this.entities = entities;
		this.entityA = entities.getIdAt(0);
		this.entityB = entities.getIdAt(1);
		this.type = 0;
	}
	
	public Relation(EntitiesList entities, int entityA, int entityB) {
		this.entities = entities;
		this.entityA = entityA;
		this.entityB = entityB;
		this.type = 0;
	}
	
	protected Entity getById(int id) {
		return getById(id, entities);
	}
	
	protected Entity getById(int id, EntitiesList list) {
		for (Entity e: list.getUnits()) {
			if (e.getId()==id) {
				System.out.println("Found Entity " + e.toString());
				return e;
			}
		}
		System.out.println("Could not find Entity with id " + id);
		return null;
	}
	
	public int getEntityA() {
		return entityA;
	}
	public String getEntityAText() {
		return getEntityAObject().getLemma();
	}
	public Entity getEntityAObject() {
		return getById(getEntityA());
	}
	public void setEntityA(int entityA) {
		this.entityA = entityA;
	}
	public int getEntityB() {
		return entityB;
	}
	public String getEntityBText() {
		return getEntityBObject().getLemma();
	}
	public Entity getEntityBObject() {
		return getById(getEntityB());
	}
	public void setEntityB(int entityB) {
		this.entityB = entityB;
	}
	public int getType() {
		return type;
	}
	public String getTypeWord() {
		return "";
	}
	public void setType(int type) {
		this.type = type;
	}
	public EntitiesList getEntities() {
		return entities;
	}
	public void setEntities(EntitiesList entities) {
		this.entities = entities;
	}
	
	public String toString() {
		return "(" + getEntityAText() + " <- " + getTypeWord() + " -> " + getEntityBText() + ")";
	}
}
