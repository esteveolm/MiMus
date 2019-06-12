package model;

public abstract class Entity extends Unit {
	
	private int specificId;

	public abstract String getLemma();
	
	public int getSpecificId() {
		return specificId;
	}
	public void setSpecificId(int specificId) {
		this.specificId = specificId;
	}
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
