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
import model.Artista;
import model.EntitiesList;
import ui.table.ArtistaTableViewer;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class ArtistaView extends DeclarativeView {
	
	private EntitiesList artists;
	
	public ArtistaView() {
		super();
		getControl().setArtistaView(this);
		artists = new EntitiesList();
		// TODO: load previously created artists
	}
	
	public String getViewName() {
		return "Artista";
	}
	
	public String getAddPattern() {
		return "/MiMusCorpus/artista.xml";
	}
	
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new Entity");
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
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
		addButtons(sectAdd.getParent());
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
		setTv(artistaHelper.createTableViewer());	
		
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
					Artista art = new Artista(
							getResources().getIncrementCurrentID(),
							textName.getText(), 
							comboSex.getText().equals("Female"));
					artists.addUnit(art);
					System.out.println("Artist created successfully.");
					LabelPrinter.printInfo(label, "Artist created successfully.");
					MiMusXML.openArtista().append(art).write();
					notifyObservers();
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Artista art = (Artista) 
						((IStructuredSelection) getTv().getSelection())
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
		
		Button btnLocal = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnLocal.setText("Save to local");
		btnLocal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
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
		getControl().unsetArtistaView();
	}
}
