package ui.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import model.Bibliography;

/**
 * Implementation of EditorDialog for inserting References, i.e. for
 * selecting a Bibliography and making a Reference that relates the
 * Bibliography with the Document.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ReferenceDialog extends EditorDialog<Bibliography> {

	/* Model attributes */
	private int type;
	private String pages;
	
	protected ReferenceDialog(List<Bibliography> units, Shell parentShell) {
		super(units, parentShell);
		this.setType(-1);
		this.setPages(null);
		
		/* Dialog shows entities ordered alphabetically by lemma */
		Collections.sort(units, new Comparator<Bibliography>() {
			@Override
			public int compare(Bibliography b1, Bibliography b2) {
				return b1.getShortReference()
						.compareTo(b2.getShortReference());
			}
		});
	}

	/**
	 * Draws the Dialog, which contains a ComboBox with the type of
	 * reference and a text field to specify pages.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		/* Type field (combo) */
		Label typeLabel = new Label(getForm().getBody(), SWT.VERTICAL);
		typeLabel.setText("Type:");
		String[] types = {"Edició", "Regest", "Citació"};
		Combo typeCombo = new Combo(getForm().getBody(), SWT.VERTICAL | SWT.WRAP);
		typeCombo.setItems(types);
		typeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setType(typeCombo.getSelectionIndex());
			}
		});
		
		/* Pages field (text) */
		Label pagesLabel = new Label(getForm().getBody(), SWT.VERTICAL);
		pagesLabel.setText("Pages:");
		Text pagesText = new Text(getForm().getBody(), SWT.VERTICAL);
		pagesText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPages(pagesText.getText());
			}
		});
		
		return composite;
	}
	
	/**
	 * Returns all elements passed to the Dialog.
	 */
	@Override
	public List<Bibliography> getUnitsUsed() {
		return getUnits();
	}

	@Override
	public String getDialogName() {
		return "Reference";
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

}
