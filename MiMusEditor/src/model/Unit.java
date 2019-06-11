package model;

import java.util.List;

public class Unit {
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public static Unit findById(List<? extends Unit> units, int id) {
		for (Unit u : units) {
			if (u.getId() == id) {
				return u;
			}
		}
		return null;
	}
}
