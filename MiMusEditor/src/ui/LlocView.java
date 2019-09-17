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

import model.Lloc;
import persistence.DaoNotImplementedException;
import persistence.LlocDao;
import ui.table.LlocTableViewer;
import util.LabelPrinter;

public class LlocView extends DeclarativeView<Lloc> {

	private List<Lloc> llocs;
	
	private Text textNomComplet;
	private Combo comboArea;
	private Combo comboRegne;
	
	public LlocView() {
		super();
		
		try {
			llocs = new LlocDao(getConnection()).selectAll();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load llocs from DB.");
		}
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new " + getViewName());
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		addStateLabel(sectAdd.getParent());
		
		/* NomComplet: text field */
		Label labelNomComplet = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomComplet.setText("Nom complet:");
		textNomComplet = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomComplet.setLayoutData(grid);
		
		/* Area: option field */
		Label labelArea = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelArea.setText("Àrea:");
		comboArea = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboArea.setItems(Lloc.AREES);
		/* Default selection at start lets comboRegne know what to load */
		comboArea.select(0);	
		comboArea.setLayoutData(grid);
		
		/* Regne: option field */
		Label labelRegne = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelRegne.setText("Regne:");
		comboRegne = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboRegne.setItems(Lloc.REGNES[0]);	/* At start, comboFamily at 0 */
		comboRegne.select(0);
		comboRegne.setLayoutData(grid);
		
		/* When comboArea changes, update comboRegne options */
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboRegne.setItems(
						Lloc.REGNES[comboArea.getSelectionIndex()]);
				comboRegne.select(0);
			}
		});
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				comboArea.select(0);	/* Something is required for comboRegne */
				comboRegne.setItems(Lloc.REGNES[0]);
				comboRegne.select(0);				
			}
		});
		
		/* Table for Llocs creation */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Llocs created");
				
		LlocTableViewer llocHelper = 
				new LlocTableViewer(sectTable.getParent(), llocs);
		setTv(llocHelper.createTableViewer());	
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createTableActions();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete lloc");
		
		/* Button listeners */
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/* Selecting nothing == Selection index 0 ("unmarked") */
				if (comboRegne.getSelectionIndex() == -1) {
					comboRegne.select(0);
				} 
				if (comboArea.getSelectionIndex() == -1) {
					comboArea.select(0);
				}
				
				Lloc lloc = new Lloc(
						0, 0,
						textNomComplet.getText(), 
						comboRegne.getSelectionIndex(), 
						comboArea.getSelectionIndex());
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new LlocDao(getConnection()).insert(lloc);
						if (id>0) {
							llocs.clear();
							llocs.addAll(new LlocDao(getConnection()).selectAll());
							LabelPrinter.printInfo(label, "Lloc added successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert LLoc into DB.");
						}
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not insert Lloc into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						lloc.setSpecificId(getSelectedId());
						new LlocDao(getConnection()).update(lloc);
						llocs.clear();
						llocs.addAll(new LlocDao(getConnection()).selectAll());
						LabelPrinter.printInfo(label, "Lloc updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not update Lloc into DB.");
					}
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Lloc lloc = (Lloc) 
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (lloc==null) {
					System.out.println("Could not remove Lloc because none was selected.");
					LabelPrinter.printError(label, "You must select a Lloc in order to remove it.");
				} else {
					try {
						new LlocDao(getConnection()).delete(lloc);
						llocs.clear();
						llocs.addAll(new LlocDao(getConnection()).selectAll());
						LabelPrinter.printInfo(label, "Lloc deleted successfully.");
						getTv().refresh();
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Lloc from DB.");
					}
				}
			}
		});
	}
	
	@Override
	public String getViewName() {
		return "Lloc";
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	protected void fillFieldsFromSelection(Lloc ent) {
		textNomComplet.setText(ent.getNomComplet());
		comboArea.select(ent.getArea());
		comboRegne.setItems(
				Lloc.REGNES[comboArea.getSelectionIndex()]);
		comboRegne.select(ent.getRegne());
	}

	@Override
	public List<Lloc> getEntities() {
		return llocs;
	}

	@Override
	public List<Lloc> retrieveEntities() throws SQLException {
		return new LlocDao(getConnection()).selectAll();
	}

	@Override
	public void setEntities(List<Lloc> entities) {
		llocs = entities;
	}
}
