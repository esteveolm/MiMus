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
import model.Unit;
import persistence.DaoNotImplementedException;
import persistence.LlocDao;
import ui.table.LlocTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class LlocView extends DeclarativeView {

	private SharedResources resources;
	private List<Unit> llocs;
	
	public LlocView() {
		super();
		getControl().setLlocView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		llocs = resources.getLlocs();
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
						getResources().getIncrementId(),
						textNomComplet.getText(), 
						comboRegne.getSelectionIndex(), 
						comboArea.getSelectionIndex());
				llocs.add(lloc);
				System.out.println("Lloc created successfully.");
				LabelPrinter.printInfo(label, "Lloc created successfully.");
				MiMusXML.openLloc().append(lloc).write();
				try {
					Connection conn = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
							"mimus01", "colinet19");
					new LlocDao(conn).insert(lloc);
					LabelPrinter.printInfo(label, "Lloc added successfully.");
					notifyObservers();
				} catch (SQLException e2) {
					e2.printStackTrace();
					System.out.println("Could not insert Lloc into DB.");
				} catch (DaoNotImplementedException e1) {
					e1.printStackTrace();
					System.out.println("Insert operation not implemented, this should never happen.");
				}
				getTv().refresh();
				notifyObservers();
				System.out.println(resources.getLlocs().size() + " LLOCS");
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
					llocs.remove(lloc);
					MiMusXML.openLloc().remove(lloc).write();
					try {
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new LlocDao(conn).delete(lloc);
						LabelPrinter.printInfo(label, "Lloc deleted successfully.");
						notifyObservers();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Lloc from DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Delete operation not implemented, this should never happen.");
					}
					getTv().refresh();
					notifyObservers();
					System.out.println("Lloc removed successfully.");
					LabelPrinter.printInfo(label, "Lloc removed successfully.");
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
