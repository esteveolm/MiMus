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

import model.Entity;
import model.Unit;
import util.LabelPrinter;

public abstract class InstanceDialog extends Dialog {

	protected ScrolledForm form;
	private List<? extends Entity> entities;
	private int selection;
	private Unit entity;
	
	public InstanceDialog(List<? extends Entity> entities, Shell parentShell) {
		super(parentShell);
		form = null;
		this.entities = entities;
		System.out.println("Entities of type " + getDialogName() 
				+ " available: " + this.entities.size());
		
		/* Dialog window will block Editor until closed */
		this.setBlockOnOpen(true);
	}
	
	public abstract String getDialogName();
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		form = toolkit.createScrolledForm(composite);
		form.setText("Select " + getDialogName());
		form.getBody().setLayout(new GridLayout());
		
		/* Create combo with artist string representations as values */
		Combo combo = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		String[] artistNames = new String[entities.size()];
		for (int i=0; i<entities.size(); i++) {
			artistNames[i] = entities.get(i).toString();
		}
		combo.setItems(artistNames);
		combo.select(0);
		
		/* Initialize stored values */
		this.setSelection(0);
		if (entities.size()==0) {
			this.setEntity(null);
		} else {
			this.setEntity(entities.get(0));
		}
		
		/* Updates variable that stores selected artist index */
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InstanceDialog.this.setSelection(combo.getSelectionIndex());
				InstanceDialog.this.setEntity(entities.get(getSelection()));
			}
		});
		
		Label label = new Label(form.getBody(), SWT.VERTICAL);
		label.setText("");
		if (entities.size()==0) {
			LabelPrinter.printError(label, "You cannot add any " 
					+ getDialogName() + " because none was declared yet.");
		}
		
		return composite;
	}

	public List<? extends Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<? extends Entity> entities) {
		this.entities = entities;
	}
	public int getSelection() {
		return selection;
	}
	public void setSelection(int selection) {
		this.selection = selection;
	}
	public Unit getEntity() {
		return entity;
	}
	public void setEntity(Unit entity) {
		this.entity = entity;
	}
}
