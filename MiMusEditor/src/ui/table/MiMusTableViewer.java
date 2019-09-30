package ui.table;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * MiMusTableViewer is any Eclipse JFace TableViewer used
 * in the MiMus application. It contains common functionality
 * used in the project.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public abstract class MiMusTableViewer {

	protected Composite parent;
	protected TableViewer tv;

	protected String[] columnNames;
	
	public MiMusTableViewer(Composite parent) {
		this.parent = parent;
	}
	
	/**
	 * Creates a JFace TableViewer.
	 */
	public abstract TableViewer createTableViewer();

	/**
	 * Updates the size of columns.
	 */
	public void packColumns() {
		for (int i = 0; i < columnNames.length; i++) {
			tv.getTable().getColumn(i).pack();
		}
	}
	
	public void refresh() {
		tv.refresh();
	}
	
	public List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	public TableViewer getTv() {
		return tv;
	}

	public void setTv(TableViewer tv) {
		this.tv = tv;
	}
}
