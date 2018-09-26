package editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import model.EntitiesList;
import model.Entity;
import model.Unit;
import ui.TextStyler;

public class EntityTableViewer extends MiMusTableViewer {

	private EntitiesList entities;

	public EntityTableViewer(Composite parent, TextStyler styler, String[] words) {
		super(parent, styler);
		String[] aux = {"Text", "Type", "Subtype"};
		columnNames = aux;
		entities = new EntitiesList(words);
	}

	@Override
	public TableViewer createTableViewer() {
		tvEnt = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tvEnt.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tvEnt.setUseHashlookup(true);
		tvEnt.setColumnProperties(columnNames);
		
		/* Create cell editors for each column */
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tvEnt.getTable(), SWT.READ_ONLY);
		editors[1] = new ComboBoxCellEditor(tvEnt.getTable(), entityTypesAsStrings(), SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[2] = new ComboBoxCellEditor(tvEnt.getTable(), personTypesAsStrings(), SWT.READ_ONLY | SWT.DROP_DOWN);
		tvEnt.setCellEditors(editors);
		tvEnt.setCellModifier(new EntityCellModifier());
		tvEnt.setContentProvider(new EntityContentProvider());
		tvEnt.setLabelProvider(new EntityLabelProvider());
		tvEnt.setInput(entities);
		tvEnt.getTable().setLinesVisible(true);
		tvEnt.getTable().setHeaderVisible(true);
		tvEnt.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		packColumns();
		
//		tvEnt.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				Entity ent = (Entity) tvEnt.getStructuredSelection().getFirstElement();
//				styler.select(ent.getFrom(), ent.getTo());
//			}
//		});
		return tvEnt;
	}
	
	public void packColumns() {
		for (int i = 0; i < columnNames.length; i++) {
			tvEnt.getTable().getColumn(i).pack();
		}
	}
	
	public List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	private static String[] entityTypesAsStrings() {
		String[] res = new String[Editor.ENTITY_TYPES.length];
		for (int i=0; i<res.length; i++) {
			res[i] = Editor.ENTITY_TYPES[i];
		}
		return res;
	}
	
	private static String[] personTypesAsStrings() {
		String[] res = new String[Editor.PERSON_TYPES.length];
		for (int i=0; i<res.length; i++) {
			res[i] = Editor.PERSON_TYPES[i];
		}
		return res;
	}
	
	private static String[] placeTypesAsStrings() {
		String[] res = new String[Editor.PLACE_TYPES.length];
		for (int i=0; i<res.length; i++) {
			res[i] = Editor.PLACE_TYPES[i];
		}
		return res;
	}
	
	class EntityCellModifier implements ICellModifier {	
		@Override
		public boolean canModify(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			return colIdx != 0;	// Col 0 is Text, the rest can be modified
		}

		@Override
		public Object getValue(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			Entity ent = (Entity) element;
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
			Entity ent = (Entity) ((TableItem) element).getData();
			int valueIdx = (int) value;
			switch(colIdx) {
			case 1:	// Type
				ent.setType(valueIdx);
				if (valueIdx==0) {
					tvEnt.getCellEditors()[2] = new ComboBoxCellEditor(tvEnt.getTable(), personTypesAsStrings(), SWT.READ_ONLY | SWT.DROP_DOWN);
				} else if (valueIdx==2) {
					tvEnt.getCellEditors()[2] = new ComboBoxCellEditor(tvEnt.getTable(), placeTypesAsStrings(), SWT.READ_ONLY | SWT.DROP_DOWN);
				}
				tvEnt.refresh();
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
			Entity ent = (Entity) element;
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
			tvEnt.add(u);
		}
		
		public void removeUnit(Unit u) {
			tvEnt.remove(u);
		}
		
		public void updateUnit(Unit u) {
			tvEnt.update(u, null);
		}
		
	}
	
	public EntitiesList getEntities() {
		return entities;
	}

	public void setEntities(EntitiesList entities) {
		this.entities = entities;
	}

}
