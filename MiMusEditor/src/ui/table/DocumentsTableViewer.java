package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.layout.GridData;
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
	private String filterText;
	
	public DocumentsTableViewer(Composite parent, List<Document> documents) {
		tv = new ListViewer(parent);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setInput(documents);
		tv.setFilters(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (filterText == null || filterText.length() == 0)
	            {
	                return true;
	            } else {
	            	return element.toString().indexOf(filterText)>=0;
	            }
			}
			
		});
		tv.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		tv.setInput(documents);
	}

	public void setFilterText(String text) {
		this.filterText = text;
		tv.refresh();		
	}
}
