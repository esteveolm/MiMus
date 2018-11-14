package model;

import java.util.Iterator;

import ui.table.MiMusContentProvider;
//import ui.table.RelationTableViewer;

public class EntitiesList extends UnitsList<Entity> {
	
//	private RelationTableViewer relations;

	public EntitiesList() {
		super();
//		relations = null;
		initList();
	}
	
	public int getIdAt(int i) {
		return getUnits().get(i).getId();
	}
	
	@Override
	public void addUnit(Entity unit) {
		getUnits().add(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).addUnit(unit);	// Unnecessary cast?
		}
//		relations.reflectEntitiesChanged();
	}

	@Override
	public void removeUnit(Entity unit) {
		getUnits().remove(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).removeUnit(unit);	// Unnecessary cast?
		}
//		relations.reflectEntitiesChanged();
	}
	
	@Override
	public void unitChanged(Entity unit) {
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).updateUnit(unit);
		}
	}
	
	@Override
	public void initList() {
		//addUnit();
	}
	
//	public RelationTableViewer getRelations() {
//		return relations;
//	}
//
//	public void setRelations(RelationTableViewer relations) {
//		this.relations = relations;
//	}
}
