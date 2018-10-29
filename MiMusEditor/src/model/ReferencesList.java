package model;

import java.util.Iterator;

import editor.MiMusContentProvider;

public class ReferencesList extends UnitsList<MiMusReference> {

	@Override
	public void initList() {
		
	}

	@Override
	public void addUnit(MiMusReference unit) {
		getUnits().add(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).addUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void removeUnit(MiMusReference unit) {
		getUnits().remove(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).removeUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void unitChanged(MiMusReference unit) {
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).updateUnit(unit);
		}
	}

}
