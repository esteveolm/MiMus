package model;

import editor.IllegalTextRangeException;

public class Relation extends Unit {

	private EntitiesList entities;
	private int entityA;
	private int entityB;
	private int type;
	
	public Relation(EntitiesList entities) {
		this.entities = entities;
		this.entityA = 0;
		this.entityB = 0;
		this.type = 0;
	}
	
	public Relation(EntitiesList entities, int entityA, int entityB) {
		this.entities = entities;
		this.entityA = entityA;
		this.entityB = entityB;
		this.type = 0;
	}
	
	private Entity getById(int id) {
		for (Entity e: entities.getUnits()) {
			if (e.getId()==id) return e;
		}
		return null;
	}
	
	public int getEntityA() {
		return entityA;
	}
	public String getEntityAText() {
		try {
			return getEntityAObject().getText();
		} catch (IllegalTextRangeException e) {
			return "FATAL ERROR. THIS SHOULD NEVER HAPPEN";
		}
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
		try {
			return getEntityBObject().getText();
		} catch (IllegalTextRangeException e) {
			return "FATAL ERROR. THIS SHOULD NEVER HAPPEN";
		}
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
		return TypedEntity.RELATION_TYPES[type];
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
