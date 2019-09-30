package ui.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import model.Entity;
import util.LabelPrinter;

/**
 * Implementation of EditorDialog for inserting a certain type of
 * Entities, i.e. for creating EntityInstances of a certain type of
 * Entity.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <E> the specific Entity class
 */
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
	
	/**
	 * Returns all entities passed to the dialog.
	 */
	@Override
	public List<E> getUnitsUsed() {
		return getUnits();
	}
	
	/**
	 * Returns the name of the Dialog, for presentation purposes.
	 */
	@Override
	public abstract String getDialogName();
	
	/**
	 * Draws the dialog. Must be inherited and completed in the
	 * implementation class.
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		if (getUnits().size()==0) {
			LabelPrinter.printError(getLabel(), "You cannot add any " 
					+ getDialogName() + " because none was declared yet.");
		}
		
		return composite;
	}
}
