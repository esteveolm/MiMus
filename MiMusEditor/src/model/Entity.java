package model;

import util.xml.Persistable;

public abstract class Entity extends Unit 
		implements Persistable {
	
	private int id;
	private String type;
	
	public Entity() {}
	
	public Entity(int id) {
		this(id, "Unknown");
	}
	
	public Entity(int id, String type) {
		this.setId(id);
		this.setType(type);
	}

	public abstract String getLemma();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	/* Implementation of MiMusWritable that is common to all entities */

	@Override
	public String getWritableId() {
		return String.valueOf(id);
	}
}
