package ui.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import model.Entity;
import util.LabelPrinter;

public abstract class InstanceDialog<E extends Entity> extends EditorDialog<E> {
	
	public InstanceDialog(List<E> entities, Shell parentShell) {
		super(entities, parentShell);
		
		/* Dialog shows entities ordered alphabetically by lemma */
		Collections.sort(entities, new Comparator<E>() {
			@Override
			public int compare(E e1, E e2) {
				return e1.getLemma().compareTo(e2.getLemma());
			}
		});
	}
	
	@Override
	public List<E> getUnitsUsed() {
		return getUnits();
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
