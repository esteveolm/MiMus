package ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import model.Entity;
import model.Unit;

public abstract class TranscriptionDialog extends InstanceDialog {
	
	private String selectedText;
	private String transcription;
	
	public TranscriptionDialog(List<Unit> entities, 
			Shell parentShell, String selectedText) {
		super(entities, parentShell);
		this.setSelectedText(selectedText);
		this.setTranscription("");
	}
	
	protected Control createDialogArea(Composite parent) {
		/* Inherits ComboBox Entity selection from parent InstanceDialog */
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Label labelSelection = new Label(form.getBody(), SWT.SINGLE);
		labelSelection.setText("Selection is: " + selectedText);
		Label labelForm = new Label(form.getBody(), SWT.SINGLE);
		labelForm.setText("New form: ");
		Text textForm = new Text(form.getBody(), SWT.SINGLE);
		textForm.setText("");
		textForm.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				/* Stores text introduced by user */
				TranscriptionDialog.this.setTranscription(textForm.getText());
				TranscriptionDialog.this.setEntity(
						TranscriptionDialog.this.getEntities().get(getSelection()));
			}
			
		});
		return composite;
	}
	
	@Override
	public abstract String getDialogName();

	public String getSelectedText() {
		return selectedText;
	}
	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
	public String getTranscription() {
		return transcription;
	}
	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}
}
