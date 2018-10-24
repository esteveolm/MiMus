package view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import editor.MiMusBiblioReader;
import model.MiMusBibEntry;

public class BiblioView extends ViewPart {

	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	private ListViewer lv;
	private List<MiMusBibEntry> entries;
	private String biblioPath;
	
	public BiblioView() {
		super();
		entries = new ArrayList<>();
		
		/* Load stored entries from path */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder stringsFolder = project.getFolder("strings");
		IFile biblioFile = stringsFolder.getFile("bibliography.xml");
		biblioPath = biblioFile.getLocation().toString();
		entries = MiMusBiblioReader.read(biblioPath);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Bibliography Entries");
		form.getBody().setLayout(new GridLayout());
		
		/* Form for introduction of new entries */
		Section sectAdd = new Section(form.getBody(), Section.TITLE_BAR);
		sectAdd.setText("Add a new entry");
		
		final int LABEL_FLAGS = SWT.VERTICAL;
		final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
		final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
		final int REFERENCE_FLAGS = SWT.MULTI | SWT.WRAP | SWT.VERTICAL;
		Label[] labelAuthors = new Label[NUM_AUTHORS];
		Text[] textAuthors = new Text[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			labelAuthors[i] = new Label(sectAdd.getParent(), LABEL_FLAGS);
			labelAuthors[i].setText("Autor " + (i+1) + ":");
			textAuthors[i] = new Text(sectAdd.getParent(), TEXT_FLAGS);
		}
		Label[] labelSecondaries = new Label[NUM_SECONDARY];
		Text[] textSecondaries = new Text[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			labelSecondaries[i] = new Label(sectAdd.getParent(), LABEL_FLAGS);
			labelSecondaries[i].setText("Autor secundari " + (i+1) + ":");
			textSecondaries[i] = new Text(sectAdd.getParent(), TEXT_FLAGS);
		}
		
		Label labelYear = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelYear.setText("Any:");
		Text textYear = new Text(sectAdd.getParent(), TEXT_FLAGS);

		Label labelDistinction = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistinction.setText("Distinció:");
		Text textDistinction = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitle.setText("Títol:");
		Text textTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelMainTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelMainTitle.setText("Títol principal:");
		Text textMainTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelVolume = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelVolume.setText("Volum:");
		Text textVolume = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelPlace = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPlace.setText("Lloc:");
		Text textPlace = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelEditorial = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEditorial.setText("Editorial:");
		Text textEditorial = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		Label labelSeries = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSeries.setText("Sèrie:");
		Text textSeries = new Text(sectAdd.getParent(), TEXT_FLAGS);
		
		/* Declare form buttons */
		Button btnAdd = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnAdd.setText("Add entry");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO: this code could be streamed
				String[] authors = new String[NUM_AUTHORS];
				for (int i=0; i<NUM_AUTHORS; i++) {
					authors[i] = textAuthors[i].getText();
				}
				String[] secondaries = new String[NUM_SECONDARY];
				for (int i=0; i<NUM_SECONDARY; i++) {
					secondaries[i] = textSecondaries[i].getText();
				}
				MiMusBibEntry newEntry = new MiMusBibEntry(
						authors, secondaries, 
						Integer.parseInt(textYear.getText()), 
						textDistinction.getText(), 
						textTitle.getText(),
						textMainTitle.getText(), 
						Integer.parseInt(textVolume.getText()), 
						textPlace.getText(), 
						textEditorial.getText(), 
						textSeries.getText(), 
						0);	// TODO: how-to IDs
				entries.add(newEntry);
				lv.refresh();
				MiMusBiblioReader.append(biblioPath, newEntry);
			}
		});
		Button btnClear = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnClear.setText("Clear fields");
		btnClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (int i=0; i<NUM_AUTHORS; i++) {
					textAuthors[i].setText("");
				}
				for (int i=0; i<NUM_SECONDARY; i++) {
					textSecondaries[i].setText("");
				}
				textYear.setText("");
				textDistinction.setText("");
				textTitle.setText("");
				textMainTitle.setText("");
				textVolume.setText("");
				textPlace.setText("");
				textEditorial.setText("");
				textSeries.setText("");
			}
		});
		
		/* List of existing entries */
		Section sectList = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectList.setText("Declared entries");
		lv = new ListViewer(sectList.getParent());
		lv.setUseHashlookup(true);
		lv.setContentProvider(new BiblioContentProvider());
		lv.setLabelProvider(new BiblioLabelProvider());
		lv.setInput(entries);
		lv.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Text of selected entity and listener that prints selection */
		Text fullReference = new Text(sectList.getParent(), REFERENCE_FLAGS);
		fullReference.setEditable(false);
		fullReference.setLayoutData(new GridData(GridData.FILL_BOTH));
		lv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				MiMusBibEntry selectedEntry = (MiMusBibEntry) 
						lv.getStructuredSelection().getFirstElement();
				/* Null happens when nothing gets selected */
				if (selectedEntry != null) {
					fullReference.setText(selectedEntry.getFullReference());
				}
			}
		});
		
		/* Declare button and listener for removing selection */
		Button btnRemove = new Button(sectList.getParent(), BUTTON_FLAGS);
		btnRemove.setText("Remove selected entry");
		btnRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MiMusBibEntry selectedEntry = (MiMusBibEntry) 
						lv.getStructuredSelection().getFirstElement();
				entries.remove(selectedEntry);
				lv.refresh();
				fullReference.setText(""); /* Clear full reference */
			}
		});
	}
	
	class BiblioContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return entries.toArray();
		}
	}
	
	class BiblioLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((MiMusBibEntry)element).getShortReference();
		}
	}
	
	@Override
	public void setFocus() {
		
	}
}
