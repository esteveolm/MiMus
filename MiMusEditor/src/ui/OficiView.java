package ui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Instrument;
import model.Ofici;
import persistence.DaoNotImplementedException;
import persistence.InstrumentDao;
import persistence.OficiDao;
import ui.table.OficiTableViewer;
import util.LabelPrinter;

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
	
	public OficiView() {
		super();
		comboInstrument = null;
		
		try {
			setUnits(new OficiDao(getConnection()).selectAll());
			insts = new InstrumentDao(getConnection()).selectAll();
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
		sectAdd.setText("Add a new " + getViewName());
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		addStateLabel(sectAdd.getParent());
		
		/* Nom Complet (text) */
		Label labelNomComplet = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomComplet.setText("Nom complet:");
		textNomComplet = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomComplet.setLayoutData(grid);
		
		/* Terme (text) */
		Label labelTerme = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTerme.setText("Terme:");
		textTerme = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTerme.setLayoutData(grid);
		
		/* Especialitat (combo) */
		Label labelEspecialitat = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEspecialitat.setText("Especialitat:");
		comboEspecialitat = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboEspecialitat.setLayoutData(grid);
		comboEspecialitat.setItems(Ofici.ESPECIALITATS);
		comboEspecialitat.select(0);
		
		/* Instrument (combo) */
		Label labelInstrument = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelInstrument.setText("Instruments:");
		comboInstrument = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboInstrument.setLayoutData(grid);
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
				if (Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()]
								.equals("instrument") ||
						Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()]
								.equals("artesà")) {
					/* First, update list of instruments */
					try {
						insts = new InstrumentDao(getConnection()).selectAll();
						String[] instNames = new String[insts.size()];
						for (int i=0; i<instNames.length; i++) {
							instNames[i] = ((Instrument) insts.get(i)).getLemma();
						}
						comboInstrument.setItems(instNames);
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not select Instruments from DB.");
					}
					
					comboInstrument.setEnabled(true);
					comboInstrument.select(0);
				} else {
					comboInstrument.deselectAll();	/* Clearer if field gets empty */
					comboInstrument.setEnabled(false);
				}
			}
		});
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				textTerme.setText("");
				comboEspecialitat.select(0);
				comboInstrument.deselectAll();
				comboInstrument.setEnabled(false);
			}
		});
		
		/* Table for oficis created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText(getViewName() + "s created");
		
		OficiTableViewer oficiHelper = 
				new OficiTableViewer(sectTable.getParent(), getUnits(), insts);
		setTv(oficiHelper.createTableViewer());
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createDeselectAction();
		createEditAction();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete " + getViewName());
		
		/* Button listeners */
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Instrument inst = null;
				if (insts.size()>0 && comboInstrument.isEnabled() &&
						comboInstrument.getSelectionIndex()>=0) {
					/* Only set Instrument if available */
					inst = (Instrument) 
							insts.get(comboInstrument.getSelectionIndex());
				}
				Ofici ofici = new Ofici(
						0, 0,
						textNomComplet.getText(),
						textTerme.getText(),
						comboEspecialitat.getSelectionIndex(),
						inst);
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new OficiDao(getConnection()).insert(ofici);
						if (id>0) {
							getUnits().clear();
							getUnits().addAll(new OficiDao(getConnection()).selectAll());
							System.out.println("Ofici created successfully.");
							LabelPrinter.printInfo(label, "Ofici added successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Artist into DB.");
						}					
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not insert Ofici into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						ofici.setSpecificId(getSelectedId());
						new OficiDao(getConnection()).update(ofici);
						getUnits().clear();
						getUnits().addAll(new OficiDao(getConnection()).selectAll());
						System.out.println("Ofici updated successfully.");
						LabelPrinter.printInfo(label, "Ofici updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not update Ofici into DB.");
					}
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Ofici ofici = (Ofici) 
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (ofici == null) {
					System.out.println("Could not remove " 
							+ getViewName() + " because none was selected.");
					LabelPrinter.printError(label, "You must select a " 
							+ getViewName() + " in order to remove it.");
				} else {
					try {
						new OficiDao(getConnection()).delete(ofici);
						getUnits().clear();
						getUnits().addAll(new OficiDao(getConnection()).selectAll());
						getTv().refresh();
						System.out.println(getViewName() + " removed successfully.");
						LabelPrinter.printInfo(label, "Ofici deleted successfully.");
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Ofici from DB.");
					}
				}
			}
		});
	}

	@Override
	protected void fillFieldsFromSelection(Ofici ent) {
		textNomComplet.setText(ent.getNomComplet());
		textTerme.setText(ent.getTerme());
		comboEspecialitat.select(ent.getEspecialitat());
		if (Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()]
				.equals("instrument") ||
				Ofici.ESPECIALITATS[comboEspecialitat.getSelectionIndex()]
						.equals("artesà")) {
			/* If selection has instrument, find it and select it */
			comboInstrument.setEnabled(true);
			for (int i=0; i<insts.size(); i++) {
				if (insts.get(i).getSpecificId() == 
						ent.getInstrument().getSpecificId()) {
					comboInstrument.select(i);
					break;
				}
			}
		} else {
			comboInstrument.deselectAll();	/* Clearer if field gets empty */
			comboInstrument.setEnabled(false);
		}
		
	}

	@Override
	public List<Ofici> retrieveUnits() throws SQLException {
		return new OficiDao(getConnection()).selectAll();
	}
}
