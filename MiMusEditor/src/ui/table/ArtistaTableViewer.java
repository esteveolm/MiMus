package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import model.Artista;
import model.Unit;

public class ArtistaTableViewer extends DeclarativeTableViewer {

	private static final String[] GENDERS = {"Male", "Female"};
	
	public ArtistaTableViewer(Composite parent, List<Unit> artists) {
		super(parent);
		this.entities = artists;
		String[] aux = {"Name", "Sex"};
		this.columnNames = aux;
	}
	
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new ComboBoxCellEditor(tv.getTable(), GENDERS, 
				SWT.READ_ONLY | SWT.DROP_DOWN);
		return editors;
	}
	
	public void developProviders() {
		tv.setCellModifier(new ArtistaCellModifier());
		tv.setLabelProvider(new ArtistaLabelProvider());
		tv.setComparator(new ArtistaComparator());
	}
	
	class ArtistaCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return true;	/* All fields can be modified */
		}

		@Override
		public Object getValue(Object element, String property) {
			Artista art = (Artista) element;
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Name	(Text)
				return art.getNombreCompleto();
			case 1:	// Sex (ComboBox)
				return art.getGenero();
			default:	// Shouldn't reach here
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Artista art = (Artista) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Name (Text)
				String newName = (String) value;
				art.setNombreCompleto(newName);
				break;
			case 1:	// Sex (ComboBox)
				int selectionIdx = (int) value;
				art.setGenero(selectionIdx);
				break;
			default:	// Shouldn't reach here
				break;
			}
		}
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
			case 0:	// Name (Text)
				return art.getNombreCompleto();
			case 1:	// Sex (ComboBox)
				return art.getGeneroStr();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class ArtistaComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Artista art1 = (Artista) e1;
			Artista art2 = (Artista) e2;
			return art1.getId()-art2.getId();
		}
	}
}
