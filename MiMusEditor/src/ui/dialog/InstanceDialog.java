package ui.dialog;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import model.Entity;
import util.LabelPrinter;

public abstract class InstanceDialog<E extends Entity> extends EditorDialog<E> {
	
	public InstanceDialog(List<E> entities, Shell parentShell) {
		super(entities, parentShell);
		System.out.println("Entities of type " + getDialogName() 
				+ " available: " + getUnits().size());
		
		/* Dialog window will block Editor until closed */
		this.setBlockOnOpen(true);
	}
	
	@Override
	public abstract String getDialogName();
	
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		if (getUnits().size()==0) {
			LabelPrinter.printError(getLabel(), "You cannot add any " 
					+ getDialogName() + " because none was declared yet.");
		}
		
		return composite;
	}
}
