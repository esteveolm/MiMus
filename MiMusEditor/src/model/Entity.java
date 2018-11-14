package model;

public abstract class Entity extends Unit {
	
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
