package model;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import ui.MiMusContentProvider;

public abstract class UnitsList<U> {
	
	private Vector<U> units;
	private Set<MiMusContentProvider> changeListeners;
	
	public UnitsList() {
		units = new Vector<>();
		changeListeners = new HashSet<>();
	}
	
	public abstract void initList();
		
	public abstract void addUnit(U unit);
	
	public abstract void removeUnit(U unit);
	
	public abstract void unitChanged(U unit);
	
	public Vector<U> getUnits() {
		return units;
	}

	public void setUnits(Vector<U> units) {
		this.units = units;
	}
	
	public int countUnits() {
		return units.size();
	}
	
	public Set<MiMusContentProvider> getChangeListeners() {
		return changeListeners;
	}

	public void setChangeListeners(Set<MiMusContentProvider> changeListeners) {
		this.changeListeners = changeListeners;
	}
	
	public void addChangeListener(MiMusContentProvider provider) {
		changeListeners.add(provider);
	}

	public void removeChangeListener(MiMusContentProvider provider) {
		changeListeners.remove(provider);
	}
}
