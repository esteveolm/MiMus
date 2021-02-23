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
import model.Ofici;

/**
 * TableViewer for Ofici view.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class OficiTableViewer extends DeclarativeTableViewer {

	private static final String[] ESPECIALITATS = {"-", "sense especificar", 
			"instrument", "veu", "dansa", "artesà", "malabars i altres"};
	
	public OficiTableViewer(Composite parent, List<Ofici> oficis, 
			List<Instrument> instruments) {
		super(parent, oficis);
		String[] aux = {"Nom complet", "Terme genèric", "Especialitat", "Instrument", "Observacions"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE | SWT.READ_ONLY);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[4] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setLabelProvider(new OficiLabelProvider());
		tv.setComparator(new OficiComparator());
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
			case 4:
				return ofici.getObservacions().replaceAll("\n", "");
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
