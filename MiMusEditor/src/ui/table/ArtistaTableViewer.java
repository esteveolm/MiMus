package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
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

import control.SharedResources;
import model.Artista;
import model.Ofici;
import model.Unit;

public class ArtistaTableViewer extends DeclarativeTableViewer {
	
	public ArtistaTableViewer(Composite parent, List<Unit> artists) {
		super(parent);
		this.entities = artists;
		String[] aux = {"Nom Complet", "Tractament", "Nom", "Cognom", "Sobrenom",
				"Gènere", "Religió", "Origen", "Ofici"};
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
			case 0:	// Nom Complet	(Text)
				return art.getNombreCompleto();
			case 1:	// Tractament	(Text)
				return art.getNombreCompleto();
			case 2:	// Nom	(Text)
				return art.getNombreCompleto();
			case 3:	// Cognom	(Text)
				return art.getNombreCompleto();
			case 4:	// Sobrenom	(Text)
				return art.getNombreCompleto();
			case 5:	// Gènere (ComboBox)
				return art.getGenero();
			case 6: // Religió (ComboBox)
				return art.getReligion();
			case 7:	// Origen (Text)
				return art.getOrigen();
			case 8:	// Ofici (ComboBox)
				return art.getOfici().getLemma();
			default:	// Shouldn't reach here
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Artista art = (Artista) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Nom Complet (Text)
				art.setNombreCompleto((String) value);
				break;
			case 1:	// Tractament (Text)
				art.setTratamiento((String) value);
				break;
			case 2:	// Nom (Text)
				art.setNombre((String) value);
				break;
			case 3:	// Cognom (Text)
				art.setApellido((String) value);
				break;
			case 4:	// Sobrenom (Text)
				art.setSobrenombre((String) value);
				break;
			case 5:	// Gènere (ComboBox)
				art.setGenero((int) value);
				break;
			case 6:	// Religió (ComboBox)
				art.setGenero((int) value);
				break;
			case 7:	// Origen (ComboBox)
				art.setOrigen((String) value);
				break;
			case 8:	// Ofici (ComboBox)
				Ofici ofici = (Ofici) Unit.findUnit(
						SharedResources.getInstance().getOficis(),
						(int) value);
				if (ofici != null)
					art.setOfici(ofici);
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
			case 0:	// Nom Complet (Text)
				return art.getNombreCompleto();
			case 1:	// Tractament (Text)
				return art.getTratamiento();
			case 2:	// Nom (Text)
				return art.getNombre();
			case 3:	// Cognom (Text)
				return art.getApellido();
			case 4:	// Sobrenom (Text)
				return art.getSobrenombre();
			case 5:	// Gènere (ComboBox)
				return art.getGeneroStr();
			case 6:	// Religió (ComboBox)
				return art.getReligionStr();
			case 7:	// Origen (Text)
				return art.getOrigen();
			case 8:	// Ofici
				Ofici ofici = art.getOfici();
				if (ofici != null)
					return ofici.getLemma();
				return "";
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
