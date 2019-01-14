package ui;

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
import model.Ofici;
import model.Unit;
import ui.table.OficiTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class OficiView extends DeclarativeView {

	private SharedResources resources;
	private List<Unit> oficis;
	
	public OficiView() {
		super();
		getControl().setOficiView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		oficis = resources.getOficis();
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
		
		/* Terme (text) */
		Label labelTerme = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTerme.setText("Terme:");
		Text textTerme = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTerme.setLayoutData(grid);
		
		/* Especialitat (text) */
		Label labelEspecialitat = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEspecialitat.setText("Especialitat:");
		Text textEspecialitat = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textEspecialitat.setLayoutData(grid);
		
		/* Instrument (combo) */
		Label labelInstrument = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelInstrument.setText("Instrument:");
		Combo comboInstrument = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		// XXX: find a better way to extract a list of attributes from any Unit
		List<Unit> insts = SharedResources.getInstance().getInstruments();
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
				textTerme.setText("");
				textEspecialitat.setText("");
				comboInstrument.deselectAll();
			}
		});
		
		/* Table for oficis created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText(getViewName() + "s created");
		
		OficiTableViewer oficiHelper = 
				new OficiTableViewer(sectTable.getParent(), oficis);
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
				Ofici ofici = new Ofici(
						getResources().getIncrementId(),
						textTerme.getText(),
						textEspecialitat.getText(),
						inst);
				oficis.add(ofici);
				System.out.println(getViewName() + " created successfully.");
				LabelPrinter.printInfo(label, 
						getViewName() + " created successfully.");
				MiMusXML.openOfici().append(ofici).write();
				getTv().refresh();
				notifyObservers();
				System.out.println(resources.getOficis().size() + " OFICIS");
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
					oficis.remove(ofici);
					MiMusXML.openOfici().remove(ofici).write();
					getTv().refresh();
					System.out.println(getViewName() + " removed successfully.");
					LabelPrinter.printInfo(label, getViewName() 
							+ " removed successfully.");
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
}
