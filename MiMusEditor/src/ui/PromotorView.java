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
import model.Casa;
import model.Promotor;
import model.Unit;
import ui.table.PromotorTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class PromotorView extends DeclarativeView {
	
	private SharedResources resources;
	private List<Unit> promotors;
	private Combo comboCasa;
	
	public PromotorView() {
		super();
		getControl().setPromotorView(this);
		
		/* Loads previously created artists if they exist */
		resources = SharedResources.getInstance();
		promotors = resources.getPromotors();
		
		comboCasa = null;
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
		
		/* Nom (text) */
		Label labelNom = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNom.setText("Nom:");
		Text textNom = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNom.setLayoutData(grid);
		
		/* Numeral (text) */
		Label labelNumeral = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNumeral.setText("Numeral:");
		Text textNumeral = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNumeral.setLayoutData(grid);
		
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
		
		/* Genere (combo) */
		Label labelGenere = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelGenere.setText("Genere:");
		Combo comboGenere = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboGenere.setItems("No marcat", "Home", "Dona");
		
		/* Casa (combo) */
		Label labelCasa = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelCasa.setText("Casa:");
		comboCasa = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		// XXX: find a better way to extract a list of attributes from any Unit
		List<Unit> cases = SharedResources.getInstance().getCases();
		String[] casaNames = new String[cases.size()];
		for (int i=0; i<casaNames.length; i++) {
			casaNames[i] = ((Casa) cases.get(i)).getLemma();
		}
		comboCasa.setItems(casaNames);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNom.setText("");
				textNumeral.setText("");
				textCognom.setText("");
				textSobrenom.setText("");
				comboGenere.deselectAll();
				comboCasa.deselectAll();
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
				if (comboCasa.getSelectionIndex() == -1) {
					comboCasa.select(0);
				}
				Casa casa = null;
				if (cases.size()>0 &&
						comboCasa.getSelectionIndex()>=0) {
					/* Only set Casa if available */
					casa = (Casa) cases.get(comboCasa.getSelectionIndex());
				}
				Promotor prom = new Promotor(
						getResources().getIncrementId(),
						textNom.getText(),
						textNumeral.getText(),
						textCognom.getText(),
						textSobrenom.getText(),
						comboGenere.getSelectionIndex(),
						casa);
				promotors.add(prom);
				System.out.println(getViewName() + " created successfully.");
				LabelPrinter.printInfo(label, 
						getViewName() + " created successfully.");
				MiMusXML.openPromotor().append(prom).write();
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
	public void update() {
		List<Unit> cases = resources.getCases();
		String[] casaNames = new String[cases.size()];
		for (int i=0; i<casaNames.length; i++) {
			casaNames[i] = ((Casa) cases.get(i)).getLemma();
		}
		comboCasa.setItems(casaNames);
	}
}
