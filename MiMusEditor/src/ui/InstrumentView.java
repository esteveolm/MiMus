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
import model.Instrument;
import model.Unit;
import persistence.DaoNotImplementedException;
import persistence.InstrumentDao;
import ui.table.InstrumentTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class InstrumentView extends DeclarativeView {
	
	private SharedResources resources;
	private List<Unit> instruments;
	
	public InstrumentView() {
		super();
		getControl().setInstrumentView(this);
		
		/* Loads previously created instruments if they exist */
		resources = SharedResources.getInstance();
		//resources.globallySetUpdateId();
		instruments = resources.getInstruments();
	}
	
	@Override
	public String getViewName() {
		return "Instrument";
	}
	
	@Override
	public String getAddPattern() {
		return "/instruments.xml";
	}

	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new Entity");
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* Nom: text field */
		Label labelNom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNom.setText("Nom:");
		Text textNom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNom.setLayoutData(grid);
		
		/* Family: categorical field (Combo) */
		Label labelFamily = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelFamily.setText("Família:");
		Combo comboFamily = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboFamily.setItems(SharedResources.FAMILY);
		comboFamily.setLayoutData(grid);
		
		/* Classe: categorical field (Combo) */
		Label labelClasse = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelClasse.setText("Classe:");
		Combo comboClasse = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboClasse.setItems(SharedResources.CLASSE);
		comboClasse.setLayoutData(grid);
		
		/* Part: text field */
		Label labelPart = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPart.setText("Part:");
		Text textPart = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPart.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNom.setText("");
				comboFamily.deselectAll();
				comboClasse.deselectAll();
				textPart.setText("");
			}
		});
		
		/* Table for instruments created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Instruments created");
		
		InstrumentTableViewer instrumentHelper = 
				new InstrumentTableViewer(sectTable.getParent(), instruments);
		setTv(instrumentHelper.createTableViewer());
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete instrument");
		
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
							getResources().getIncrementId(),
							textNom.getText(),
							comboFamily.getSelectionIndex(),
							comboClasse.getSelectionIndex(),
							textPart.getText());
					instruments.add(inst);
					try {
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new InstrumentDao(conn).insert(inst);
						LabelPrinter.printInfo(label, "Instrument added successfully.");
						notifyObservers();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not insert Instrument into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
					System.out.println("Instrument created successfully.");
					LabelPrinter.printInfo(label, "Instrument created successfully.");
					MiMusXML.openInstrument().append(inst).write();
					getTv().refresh();
					notifyObservers();
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
					instruments.remove(inst);
					MiMusXML.openInstrument().remove(inst).write();
					try {
						Connection conn = DriverManager.getConnection(
								"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
								"mimus01", "colinet19");
						new InstrumentDao(conn).delete(inst);
						LabelPrinter.printInfo(label, "Instrument deleted successfully.");
						notifyObservers();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Instrument from DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Delete operation not implemented, this should never happen.");
					}
					getTv().refresh();
					notifyObservers();
					System.out.println("Instrument removed successfully.");
					LabelPrinter.printInfo(label, "Instrument removed successfully.");
				}
			}
		});
	}
	
	/**
	 * When ArtistaView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetInstrumentView();
	}

	@Override
	public void update() {}
}
