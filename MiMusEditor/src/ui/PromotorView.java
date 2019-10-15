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

import model.Promotor;
import persistence.DaoNotImplementedException;
import persistence.PromotorDao;
import ui.table.PromotorTableViewer;
import util.LabelPrinter;

/**
 * Declarative view for Promotor entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class PromotorView extends EntityView<Promotor> {

	/* Form fields */
	private Text textNomComplet;
	private Text textNom;
	private Text textCognom;
	private Text textSobrenom;
	private Text textDistintiu;
	private Combo comboGenere;
	private Text textObs;
	
	public PromotorView() {
		super();
		
		try {
			setUnits(new PromotorDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load promotors from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Promotor";
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
		
		/* Nom (text) */
		Label labelNom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNom.setText("Nom:");
		textNom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNom.setLayoutData(grid);
		
		/* Cognom (text) */
		Label labelCognom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelCognom.setText("Cognom:");
		textCognom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textCognom.setLayoutData(grid);
		
		/* Sobrenom (text) */
		Label labelSobrenom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSobrenom.setText("Sobrenom:");
		textSobrenom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSobrenom.setLayoutData(grid);
		
		/* Distintiu (text) */
		Label labelDistintiu = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistintiu.setText("Distintiu:");
		textDistintiu = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistintiu.setLayoutData(grid);
		
		/* Genere (combo) */
		Label labelGenere = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelGenere.setText("Gènere:");
		comboGenere = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboGenere.setItems("no marcat", "home", "dona");
		
		/* Observacions: text field */
		Label labelObs = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelObs.setText("Observacions:");
		textObs = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textObs.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				textNom.setText("");
				textCognom.setText("");
				textSobrenom.setText("");
				textDistintiu.setText("");
				comboGenere.deselectAll();
				textObs.setText("");
			}
		});
		
		/* Table for promotors created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText(getViewName() + "s created");
				
		PromotorTableViewer promotorHelper = 
				new PromotorTableViewer(sectTable.getParent(), getUnits());
		setTv(promotorHelper.createTableViewer());	
		
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
				/* Selecting nothing == Selection index 0 ("unmarked") */
				if (comboGenere.getSelectionIndex() == -1) {
					comboGenere.select(0);
				} 
				Promotor prom = new Promotor(
						0, 0,
						textNomComplet.getText(),
						textNom.getText(),
						textCognom.getText(),
						textSobrenom.getText(),
						textDistintiu.getText(),
						comboGenere.getSelectionIndex(),
						textObs.getText());
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new PromotorDao().insert(prom);
						if (id > 0) {
							getUnits().clear();
							getUnits().addAll(new PromotorDao().selectAll());
							System.out.println("Promotor added successfully.");
							LabelPrinter.printInfo(label, "Promotor added successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Promotor into DB.");
						}
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("SQLException: Could not insert Promotor into DB.");
						}
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						prom.setSpecificId(getSelectedId());
						new PromotorDao().update(prom);
						getUnits().clear();
						getUnits().addAll(new PromotorDao().selectAll());
						System.out.println("Promotor updated successfully.");
						LabelPrinter.printInfo(label, "Promotor updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("SQLException: Could not insert Promotor into DB.");
						}
					}
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Promotor prom = (Promotor)
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (prom==null) {
					System.out.println("Could not remove " 
							+ getViewName() + " because none was selected.");
					LabelPrinter.printError(label, "You must select a " 
							+ getViewName() + " in order to remove it.");
				} else {
					try {
						new PromotorDao().delete(prom);
						getUnits().clear();
						getUnits().addAll(new PromotorDao().selectAll());
						System.out.println(getViewName() + " removed successfully.");
						LabelPrinter.printInfo(label, getViewName() 
								+ " removed successfully.");
						getTv().refresh();
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
							System.out.println("Could not delete Promotor from DB.");
						}
					}					
				}
			}
		});
	}

	@Override
	protected void fillFieldsFromSelection(Promotor ent) {
		textNomComplet.setText(ent.getNomComplet());
		textNom.setText(ent.getNom());
		textCognom.setText(ent.getCognom());
		textSobrenom.setText(ent.getSobrenom());
		textDistintiu.setText(ent.getDistintiu());
		comboGenere.select(ent.getGenere());
		textObs.setText(ent.getObservacions());
	}

	@Override
	public List<Promotor> retrieveUnits() throws SQLException {
		return new PromotorDao().selectAll();
	}
}
