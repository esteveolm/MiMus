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
	private Combo comboInstrument;
	
	public OficiView() {
		super();
		getControl().setOficiView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		oficis = resources.getOficis();
		
		comboInstrument = null;
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
				if (comboEspecialitat.getSelectionIndex()<0)
					comboEspecialitat.select(0);
				Ofici ofici = new Ofici(
						getResources().getIncrementId(),
						textNomComplet.getText(),
						textTerme.getText(),
						comboEspecialitat.getSelectionIndex(),
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
					notifyObservers();
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

	@Override
	public void update() {
		/* Re-read instruments and set Combo items again */
		List<Unit> insts = resources.getInstruments();
		String[] instNames = new String[insts.size()];
		for (int i=0; i<instNames.length; i++) {
			instNames[i] = ((Instrument) insts.get(i)).getLemma();
		}
		comboInstrument.setItems(instNames);
	}
}
