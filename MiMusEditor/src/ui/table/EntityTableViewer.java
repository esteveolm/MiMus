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
import org.eclipse.swt.widgets.TableItem;

import model.EntitiesList;
import model.Entity;
import model.MiMusText;
import model.TypedEntity;
import model.Unit;
import ui.IllegalTextRangeException;
import util.TextStyler;

public class EntityTableViewer extends MiMusTableViewer {

	private EntitiesList entities;

	public EntityTableViewer(Composite parent, TextStyler styler, MiMusText text) {
		super(parent, styler);
		String[] aux = {"Text", "Type", "Subtype"};
		columnNames = aux;
		entities = new EntitiesList(text.getWords());
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
		editors[1] = new ComboBoxCellEditor(tv.getTable(), TypedEntity.ENTITY_TYPES, SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[2] = new ComboBoxCellEditor(tv.getTable(), TypedEntity.PERSON_TYPES, SWT.READ_ONLY | SWT.DROP_DOWN);
		tv.setCellEditors(editors);
		tv.setCellModifier(new EntityCellModifier());
		tv.setContentProvider(new EntityContentProvider());
		tv.setLabelProvider(new EntityLabelProvider());
		tv.setInput(entities);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new EntityComparator());
		packColumns();
		return tv;
	}
	
//	private static String[] entityTypesAsStrings() {
//		String[] res = new String[Editor.ENTITY_TYPES.length];
//		for (int i=0; i<res.length; i++) {
//			res[i] = Editor.ENTITY_TYPES[i];
//		}
//		return res;
//	}
//	
//	private static String[] personTypesAsStrings() {
//		String[] res = new String[Editor.PERSON_TYPES.length];
//		for (int i=0; i<res.length; i++) {
//			res[i] = Editor.PERSON_TYPES[i];
//		}
//		return res;
//	}
//	
//	private static String[] placeTypesAsStrings() {
//		String[] res = new String[Editor.PLACE_TYPES.length];
//		for (int i=0; i<res.length; i++) {
//			res[i] = Editor.PLACE_TYPES[i];
//		}
//		return res;
//	}
	
	class EntityCellModifier implements ICellModifier {	
		@Override
		public boolean canModify(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			return colIdx != 0;	// Col 0 is Text, the rest can be modified
		}

		@Override
		public Object getValue(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			TypedEntity ent = (TypedEntity) element;
			System.out.println(ent.toString());
			switch(colIdx) {
			case 1:	// Type
				return ent.getType();
			case 2: // Subtype
				return ent.getSubtype();
			default:	// Includes exceptions AND case 0 (Text)
				return 0;
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			int colIdx = getColumnNames().indexOf(property);
			TypedEntity ent = (TypedEntity) ((TableItem) element).getData();
			int valueIdx = (int) value;
			switch(colIdx) {
			case 1:	// Type
				ent.setType(valueIdx);
				if (valueIdx==0) {
					tv.getCellEditors()[2] = new ComboBoxCellEditor(tv.getTable(), TypedEntity.PERSON_TYPES, SWT.READ_ONLY | SWT.DROP_DOWN);
				} else if (valueIdx==2) {
					tv.getCellEditors()[2] = new ComboBoxCellEditor(tv.getTable(), TypedEntity.PLACE_TYPES, SWT.READ_ONLY | SWT.DROP_DOWN);
				}
				tv.refresh();
				break;
			case 2:	// Subtype
				ent.setSubtype(valueIdx);
				break;
			default:	// Includes exceptions AND case 0 (Text)
				break;
			}
			entities.unitChanged(ent);
		}
	}
	
	class EntityLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			TypedEntity ent = (TypedEntity) element;
			switch (columnIndex) {
			case 0:	// Text
				try {
					return ent.getText();
				} catch(IllegalTextRangeException e) {
					return "ERROR FROM>TO";
				}
			case 1: // Type
				return ent.getTypeWord();
			case 2: // Subtype
				return ent.getSubtypeWord();
			default:
				return "";
			}
		}
	}
	
	public class EntityContentProvider implements MiMusContentProvider {		
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
			entities.removeChangeListener(this);
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return entities.getUnits().toArray();
		}
		
		public void addUnit(Unit u) {
			tv.add(u);
		}
		
		public void removeUnit(Unit u) {
			tv.remove(u);
		}
		
		public void updateUnit(Unit u) {
			tv.update(u, null);
			tv.refresh();
		}
	}
	
	public class EntityComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Entity ent1 = (Entity) e1;
			Entity ent2 = (Entity) e2;
			return ent1.getFrom()-ent2.getFrom();
		}
	}
	
	public EntitiesList getEntities() {
		return entities;
	}

	public void setEntities(EntitiesList entities) {
		this.entities = entities;
	}

}
