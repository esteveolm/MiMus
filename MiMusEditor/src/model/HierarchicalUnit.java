package model;

public abstract class HierarchicalUnit extends Unit implements IHierarchicalUnit {

	private int specificId;
	
	@Override
	public int getSpecificId() {
		return specificId;
	}

	@Override
	public void setSpecificId(int id) {
		this.specificId = id;
	}

}
