package ui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import model.EntityInstance;

public abstract class TranscriptionDialog<T extends Entity>
		extends EditorDialog<EntityInstance> {
	
	private String selectedText;
	private String transcription;
	
	public TranscriptionDialog(List<EntityInstance> entities, 
			Shell parentShell, String selectedText) {
		super(entities, parentShell);
		this.setSelectedText(selectedText);
		this.setTranscription("");
		
		/* Dialog shows entities ordered alphabetically by lemma */
		Collections.sort(entities, new Comparator<EntityInstance>() {
			@Override
			public int compare(EntityInstance e1, EntityInstance e2) {
				return e1.getItsEntity().getLemma()
						.compareTo(e2.getItsEntity().getLemma());
			}
		});
	}
	
	@Override
	public List<EntityInstance> getUnitsUsed() {
		List<EntityInstance> used = new ArrayList<>();
		for (EntityInstance inst: getUnits()) {
			if (inst.getItsEntity().getType().equals(getDialogName())) {
				/* Only entities of the type specified to this dialog are used */
				used.add(inst);
			}
		}
		return used;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		Label labelForm = new Label(getForm().getBody(), SWT.SINGLE);
		labelForm.setText("New form: ");
		Text textForm = new Text(getForm().getBody(), SWT.SINGLE);
		textForm.setText("");
		textForm.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				/* Stores text introduced by user */
				TranscriptionDialog.this.setTranscription(textForm.getText());
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
