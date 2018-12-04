package ui;

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
import model.EntitiesList;
import model.Instrument;
import ui.table.InstrumentTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class InstrumentView extends DeclarativeView {
	
	private EntitiesList instruments;
	
	public InstrumentView() {
		super();
		instruments = new EntitiesList();
	}

	@Override
	public String getViewName() {
		return "Instrument";
	}
	
	@Override
	public String getAddPattern() {
		return "/MiMusCorpus/instruments.xml";
	}

	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new Entity");
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* Name: text field */
		Label labelName = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelName.setText("Nom:");
		Text textName = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textName.setLayoutData(grid);
		
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
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textName.setText("");
				comboFamily.deselectAll();
				comboClasse.deselectAll();
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
							getResources().getIncrementCurrentID(),
							textName.getText(),
							comboFamily.getSelectionIndex(),
							comboClasse.getSelectionIndex());
					instruments.addUnit(inst);
					System.out.println("Instrument created successfully.");
					LabelPrinter.printInfo(label, "Instrument created successfully.");
					MiMusXML.openInstrument().append(inst).write();
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
					instruments.removeUnit(inst);
					MiMusXML.openInstrument().remove(inst).write();
					System.out.println("Instrument removed successfully.");
					LabelPrinter.printInfo(label, "Instrument removed successfully.");
				}
			}
		});
		Button btnRemote = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnRemote.setText("Save to remote");
		btnRemote.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pushToGit();
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
}
