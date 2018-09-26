package view;

import editor.Editor;
import search.Result;
import search.XPathEntityCriteria;
import search.XPathSearcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

public class QueryView extends ViewPart {

	private String[] columnNames = {"Text", "Type", "Subtype", "Document"};
	private TableViewer tv;
	private List<Result> results;
	
	public QueryView() {
		super();
		results = new ArrayList<>();
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Database Search");
		form.getBody().setLayout(new GridLayout());
		
		/* Query section of the form */
		Section sectQuery = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectQuery.setText("Searching criteria");
		
		/* Text contains */
		Label searchLabel = new Label(sectQuery.getParent(), SWT.VERTICAL);
		searchLabel.setText("Text contains...");
		Text searchText = new Text(sectQuery.getParent(), SWT.SINGLE | SWT.WRAP | SWT.SEARCH | SWT.ICON_SEARCH);
		//searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
		
		/* Type is */
		Label typeLabel = new Label(sectQuery.getParent(), SWT.VERTICAL);
		typeLabel.setText("Type");
		Combo typeCombo = new Combo(sectQuery.getParent(), SWT.SINGLE | SWT.WRAP | SWT.SEARCH | SWT.ICON_SEARCH);
		typeCombo.setItems(Editor.ENTITY_TYPES);
		typeCombo.select(0);	// This way subtype combo knows what to load at start
		
		/* Subtype is */
		Label subtypeLabel = new Label(sectQuery.getParent(), SWT.VERTICAL);
		subtypeLabel.setText("Subtype");
		Combo subtypeCombo = new Combo(sectQuery.getParent(), SWT.SINGLE | SWT.WRAP | SWT.SEARCH | SWT.ICON_SEARCH);
		subtypeCombo.setItems(Editor.PERSON_TYPES);
		subtypeCombo.select(0);	// This way subtype combo knows what to load at start

		/* Change subtype Combo options when type is modified */
		typeCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				subtypeCombo.select(0);
				switch(typeCombo.getSelectionIndex()) {
				case 0:
					subtypeCombo.setItems(Editor.PERSON_TYPES);
					break;
				case 1:
					subtypeCombo.setItems(new String[0]);
					break;
				case 2:
					subtypeCombo.setItems(Editor.PLACE_TYPES);
					break;
				default:
					break;
				}
			}
		});
		
		/* Search button */
		Button searchBtn = new Button(sectQuery.getParent(), SWT.PUSH | SWT.CENTER);
		searchBtn.setText("Search");
		
		/* Clear button */
		Button clearBtn = new Button(sectQuery.getParent(), SWT.PUSH | SWT.CENTER);
		clearBtn.setText("Clear all");
		clearBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				searchText.setText("");
				typeCombo.select(0);
				subtypeCombo.setItems(Editor.PERSON_TYPES);
				subtypeCombo.select(0);
				results = new ArrayList<>();
				tv.refresh();
			}
		});
		
		/* Results section of the form */
		Section sectResult = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectResult.setText("Results");
				
		tv = new TableViewer(sectResult.getParent());
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setContentProvider(new QueryViewContentProvider());
		tv.setLabelProvider(new QueryViewLabelProvider());
		tv.getTable().setLinesVisible(true);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tv.setInput(results);
		
		for (int i = 0; i < columnNames.length; i++) {
			tv.getTable().getColumn(i).pack();
		}
		
		/* Search button listener that updates the TableViewer with new results */
		searchBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String contains = searchText.getText();
				String type = typeCombo.getText();
				String subtype = subtypeCombo.getText();
				System.out.println(contains + " " + type + " " + subtype);
				XPathEntityCriteria criteria = new XPathEntityCriteria(contains, type, subtype);
				
				String xmlPath = ResourcesPlugin.getWorkspace().getRoot()
						.getProject("MiMus").getFolder("xml")
						.getLocation().toString();
				try {
					XPathSearcher searcher = new XPathSearcher(criteria, new File(xmlPath));
					results = searcher.search();
					tv.refresh();	// Otherwise TableViewer does not update its look
				} catch (IOException ioe) {
					System.out.println("Could not retrieve search results due to IO Exception");
				}
			}
		});
	}

	@Override
	public void setFocus() {
		
	}
	
	public class QueryViewContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return results.toArray();
		}
	}
	
	public class QueryViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Result res = (Result) element;
			switch(columnIndex) {
			case 0:
				return res.getText();
			case 1:
				return res.getType();
			case 2:
				return res.getSubtype();
			case 3:
				return res.getDocument();
			default:
				return "";
			}
		}
	}

}
