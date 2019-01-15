package ui;

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

import control.SharedResources;
import model.Casa;
import model.Unit;
import ui.table.CasaTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class CasaView extends DeclarativeView {

	private SharedResources resources;
	private List<Unit> cases;
	
	public CasaView() {
		super();
		getControl().setCasaView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		cases = resources.getCases();
	}
	
	@Override
	public String getViewName() {
		return "Casa";
	}
	
	@Override
	public String getAddPattern() {
		return "/cases.xml";
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new " + getViewName());
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* Titol (text) */
		Label labelTitol = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitol.setText("Titol:");
		Text textTitol = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTitol.setLayoutData(grid);
		
		/* Cort (text) */
		Label labelCort = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelCort.setText("Cort:");
		Text textCort = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textCort.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textTitol.setText("");
				textCort.setText("");
			}
		});
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Artists created");
		
		CasaTableViewer casaHelper =
				new CasaTableViewer(sectTable.getParent(), cases);
		setTv(casaHelper.createTableViewer());
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete artist");
		
		/* Button listeners */
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Casa casa = new Casa(getResources().getIncrementId(),
						textTitol.getText(), 
						textCort.getText());
				cases.add(casa);
				System.out.println(getViewName() + " created successfully.");
				LabelPrinter.printInfo(label, getViewName() + " created successfully.");
				MiMusXML.openCasa().append(casa).write();
				getTv().refresh();
				notifyObservers();
				System.out.println(resources.getArtistas().size() + " CASES");
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Casa casa = (Casa)
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (casa==null) {
					System.out.println("Could not remove " + getViewName() + " because none was selected.");
					LabelPrinter.printError(label, "You must select a " + getViewName() + " in order to remove it.");
				} else {
					cases.remove(casa);
					MiMusXML.openCasa().remove(casa).write();
					getTv().refresh();
					notifyObservers();
					System.out.println(getViewName() + " removed successfully.");
					LabelPrinter.printInfo(label, getViewName() + " removed successfully.");
				}
			}
		});
	}
	
	/**
	 * When CasaView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetCasaView();
	}

	@Override
	public void update() {}
}
