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
import persistence.DaoNotImplementedException;
import persistence.InstrumentDao;
import ui.table.InstrumentTableViewer;
import util.LabelPrinter;

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
			setUnits(new InstrumentDao(getConnection()).selectAll());
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
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new " + getViewName());
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		addStateLabel(sectAdd.getParent());
		
		/* Nom: text field */
		Label labelNom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNom.setText("Nom:");
		textNom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNom.setLayoutData(grid);
		
		/* Family: categorical field (Combo) */
		Label labelFamily = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelFamily.setText("Família:");
		comboFamily = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboFamily.setItems(Instrument.FAMILIES);
		/* Default selection at start lets comboClasse know what to load */
		comboFamily.select(0);	
		comboFamily.setLayoutData(grid);
		
		/* Classe: categorical field (Combo) */
		Label labelClasse = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelClasse.setText("Classe:");
		comboClasse = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboClasse.setItems(Instrument.CLASSES[0]); /* At start, comboFamily at 0 */
		comboClasse.select(0);
		comboClasse.setLayoutData(grid);
		
		/* When comboFamily changes, update comboClasse options */
		comboFamily.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboClasse.setItems(
						Instrument.CLASSES[comboFamily.getSelectionIndex()]);
				comboClasse.select(0);
			}
		});
		
		/* Part: text field */
		Label labelPart = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPart.setText("Part:");
		textPart = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPart.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNom.setText("");
				comboFamily.select(0);	/* Something is required for comboClasse */
				comboClasse.setItems(Instrument.CLASSES[0]);
				comboClasse.select(0);
				textPart.setText("");
			}
		});
		
		/* Table for instruments created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Instruments created");
		
		InstrumentTableViewer instrumentHelper = 
				new InstrumentTableViewer(sectTable.getParent(), getUnits());
		setTv(instrumentHelper.createTableViewer());
		
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
				if (comboFamily.getSelectionIndex()==-1) {
					System.out.println("Could not create Instrument because no family was selected.");
					LabelPrinter.printError(label, "You must specify a family to create an Instrument.");
				} else if (comboClasse.getSelectionIndex()==-1) {
					System.out.println("Could not create Instrument because no classe was selected.");
					LabelPrinter.printError(label, "You must specify a classe to create an Instrument.");
				} else {
					Instrument inst = new Instrument(
							0, 0,
							textNom.getText(),
							comboFamily.getSelectionIndex(),
							comboClasse.getSelectionIndex(),
							textPart.getText());
					if (isStateAdd()) {
						/* Add new entity */
						try {
							int id = new InstrumentDao(getConnection()).insert(inst);
							if (id>0) {
								getUnits().clear();
								getUnits().addAll(new InstrumentDao(getConnection()).selectAll());
								LabelPrinter.printInfo(label, "Instrument added successfully.");
								getTv().refresh();
							} else {
								System.out.println("DAO: Could not insert Instrument into DB.");
							}
						} catch (SQLException e2) {
							if (e2.getSQLState().equals("42000")) {
								System.out.println("Disconnected exception.");
								LabelPrinter.printError(label, 
										"You must be connected to perform changes to the DB.");
							} else {
								e2.printStackTrace();
								System.out.println("SQLException: Could not insert Instrument into DB.");
							}
						} catch (DaoNotImplementedException e1) {
							e1.printStackTrace();
							System.out.println("Insert operation not implemented, this should never happen.");
						}
					} else {
						/* Update values from selected entity */
						try {
							/* Recover ID from selection */
							inst.setSpecificId(getSelectedId());
							new InstrumentDao(getConnection()).update(inst);
							getUnits().clear();
							getUnits().addAll(new InstrumentDao(getConnection()).selectAll());
							LabelPrinter.printInfo(label, "Instrument updated successfully.");
							getTv().refresh();
						} catch (SQLException e2) {
							if (e2.getSQLState().equals("42000")) {
								System.out.println("Disconnected exception.");
								LabelPrinter.printError(label, 
										"You must be connected to perform changes to the DB.");
							} else {
								e2.printStackTrace();
								System.out.println("SQLException: Could not insert Instrument into DB.");
							}
						}
					}
				}
			}
		});
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Instrument inst = (Instrument)
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (inst==null) {
					System.out.println("Could not remove Instrument because none was selected.");
					LabelPrinter.printError(label, "You must select an Instrument in order to remove it.");
				} else {
					try {
						new InstrumentDao(getConnection()).delete(inst);
						getUnits().clear();
						getUnits().addAll(new InstrumentDao(getConnection()).selectAll());
						LabelPrinter.printInfo(label, "Instrument deleted successfully.");
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("Could not delete Instrument from DB.");
						}
					}
					getTv().refresh();
					System.out.println("Instrument removed successfully.");
				}
			}
		});
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
		return new InstrumentDao(getConnection()).selectAll();
	}
}
