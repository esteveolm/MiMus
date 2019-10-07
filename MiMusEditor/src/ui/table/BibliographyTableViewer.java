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

import model.Bibliography;

public class BibliographyTableViewer extends DeclarativeTableViewer {

	public BibliographyTableViewer(Composite parent, List<Bibliography> entities) {
		super(parent, entities);
		String[] aux = {"Bibliografia"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new BibliographyLabelProvider());
		tv.setComparator(new BibliographyComparator());
	}
	
	class BibliographyLabelProvider extends LabelProvider 
			implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Bibliography biblio = (Bibliography) element;
			switch(columnIndex) {
			case 0:	// Short Reference
				return biblio.getShortReference();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class BibliographyComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Bibliography b1 = (Bibliography) e1;
			Bibliography b2 = (Bibliography) e2;
			return b1.getShortReference().compareTo(b2.getShortReference());
		}
	}

}
