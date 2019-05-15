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

import control.SharedResources;
import model.Instrument;

public class InstrumentTableViewer extends DeclarativeTableViewer {

	public InstrumentTableViewer(Composite parent, List<Instrument> instruments) {
		super(parent);
		this.entities = instruments;
		String[] aux = {"Nom", "Família", "Classe", "Part"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new ComboBoxCellEditor(tv.getTable(), 
				SharedResources.FAMILY,	SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[2] = new ComboBoxCellEditor(tv.getTable(), 
				SharedResources.CLASSE,	SWT.READ_ONLY | SWT.DROP_DOWN);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setCellModifier(new InstrumentCellModifier());
		tv.setLabelProvider(new InstrumentLabelProvider());
		tv.setComparator(new InstrumentComparator());
	}
	
	class InstrumentCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return true;	/* All fields can be modified */
		}

		@Override
		public Object getValue(Object element, String property) {
			Instrument inst = (Instrument) element;
			int colIdx = getColumnNames().indexOf(property);
			switch (colIdx) {
			case 0:	// Name (text)
				return inst.getNom();
			case 1:	// Family (combo)
				return inst.getFamily();
			case 2:	// Classe (combo)
				return inst.getClasse();
			case 3:	// Part (text)
				return inst.getPart();
			default:	// This should never happen
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Instrument inst = (Instrument) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch (colIdx) {
			case 0:	// Name (text)
				inst.setNom((String) value);
				break;
			case 1:	// Family (combo)
				inst.setFamily((int) value);
				break;
			case 2:	// Classe (combo)
				inst.setClasse((int) value);
				break;
			case 3:	// Part (text)
				inst.setPart((String) value);
			default:	// This should never happen
				break;
			}
		}
	}
	
	class InstrumentLabelProvider extends LabelProvider
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Instrument inst = (Instrument) element;
			switch (columnIndex) {
			case 0:	// Name (text)
				return inst.getNom();
			case 1:	// Family (combo)
				return SharedResources.FAMILY[inst.getFamily()];
			case 2: // Classe (combo)
				return SharedResources.CLASSE[inst.getClasse()];
			case 3:	// Part (text)
				return inst.getPart();
			default:	// This should never happen
				return "";
			}
		}
	}
	
	class InstrumentComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Instrument i1 = (Instrument) e1;
			Instrument i2 = (Instrument) e2;
			int byFamily = i1.getFamily() - i2.getFamily();
			if (byFamily == 0) {
				int byClasse = i1.getClasse() - i2.getClasse();
				if (byClasse == 0) {
					return i1.getNom().compareTo(i2.getNom());
				}
				return byClasse;
			}		
			return byFamily;
		}
	}

}
