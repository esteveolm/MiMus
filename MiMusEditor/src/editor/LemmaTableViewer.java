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

import model.EntitiesList;
import model.Lemma;
import model.LemmasList;
import model.Unit;
import ui.TextStyler;

public class LemmaTableViewer extends RelationTableViewer {
	
	private LemmasList lemmas;

	public LemmaTableViewer(Composite parent, TextStyler styler, EntitiesList regestEntities, EntitiesList transcriptionEntities) {
		super(parent, styler, regestEntities);
		String[] cols = {"Word form", "Lemma"};
		columnNames = cols;
		this.lemmas = new LemmasList(regestEntities, transcriptionEntities);
		transcriptionEntities.setRelations(this);
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
		editors[1] = new ComboBoxCellEditor(tv.getTable(), getEntitiesText(), SWT.READ_ONLY | SWT.DROP_DOWN);
		tv.setCellEditors(editors);
		tv.setCellModifier(new LemmaCellModifier());
		tv.setContentProvider(new LemmaContentProvider());
		tv.setLabelProvider(new LemmaLabelProvider());
		tv.setInput(lemmas);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new LemmaComparator());
		packColumns();
		return tv;
	}

	public void reflectEntitiesChanged() {
		CellEditor[] editors = tv.getCellEditors();
		((ComboBoxCellEditor) editors[1]).setItems(getEntitiesText());
		tv.refresh();
	}
	
	class LemmaCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			return colIdx != 0;	// Col 0 is Text, Col 1 is ComboBox
		}

		@Override
		public Object getValue(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			Lemma lemma = (Lemma) element;
			switch (colIdx) {
			case 1:	// Regest entity
				return lemma.getRegestEntity();
			default:	// Includes Transcription entity (Text)
				return 0;
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			int colIdx = getColumnNames().indexOf(property);
			Lemma lemma = (Lemma) ((TableItem) element).getData();
			int valueIdx = (int) value;
			switch (colIdx) {
			case 1:	// Regest entity
				lemma.setRegestEntity(valueIdx);
				break;
			default:	// Includes Transcription entity (non-editable by table)
				break;
			}
			lemmas.unitChanged(lemma);
		}
	}
	
	class LemmaLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Lemma lemma = (Lemma) element;
			switch (columnIndex) {
			case 0:	// Transcription entity
				return lemma.getTranscriptionEntityText();
			case 1:	// Regest entity
				return lemma.getRegestEntityText();
			default:
				return "";
			}
		}
	}
	
	class LemmaContentProvider implements MiMusContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((LemmasList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((LemmasList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			lemmas.removeChangeListener(this);
		}
		
		@Override
		public Object[] getElements(Object inputElement) {
			return lemmas.getUnits().toArray();
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
	
	public class LemmaComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Lemma l1 = (Lemma) e1;
			Lemma l2 = (Lemma) e2;
			if (l1.getRegestEntityObject().equals(l2.getRegestEntityObject())) {
				/* Same Regest, order by Transcription position (from-based) */
				return l1.getTranscriptionEntityObject().getFrom()-l2.getTranscriptionEntityObject().getFrom();
			} else {
				/* Different Regest, order by Regest position (from-based) */
				return l1.getRegestEntityObject().getFrom()-l2.getRegestEntityObject().getFrom();
			}
		}
	}
	
	public LemmasList getLemmas() {
		return lemmas;
	}
	public void setLemmas(LemmasList lemmas) {
		this.lemmas = lemmas;
	}
}
