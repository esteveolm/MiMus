package ui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import model.Bibliography;
import model.Document;
import persistence.BibliographyDao;
import persistence.DocumentDao;
import ui.table.BibliographyTableViewer;
import util.LabelPrinter;

/**
 * Eclipse View for declaration of Bibliography units. Though
 * it is not a DeclarativeView because Bibliography are not
 * entities, this class is analogous to DeclarativeView in
 * all senses.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class BiblioView extends DeclarativeView<Bibliography> {

	/* Constants for number of authors in bibliography */
	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;

	/* Form fields */
	private Text[] textAuthors = new Text[NUM_AUTHORS];
	private Text[] textSecondaries = new Text[NUM_SECONDARY];
	private Text textYear;
	private Text textDistinction;
	private Text textTitle;
	private Text textMainTitle;
	private Text textVolume;
	private Text textPlace;
	private Text textEditorial;
	private Text textSeries;
	private Text textPages;
	private Text textShort;
	
	public BiblioView() {
		super();
		try {
			setUnits(retrieveUnits());
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entries */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new entry");
		
		addStateLabel(sectAdd.getParent());
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		Label[] labelAuthors = new Label[NUM_AUTHORS];
		textAuthors = new Text[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			labelAuthors[i] = new Label(sectAdd.getParent(), LABEL_FLAGS);
			labelAuthors[i].setText("Autor " + (i+1) + ":");
			textAuthors[i] = new Text(sectAdd.getParent(), TEXT_FLAGS);
			textAuthors[i].setLayoutData(grid);
		}
		Label[] labelSecondaries = new Label[NUM_SECONDARY];
		textSecondaries = new Text[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			labelSecondaries[i] = new Label(sectAdd.getParent(), LABEL_FLAGS);
			labelSecondaries[i].setText("Autor secundari " + (i+1) + ":");
			textSecondaries[i] = new Text(sectAdd.getParent(), TEXT_FLAGS);
			textSecondaries[i].setLayoutData(grid);
		}
		
		Label labelYear = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelYear.setText("Any:");
		textYear = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textYear.setLayoutData(grid);
		
		Label labelDistinction = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistinction.setText("Distinció:");
		textDistinction = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistinction.setLayoutData(grid);
		
		Label labelTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTitle.setText("Títol:");
		textTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTitle.setLayoutData(grid);
		
		Label labelMainTitle = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelMainTitle.setText("Títol principal:");
		textMainTitle = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textMainTitle.setLayoutData(grid);
		
		Label labelVolume = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelVolume.setText("Volum:");
		textVolume = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textVolume.setLayoutData(grid);
		
		Label labelPlace = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPlace.setText("Lloc:");
		textPlace = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPlace.setLayoutData(grid);
		
		Label labelEditorial = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelEditorial.setText("Editorial:");
		textEditorial = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textEditorial.setLayoutData(grid);
		
		Label labelSeries = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSeries.setText("Sèrie:");
		textSeries = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSeries.setLayoutData(grid);
		
		Label labelPages = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelPages.setText("Pàgines:");
		textPages = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textPages.setLayoutData(grid);
		
		Label labelShort = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelShort.setText("Referència abreujada:\n(deixar blanc per usar referència per defecte)");
		textShort = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textShort.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
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
		Section sectList = new Section(form.getBody(), PROP_TITLE);
		sectList.setText("Declared entries");
		
		BibliographyTableViewer bibliographyHelper =
				new BibliographyTableViewer(sectList.getParent(), getUnits());
		setTv(bibliographyHelper.createTableViewer());
		
		addAnnotationsLabel(sectList.getParent(), grid);
		createDeselectAction();
		createEditAction();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectList.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete " + getViewName());
		
		/* Text of selected entity and listener that prints selection */
		StyledText fullReference = new StyledText(sectList.getParent(), REFERENCE_FLAGS);
		fullReference.setEditable(false);
		fullReference.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/* Listeners for buttons and list */
		
		getTv().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				/* 1st removes old style in case this is a change of reference */
				StyleRange defaultStyle = new StyleRange(0, fullReference.getText().length(), null, null);
				fullReference.setStyleRange(defaultStyle);
				
				Bibliography selectedEntry = (Bibliography) 
						getTv().getStructuredSelection().getFirstElement();
				
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
				
				/* Automatically generate short reference if left blank */
				if (textShort.getText().equals("")) {
					String generated = Bibliography.generateShortReference(
							textAuthors[0].getText().replaceAll(",", "").split(" ")[0],
							textAuthors[1].getText().replaceAll(",", "").split(" ")[0],
							textYear.getText(), textDistinction.getText());
					textShort.setText(generated);
				}
				
				Bibliography biblio = new Bibliography(
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
				if (isStateAdd()) {
					/* Add a new entity */
					try {
						int id = new BibliographyDao().insert(biblio);
						if (id>0) {
							biblio.setId(id);
							getUnits().clear();
							getUnits().addAll(new BibliographyDao().selectAll());
							LabelPrinter.printInfo(label, "Bibliography entry added successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert entry into DB.");
						}
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println("SQLException: Could not insert entry into DB.");
						}
					}
				} else {
					/* Update values from selected unit */
					try {
						/* Recover ID from selection */
						biblio.setId(getSelectedId());
						new BibliographyDao().update(biblio);
						getUnits().clear();
						getUnits().addAll(
								new BibliographyDao().selectAll());
						System.out.println("Bibliography updated successfully.");
						LabelPrinter.printInfo(label, 
								"Bibliography updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e2.printStackTrace();
							System.out.println(
									"SQLException: Could not update Bibliography to DB.");
						}
					}
				}
			}
		});
		btnDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Bibliography selectedEntry = (Bibliography) 
						getTv().getStructuredSelection().getFirstElement();
				if (selectedEntry == null) {
					System.out.println("Could not remove bibEntry because none was selected.");
					LabelPrinter.printError(label, "You must select a bibliography entry to delete it.");
				} else if (selectedEntry.getId()==1) {
					System.out.println("Could not remove bibEntry because it is the default entry (id=0).");
					LabelPrinter.printError(label, "You cannot delete the default bibliography entry.");
				} else {
					try {
						new BibliographyDao().delete(selectedEntry);
						getUnits().clear();
						getUnits().addAll(new BibliographyDao().selectAll());
						System.out.println("BibEntry removed successfully.");
						LabelPrinter.printInfo(label, "Bibliography entry deleted successfully.");
						getTv().refresh();
						fullReference.setText(""); /* Clear full reference */
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e1) {
						if (e1.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e1.printStackTrace();
							System.out.println("Could not delete Bibliography from DB.");
						}
					}
				}
			}
		});
	}
	
	/**
	 * LabelProvider for Bibliography TableViewer, which presents
	 * Bibliography elements as their short reference.
	 */
	class BiblioLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((Bibliography)element).getShortReference();
		}
	}
	
	/**
	 * Comparator for Bibliography TableViewer which sorts elements
	 * based on their short reference.
	 */
	class BiblioComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			Bibliography b1 = (Bibliography) e1;
			Bibliography b2 = (Bibliography) e2;
			return b1.getShortReference().compareTo(b2.getShortReference());
		}
	}
	
	@Override
	protected void fillAnnotationsLabel(Bibliography unit) {
		List<Document> docs = new ArrayList<>();
		try {
			docs = new DocumentDao()
					.selectWhereBiblio(unit.getId());
		} catch (SQLException e) {
			System.out.println("Could not retrieve documents where bibliography is.");
			e.printStackTrace();
		}
		String ids = "";
		for (Document doc : docs) {
			ids += doc.getIdStr() + ", ";
		}
		if (ids.length()>2) {
			ids = ids.substring(0, ids.length()-2);
			annotationsText.setText("Used in documents: " + ids);
		} else {
			annotationsText.setText("Used in 0 documents.");
		}
	}
	
	@Override
	public void setFocus() {}

	public List<Bibliography> retrieveUnits() throws SQLException {
		return new BibliographyDao().selectAll();
	}

	@Override
	public String getViewName() {
		return "Bibliography";
	}

	@Override
	protected void fillFieldsFromSelection(Bibliography unit) {
		for (int i=0; i<unit.getAuthors().length; i++) {
			textAuthors[i].setText(unit.getAuthors()[i]);
		}
		for (int i=0; i<unit.getSecondaryAuthors().length; i++) {
			textSecondaries[i].setText(unit.getSecondaryAuthors()[i]);
		}
		textYear.setText(unit.getYear());
		textDistinction.setText(unit.getDistinction());
		textTitle.setText(unit.getTitle());
		textMainTitle.setText(unit.getMainTitle());
		textVolume.setText(unit.getVolume());
		textPlace.setText(unit.getPlace());
		textEditorial.setText(unit.getEditorial());
		textSeries.setText(unit.getSeries());
		textPages.setText(unit.getPages());
		textShort.setText(unit.getShortReference());
	}
	
	@Override
	public void createEditAction() {
		/* Select a table row to enter edit mode with the row selected */
		getTv().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				/* Unchecked warning because selection is Object type */
				Object o = getTv().getStructuredSelection().getFirstElement();
				if (o != null) {
					/* User selected a row from the table, move to Edit mode */
					setStateAdd(false);
					setSelectedId(((Bibliography) o).getId());	/* Save ID */
					System.out.println("ID is " + getSelectedId());
					stateLabel.setText(STATE_EDIT);
					btnAdd.setText(BUTTON_EDIT);
					fillFieldsFromSelection((Bibliography) o);
					
					/* Tell documents where entity is annotated */
					fillAnnotationsLabel((Bibliography) o);
				} else {
					/* After an entity is edited, back to add mode */ 
					setStateAdd(true);
					setSelectedId(0);
					stateLabel.setText(STATE_ADD);
					btnAdd.setText(BUTTON_ADD);
					
					/* Empty label for document annotations */
					annotationsText.setText("");
				}
			}
		});
	}
}
