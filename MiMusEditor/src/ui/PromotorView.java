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
import model.Promotor;
import model.Unit;
import persistence.DaoNotImplementedException;
import persistence.PromotorDao;
import ui.table.PromotorTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class PromotorView extends DeclarativeView {
	
	private SharedResources resources;
	private List<Unit> promotors;
	
	public PromotorView() {
		super();
		getControl().setPromotorView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		promotors = resources.getPromotors();
	}
	
	@Override
	public String getViewName() {
		return "Promotor";
	}
	
	@Override
	public String getAddPattern() {
		return "/promotors.xml";
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new " + getViewName());
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* Nom Complet (text) */
		Label labelNomComplet = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNomComplet.setText("Nom Complet:");
		Text textNomComplet = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNomComplet.setLayoutData(grid);
		
		/* Nom (text) */
		Label labelNom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNom.setText("Nom:");
		Text textNom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNom.setLayoutData(grid);
		
		/* Cognom (text) */
		Label labelCognom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelCognom.setText("Cognom:");
		Text textCognom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textCognom.setLayoutData(grid);
		
		/* Sobrenom (text) */
		Label labelSobrenom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSobrenom.setText("Sobrenom:");
		Text textSobrenom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSobrenom.setLayoutData(grid);
		
		/* Distintiu (text) */
		Label labelDistintiu = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistintiu.setText("Distintiu:");
		Text textDistintiu = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistintiu.setLayoutData(grid);
		
		/* Genere (combo) */
		Label labelGenere = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelGenere.setText("Genere:");
		Combo comboGenere = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboGenere.setItems("No marcat", "Home", "Dona");
		
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
			}
		});
		
		/* Table for promotors created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText(getViewName() + "s created");
				
		PromotorTableViewer promotorHelper = 
				new PromotorTableViewer(sectTable.getParent(), promotors);
		setTv(promotorHelper.createTableViewer());	
		
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
						getResources().getIncrementId(),
						textNomComplet.getText(),
						textNom.getText(),
						textCognom.getText(),
						textSobrenom.getText(),
						textDistintiu.getText(),
						comboGenere.getSelectionIndex());
				promotors.add(prom);
				System.out.println(getViewName() + " created successfully.");
				LabelPrinter.printInfo(label, 
						getViewName() + " created successfully.");
				MiMusXML.openPromotor().append(prom).write();
				try {
					Connection conn = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
							"mimus01", "colinet19");
					new PromotorDao(conn).insert(prom);
					LabelPrinter.printInfo(label, "Promotor added successfully.");
					notifyObservers();
				} catch (SQLException e2) {
					e2.printStackTrace();
					System.out.println("Could not insert Promotor into DB.");
				} catch (DaoNotImplementedException e1) {
					e1.printStackTrace();
					System.out.println("Insert operation not implemented, this should never happen.");
				}
				getTv().refresh();
				notifyObservers();
				System.out.println(resources.getPromotors().size() + " PROMOTORS");
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
					promotors.remove(prom);
					MiMusXML.openPromotor().remove(prom).write();
					try {
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new PromotorDao(conn).delete(prom);
						LabelPrinter.printInfo(label, "Promotor deleted successfully.");
						notifyObservers();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Promotor from DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Delete operation not implemented, this should never happen.");
					}
					getTv().refresh();
					notifyObservers();
					System.out.println(getViewName() + " removed successfully.");
					LabelPrinter.printInfo(label, getViewName() 
							+ " removed successfully.");
				}
			}
		});
	}
	
	/**
	 * When PromotorView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetPromotorView();
	}

	@Override
	public void update() {}
}
