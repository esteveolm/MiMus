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

public class CasaView extends DeclarativeView<Casa> {

	private List<Casa> cases;
	
	private Text textNomComplet;
	private Text textTitol;
	private Text textCort;
	
	public CasaView() {
		super();
		try {
			cases = new CasaDao(getConnection()).selectAll();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load cases from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "casa";
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
		labelNomComplet.setText("Nom Complet:");
		textNomComplet = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomComplet.setLayoutData(grid);
		
		/* Titol (text) */
		Label labelTitol = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitol.setText("Titol:");
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
		sectTable.setText("Artists created");
		
		CasaTableViewer casaHelper =
				new CasaTableViewer(sectTable.getParent(), cases);
		setTv(casaHelper.createTableViewer());
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createTableActions();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete artist");
		
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
						int id = new CasaDao(getConnection()).insert(casa);
						if (id>0) {
							cases.clear();
							cases.addAll(new CasaDao(getConnection()).selectAll());
							System.out.println(getViewName() + " created successfully.");
							LabelPrinter.printInfo(label, getViewName() + " created successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Casa into DB.");
						}
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not insert Casa into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						casa.setSpecificId(getSelectedId());
						new CasaDao(getConnection()).update(casa);
						cases.clear();
						cases.addAll(new CasaDao(getConnection()).selectAll());
						System.out.println(getViewName() + " updated successfully.");
						LabelPrinter.printInfo(label, getViewName() + " updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not update Casa to DB.");
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
						new CasaDao(getConnection()).delete(casa);
						cases.clear();
						cases.addAll(new CasaDao(getConnection()).selectAll());
						getTv().refresh();
						System.out.println(getViewName() + " removed successfully.");
						LabelPrinter.printInfo(label, getViewName() + " removed successfully.");
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Casa from DB.");
					}
				}
			}
		});
	}
	
	/**
	 * When CasaView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void fillFieldsFromSelection(Casa ent) {
		textNomComplet.setText(ent.getNomComplet());
		textTitol.setText(ent.getTitol());
		textTitol.setText(ent.getCort());
	}
}
