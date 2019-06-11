package ui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import model.EntityInstance;
import model.MiMusText;
import model.Unit;
import util.TextStyler;

public class EntityTableViewer extends MiMusTableViewer {

	private List<Unit> entities;

	public EntityTableViewer(Composite parent, 
			List<EntityInstance> initials, TextStyler styler, MiMusText text) {
		super(parent, styler);
		String[] aux = {"Type", "Entity"};
		columnNames = aux;
		entities = new ArrayList<>(initials);
	}

	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setLabelProvider(new EntityLabelProvider());
		tv.setInput(entities);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new EntityComparator());
		packColumns();
		return tv;
	}
	
	class EntityLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			EntityInstance ent = (EntityInstance) element;
			switch (columnIndex) {
			case 0:	// Type
				return ent.getItsEntity().getType();
			case 1: // Entity
				return ent.toString();
			default:
				return "";
			}
		}
	}
	
	public class EntityComparator extends ViewerComparator {
		/**
		 * Compares entities alphanumerically, using two criteria:
		 * first, by the name of their type (e.g. Artista goes before
		 * Promotor); then, ties are resolved by name of their String
		 * representation (e.g. Joan I goes before Pere IV).
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			EntityInstance ent1 = (EntityInstance) e1;
			EntityInstance ent2 = (EntityInstance) e2;
			int byType = ent1.getItsEntity().getType()
					.compareTo(ent2.getItsEntity().getType());
			if (byType==0) {
				return ent1.toString().compareTo(ent2.toString());
			} else {
				return byType;
			}
		}
	}
	
	/* Getters and setters */
	
	public List<Unit> getEntities() {
		return entities;
	}

	public void setEntities(List<Unit> entities) {
		this.entities = entities;
	}
}
