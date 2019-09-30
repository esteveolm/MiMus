package ui.table;

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

/**
 * TableViewer for annotated Entities in the Editor, that is, table
 * containing EntityInstances.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class EntityTableViewer extends MiMusTableViewer {

	private List<EntityInstance> entities;

	public EntityTableViewer(Composite parent, 
			List<EntityInstance> initials, MiMusText text) {
		super(parent);
		String[] aux = {"Tipus", "Entitat"};
		columnNames = aux;
		entities = initials;
	}

	/**
	 * Creates the table viewer and leaves it ready to use.
	 */
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
	
	/**
	 * Label provider telling how to present instances in the table.
	 */
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
	
	/**
	 * Compares entities alphanumerically, using two criteria:
	 * first, by the name of their type (e.g. Artista goes before
	 * Promotor); then, ties are resolved by name of their String
	 * representation (e.g. Joan I goes before Pere IV).
	 */
	public class EntityComparator extends ViewerComparator {
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
	
	public List<EntityInstance> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityInstance> entities) {
		this.entities = entities;
	}
}
