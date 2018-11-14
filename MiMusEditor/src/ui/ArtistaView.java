package ui;

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

import control.SharedControl;
import control.SharedResources;
import model.EntitiesList;
import ui.table.ArtistaTableViewer;

public class ArtistaView extends ViewPart {
	
	private SharedResources resources;
	private SharedControl control;
	private TableViewer tv;
	private EntitiesList artists;
	
	public ArtistaView() {
		super();
		resources = SharedResources.getInstance();
		control = SharedControl.getInstance();
		artists = new EntitiesList();
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
		final int REFERENCE_FLAGS = SWT.MULTI | SWT.WRAP | SWT.VERTICAL;
		
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
		
		// TODO: load previously created artists
		ArtistaTableViewer artistaHelper = new ArtistaTableViewer(parent, artists);
		tv = artistaHelper.createTableViewer();
		
		// TODO: save Button?
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO: add to TableViewer
			}
		});
	}

	@Override
	public void setFocus() {}
}
