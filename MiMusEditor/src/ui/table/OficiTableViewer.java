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
import model.Instrument;
import model.Ofici;
import model.Unit;

public class OficiTableViewer extends DeclarativeTableViewer {

	private static final String[] ESPECIALITATS = {"Sense especificar", "Instrument",
				"Veu", "Dansa", "Artesà", "Malabars i altres"};
	
	public OficiTableViewer(Composite parent, List<Unit> oficis) {
		super(parent);
		this.entities = oficis;
		String[] aux = {"Nom complet", "Terme genèric", "Especialitat", "Instrument"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE | SWT.READ_ONLY);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setCellModifier(new OficiCellModifier());
		tv.setLabelProvider(new OficiLabelProvider());
		tv.setComparator(new OficiComparator());
	}
	
	class OficiCellModifier implements ICellModifier {
		@Override
		public boolean canModify(Object element, String property) {
			return true;
		}

		@Override
		public Object getValue(Object element, String property) {
			Ofici ofici = (Ofici) element;
			int colIdx = getColumnNames().indexOf(property);
			switch (colIdx) {
			case 0:		// Nom Complet
				return ofici.getNomComplet();
			case 1:		// Terme
				return ofici.getTerme();
			case 2:		// Especialitat
				return ofici.getEspecialitat();
			case 3:		// Instrument
				return ofici.getInstrument();
			default:
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			Ofici ofici = (Ofici) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch (colIdx) {
			case 0:		// Nom Complet
				ofici.setNomComplet((String) value);
				break;
			case 1:		// Terme
				ofici.setTerme((String) value);
				break;
			case 2:		// Especialitat
				ofici.setEspecialitat((int) value);
				break;
			case 3:		// Instrument
				Instrument inst = (Instrument) Unit.findUnit(
						SharedResources.getInstance().getInstruments(), 
						(int) value);
				if (inst != null)
					ofici.setInstrument(inst);
				break;
			default:	// Shouldn't reach here
				break;
			}
		}
	}
	
	class OficiLabelProvider extends LabelProvider 
			implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Ofici ofici = (Ofici) element;
			switch (columnIndex) {
			case 0:		// Nom Complet
				return ofici.getNomComplet();
			case 1:		// Terme
				return ofici.getTerme();
			case 2:		// Especialitat
				return ESPECIALITATS[ofici.getEspecialitat()];
			case 3:		// Instrument
				Instrument inst = ofici.getInstrument();
				if (inst != null)
					return inst.getLemma();
				return "";	/* If no instrument, leave field blank */
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class OficiComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Ofici o1 = (Ofici) e1;
			Ofici o2 = (Ofici) e2;
			return o1.getLemma().compareTo(o2.getLemma());
		}
	}
}
