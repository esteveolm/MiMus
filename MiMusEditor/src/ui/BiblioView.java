package ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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

import control.EventObserver;
import control.EventSubject;
import control.SharedControl;
import control.SharedResources;
import model.MiMusBibEntry;
import util.LabelPrinter;
import util.xml.MiMusXML;

public class BiblioView extends ViewPart implements EventSubject {

	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	private ListViewer lv;
	private SharedResources resources;
	private SharedControl control;
	private int currentBiblioID;
	private List<EventObserver> observers;
	
	public BiblioView() {
		super();
		observers = new ArrayList<>();
		resources = SharedResources.getInstance();
		control = SharedControl.getInstance();
		control.setBiblioView(this);
		
		/* Next ID is max ID of other bibliography entries +1 */
		currentBiblioID = -1;
		for (MiMusBibEntry entry: resources.getBibEntries()) {
			currentBiblioID = Math.max(currentBiblioID, entry.getId());
		}
		currentBiblioID++;
		System.out.println("Current Biblio ID set at " + currentBiblioID);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Bibliography Entries");
		form.getBody().setLayout(new GridLayout());
		
		/* Form for introduction of new entries */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new entry");
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
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
			textAuthors[i].setLayoutData(grid);
		}
		Label[] labelSecondaries = new Label[NUM_SECONDARY];
		Text[] textSecondaries = new Text[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			labelSecondaries[i] = new Label(sectAdd.getParent(), LABEL_FLAGS);
			labelSecondaries[i].setText("Autor secundari " + (i+1) + ":");
			textSecondaries[i] = new Text(sectAdd.getParent(), TEXT_FLAGS);
			textSecondaries[i].setLayoutData(grid);
		}
		
		Label labelYear = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelYear.setText("Any:");
		Text textYear = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textYear.setLayoutData(grid);
		
		Label labelDistinction = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistinction.setText("Distinció:");
		Text textDistinction = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistinction.setLayoutData(grid);
		
		Label labelTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitle.setText("Títol:");
		Text textTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTitle.setLayoutData(grid);
		
		Label labelMainTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelMainTitle.setText("Títol principal:");
		Text textMainTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textMainTitle.setLayoutData(grid);
		
		Label labelVolume = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelVolume.setText("Volum:");
		Text textVolume = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textVolume.setLayoutData(grid);
		
		Label labelPlace = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPlace.setText("Lloc:");
		Text textPlace = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPlace.setLayoutData(grid);
		
		Label labelEditorial = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEditorial.setText("Editorial:");
		Text textEditorial = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textEditorial.setLayoutData(grid);
		
		Label labelSeries = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSeries.setText("Sèrie:");
		Text textSeries = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSeries.setLayoutData(grid);
		
		Label labelShort = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelShort.setText("Referència abreujada:");
		Text textShort = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textShort.setLayoutData(grid);
		Button btnGen = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnGen.setText("Generate automatically");
		btnGen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String generated = MiMusBibEntry.generateShortReference(
						textAuthors[0].getText().replaceAll(",", "").split(" ")[0],
						textAuthors[1].getText().replaceAll(",", "").split(" ")[0],
						textYear.getText(), textDistinction.getText());
				textShort.setText(generated);
			}
		});
		
		/* Label of form */
		Label labelForm = toolkit.createLabel(sectAdd.getParent(), "");
		labelForm.setLayoutData(grid);
		
		/* Declare form buttons */
		Button btnAdd = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnAdd.setText("Add entry");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {		
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
						textYear.getText(), 
						textDistinction.getText(), 
						textTitle.getText(),
						textMainTitle.getText(), 
						textVolume.getText(), 
						textPlace.getText(), 
						textEditorial.getText(), 
						textSeries.getText(),
						textShort.getText(),
						currentBiblioID++);
				resources.getBibEntries().add(newEntry);
				lv.refresh();
				MiMusXML.openBiblio().append(newEntry).write();
				//MiMusBiblioReader.appendEntry(resources.getBiblioPath(), newEntry);
				LabelPrinter.printInfo(labelForm, "Bibliography entry added successfully.");
				notifyObservers();
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
				textShort.setText("");
			}
		});
		
		/* List of existing entries */
		Section sectList = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectList.setText("Declared entries");
		lv = new ListViewer(sectList.getParent());
		lv.setUseHashlookup(true);
		lv.setContentProvider(new BiblioContentProvider());
		lv.setLabelProvider(new BiblioLabelProvider());
		lv.setInput(resources.getBibEntries());
		lv.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Label of results */
		Label labelList = toolkit.createLabel(sectList.getParent(), "");
		labelList.setLayoutData(grid);
		
		/* Text of selected entity and listener that prints selection */
		StyledText fullReference = new StyledText(sectList.getParent(), REFERENCE_FLAGS);
		fullReference.setEditable(false);
		fullReference.setLayoutData(new GridData(GridData.FILL_BOTH));
		lv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				/* 1st removes old style in case this is a change of reference */
				StyleRange defaultStyle = new StyleRange(0, fullReference.getText().length(), null, null);
				fullReference.setStyleRange(defaultStyle);
				
				MiMusBibEntry selectedEntry = (MiMusBibEntry) 
						lv.getStructuredSelection().getFirstElement();
				
				/* Null happens when nothing gets selected */
				if (selectedEntry != null) {
					fullReference.setText(selectedEntry.getFullReference());
					
					/* Makes main title italic */
					int italicStart = selectedEntry.getFullReference()
							.indexOf(selectedEntry.getMainTitle());
					StyleRange italic = new StyleRange(italicStart, 
							selectedEntry.getMainTitle().length(),
							null, null, SWT.ITALIC);
					fullReference.setStyleRange(italic);
				} else {
					/* Stops displaying reference when deselected */
					fullReference.setStyleRange(defaultStyle);
					fullReference.setText("");
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
				if (selectedEntry == null) {
					System.out.println("Could not remove bibEntry because none was selected.");
					LabelPrinter.printError(labelList, "You must select a bibliography entry to delete it.");
				} else if (!selectedEntry.getUsers().isEmpty()) {
					System.out.println("Could not remove bibEntry because because it is in use by some documents.");
					LabelPrinter.printError(labelList, inUseMessage(selectedEntry.getUsers()));
				} else if (selectedEntry.getId()==0) {
					System.out.println("Could not remove bibEntry because it is the default entry (id=0).");
					LabelPrinter.printError(labelList, "You cannot delete the default bibliography entry.");
				} else {
					resources.getBibEntries().remove(selectedEntry);
					lv.refresh();
					fullReference.setText(""); /* Clear full reference */
					MiMusXML.openBiblio().remove(selectedEntry).write();
					//MiMusBiblioReader.removeEntry(resources.getBiblioPath(), selectedEntry);
					System.out.println("BibEntry removed successfully.");
					LabelPrinter.printInfo(labelList, "Bibliography entry deleted successfully.");
					notifyObservers();
				}
			}
		});
		
		Button btnPush = new Button(sectList.getParent(), BUTTON_FLAGS);
		btnPush.setText("Remote");
		btnPush.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Git git = resources.getGit();
				if (git != null) {
					AddCommand gitAdd = git.add();
					gitAdd.addFilepattern(resources.getBiblioPath());
					try {
						gitAdd.call();
						CommitCommand gitCommit = git.commit();
						gitCommit.setMessage("Updating BiblioView");
						gitCommit.call();
						PushCommand gitPush = git.push();
						gitPush.setRemote(resources.getRemote());
						gitPush.call();
					} catch (GitAPIException e1) {
						e1.printStackTrace();
					}
					
				} else {
					System.out.println("Git adapter not working");
				}
			}
		});
	}
	
	private String inUseMessage(List<Integer> users) {
		String str = "First you must remove its references from docs: ";
		for (Integer u: users) {
			str += String.valueOf(u) + ", ";
		}
		return str.substring(0, str.lastIndexOf(",")) + ".";
	}
	
	class BiblioContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			return resources.getBibEntries().toArray();
		}
	}
	
	class BiblioLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((MiMusBibEntry)element).getShortReference();
		}
	}
	
	/**
	 * When BiblioView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		control.unsetBiblioView();
	}
	
	@Override
	public void setFocus() {}
	
	/* Observer pattern for updates in state changes to Editors */
	
	@Override
	public void attach(EventObserver o) {
		observers.add(o);
	}
	
	@Override
	public void detach(EventObserver o) {
		observers.remove(o);
	}
	
	@Override
	public List<EventObserver> getObservers() {
		return observers;
	}
}
