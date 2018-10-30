package editor;

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
import org.eclipse.swt.widgets.TableItem;

import model.MiMusBibEntry;
import model.MiMusReference;
import model.ReferencesList;
import model.Unit;

public class ReferenceTableViewer extends MiMusTableViewer {
	
	private ReferencesList references;
	
	public ReferenceTableViewer(Composite parent, ReferencesList references) {
		super(parent);
		this.references = references;
		String[] aux = {"Bibliography Entry", "Page info", "Reference Type"};
		columnNames = aux;
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
		
		/* Cell editors */
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new ComboBoxCellEditor(tv.getTable(), 
				getBibEntriesText(), SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new ComboBoxCellEditor(tv.getTable(), 
				SharedResources.getInstance().getReferenceTypes(), 
				SWT.READ_ONLY | SWT.DROP_DOWN);
		tv.setCellEditors(editors);
		tv.setCellModifier(new ReferenceCellModifier());
		tv.setContentProvider(new ReferenceContentProvider());
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

	private String[] getBibEntriesText() {
		String[] res = new String[references.getBibEntries().size()];
		for (int i=0; i<references.getBibEntries().size(); i++) {
			res[i] = references.getBibEntries().get(i).getShortReference();
		}
		return res;
	}
	
	public void reflectEntitiesChanged() {
		CellEditor[] editors = tv.getCellEditors();
		((ComboBoxCellEditor) editors[0]).setItems(getBibEntriesText());
		tv.refresh();
	}
	
	class ReferenceCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			MiMusReference ref = (MiMusReference) element;
			System.out.println(ref.toString());
			switch(colIdx) {
			case 0:	// BibEntry
				return ref.getBibEntry().getId();
			case 1: // Page
				return ref.getPage();
			case 2:	// Reference Type
				return ref.getType();
			default:	// Should never reach here
				return 0;
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			int colIdx = getColumnNames().indexOf(property);
			MiMusReference ref = (MiMusReference) ((TableItem) element).getData();
			switch (colIdx) {
			case 0:	// BibEntry
				int valueIdx = (int) value;
				MiMusBibEntry newEntry = null;
				for (MiMusBibEntry ent: references.getBibEntries()) {
					if (ent.getId() == valueIdx)
						newEntry = ent;
				}
				if (newEntry != null)
					ref.setBibEntry(newEntry);
				break;
			case 1:	// Page
				/* Pass from the idx in the checkbox to the ID in the EntityList */
				String page = (String) value;
				ref.setPage(page);
				break;
			case 2:	// Reference Type
				ref.setType((int) value);
			default:	// Should never reach here
				break;
			}
			references.unitChanged(ref);
		}
	}
	
	class ReferenceLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			MiMusReference ref = (MiMusReference) element;
			switch (columnIndex) {
			case 0:	// BibEntry as a short reference
				return ref.getBibEntry().getShortReference();
			case 1: // Page
				return ref.getPage();
			case 2:	// Reference TYpe
				return SharedResources.getInstance().getReferenceTypes()[ref.getType()];
			default:
				return "";
			}
		}
	}
	
	class ReferenceContentProvider implements MiMusContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((ReferencesList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((ReferencesList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			references.removeChangeListener(this);
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return references.getUnits().toArray();
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
	
	class ReferenceComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			MiMusReference ref1 = (MiMusReference) e1;
			MiMusReference ref2 = (MiMusReference) e2;
			return ref1.getBibEntry().getYear().compareTo(ref2.getBibEntry().getYear());
		}
	}
	
	public ReferencesList getReferences() {
		return references;
	}
	public void setReferences(ReferencesList references) {
		this.references = references;
	}
}
