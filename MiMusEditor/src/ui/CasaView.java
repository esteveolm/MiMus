package ui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Casa;
import persistence.CasaDao;
import persistence.DaoNotImplementedException;
import ui.table.CasaTableViewer;
import util.LabelPrinter;

/**
 * Declarative view for Casa entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class CasaView extends EntityView<Casa> {
	
	/* Form fields */
	private Text textNomComplet;
	private Text textTitol;
	private Text textCort;
	
	public CasaView() {
		super();
		try {
			setUnits(new CasaDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load cases from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Casa";
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
		
		/* Titol (text) */
		Label labelTitol = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitol.setText("Títol:");
		textTitol = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTitol.setLayoutData(grid);
		
		/* Cort (text) */
		Label labelCort = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelCort.setText("Cort:");
		textCort = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textCort.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				textTitol.setText("");
				textCort.setText("");
			}
		});
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Cases created");
		
		CasaTableViewer casaHelper =
				new CasaTableViewer(sectTable.getParent(), getUnits());
		setTv(casaHelper.createTableViewer());
		
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
				Casa casa = new Casa(0, 0,
						textNomComplet.getText(),
						textTitol.getText(), 
						textCort.getText());
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new CasaDao().insert(casa);
						if (id>0) {
							getUnits().clear();
							getUnits().addAll(new CasaDao().selectAll());
							System.out.println(getViewName() + " created successfully.");
							LabelPrinter.printInfo(label, getViewName() + " created successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Casa into DB.");
						}
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("SQLException: Could not insert Casa into DB.");
						}
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						casa.setSpecificId(getSelectedId());
						new CasaDao().update(casa);
						getUnits().clear();
						getUnits().addAll(new CasaDao().selectAll());
						System.out.println(getViewName() + " updated successfully.");
						LabelPrinter.printInfo(label, getViewName() + " updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("SQLException: Could not update Casa to DB.");
						}
					}
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Casa casa = (Casa)
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (casa==null) {
					System.out.println("Could not remove " + getViewName() + " because none was selected.");
					LabelPrinter.printError(label, "You must select a " + getViewName() + " in order to remove it.");
				} else {
					try {
						new CasaDao().delete(casa);
						getUnits().clear();
						getUnits().addAll(new CasaDao().selectAll());
						getTv().refresh();
						System.out.println(getViewName() + " removed successfully.");
						LabelPrinter.printInfo(label, getViewName() + " removed successfully.");
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
							System.out.println("Could not delete Casa from DB.");
						}
					}
				}
			}
		});
	}

	@Override
	protected void fillFieldsFromSelection(Casa ent) {
		textNomComplet.setText(ent.getNomComplet());
		textTitol.setText(ent.getTitol());
		textCort.setText(ent.getCort());
	}

	@Override
	public List<Casa> retrieveUnits() throws SQLException {
		return new CasaDao().selectAll();
	}
}
