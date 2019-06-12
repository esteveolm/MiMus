package ui;

import java.util.ArrayList;
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

import model.EntityInstance;
import model.Unit;
import util.LabelPrinter;

public class RelationDialog extends Dialog {

	protected ScrolledForm form;
	private List<EntityInstance> instances;
	private int selection1;
	private int selection2;
	private Unit instance1;
	private Unit instance2;
	private String entityType1;
	private String entityType2;
	
	public RelationDialog(List<EntityInstance> instances, Shell parentShell, 
			String entityType1, String entityType2) {
		super(parentShell);
		form = null;
		this.instances = instances;
		this.entityType1 = entityType1;
		this.entityType2 = entityType2;
		
		/* Dialog window will block Editor until closed */
		this.setBlockOnOpen(true);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		form = toolkit.createScrolledForm(composite);
		form.setText("Select " + getEntityType1() + " and " + getEntityType2());
		form.getBody().setLayout(new GridLayout());
		
		/* Create lists of entities only with specified types */
		List<EntityInstance> instances1 = new ArrayList<>();
		List<EntityInstance> instances2 = new ArrayList<>();
		for (int i=0; i<instances.size(); i++) {
			EntityInstance thisInst = (EntityInstance)instances.get(i);
			if (thisInst.getItsEntity().getType()
					.equals(entityType1))
				instances1.add(thisInst);
			if (thisInst.getItsEntity().getType()
					.equals(entityType2))
				instances2.add(thisInst);
		}
		
		/* Make 1st combo with entity strings */
		String[] e1Names = new String[instances1.size()];
		for (int i=0; i<instances1.size(); i++) {
			e1Names[i] = instances1.get(i).toString();
		}
		Combo combo1 = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		combo1.setItems(e1Names);
		combo1.select(0);
		
		/* Make 2nd combo with entity strings */
		String[] e2Names = new String[instances2.size()];
		for (int i=0; i<instances2.size(); i++) {
			e2Names[i] = instances2.get(i).toString();
		}
		Combo combo2 = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		combo2.setItems(e2Names);
		combo2.select(0);
		
		/* Initialize stored values */
		this.setSelection1(0);
		if (instances1.size()==0) {
			this.setInstance1(null);
		} else {
			this.setInstance1(instances1.get(0));
		}
		this.setSelection2(0);
		if (instances2.size()==0) {
			this.setInstance2(null);
		} else {
			this.setInstance2(instances2.get(0));
		}
		
		/* Updates variable that stores selected artist index */
		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection1(combo1.getSelectionIndex());
				RelationDialog.this.setInstance1(instances1.get(getSelection1()));
			}
		});
		combo2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection2(combo2.getSelectionIndex());
				RelationDialog.this.setInstance2(instances2.get(getSelection2()));
			}
		});
		
		Label label = new Label(form.getBody(), SWT.VERTICAL);
		label.setText("");
		if (instances1.size()==0 || instances2.size()==0) {
			LabelPrinter.printError(label, "You cannot add this relation yet, " +
					"at least an entity must have been annotated for each type");
		}
		
		return composite;
	}

	public List<EntityInstance> getInstances() {
		return instances;
	}
	public void setInstances(List<EntityInstance> instances) {
		this.instances = instances;
	}
	public int getSelection1() {
		return selection1;
	}
	public void setSelection1(int selection1) {
		this.selection1 = selection1;
	}
	public int getSelection2() {
		return selection2;
	}
	public void setSelection2(int selection2) {
		this.selection2 = selection2;
	}
	public Unit getInstance1() {
		return instance1;
	}
	public void setInstance1(Unit instance1) {
		this.instance1 = instance1;
	}
	public Unit getInstance2() {
		return instance2;
	}
	public void setInstance2(Unit instance2) {
		this.instance2 = instance2;
	}
	public String getEntityType1() {
		return entityType1;
	}
	public void setEntityType1(String entityType1) {
		this.entityType1 = entityType1;
	}
	public String getEntityType2() {
		return entityType2;
	}
	public void setEntityType2(String entityType2) {
		this.entityType2 = entityType2;
	}
	public String getRelType() {
		return getEntityType1()+"-"+getEntityType2();
	}
}
