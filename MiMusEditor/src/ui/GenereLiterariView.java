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

import model.GenereLiterari;
import persistence.DaoNotImplementedException;
import persistence.GenereLiterariDao;
import ui.table.GenereTableViewer;
import util.LabelPrinter;

public class GenereLiterariView extends DeclarativeView<GenereLiterari> {

	private List<GenereLiterari> generes;
	
	private Text textNombreCompleto;
	private Text textNomFrances;
	private Text textNomOccita;
	private Text textDefinicio;
	
	public GenereLiterariView() {
		super();
		getControl().setGenereView(this);
		try {
			generes = new GenereLiterariDao(getConnection()).selectAll();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load generes from DB.");
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
		Label labelNombreCompleto = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNombreCompleto.setText("Nom complet:");
		textNombreCompleto = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNombreCompleto.setLayoutData(grid);
		
		/* NomFrances: text field */
		Label labelNomFrances = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomFrances.setText("Nom frances:");
		textNomFrances = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomFrances.setLayoutData(grid);
		
		/* NomOccita: text field */
		Label labelNomOccita = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomOccita.setText("Nom occita:");
		textNomOccita = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomOccita.setLayoutData(grid);
		
		/* Definicio: text field */
		Label labelDefinicio = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDefinicio.setText("Definicio:");
		textDefinicio = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDefinicio.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNombreCompleto.setText("");
				textNomFrances.setText("");
				textNomOccita.setText("");
				textDefinicio.setText("");
			}
		});
		
		/* Table for generes created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Generes created");
				
		GenereTableViewer genereHelper = 
				new GenereTableViewer(sectTable.getParent(), generes);
		setTv(genereHelper.createTableViewer());	
		
		addAnnotationsLabel(sectAdd.getParent());
		createTableActions();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete genere");
		
		/* Button listeners */
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GenereLiterari gen = new GenereLiterari(
						0, 0,
						textNombreCompleto.getText(), 
						textNomFrances.getText(), 
						textNomOccita.getText(), 
						textDefinicio.getText());
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new GenereLiterariDao(getConnection()).insert(gen);
						if (id>0) {
							generes.clear();
							generes.addAll(
									new GenereLiterariDao(getConnection()).selectAll());
							System.out.println("Genere created successfully.");
							LabelPrinter.printInfo(label, "Genere added successfully.");
							notifyObservers();
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Genere into DB.");
						}
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not insert Genere into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection*/
						gen.setSpecificId(getSelectedId());
						new GenereLiterariDao(getConnection()).update(gen);
						generes.clear();
						generes.addAll(
								new GenereLiterariDao(getConnection()).selectAll());
						System.out.println("Genere updated successfully.");
						LabelPrinter.printInfo(label, "Genere updated successfully.");
						notifyObservers();
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not update Genere into DB.");
					} 
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GenereLiterari gen = (GenereLiterari) 
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (gen==null) {
					System.out.println("Could not remove Genere because none was selected.");
					LabelPrinter.printError(label, "You must select an Genere in order to remove it.");
				} else {
					try {
						new GenereLiterariDao(getConnection()).delete(gen);
						generes.clear();
						generes.addAll(
								new GenereLiterariDao(getConnection()).selectAll());
						LabelPrinter.printInfo(label, "Genere deleted successfully.");
						notifyObservers();
						getTv().refresh();
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Genere from DB.");
					}
				}
			}
		});
	}
	
	@Override
	public String getViewName() {
		return "genere_literari";
	}
	
	/**
	 * When GenereLiterariView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetGenereView();
	}
	
	@Override
	public void update() {}

	@Override
	protected void fillFieldsFromSelection(GenereLiterari ent) {
		textNombreCompleto.setText(ent.getNomComplet());
		textNomFrances.setText(ent.getNomFrances());
		textNomOccita.setText(ent.getNomOccita());
		textDefinicio.setText(ent.getDefinicio());
	}
}
