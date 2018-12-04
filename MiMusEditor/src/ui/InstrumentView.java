package ui;

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
import ui.table.InstrumentTableViewer;

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
		
		// TODO: BUTTON LISTENERS
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
