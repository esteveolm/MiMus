package ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import model.Unit;

public interface MiMusContentProvider extends IStructuredContentProvider {
	
	public void addUnit(Unit u);
	
	public void removeUnit(Unit u);
	
	public void updateUnit(Unit u);
	
}
