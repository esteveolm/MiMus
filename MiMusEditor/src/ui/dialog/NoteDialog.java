package ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import model.Note;
import model.NoteType;

/**
 * Implementation of NoteDialog for creating Notes for documents
 */
public class NoteDialog extends EditorDialog<NoteType> {

	/* Model attributes */
	private Note note;
	
	public NoteDialog(Shell parentShell, List<NoteType> noteTypes) {
		super(noteTypes, parentShell);
		this.note = new Note(0,null,null,null);
	}

	/**
	 * Draws the Dialog, which contains a ComboBox with the type of
	 * reference and a text field to specify pages.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		//composite.setLayout(new GridLayout());
		/*
		toolkit.createLabel(getForm().getBody(), "Note Type:");
		Combo typeCombo = new Combo(getForm().getBody(), SWT.VERTICAL | SWT.WRAP);
		String[] noteTypesText = new String[getUnits().size()];
		for(int i=0; i<getUnits().size(); i++) {
			noteTypesText[i]=getUnits().get(i).getText();
		}
		typeCombo.setItems(noteTypesText);
		typeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				note.setType(getUnits().get(typeCombo.getSelectionIndex()).getText());
			}
		});
		*/
		Label noteLabel = new Label(getForm().getBody(), SWT.VERTICAL);
		noteLabel.setText("Enter note text:");
		Text noteText = new Text(getForm().getBody(), SWT.VERTICAL | SWT.BORDER | SWT.MULTI );
		noteText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				note.setText(noteText.getText());
			}
		});
		noteText.setLayoutData(GridDataFactory.fillDefaults().hint(600, 60).create());
				
		return composite;
	}
	
	public Note getNote() {
		note.setType(getUnit().getText());
		return note;
	}

	public void setNote(Note note) {
		this.note = note;		
	}

	@Override
	public String getDialogName() {
		return "Note Type";
	}

	@Override
	public List<NoteType> getUnitsUsed() {
		return getUnits();
	}

}
