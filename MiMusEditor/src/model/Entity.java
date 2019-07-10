package model;

public abstract class Entity extends HierarchicalUnit implements IHierarchicalUnit {
	
	public abstract String getLemma();
	
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
