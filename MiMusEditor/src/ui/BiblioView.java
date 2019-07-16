package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import model.Bibliography;
import persistence.BibliographyDao;
import util.DBUtils;
import util.LabelPrinter;

public class BiblioView extends ViewPart implements EventSubject {

	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	private ListViewer lv;
	private List<Bibliography> bibliography;
	private SharedControl control;
	private List<EventObserver> observers;
	private Connection conn;
	
	public BiblioView() {
		super();
		observers = new ArrayList<>();
		bibliography = new ArrayList<>();
		control = SharedControl.getInstance();
		control.setBiblioView(this);
		try {
			setConnection(DBUtils.connect());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not load biblio from DB.");
		}
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
		
		Label labelPages = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPages.setText("Pàgines:");
		Text textPages = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPages.setLayoutData(grid);
		
		Label labelShort = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelShort.setText("Referència abreujada:");
		Text textShort = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textShort.setLayoutData(grid);
		Button btnGen = new Button(sectAdd.getParent(), BUTTON_FLAGS);
		btnGen.setText("Generate automatically");
		btnGen.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String generated = Bibliography.generateShortReference(
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
					System.out.println(i + " " + String.valueOf(textAuthors[i].getText()==""));
					authors[i] = (textAuthors[i].getText().length()==0) ? "" : 
						textAuthors[i].getText().trim();
				}
				String[] secondaries = new String[NUM_SECONDARY];
				for (int i=0; i<NUM_SECONDARY; i++) {
					secondaries[i] = (textSecondaries[i].getText().length()==0) ? "" : 
						textSecondaries[i].getText().trim();
				}
				
				Bibliography newEntry = new Bibliography(
						authors, secondaries, 
						(textYear.getText().length()==0) ? "" : textYear.getText().trim(), 
						(textDistinction.getText().length()==0) ? "" : textDistinction.getText().trim(), 
						(textTitle.getText().length()==0) ? "" : textTitle.getText().trim(),
						(textMainTitle.getText().length()==0) ? "" : textMainTitle.getText().trim(), 
						(textVolume.getText().length()==0) ? "" : textVolume.getText().trim(), 
						(textPlace.getText().length()==0) ? "" : textPlace.getText().trim(), 
						(textEditorial.getText().length()==0) ? "" : textEditorial.getText().trim(), 
						(textSeries.getText().length()==0) ? "" : textSeries.getText().trim(),
						(textPages.getText().length()==0) ? "" : textPages.getText().trim(),
						(textShort.getText().length()==0) ? "" : textShort.getText().trim(),
						0);
				try {
					int id = new BibliographyDao(getConnection()).insert(newEntry);
					if (id>0) {
						newEntry.setId(id);
						bibliography.clear();
						bibliography.addAll(new BibliographyDao(getConnection()).selectAll());
						LabelPrinter.printInfo(labelForm, "Bibliography entry added successfully.");
						notifyObservers();
						lv.refresh();
					} else {
						System.out.println("DAO: Could not insert entry into DB.");
					}
				} catch (SQLException e2) {
					e2.printStackTrace();
					System.out.println("SQLException: Could not insert entry into DB.");
				}
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
				textPages.setText("");
				textShort.setText("");
			}
		});
		
		/* List of existing entries from DB*/
		Section sectList = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectList.setText("Declared entries");
		lv = new ListViewer(sectList.getParent());
		lv.setUseHashlookup(true);
		lv.setContentProvider(ArrayContentProvider.getInstance());
		lv.setLabelProvider(new BiblioLabelProvider());
		
		/* Load bibliography entries from DB */
		try {
			bibliography = new BibliographyDao(getConnection()).selectAll();
			System.out.println("Bibliography length: " + bibliography.size());
			lv.setInput(bibliography);
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load bibliography from DB.");
		}
		lv.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Label of results */
		Label labelList = toolkit.createLabel(sectList.getParent(), "");
		labelList.setLayoutData(grid);
		
		/* Text of selected entity and listener that prints selection */
		StyledText fullReference = new StyledText(sectList.getParent(), REFERENCE_FLAGS);
		fullReference.setEditable(false);
		fullReference.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/* Listeners for buttons and list */
		
		lv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				/* 1st removes old style in case this is a change of reference */
				StyleRange defaultStyle = new StyleRange(0, fullReference.getText().length(), null, null);
				fullReference.setStyleRange(defaultStyle);
				
				Bibliography selectedEntry = (Bibliography) 
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
				Bibliography selectedEntry = (Bibliography) 
						lv.getStructuredSelection().getFirstElement();
				if (selectedEntry == null) {
					System.out.println("Could not remove bibEntry because none was selected.");
					LabelPrinter.printError(labelList, "You must select a bibliography entry to delete it.");
				} else if (!selectedEntry.getUsers().isEmpty()) {
					System.out.println("Could not remove bibEntry because because it is in use by some documents.");
					LabelPrinter.printError(labelList, inUseMessage(selectedEntry.getUsers()));
				} else if (selectedEntry.getId()==1) {
					System.out.println("Could not remove bibEntry because it is the default entry (id=0).");
					LabelPrinter.printError(labelList, "You cannot delete the default bibliography entry.");
				} else {
					try {
						new BibliographyDao(getConnection()).delete(selectedEntry);
						bibliography.clear();
						bibliography.addAll(new BibliographyDao(getConnection()).selectAll());
						System.out.println("BibEntry removed successfully.");
						LabelPrinter.printInfo(labelList, "Bibliography entry deleted successfully.");
						notifyObservers();
						lv.refresh();
						fullReference.setText(""); /* Clear full reference */
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(labelList, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e1) {
						e1.printStackTrace();
						System.out.println("Could not delete Bibliography from DB.");
					}
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
	
	class BiblioLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((Bibliography)element).getShortReference();
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

	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
