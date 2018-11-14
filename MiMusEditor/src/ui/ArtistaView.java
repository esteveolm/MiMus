package ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import control.SharedControl;
import control.SharedResources;

public class ArtistaView extends ViewPart {
	
	private SharedResources resources;
	private SharedControl control;
	
	public ArtistaView() {
		super();
		resources = SharedResources.getInstance();
		control = SharedControl.getInstance();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {}
}
