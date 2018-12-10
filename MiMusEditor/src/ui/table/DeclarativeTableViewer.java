package ui.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import model.EntitiesList;
import model.Unit;

public abstract class DeclarativeTableViewer extends MiMusTableViewer {
	
	protected EntitiesList entities;
	
	public DeclarativeTableViewer(Composite parent) {
		super(parent);
	}

	public abstract CellEditor[] developEditors();
	
	public abstract void developProviders();
	
	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		
		tv.setCellEditors(developEditors());
		tv.setContentProvider(new DeclarativeContentProvider());
		developProviders();
		
		tv.setInput(entities);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		packColumns();
		return tv;
	}
	
	class DeclarativeContentProvider implements MiMusContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((EntitiesList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((EntitiesList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			entities.removeChangeListener(this);
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return entities.getUnits().toArray();
		}

		@Override
		public void addUnit(Unit u) {
			tv.add(u);
		}

		@Override
		public void removeUnit(Unit u) {
			tv.remove(u);
		}

		@Override
		public void updateUnit(Unit u) {
			tv.update(u, null);
			tv.refresh();
		}
	}
}
