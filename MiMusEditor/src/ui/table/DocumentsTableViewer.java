package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import model.Document;

/**
 * TableViewer for DocumentsView. It is a 1-column table, effectively
 * a list. It shows all Documents in MiMus DB and, when one is selected,
 * it is opened in the MiMus Editor.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DocumentsTableViewer {
		
	private ListViewer tv;
	private List<Document> documents;
	
	public DocumentsTableViewer(Composite parent, List<Document> documents) {
		tv = new ListViewer(parent);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setInput(documents);
		
		/* Listener that opens the Document in Editor */
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				/* From UI selection to Document */
				IStructuredSelection selection = 
						(IStructuredSelection)event.getSelection();
				Document document = (Document) selection.getFirstElement();
				if (document != null) {	
					/* Tells Eclipse to open MiMusEditor with Document as input */
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();

					try {
						IDE.openEditor(page, document, "MiMusEditor.mimusEditor");
					} catch (PartInitException e) {
						e.printStackTrace();
						System.out.println("Cannot open editor from document.");
					}
				} else {
					System.out.println("Tried opening editor but nothing was selected.");
				}
			}
		}
		);
	}

	public List<Document> getDocuments() {
		return documents;
	}
	
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
}
