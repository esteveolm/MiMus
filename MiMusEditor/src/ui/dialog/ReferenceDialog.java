package ui.dialog;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import model.Bibliography;

public class ReferenceDialog extends EditorDialog<Bibliography> {

	/* Model attributes */
	private int type;
	private String pages;
	
	/* UI attributes */
	private ScrolledForm form;
	
	protected ReferenceDialog(List<Bibliography> units, Shell parentShell) {
		super(units, parentShell);
		this.setType(-1);
		this.setPages(null);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		form = toolkit.createScrolledForm(composite);
		form.setText("Select " + getDialogName());
		form.getBody().setLayout(new GridLayout());
		
		/* Type field (combo) */
		Label typeLabel = new Label(form.getBody(), SWT.VERTICAL);
		typeLabel.setText("Type:");
		String[] types = {"Edition", "Register", "Citation"};
		Combo typeCombo = new Combo(form.getBody(), SWT.VERTICAL | SWT.WRAP);
		typeCombo.setItems(types);
		typeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setType(typeCombo.getSelectionIndex());
			}
		});
		
		/* Pages field (text) */
		Label pagesLabel = new Label(form.getBody(), SWT.VERTICAL);
		pagesLabel.setText("Pages:");
		Text pagesText = new Text(form.getBody(), SWT.VERTICAL);
		pagesText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPages(pagesText.getText());
			}
		});
		
		return composite;
	}
	
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
