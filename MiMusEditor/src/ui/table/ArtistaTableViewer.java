package ui.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import model.Artista;
import model.EntitiesList;
import model.Unit;

public class ArtistaTableViewer extends MiMusTableViewer {

	private EntitiesList artists;
	
	public ArtistaTableViewer(Composite parent, EntitiesList artists) {
		super(parent);
		this.artists = artists;
		String[] aux = {"Name", "Sex"};
		this.columnNames = aux;
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
		
		/* Create cell editors for each column */
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.READ_ONLY);
		editors[1] = new ComboBoxCellEditor(tv.getTable(), new String[0], SWT.READ_ONLY | SWT.DROP_DOWN);
		tv.setCellEditors(editors);
		tv.setCellModifier(new ArtistaCellModifier());
		tv.setContentProvider(new ArtistaContentProvider());
		tv.setLabelProvider(new ArtistaLabelProvider());
		tv.setInput(artists);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new ArtistaComparator());
		packColumns();
		return tv;
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
				return art.getName();
			case 1:	// Sex (ComboBox)
				return art.isFemale() ? 1 : 0;
			default:	// Shouldn't reach here
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Artista art = (Artista) element;
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Name (Text)
				String newName = (String) value;
				art.setName(newName);
				break;
			case 1:	// Sex (ComboBox)
				int selectionIdx = (int) value;
				art.setFemale(selectionIdx>0);
				break;
			default:	// Shouldn't reach here
				break;
			}
			artists.unitChanged(art);
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
				return art.getName();
			case 1:	// Sex (ComboBox)
				return art.getGender();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class ArtistaContentProvider implements MiMusContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((EntitiesList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((EntitiesList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			artists.removeChangeListener(this);
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return artists.getUnits().toArray();
		}

		@Override
		public void addUnit(Unit u) {
			tv.add(u);
		}

		@Override
		public void removeUnit(Unit u) {
			tv.remove(u);
		}

		@Override
		public void updateUnit(Unit u) {
			tv.update(u, null);
			tv.refresh();
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
