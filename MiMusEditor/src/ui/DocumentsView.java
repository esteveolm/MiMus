package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.IDE;
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
		
		Action createDocumentAction = new Action("Add new Document", 
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD)) {

			@Override
			public void run() {
				try {
					Connection conn = DBUtils.connect();
					DocumentDao dao = new DocumentDao(conn);
					int docId = dao.insertGetNextId();
					Document doc = new Document();					
					doc.setId(docId);					
					dao.insert(doc);
					refresh();
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, doc, "MiMusEditor.mimusEditor");
					} catch (PartInitException e) {
						e.printStackTrace();
						System.out.println("Cannot open editor from document.");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					ErrorDialog.openError(null, "Error", "Could create the document", new Status(IStatus.ERROR,"MiMusEditor", e.getMessage()));
				}
				
			}
			
		};
		Action refreshDocumentsAction = new Action("Refresh Documents", 
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_UP)) {

			@Override
			public void run() {
				refresh();
			}
			
		};
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.getToolBarManager().add(createDocumentAction);
		actionBars.getToolBarManager().add(refreshDocumentsAction);
		actionBars.updateActionBars();
		
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Select a document");
		form.getBody().setLayout(new GridLayout());
		
		Text filterText = toolkit.createText(form.getBody(), "", SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		filterText.setMessage("type something to filter");
		filterText.setLayoutData(GridDataFactory.fillDefaults().create());
		filterText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getTv().setFilterText(filterText.getText());				
			}
		});
		
		
		
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
	
	public void refresh() {
		try {
			Connection conn = DBUtils.connect();
			DocumentDao dao = new DocumentDao(conn);
			getTv().setDocuments(dao.selectAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
