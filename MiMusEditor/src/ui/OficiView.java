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

import model.Instrument;
import model.Ofici;
import persistence.DaoNotImplementedException;
import persistence.InstrumentDao;
import persistence.OficiDao;
import ui.table.OficiTableViewer;
import util.LabelPrinter;

public class OficiView extends DeclarativeView {

	private List<Ofici> oficis;
	private List<Instrument> insts;
	private Combo comboInstrument;
	private Connection conn;
	
	public OficiView() {
		super();
		getControl().setOficiView(this);
		comboInstrument = null;
		
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			oficis = new OficiDao(conn).selectAll();
			insts = new InstrumentDao(conn).selectAll();
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
	public String getAddPattern() {
		return "/oficis.xml";
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
		
		/* Terme (text) */
		Label labelTerme = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTerme.setText("Terme:");
		Text textTerme = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTerme.setLayoutData(grid);
		
		/* Especialitat (combo) */
		Label labelEspecialitat = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEspecialitat.setText("Especialitat:");
		Combo comboEspecialitat = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboEspecialitat.setLayoutData(grid);
		comboEspecialitat.setItems("-", "Sense especificar", "Instrument",
				"Veu", "Dansa", "Artesà", "Malabars i altres");
		
		/* Instrument (combo) */
		Label labelInstrument = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelInstrument.setText("Instrument:");
		comboInstrument = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		String[] instNames = new String[insts.size()];
		for (int i=0; i<instNames.length; i++) {
			instNames[i] = ((Instrument) insts.get(i)).getLemma();
		}
		comboInstrument.setItems(instNames);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNomComplet.setText("");
				textTerme.setText("");
				comboEspecialitat.deselectAll();
				comboInstrument.deselectAll();
			}
		});
		
		/* Table for oficis created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText(getViewName() + "s created");
		
		OficiTableViewer oficiHelper = 
				new OficiTableViewer(sectTable.getParent(), oficis, insts);
		setTv(oficiHelper.createTableViewer());
		
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
				if (insts.size()>0 && 
						comboInstrument.getSelectionIndex()>=0) {
					/* Only set Instrument if available */
					inst = (Instrument) 
							insts.get(comboInstrument.getSelectionIndex());
				}
				if (comboEspecialitat.getSelectionIndex()<0)
					comboEspecialitat.select(0);
				Ofici ofici = new Ofici(
						0, 0,
						textNomComplet.getText(),
						textTerme.getText(),
						comboEspecialitat.getSelectionIndex(),
						inst);
				try {
					Connection conn = DriverManager.getConnection(
							"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
							"mimus01", "colinet19");
					int id = new OficiDao(conn).insert(ofici);
					if (id>0) {
						oficis.clear();
						oficis.addAll(new OficiDao(conn).selectAll());
						System.out.println("Ofici created successfully.");
						LabelPrinter.printInfo(label, "Ofici added successfully.");
						notifyObservers();
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
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new OficiDao(conn).delete(ofici);
						oficis.clear();
						oficis.addAll(new OficiDao(conn).selectAll());
						getTv().refresh();
						System.out.println(getViewName() + " removed successfully.");
						LabelPrinter.printInfo(label, "Ofici deleted successfully.");
						notifyObservers();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Ofici from DB.");
					}
				}
			}
		});
	}
	
	/**
	 * When OficiView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetOficiView();
	}

	@Override
	public void update() {
		/* Re-read instruments and set Combo items again */
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			List<Instrument> insts = new InstrumentDao(conn).selectAll();
			String[] instNames = new String[insts.size()];
			for (int i=0; i<instNames.length; i++) {
				instNames[i] = ((Instrument) insts.get(i)).getLemma();
			}
			comboInstrument.setItems(instNames);
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not select Instruments from DB.");
		}
	}
}
