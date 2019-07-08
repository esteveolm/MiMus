package ui.dialog;

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
import util.LabelPrinter;

public class RelationDialog extends Dialog {

	private List<EntityInstance> units;
	private int selection1;
	private int selection2;
	private EntityInstance unit1;
	private EntityInstance unit2;
	private String entityType1;
	private String entityType2;
	
	public RelationDialog(List<EntityInstance> instances, Shell parentShell, 
			String entityType1, String entityType2) {
		super(parentShell);
		this.units = instances;
		this.selection1 = 0;
		this.selection2 = 0;
		this.unit1 = null;
		this.unit2 = null;
		this.entityType1 = entityType1;
		this.entityType2 = entityType2;		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(composite);
		form.setText("Select Instances for Relation");
		form.getBody().setLayout(new GridLayout());
		
		/* Create lists of entities only with specified types */
		List<EntityInstance> instances1 = new ArrayList<>();
		List<EntityInstance> instances2 = new ArrayList<>();
		for (int i=0; i<getUnits().size(); i++) {
			EntityInstance thisInst = getUnits().get(i);
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
			this.setUnit1(null);
		} else {
			this.setUnit1(instances1.get(0));
		}
		this.setSelection2(0);
		if (instances2.size()==0) {
			this.setUnit2(null);
		} else {
			this.setUnit2(instances2.get(0));
		}
		
		/* Updates variable that stores selected artist index */
		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection1(combo1.getSelectionIndex());
				RelationDialog.this.setUnit1(instances1.get(getSelection1()));
			}
		});
		combo2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection2(combo2.getSelectionIndex());
				RelationDialog.this.setUnit2(instances2.get(getSelection2()));
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

	
	public List<EntityInstance> getUnits() {
		return units;
	}
	public void setUnits(List<EntityInstance> units) {
		this.units = units;
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
	public EntityInstance getUnit1() {
		return unit1;
	}
	public void setUnit1(EntityInstance unit1) {
		this.unit1 = unit1;
	}
	public EntityInstance getUnit2() {
		return unit2;
	}
	public void setUnit2(EntityInstance unit2) {
		this.unit2 = unit2;
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
