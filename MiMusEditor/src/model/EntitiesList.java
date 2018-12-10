package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ui.table.MiMusContentProvider;
//import ui.table.RelationTableViewer;

public class EntitiesList extends UnitsList<Entity> {
	
//	private RelationTableViewer relations;

	public EntitiesList() {
		this(new ArrayList<Entity>());
//		relations = null;
	}
	
	public EntitiesList(Collection<? extends Entity> ents) {
		for (Entity ent : ents) {
			this.addUnit(ent);
		}
	}
	
	public int getIdAt(int i) {
		return getUnits().get(i).getId();
	}
	
	public int getMaxId() {
		int id = -1;
		for (int i=0; i<countUnits(); i++) {
			id = Math.max(id, getIdAt(i));
		}
		return id;
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
	
//	public RelationTableViewer getRelations() {
//		return relations;
//	}
//
//	public void setRelations(RelationTableViewer relations) {
//		this.relations = relations;
//	}
}
