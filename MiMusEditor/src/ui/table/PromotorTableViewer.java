package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import model.Promotor;

public class PromotorTableViewer extends DeclarativeTableViewer {
	
	private static final String[] GENDERS = {"No marcat", "Home", "Dona"};
	
	public PromotorTableViewer(Composite parent, List<Promotor> promotors) {
		super(parent);
		this.entities = promotors;
		String[] aux = {"Nom complet", "Nom", "Cognom", "Sobrenom",
				"Distintiu", "Gènere", "Observacions"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[4] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[5] = new ComboBoxCellEditor(tv.getTable(), GENDERS, 
				SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[6] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new PromotorLabelProvider());
		tv.setComparator(new PromotorComparator());
	}
	
	class PromotorLabelProvider extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Promotor prom = (Promotor) element;
			switch (columnIndex) {
			case 0:		// Nom Complet
				return prom.getNomComplet();
			case 1:		// Nom
				return prom.getNom();
			case 2:		// Cognom
				return prom.getCognom();
			case 3:		// Sobrenom
				return prom.getSobrenom();
			case 4:		// Distintiu
				return prom.getDistintiu();
			case 5:		// Genere
				return GENDERS[prom.getGenere()];
			case 6:		// Observacions
				return prom.getObservacions();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class PromotorComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Promotor p1 = (Promotor) e1;
			Promotor p2 = (Promotor) e2;
			return p1.getLemma().compareTo(p2.getLemma());
		}
	}
}
