package editor;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import ui.TextStyler;


public abstract class MiMusTableViewer {

	protected Composite parent;
	protected TableViewer tvEnt, tvRel;
	protected TextStyler styler;
	protected String[] columnNames;
	
	public MiMusTableViewer(Composite parent, TextStyler styler) {
		this.parent = parent;
		this.styler = styler;
	}
		
	public abstract TableViewer createTableViewer();

	public abstract void packColumns();
}
