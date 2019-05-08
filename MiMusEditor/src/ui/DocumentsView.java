package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import control.SharedResources;
import model.Document;
import persistence.DocumentDao;
import ui.table.DocumentsTableViewer;

public class DocumentsView extends ViewPart {
	
	private SharedResources resources;
	private DocumentsTableViewer tv;
	
	public DocumentsView() {
		super();
		setResources(SharedResources.getInstance());
		setTv(null);
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Select a document");
		form.getBody().setLayout(new GridLayout());
		
		List<Document> documents = new ArrayList<>();
		
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			documents = new DocumentDao(conn).selectAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setTv(new DocumentsTableViewer(parent, documents));
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
	public DocumentsTableViewer getTv() {
		return tv;
	}
	public void setTv(DocumentsTableViewer tv) {
		this.tv = tv;
	}
}
