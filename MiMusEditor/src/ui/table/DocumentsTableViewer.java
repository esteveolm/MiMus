package ui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Composite;

import model.Document;
import model.Unit;

public class DocumentsTableViewer {
	
	private ListViewer tv;
	private List<Document> documents;
	
	public DocumentsTableViewer(Composite parent, List<Document> documents) {
		documents = new ArrayList<>(documents);
		tv = new ListViewer(parent);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setInput(documents);
	}

	public List<Document> getDocuments() {
		return documents;
	}
	
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
}
