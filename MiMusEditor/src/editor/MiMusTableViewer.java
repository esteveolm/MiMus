package editor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import ui.TextStyler;


public abstract class MiMusTableViewer {

	protected Composite parent;
	protected TableViewer tv;
	protected TextStyler styler;
	protected String[] columnNames;
	
	public MiMusTableViewer(Composite parent, TextStyler styler) {
		this.parent = parent;
		this.styler = styler;
	}
		
	public abstract TableViewer createTableViewer();

	public void packColumns() {
		for (int i = 0; i < columnNames.length; i++) {
			tv.getTable().getColumn(i).pack();
		}
	}
}
