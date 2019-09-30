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
import model.GenereLiterari;

/**
 * TableViewer for GenereLiterari view.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class GenereTableViewer extends DeclarativeTableViewer {

	public GenereTableViewer(Composite parent, List<GenereLiterari> generes) {
		super(parent, generes);
		String[] aux = {"Nom complet", "Nom francès", "Nom occità", "Definició"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new GenereLabelProvider());
		tv.setComparator(new GenereComparator());
	}
	
	class GenereLabelProvider extends LabelProvider 
		implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
		@Override
		public String getColumnText(Object element, int columnIndex) {
			GenereLiterari gen = (GenereLiterari) element;
			switch(columnIndex) {
			case 0:	// Nom Complet (Text)
				return gen.getNomComplet();
			case 1:	// Nom Frances (Text)
				return gen.getNomFrances();
			case 2:	// Nom Occita (Text)
				return gen.getNomOccita();
			case 3:	// Definicio (Text)
				return gen.getDefinicio();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class GenereComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			GenereLiterari gen1 = (GenereLiterari) e1;
			GenereLiterari gen2 = (GenereLiterari) e2;
			return gen1.getLemma().compareTo(gen2.getLemma());
		}
	}
}
