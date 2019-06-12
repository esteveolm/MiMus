package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

import control.SharedResources;
import model.Lloc;
import persistence.DaoNotImplementedException;
import persistence.LlocDao;
import ui.table.LlocTableViewer;
import util.LabelPrinter;

public class LlocView extends DeclarativeView {

	private List<Lloc> llocs;
	
	public LlocView() {
		super();
		getControl().setLlocView(this);
		
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			llocs = new LlocDao(conn).selectAll();
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
		
		/* NomComplet: text field */
		Label labelNomComplet = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomComplet.setText("Nom complet:");
		Text textNomComplet = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomComplet.setLayoutData(grid);
		
		/* Area: option field */
		Label labelArea = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelArea.setText("Àrea:");
		Combo comboArea = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboArea.setItems(SharedResources.AREA);
		
		/* Regne: option field */
		Label labelRegne = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelRegne.setText("Regne:");
		Combo comboRegne = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboRegne.setItems(SharedResources.REGNE);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				comboRegne.deselectAll();
				comboArea.deselectAll();
			}
		});
		
		/* Table for Llocs creation */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Llocs created");
				
		LlocTableViewer llocHelper = 
				new LlocTableViewer(sectTable.getParent(), llocs);
		setTv(llocHelper.createTableViewer());	
		
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
				try {
					Connection conn = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
							"mimus01", "colinet19");
					int id = new LlocDao(conn).insert(lloc);
					if (id>0) {
						llocs.clear();
						llocs.addAll(new LlocDao(conn).selectAll());
						LabelPrinter.printInfo(label, "Lloc added successfully.");
						notifyObservers();
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
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new LlocDao(conn).delete(lloc);
						llocs.clear();
						llocs.addAll(new LlocDao(conn).selectAll());
						LabelPrinter.printInfo(label, "Lloc deleted successfully.");
						getTv().refresh();
						notifyObservers();
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
	public String getAddPattern() {
		return "/lloc.xml";
	}
	
	@Override
	public void update() {}
	
	/**
	 * When LlocView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetLlocView();
	}
}
