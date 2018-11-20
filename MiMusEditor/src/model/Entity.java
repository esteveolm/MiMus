package model;

import util.xml.MiMusWritable;

public abstract class Entity extends Unit implements MiMusWritable {
	
	private int id;
	
	public Entity(int id) {
		this.setId(id);
	}

	public abstract String getLemma();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
