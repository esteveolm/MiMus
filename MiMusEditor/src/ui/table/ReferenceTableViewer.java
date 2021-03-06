package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import model.Bibliography;
import model.Document;
import model.MiMusReference;

/**
 * TableViewer for annotated bibliography in the Editor, that is, table
 * containing References.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ReferenceTableViewer extends MiMusTableViewer {
	
	private List<MiMusReference> references;
	private List<Bibliography> bibEntries;
	
	public ReferenceTableViewer(Composite parent, List<MiMusReference> references, 
			List<Bibliography> bibEntries, Document docEntry) {
		super(parent);
		this.references = references;
		this.bibEntries = bibEntries;
		String[] aux = {"Tipus de referència", "Referència bibliogràfica", 
				"Pàgina", "Nota"};
		columnNames = aux;
	}

	/**
	 * Creates the table viewer and leaves it ready to use.
	 */
	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		
		/* Cell editors */
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new ComboBoxCellEditor(tv.getTable(), 
				MiMusReference.TYPES, 
				SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[1] = new ComboBoxCellEditor(tv.getTable(), 
				getBibEntriesText(), SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		
		tv.setCellEditors(editors);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setLabelProvider(new ReferenceLabelProvider());
		tv.setInput(references);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new ReferenceComparator());
		packColumns();
		return tv;
	}

	/**
	 * Return an array of short reference representations.
	 */
	private String[] getBibEntriesText() {
		List<Bibliography> entries = getBibEntries();
		String[] res = new String[entries.size()];
		for (int i=0; i<entries.size(); i++) {
			res[i] = entries.get(i).getShortReference();
		}
		return res;
	}
	
	/**
	 * When ReferenceTableViewer is refreshed, ComboBox options in the
	 * column for bibliography entries must be reloaded, because the
	 * list of entries could have changed.
	 */
	@Override
	public void refresh() {
		super.refresh();
		CellEditor[] editors = tv.getCellEditors();
		((ComboBoxCellEditor) editors[0]).setItems(getBibEntriesText());
		tv.refresh();
	}
	
	/**
	 * Label provider telling how to present references in the table.
	 */
	class ReferenceLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			MiMusReference ref = (MiMusReference) element;
			switch (columnIndex) {
			case 0:	// Reference Type
				return MiMusReference.TYPES[ref.getType()];
			case 1:	// BibEntry as a short reference
				return ref.getItsBiblio().getShortReference();
			case 2: // Page
				return ref.getPage();
			case 3:	// Nota
				if (ref.getItsNote() != null) {
					return ref.getItsNote().getText().substring(0, 
							ref.getItsNote().getText().indexOf('}') +1);
				} else {
					return "-";
				}
				
			default:
				return "";
			}
		}
	}
	
	/**
	 * References are sorted by year, alphanumerically.
	 */
	class ReferenceComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			MiMusReference ref1 = (MiMusReference) e1;
			MiMusReference ref2 = (MiMusReference) e2;
			return ref1.getItsBiblio().getYear()
					.compareTo(ref2.getItsBiblio().getYear());
		}
	}
	
	public List<MiMusReference> getReferences() {
		return references;
	}
	public void setReferences(List<MiMusReference> references) {
		this.references = references;
	}
	public List<Bibliography> getBibEntries() {
		return bibEntries;
	}
	public void setBibEntries(List<Bibliography> bibEntries) {
		this.bibEntries = bibEntries;
	}
}
