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
import model.Casa;

public class CasaTableViewer extends DeclarativeTableViewer {

	public CasaTableViewer(Composite parent, List<Casa> cases) {
		super(parent, cases);
		String[] aux = {"Nom complet", "Títol", "Cort"};
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
		tv.setLabelProvider(new CasaLabelProvider());
		tv.setComparator(new CasaComparator());
	}
	
	class CasaLabelProvider extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Casa casa = (Casa) element;
			switch (columnIndex) {
			case 0:		// Nom Complet
				return casa.getNomComplet();
			case 1:		// Titol
				return casa.getTitol();
			case 2:		// Cort
				return casa.getCort();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class CasaComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Casa c1 = (Casa) e1;
			Casa c2 = (Casa) e2;
			return c1.getLemma().compareTo(c2.getLemma());
		}
	}

}
