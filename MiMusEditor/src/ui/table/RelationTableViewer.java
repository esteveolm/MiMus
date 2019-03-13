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

import model.Relation;
import model.Unit;
import util.TextStyler;

public class RelationTableViewer extends MiMusTableViewer {
	
	private List<Unit> relations;
	
	public RelationTableViewer(Composite parent, List<Unit> initials, 
			TextStyler styler) {
		super(parent, styler);
		String[] aux = {"Tipus", "Entitat A", "Entitat B"};
		columnNames = aux;
		setRelations(new ArrayList<>(initials));
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
		tv.setLabelProvider(new RelationLabelProvider());
		tv.setInput(relations);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new RelationComparator());
		packColumns();
		return tv;
	}
	
	class RelationLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Relation rel = (Relation) element;
			switch (columnIndex) {
			case 0:		// Tipus
				return rel.getType();
			case 1:		// Entitat A
				return rel.getItsEntity1().getItsEntity().getLemma();
			case 2:		// Entitat B
				return rel.getItsEntity2().getItsEntity().getLemma();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	public class RelationComparator extends ViewerComparator {
		/**
		 * Compares relations alphanumerically, based on their entities names.
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			Relation rel1 = (Relation) e1;
			Relation rel2 = (Relation) e2;
			int c1 = rel1.getType().compareTo(rel2.getType());
			if (c1==0) {
				int c2 = rel1.getItsEntity1().getItsEntity().getLemma().compareTo(
						rel2.getItsEntity1().getItsEntity().getLemma());
				if (c2==0) 
					return rel1.getItsEntity2().getItsEntity().getLemma().compareTo(
							rel2.getItsEntity2().getItsEntity().getLemma());
				return c2;
			}
			return c1;		
		}
	}
	
	public List<Unit> getRelations() {
		return relations;
	}
	public void setRelations(List<Unit> relations) {
		this.relations = relations;
	}
}
