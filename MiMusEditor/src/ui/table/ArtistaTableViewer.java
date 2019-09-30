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
import model.Artista;

/**
 * TableViewer for Artista view.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ArtistaTableViewer extends DeclarativeTableViewer {
	
	public ArtistaTableViewer(Composite parent, List<Artista> artists) {
		super(parent, artists);
		String[] aux = {"Nom complet", "Tractament", "Nom", "Cognom", "Sobrenom",
				"Distintiu", "Gènere", "Religió", "Origen", "Observacions"};
		this.columnNames = aux;
	}
	
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[4] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[5] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[6] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[7] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[8] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[9] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}
	
	public void developProviders() {
		tv.setLabelProvider(new ArtistaLabelProvider());
		tv.setComparator(new ArtistaComparator());
	}
	
	class ArtistaLabelProvider extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Artista art = (Artista) element;
			switch(columnIndex) {
			case 0:	// Nom Complet (Text)
				return art.getNomComplet();
			case 1:	// Tractament (Text)
				return art.getTractament();
			case 2:	// Nom (Text)
				return art.getNom();
			case 3:	// Cognom (Text)
				return art.getCognom();
			case 4:	// Sobrenom (Text)
				return art.getSobrenom();
			case 5:	// Distintiu (Text)
				return art.getDistintiu();
			case 6:	// Gènere (ComboBox)
				return art.getGenereStr();
			case 7:	// Religió (ComboBox)
				return art.getReligioStr();
			case 8:	// Origen (Text)
				return art.getOrigen();
			case 9:	// Observacions (Text)
				return art.getObservacions();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class ArtistaComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Artista art1 = (Artista) e1;
			Artista art2 = (Artista) e2;
			return art1.getLemma().compareTo(art2.getLemma());
		}
	}
}
