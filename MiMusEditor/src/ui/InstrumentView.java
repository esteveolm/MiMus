package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import model.Instrument;
import persistence.InstrumentDao;
import persistence.UnitDao;
import ui.table.InstrumentTableViewer;

/**
 * Declarative view for Instrument entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class InstrumentView extends EntityView<Instrument> {
	
	/* Form fields */
	private Text textNom;
	private Combo comboFamily;
	private Combo comboClasse;
	private Text textPart;
		
	public InstrumentView() {
		super();
		
		try {
			setUnits(new InstrumentDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load instruments from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Instrument";
	}

	@Override
	public void developForm(Form form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);

		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		textNom = addTextControl(sectAdd.getParent(), "Nom:");
		comboFamily = addComboControl(sectAdd.getParent(), "Família:", Instrument.FAMILIES);
		/* Default selection at start lets comboClasse know what to load */
		comboFamily.select(0);	

		comboClasse = addComboControl(sectAdd.getParent(), "Classe:", Instrument.CLASSES[0]); /* At start, comboFamily at 0 */
		comboClasse.select(0);
		
		/* When comboFamily changes, update comboClasse options */
		comboFamily.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboClasse.setItems(Instrument.CLASSES[comboFamily.getSelectionIndex()]);
				comboClasse.select(0);
			}
		});
		
		textPart = addTextControl(sectAdd.getParent(), "Part:");
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		
		/* Table for instruments created */
		Section sectTable = new Section(form.getBody(), 0);
		
		InstrumentTableViewer instrumentHelper = 
				new InstrumentTableViewer(sectTable.getParent(), getUnits());
		setTv(instrumentHelper.createTableViewer());
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createEditAction();
		
	}
	
	
	@Override
	protected Instrument getUnitToSave() {
	
				if (comboFamily.getSelectionIndex()==-1) {
					System.out.println("Could not create Instrument because no family was selected.");
					MessageDialog.openError(null, "Save Instrument", "You must specify a family to create an Instrument.");
					return null;
				} else if (comboClasse.getSelectionIndex()==-1) {
					System.out.println("Could not create Instrument because no classe was selected.");
					MessageDialog.openError(null, "Save Instrument", "You must specify a classe to create an Instrument.");
					return null;
				} else {
					Instrument inst = new Instrument(
							0, 0,
							textNom.getText(),
							comboFamily.getSelectionIndex(),
							comboClasse.getSelectionIndex(),
							textPart.getText());
					return inst;
				}
	}

	@Override
	protected void fillFieldsFromSelection(Instrument ent) {
		textNom.setText(ent.getNom());
		comboFamily.select(ent.getFamily());
		comboClasse.setItems(
				Instrument.CLASSES[comboFamily.getSelectionIndex()]);
		comboClasse.select(ent.getClasse());
		textPart.setText(ent.getPart());
	}

	@Override
	public List<Instrument> retrieveUnits() throws SQLException {
		return new InstrumentDao().selectAll();
	}

	@Override
	protected UnitDao<Instrument> getDao() throws SQLException {
		return new InstrumentDao();
	}

}
