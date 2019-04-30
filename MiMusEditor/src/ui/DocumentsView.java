package ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import control.SharedResources;

public class DocumentsView extends ViewPart {
	
	private SharedResources resources;
	
	public DocumentsView() {
		super();
		setResources(SharedResources.getInstance());
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Select a document");
		form.getBody().setLayout(new GridLayout());
		
	}

	@Override
	public void setFocus() {}

	
	/* Getters and setters */
	
	public SharedResources getResources() {
		return resources;
	}
	public void setResources(SharedResources resources) {
		this.resources = resources;
	}
}
