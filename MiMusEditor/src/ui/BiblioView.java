package ui;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import model.Bibliography;
import model.Unit;
import persistence.BibliographyDao;
import persistence.UnitDao;
import ui.table.BibliographyTableViewer;

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
	
	private StyledText fullReference ;

	Section sectAdd;
	
	public BiblioView() {
		super();
		try {
			setUnits(retrieveUnits());
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void developForm(Form form) {
		/* Form for introduction of new entries */
		sectAdd = new Section(form.getBody(), Section.TWISTIE | Section.TITLE_BAR);
		
		sectAdd.setText("Autors:");
		sectAdd.setExpanded(false);
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		sectAdd.setLayoutData(grid);
		
		Composite autors = new Composite(sectAdd,SWT.NONE);
		autors.setLayout(new GridLayout(1,true));
		autors.setLayoutData(grid);
				
		textAuthors = new Text[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			textAuthors[i] = addTextControl(autors, "Autor " + (i+1) + ":");
		}
		
		textSecondaries = new Text[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			textSecondaries[i] = addTextControl(autors, "Autor secundari " + (i+1) + ":");
		}
		sectAdd.setClient(autors);
		
		textYear = addTextControl(sectAdd.getParent(), "Any:");
		textDistinction = addTextControl(sectAdd.getParent(), "Distinció:");		
		textTitle = addTextControl(sectAdd.getParent(), "Títol:");
		textMainTitle = addTextControl(sectAdd.getParent(), "Títol principal:");
		textVolume = addTextControl(sectAdd.getParent(), "Volum:");
		textPlace = addTextControl(sectAdd.getParent(), "Lloc:");
		textEditorial = addTextControl(sectAdd.getParent(), "Editorial:");
		textSeries = addTextControl(sectAdd.getParent(), "Sèrie:");
		textPages = addTextControl(sectAdd.getParent(), "Pàgines:");		
		textShort = addTextControl(sectAdd.getParent(), "Referència abreujada:    (deixar blanc per usar referència per defecte)");
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		
		/* List of existing entries from DB*/
		Section sectList =  new Section(form.getBody(), 0);

		
		BibliographyTableViewer bibliographyHelper =
				new BibliographyTableViewer(sectList.getParent(), getUnits());
		setTv(bibliographyHelper.createTableViewer());
		
		addAnnotationsLabel(sectList.getParent(), grid);
		createEditAction();

		/* Text of selected entity and listener that prints selection */
		fullReference = new StyledText(sectList.getParent(), REFERENCE_FLAGS);
		fullReference.setEditable(false);
		fullReference.setLayoutData(grid);

		/* Listener for list */
		
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
					fullReference.getParent().layout();
				} else {
					/* Stops displaying reference when deselected */
					fullReference.setStyleRange(defaultStyle);
					fullReference.setText("");
				}
			}
		});
		
	}
	
	@Override
	protected Bibliography getUnitToSave() {
	
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
				
				return biblio;
				
	}

	
	// TODO: when delete
	//fullReference.setText(""); /* Clear full reference */

	
		
	public List<Bibliography> retrieveUnits() throws SQLException {
		return new BibliographyDao().selectAll();
	}

	@Override
	public boolean deleteAction() {
		boolean result = false;
		Unit art = (Unit) ((IStructuredSelection) getTv().getSelection()).getFirstElement();
		if (art!=null && art.getId()<=1 && art instanceof Bibliography) {
			MessageDialog.openError(null, "Delete error", "You cannot delete the default bibliography entry.");
		} else {
			result = super.deleteAction();
			if(result) {
				fullReference.setText("");				
			} 
		}
		return result;
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
		
		String autors = Stream.concat(Stream.of(unit.getAuthors()), Stream.of(unit.getSecondaryAuthors())).filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(","));
		sectAdd.setText("Autors: "+autors);
		
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
	protected UnitDao<Bibliography> getDao() throws SQLException {
		return new BibliographyDao();
	}

}
