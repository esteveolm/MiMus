package ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import control.EventObserver;
import control.EventSubject;
import control.SharedControl;
import control.SharedResources;
import model.Artista;
import model.EntitiesList;
import ui.table.ArtistaTableViewer;
import util.LabelPrinter;

public class ArtistaView extends ViewPart implements EventSubject {
	
	private SharedResources resources;
	private SharedControl control;
	private List<EventObserver> observers;
	private TableViewer tv;
	private EntitiesList artists;
	
	public ArtistaView() {
		super();
		observers = new ArrayList<>();
		resources = SharedResources.getInstance();
		control = SharedControl.getInstance();
		control.setArtistaView(this);
		artists = new EntitiesList();
		// TODO: load previously created artists
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Declare Artist Entity");
		form.getBody().setLayout(new GridLayout());
		
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new Entity");
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		final int LABEL_FLAGS = SWT.VERTICAL;
		final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
		final int COMBO_FLAGS = SWT.DROP_DOWN | SWT.READ_ONLY;
		final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
		
		/* Name: text field */
		Label labelName = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelName.setText("Name:");
		Text textName = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textName.setLayoutData(grid);
		
		/* Sex: option field */
		Label labelSex = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSex.setText("Sex:");
		Combo comboSex = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboSex.setItems("Male", "Female");
		
		/* Form buttons */
		Button btnAdd = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnAdd.setText("Add Entity");
		Button btnClr = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnClr.setText("Clear fields");
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textName.setText("");
				comboSex.deselectAll();
			}
		});
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Artists created");
				
		ArtistaTableViewer artistaHelper = 
				new ArtistaTableViewer(sectTable.getParent(), artists);
		tv = artistaHelper.createTableViewer();	
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete artist");
		
		/* Button listeners */
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboSex.getSelectionIndex()==-1) {
					System.out.println("Could not create Artist because no sex was selected.");
					LabelPrinter.printError(label, "You must specify a sex to create an Artist.");
				} else {
					artists.addUnit(new Artista(
							resources.getIncrementCurrentID(),
							textName.getText(), 
							comboSex.getText().equals("Female")));
					System.out.println("Artist created successfully.");
					LabelPrinter.printInfo(label, "Artist created successfully.");
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Artista art = (Artista) 
						((IStructuredSelection) tv.getSelection())
						.getFirstElement();
				if (art==null) {
					System.out.println("Could not remove Artist because none was selected.");
					LabelPrinter.printError(label, "You must select an Artist in order to remove it.");
				} else {
					artists.removeUnit(art);
					System.out.println("Artist removed successfully.");
					LabelPrinter.printInfo(label, "Artist removed successfully.");
				}
			}
		});
		
		// TODO: save (with button or automatically?)
	}
	
	/**
	 * When ArtistaView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		control.unsetArtistaView();
	}
	
	@Override
	public void setFocus() {}

	@Override
	public void attach(EventObserver o) {
		observers.add(o);
	}

	@Override
	public void detach(EventObserver o) {
		observers.remove(o);
	}

	@Override
	public List<EventObserver> getObservers() {
		return observers;
	}
}
