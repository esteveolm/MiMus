package editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import model.EntitiesList;
import model.Relation;
import model.RelationsList;
import model.Unit;
import ui.TextStyler;

public class RelationTableViewer extends MiMusTableViewer {

	private RelationsList relations;

	public RelationTableViewer(Composite parent, TextStyler styler, EntitiesList entities) {
		super(parent, styler);
		String[] aux = {"Entity A", "Entity B", "Type"};
		columnNames = aux;
		relations = new RelationsList(entities);
		entities.setRelations(this);
	}

	@Override
	public TableViewer createTableViewer() {
		tvRel = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tvRel.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tvRel.setUseHashlookup(true);
		tvRel.setColumnProperties(columnNames);
		
		/* Create cell editors for each column */
		ComboBoxCellEditor[] editors = new ComboBoxCellEditor[columnNames.length];
		editors[0] = new ComboBoxCellEditor(tvRel.getTable(), getEntitiesText(), SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[1] = new ComboBoxCellEditor(tvRel.getTable(), getEntitiesText(), SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[2] = new ComboBoxCellEditor(tvRel.getTable(), relationTypesAsStrings(), SWT.READ_ONLY | SWT.DROP_DOWN);
		tvRel.setCellEditors(editors);
		tvRel.setCellModifier(new RelationCellModifier());
		tvRel.setContentProvider(new RelationContentProvider());
		tvRel.setLabelProvider(new RelationLabelProvider());
		tvRel.setInput(relations);
		tvRel.getTable().setHeaderVisible(true);
		tvRel.getTable().setLinesVisible(true);
		tvRel.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		packColumns();
		return tvRel;
	}
	
	public void packColumns() {
		for (int i = 0; i < columnNames.length; i++) {
			tvRel.getTable().getColumn(i).pack();
		}
	}
	
	private static String[] relationTypesAsStrings() {
		String[] res = new String[Editor.RELATION_TYPES.length];
		for (int i=0; i<res.length; i++) {
			res[i] = Editor.RELATION_TYPES[i];
		}
		return res;
	}
	
	public void reflectEntitiesChanged() {
		CellEditor[] editors = tvRel.getCellEditors();
		((ComboBoxCellEditor) editors[0]).setItems(getEntitiesText());
		((ComboBoxCellEditor) editors[1]).setItems(getEntitiesText());
		tvRel.refresh();
	}
	
	class RelationCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			int colIdx = getColumnNames().indexOf(property);
			Relation rel = (Relation) element;
			switch(colIdx) {
			case 0:	// Entity A
				return rel.getEntityA();
			case 1:	// Entity B
				return rel.getEntityB();
			case 2:	// Relation type
				return rel.getType();
			default:
				return 0;
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			int colIdx = getColumnNames().indexOf(property);
			Relation rel = (Relation) ((TableItem) element).getData();
			int valueIdx = (int) value;
			switch(colIdx) {
			case 0:	// Entity A
				rel.setEntityA(valueIdx);
				break;
			case 1:	// Entity B
				rel.setEntityB(valueIdx);
				break;
			case 2:	// Relation type
				rel.setType(valueIdx);
				break;
			default:
				break;
			}
			relations.unitChanged(rel);
		}
	}
	
	public List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	class RelationLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Relation rel = (Relation) element;
			switch (columnIndex) {
			case 0:	// Entity A
				return rel.getEntityAText();
			case 1:	// Entity B
				return rel.getEntityBText();
			case 2:	// Relation type
				return rel.getTypeWord();
			default:
				return "";
			}
		}
	}
	
	class RelationContentProvider implements MiMusContentProvider {
		
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null) {
				((RelationsList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((RelationsList) oldInput).removeChangeListener(this);
			}
		}
		
		@Override
		public void dispose() {
			relations.removeChangeListener(this);
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return relations.getUnits().toArray();
		}

		@Override
		public void addUnit(Unit u) {
			tvRel.add(u);
		}

		@Override
		public void removeUnit(Unit u) {
			tvRel.remove(u);
		}

		@Override
		public void updateUnit(Unit u) {
			tvRel.update(u, null);
		}
	}
	
	public RelationsList getRelations() {
		return relations;
	}

	public void setRelations(RelationsList relations) {
		this.relations = relations;
	}
	
	public String[] getEntitiesText() {
		String[] res = new String[relations.getEntities().countUnits()];
		for (int i=0; i<relations.getEntities().countUnits(); i++) {
			try {
				res[i] = relations.getEntities().getUnits().get(i).getText();
			} catch (IllegalTextRangeException e) {
				res[i] = "FATAL ERROR. THIS SHOULD NOT HAPPEN";
			}
		}
		System.out.println("ENTITIES: " + res.length);
		return res;
	}

}
