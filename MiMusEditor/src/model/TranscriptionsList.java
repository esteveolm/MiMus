package model;

import java.util.Iterator;

import ui.table.MiMusContentProvider;

public class TranscriptionsList extends UnitsList<Transcription> {

	public TranscriptionsList() {
		super();
	}
	
	@Override
	public void addUnit(Transcription unit) {
		getUnits().add(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).addUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void removeUnit(Transcription unit) {
		getUnits().remove(unit);
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).removeUnit(unit);	// Unnecessary cast?
		}
	}

	@Override
	public void unitChanged(Transcription unit) {
		Iterator<MiMusContentProvider> it = getChangeListeners().iterator();
		while (it.hasNext()) {
			((MiMusContentProvider) it.next()).updateUnit(unit);
		}
	}
}
