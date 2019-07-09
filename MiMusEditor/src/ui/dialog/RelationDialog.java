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

import model.Entity;
import model.EntityInstance;
import util.LabelPrinter;

public class RelationDialog extends Dialog {

	private List<Entity> units;
	private int selection1;
	private int selection2;
	private Entity unit1;
	private Entity unit2;
	private String entityType1;
	private String entityType2;
	
	public RelationDialog(List<EntityInstance> instances, Shell parentShell, 
			String entityType1, String entityType2) {
		super(parentShell);
		this.units = getEntitiesFromInstances(instances);
		this.selection1 = 0;
		this.selection2 = 0;
		this.unit1 = null;
		this.unit2 = null;
		this.entityType1 = entityType1;
		this.entityType2 = entityType2;		
	}
	
	private List<Entity> getEntitiesFromInstances(List<EntityInstance> instances) {
		List<Entity> entities = new ArrayList<>();
		for (EntityInstance inst: instances) {
			entities.add(inst.getItsEntity());
		}
		return entities;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(composite);
		form.setText("Select Entities for Relation");
		form.getBody().setLayout(new GridLayout());
		
		/* Create lists of entities only with specified types */
		List<Entity> entities1 = new ArrayList<>();
		List<Entity> entities2 = new ArrayList<>();
		for (int i=0; i<getUnits().size(); i++) {
			Entity thisEnt = getUnits().get(i);
			if (thisEnt.getType().equals(entityType1))
				entities1.add(thisEnt);
			if (thisEnt.getType().equals(entityType2))
				entities2.add(thisEnt);
		}
		
		/* Make 1st combo with entity strings */
		String[] e1Names = new String[entities1.size()];
		for (int i=0; i<entities1.size(); i++) {
			e1Names[i] = entities1.get(i).toString();
		}
		Combo combo1 = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		combo1.setItems(e1Names);
		combo1.select(0);
		
		/* Make 2nd combo with entity strings */
		String[] e2Names = new String[entities2.size()];
		for (int i=0; i<entities2.size(); i++) {
			e2Names[i] = entities2.get(i).toString();
		}
		Combo combo2 = new Combo(form.getBody(), SWT.SINGLE | SWT.WRAP);
		combo2.setItems(e2Names);
		combo2.select(0);
		
		/* Initialize stored values */
		this.setSelection1(0);
		if (entities1.size()==0) {
			this.setUnit1(null);
		} else {
			this.setUnit1(entities1.get(0));
		}
		this.setSelection2(0);
		if (entities2.size()==0) {
			this.setUnit2(null);
		} else {
			this.setUnit2(entities2.get(0));
		}
		
		/* Updates variable that stores selected artist index */
		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection1(combo1.getSelectionIndex());
				RelationDialog.this.setUnit1(entities1.get(getSelection1()));
			}
		});
		combo2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RelationDialog.this.setSelection2(combo2.getSelectionIndex());
				RelationDialog.this.setUnit2(entities2.get(getSelection2()));
			}
		});
		
		Label label = new Label(form.getBody(), SWT.VERTICAL);
		label.setText("");
		if (entities1.size()==0 || entities2.size()==0) {
			LabelPrinter.printError(label, "You cannot add this relation yet, " +
					"at least an entity must have been annotated for each type");
		}
		
		return composite;
	}

	
	public List<Entity> getUnits() {
		return units;
	}
	public void setUnits(List<Entity> units) {
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
	public Entity getUnit1() {
		return unit1;
	}
	public void setUnit1(Entity unit1) {
		this.unit1 = unit1;
	}
	public Entity getUnit2() {
		return unit2;
	}
	public void setUnit2(Entity unit2) {
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
