package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import model.Lloc;

public class LlocTableViewer extends DeclarativeTableViewer {

	public LlocTableViewer(Composite parent, List<Lloc> llocs) {
		super(parent, llocs);
		String[] aux = {"Nom complet", "Àrea", "Regne"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new LlocLabelProvider());
		tv.setComparator(new LlocComparator());
	}
	
	class LlocLabelProvider  extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Lloc lloc = (Lloc) element;
			switch (columnIndex) {
			case 0:	// Nom Complet (Text)
				return lloc.getNomComplet();
			case 1:	// Area (ComboBox)
				return Lloc.AREES[lloc.getArea()];
			case 2:	// Regne (ComboBox)
				return Lloc.REGNES[lloc.getArea()][lloc.getRegne()];
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class LlocComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Lloc l1 = (Lloc) e1;
			Lloc l2 = (Lloc) e2;
			return l1.getLemma().compareTo(l2.getLemma());
		}
	}
}
