package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import model.Document;
import persistence.DocumentDao;
import ui.table.DocumentsTableViewer;
import util.DBUtils;

/**
 * Eclipse View that lists all Documents on MiMus database,
 * and allows users to click and open them in the Editor.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DocumentsView extends ViewPart {
	
	private DocumentsTableViewer tv;
	
	public DocumentsView() {
		super();
		setTv(null);
	}

	/**
	 * Draws the DocumentsView, which contains a List of documents
	 * selectable to open their editor.
	 */
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Select a document");
		form.getBody().setLayout(new GridLayout());
		
		List<Document> documents = new ArrayList<>();
		
		try {
			Connection conn = DBUtils.connect();
			documents = new DocumentDao(conn).selectAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setTv(new DocumentsTableViewer(form.getBody(), documents));
	}

	@Override
	public void setFocus() {}

	
	/* Getters and setters */
	public DocumentsTableViewer getTv() {
		return tv;
	}
	public void setTv(DocumentsTableViewer tv) {
		this.tv = tv;
	}
}
