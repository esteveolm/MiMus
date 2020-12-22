package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import model.Instrument;

/**
 * TableViewer for Instrument view.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class InstrumentTableViewer extends DeclarativeTableViewer {

	public InstrumentTableViewer(Composite parent, List<Instrument> instruments) {
		super(parent, instruments);
		String[] aux = {"Nom", "Família", "Classe", "Part", "Observacions"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[4] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new InstrumentLabelProvider());
		tv.setComparator(new InstrumentComparator());
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
				return Instrument.FAMILIES[inst.getFamily()];
			case 2: // Classe (combo)
				return Instrument.CLASSES[inst.getFamily()][inst.getClasse()];
			case 3:	// Part (text)
				return inst.getPart();
			case 4: 
				return inst.getObservacions();
			default:	// This should never happen
				return "";
			}
		}
	}
	
	class InstrumentComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Instrument i1 = (Instrument) e1;
			Instrument i2 = (Instrument) e2;
			return i1.getLemma().compareTo(i2.getLemma());
		}
	}

}
