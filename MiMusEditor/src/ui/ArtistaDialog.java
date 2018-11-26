package ui;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import model.Artista;
import util.LabelPrinter;

public class ArtistaDialog extends Dialog {

	private List<Artista> artists;
	private int selection;
	
	protected ArtistaDialog(List<Artista> artists, Shell parentShell) {
		super(parentShell);
		this.artists = artists;
		System.out.println("Artists available: " + this.artists.size());
		this.setSelection(-1);
		
		/* Dialog window will block Editor until closed */
		this.setBlockOnOpen(true);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(composite);
		form.setText("Select an Artist");
		form.getBody().setLayout(new GridLayout());
		
		/* Create combo with artist string representations as values */
		Combo combo = new Combo(composite, SWT.SINGLE | SWT.WRAP);
		String[] artistNames = new String[artists.size()];
		for (int i=0; i<artists.size(); i++) {
			artistNames[i] = artists.get(i).toString();
		}
		combo.setItems(artistNames);
		combo.select(0);
		
		/* Updates variable that stores selected artist index */
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ArtistaDialog.this.setSelection(combo.getSelectionIndex());
			}
		});
		
		// TODO: make it show
		Label label = new Label(composite, SWT.VERTICAL);
		label.setText("");
		if (artists.size()==0) {
			LabelPrinter.printError(label, "You cannot add any Artist because none was declared yet.");
		}
		
		return composite;
	}

	public int getSelection() {
		return selection;
	}
	public void setSelection(int selection) {
		this.selection = selection;
		System.out.println(selection);
	}
}
