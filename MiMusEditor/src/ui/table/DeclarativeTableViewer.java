package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import model.Unit;

/**
 * DeclarativeTableViewer is the MiMusTableViewer used in DeclarativeViews.
 * These are the tables of declared entities.
 * 
 * All implementations must declare a LabelProvider that says how to present
 * the MiMus elements in the table, and optionally a ViewerComparator that
 * sorts the table elements based on a certain criterion. These objects must
 * be related to the DeclarativeTableViewer implementing method developProviders().
 * 
 * @author Javier Beltrán Jorba
 *
 */
public abstract class DeclarativeTableViewer extends MiMusTableViewer {
	
	/* List of entities on the table */
	protected List<? extends Unit> entities;
	
	public DeclarativeTableViewer(Composite parent, List<? extends Unit> entities) {
		super(parent);
		this.entities = entities;
	}
	
	/**
	 * Assigns the CellEditors for every column in a table.
	 */
	public abstract CellEditor[] developEditors();
	
	/**
	 * Relates the TableViewer with the JFace Providers needed for
	 * TableViewers to work, specifically LabelProvider and ViewerComparator.
	 */
	public abstract void developProviders();
	
	/**
	 * Creates the table and leaves it ready for use.
	 */
	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		
		tv.setCellEditors(developEditors());
		tv.setContentProvider(ArrayContentProvider.getInstance());
		developProviders();
		
		tv.setInput(entities);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		packColumns();
		return tv;
	}
}
