package model;

import java.util.Iterator;

import editor.MiMusContentProvider;

public class RelationsList extends UnitsList<Relation> {
	
	private EntitiesList entities;
	
	public RelationsList(EntitiesList entities) {
		super();
		this.entities = entities;
		initList();
	}

	@Override
	public void addUnit(Relation unit) {
		getUnits().add(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).addUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void removeUnit(Relation unit) {
		getUnits().remove(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).removeUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void unitChanged(Relation unit) {
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).updateUnit(unit);
		}
	}
	
	public boolean using(Entity ent) {
		for (Relation rel: getUnits()) {
			if (rel.getEntityAObject()==ent || rel.getEntityBObject()==ent) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void initList() {
		//addUnit();
	}
	
	public EntitiesList getEntities() {
		return entities;
	}

	public void setEntities(EntitiesList entities) {
		this.entities = entities;
	}
}
