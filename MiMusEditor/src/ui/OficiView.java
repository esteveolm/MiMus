package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Instrument;
import model.Ofici;
import persistence.InstrumentDao;
import persistence.OficiDao;
import persistence.UnitDao;
import ui.table.OficiTableViewer;

/**
 * Declarative view for Ofici entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class OficiView extends EntityView<Ofici> {

	/* 
	 * List of Instruments needed because are associated with Oficis.
	 */
	private List<Instrument> insts;
	
	/* Form fields */
	private Text textNomComplet;
	private Text textTerme;
	private Combo comboEspecialitat;
	private Combo comboInstrument;
	private Text textObs;
	
	public OficiView() {
		super();
		comboInstrument = null;
		
		try {
			setUnits(new OficiDao().selectAll());
			insts = new InstrumentDao().selectAll();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load oficis from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Ofici";
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		textNomComplet = addTextControl(sectAdd.getParent(), "Nom complet:");
		textTerme = addTextControl(sectAdd.getParent(), "Terme:");
		comboEspecialitat = addComboControl(sectAdd.getParent(), "Especialitat:", Ofici.ESPECIALITATS);
		comboEspecialitat.select(0);
		
		comboInstrument = addComboControl(sectAdd.getParent(), "Instruments:");		
		String[] instNames = new String[insts.size()];
		for (int i=0; i<instNames.length; i++) {
			instNames[i] = ((Instrument) insts.get(i)).getLemma();
		}
		comboInstrument.setItems(instNames);
		comboInstrument.deselectAll();
		comboInstrument.setEnabled(false);
		
		comboEspecialitat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("instrument") ||
					Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("artesà")) {
					/* First, update list of instruments */
					try {
						insts = new InstrumentDao().selectAll();
						String[] instNames = new String[insts.size()];
						for (int i=0; i<instNames.length; i++) {
							instNames[i] = ((Instrument) insts.get(i)).getLemma();
						}
						comboInstrument.setItems(instNames);
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
						} else {
							e2.printStackTrace();
							System.out.println("Could not select Instruments from DB.");
						}
					}
					comboInstrument.setEnabled(true);
					comboInstrument.select(0);
				} else {
					comboInstrument.deselectAll();	/* Clearer if field gets empty */
					comboInstrument.setEnabled(false);
				}
			}
		});
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);		
		
		/* Form buttons */
		addButtons(sectAdd.getParent());

		addAnnotationsLabel(sectAdd.getParent(), grid);
		
		/* Table for oficis created */
		Section sectTable = new Section(form.getBody(), 0);
		
		OficiTableViewer oficiHelper = 
				new OficiTableViewer(sectTable.getParent(), getUnits(), insts);
		setTv(oficiHelper.createTableViewer());
		
		createEditAction();
	}
	
	@Override
	protected Ofici getUnitToSave() {
		
				Instrument inst = null;
				if (insts.size()>0 && comboInstrument.isEnabled() && comboInstrument.getSelectionIndex()>=0) {
					/* Only set Instrument if available */
					inst = (Instrument) insts.get(comboInstrument.getSelectionIndex());
				}
				Ofici ofici = new Ofici(
						0, 0,
						textNomComplet.getText(),
						textTerme.getText(),
						comboEspecialitat.getSelectionIndex(),
						inst,
						textObs.getText());
				return ofici;
	}
	
	@Override
	protected void fillFieldsFromSelection(Ofici ent) {
		textNomComplet.setText(ent.getNomComplet());
		textTerme.setText(ent.getTerme());
		comboEspecialitat.select(ent.getEspecialitat());
		if (Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("instrument") ||
			Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("artesà")) {
			/* If selection has instrument, find it and select it */
			comboInstrument.setEnabled(true);
			for (int i=0; i<insts.size(); i++) {
				if (insts.get(i).getSpecificId() == ent.getInstrument().getSpecificId()) {
					comboInstrument.select(i);
					break;
				}
			}
		} else {
			comboInstrument.deselectAll();	/* Clearer if field gets empty */
			comboInstrument.setEnabled(false);
		}
		textObs.setText(ent.getObservacions());
		
	}

	@Override
	public void addAction() {
		super.addAction();
		comboInstrument.deselectAll();
		comboInstrument.setEnabled(false);
	}


	@Override
	public void editAction() {
		super.editAction();		
		if (!Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("instrument") &&
			!Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()].equals("artesà")) {
			comboInstrument.deselectAll();
			comboInstrument.setEnabled(false);
		}
	}

		
	@Override
	public void clearControlValues() {
		super.clearControlValues();
		comboInstrument.deselectAll();
		comboInstrument.setEnabled(false);
	}

	@Override
	public List<Ofici> retrieveUnits() throws SQLException {
		return new OficiDao().selectAll();
	}

	@Override
	protected UnitDao<Ofici> getDao() throws SQLException {
		return new OficiDao();
	}

}
