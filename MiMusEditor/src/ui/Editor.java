package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import model.Artista;
import model.Bibliography;
import model.Casa;
import model.Document;
import model.Entity;
import model.EntityInstance;
import model.GenereLiterari;
import model.Instrument;
import model.Lloc;
import model.Materia;
import model.MiMusReference;
import model.MiMusText;
import model.Note;
import model.Ofici;
import model.Promotor;
import model.Relation;
import model.Transcription;
import persistence.AnyRelationDao;
import persistence.ArtistaDao;
import persistence.BibliographyDao;
import persistence.CasaDao;
import persistence.DocumentDao;
import persistence.GenereLiterariDao;
import persistence.InstanceDao;
import persistence.InstrumentDao;
import persistence.LlocDao;
import persistence.MateriaDao;
import persistence.OficiDao;
import persistence.PromotorDao;
import persistence.ReferenceDao;
import persistence.RelationDao;
import persistence.ResideixADao;
import persistence.ServeixADao;
import persistence.TeCasaDao;
import persistence.TeOficiDao;
import persistence.TranscriptionDao;
import ui.dialog.InstanceDialog;
import ui.dialog.ReferenceDialog;
import ui.dialog.RelationDialog;
import ui.dialog.TranscriptionDialog;
import ui.table.EntityTableViewer;
import ui.table.ReferenceTableViewer;
import ui.table.RelationTableViewer;
import ui.table.TranscriptionTableViewer;
import util.DBUtils;
import util.LabelPrinter;
import util.TextStyler;

/**
 * Editor is the Editor of MiMus Documents. When a user clicks
 * on an element of DocumentsView, this Editor is opened using
 * as input the Document selected.
 * 
 * The Editor is MiMus main component. It allows for annotation of
 * Entities, Relations, Transcriptions, References and metadata 
 * in the Documents. It also allows for control for the state of the
 * annotation.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Editor extends EditorPart {
	
	private Connection conn;
	private Document docEntry;
	private String docIdStr;
	private MiMusText regest;
	private Text regestText;
	private StyledText transcriptionText;
	private String docID;
	private EntityTableViewer entityHelper;
	private RelationTableViewer relationHelper;
	private TranscriptionTableViewer transcriptionHelper;
	private ReferenceTableViewer referenceHelper;
	private CheckboxTableViewer materiesTV;
	private Combo comboLlengua;
	private List<EntityInstance> entityInstances;
	private List<Relation> relations;
	private List<Transcription> transcriptions;
	private List<Bibliography> bibliography;
	private List<MiMusReference> references;
	private List<Materia> allMateries;
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private boolean editMode = false;
	private boolean dirty = false;
	
	private Button editSaveBtn;
	private Text numbering, lloc1, lloc2;
	private MimusDateControl mimusDate;
	private SignatureControl signatureA, signatureB;
	
	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			dirty=true;
			editSaveBtn.setEnabled(true);
			firePropertyChange(PROP_DIRTY);
		}		
	};
	
	public Editor() {
		super();
	}
	
	/**
	 * Initializes the Editor with the input Document and setting
	 * some class attributes.
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		System.out.println("***INIT***");
		setSite(site);
		setInput(input);
		
		try {
			conn = DBUtils.connect();
		} catch (SQLException e) {
			throw new PartInitException("Could not connect to SQL database");
		}
		
		/* 
		 * DocumentView loads all documents on opening but is unaware of changes
		 * in metadata that modify the state of a Document. Hence, first thing
		 * the editor does is retrieve the Document from DB again using the ID 
		 * (which is a field that never changes).
		 */
		docEntry = (Document) getEditorInput();
		updateDocument();
		
		docID = docEntry.getNumbering();
		System.out.println("Doc ID: " + docID);
		
		/* Txt must always be present so the text can be loaded */
		docIdStr = docEntry.getIdStr();
		regest = docEntry.getRegest();
		
		this.setPartName("Doc. " + docIdStr);
	}
	
	/**
	 * Downloads Document object from DB again, effectively refreshing
	 * the UI.
	 */
	private void updateDocument() {
		try {
			docEntry = new DocumentDao(conn).selectOne(docEntry.getId());
		} catch (SQLException e) {
			System.out.println("Couldn't download last version of Document.");
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		toolkit.dispose();
	}
	
	/**
	 * Draws the Editor.
	 */
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("***CREATE PART CONTROL***");
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Annotation");
		
		form.getBody().setLayout(new GridLayout());
		
		Composite buttons = toolkit.createComposite(form.getBody(), SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttons.setLayout(new GridLayout(6,false));
				
		Button refreshBtn = toolkit.createButton(
				buttons, (editMode?"Cancel":"Refresh"), SWT.PUSH | SWT.CENTER);
		editSaveBtn = toolkit.createButton(
				buttons, (editMode?"Save":"Edit"), SWT.PUSH | SWT.CENTER);
		
		editSaveBtn.setEnabled(!editMode);
				

		
		/* SECTION STATUS OF THE DOCUMENT */
		Section sectStatus = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectStatus.setText("Status of the document");
		sectStatus.setExpanded(false);
		
		/* State of annotation and revision: combos */
		Composite compStatus = toolkit.createComposite(sectStatus);
		sectStatus.setClient(compStatus);
		compStatus.setLayout(new GridLayout());
		
		Label labelStateAnnot = new Label(compStatus, SWT.VERTICAL);
		labelStateAnnot.setText("Estat de l'anotació:");
		Combo comboStateAnnot = 
				new Combo(compStatus, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboStateAnnot.setItems(Document.STATES_ANNOT);
		comboStateAnnot.select(docEntry.getStateAnnotIdx());
		
		Label labelStateRev = new Label(compStatus, SWT.VERTICAL);
		labelStateRev.setText("Estat de la revisió:");
		Combo comboStateRev = 
				new Combo(compStatus, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboStateRev.setItems(Document.STATES_REV);
		comboStateRev.select(docEntry.getStateRevIdx());
		
		/* Button to save state */
		Button saveState = new Button(compStatus, SWT.PUSH | SWT.CENTER);
		saveState.setText("Save state to DB");
		
		/* Info label of state of the document */
		Label stateLabel = new Label(compStatus, SWT.VERTICAL);
		stateLabel.setText("");
		stateLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		/* SECTION STATIC DATA */
		Label titleStatic = new Label(form.getBody(), SWT.VERTICAL);
		titleStatic.setText("Static:");
		FontData fontDataTitle = titleStatic.getFont().getFontData()[0];
		Font fontTitle = new Font(titleStatic.getDisplay(), 
				new FontData(fontDataTitle.getName(), 
						fontDataTitle.getHeight(), 
						SWT.BOLD));
		titleStatic.setFont(fontTitle);
		
		GridData staticData = new GridData(GridData.FILL_HORIZONTAL);
		staticData.widthHint = 10;
		
		
		if(editMode) {
			createDocumentForm();
		} else {
			Text readOnlyText = new Text(form.getBody(), 
					SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
			readOnlyText.setText(docEntry.getReadOnlyText());
			readOnlyText.setEditable(false);
			readOnlyText.setLayoutData(staticData);			
		}
		
		
		/* SECTION REGEST */		
		/* 
		 * GridData for Regest.
		 * Apparently, texts only wrap when their unwrapped width surpasses the
		 * specified widthHint in the GridData, hence we use a small widthHint
		 * to force the wrap behaviour in any usual window sizes.
		 */
		GridData regestData = new GridData(GridData.FILL_HORIZONTAL);
		regestData.widthHint = 10;
		
		Label titleRegest = new Label(form.getBody(), SWT.VERTICAL);
		titleRegest.setText("Regest:");
		titleRegest.setFont(fontTitle);
		
		/* Regest text wraps if too long */
		regestText = new Text(form.getBody(),
				SWT.BORDER | (editMode?0:SWT.READ_ONLY) | SWT.MULTI | SWT.WRAP);
		regestText.setText(docEntry.getRegestText());
		regestText.setLayoutData(regestData);
		
		if(editMode) {
			regestText.addModifyListener(modifyListener);
		}
		
		/* SECTION ENTITIES */
		Section sectEnt = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectEnt.setText("Entities at Regest");
		Composite compEnt = toolkit.createComposite(sectEnt);
		compEnt.setLayout(new GridLayout());
		sectEnt.setClient(compEnt);
		sectEnt.setExpanded(false);

		/* Table of entities */
		entityInstances = new ArrayList<>();
		try {
			entityInstances = new InstanceDao(conn).select(docEntry);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("SQLException: could not retrieve instances.");
		}
		entityHelper = new EntityTableViewer(compEnt, 
				entityInstances, regest);
		TableViewer entityTV = entityHelper.createTableViewer();
		entityTV.refresh();
		
		/* Label of Regest entities */
		Label regestLabel = toolkit.createLabel(compEnt, "");
		regestLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove entities: 3-column grid */
		Composite compEntBtns = toolkit.createComposite(compEnt);
		GridLayout layoutEntBtns = new GridLayout();
		layoutEntBtns.numColumns = 3;
		compEntBtns.setLayout(layoutEntBtns);
		
		GridData buttonsEntData = new GridData();
		buttonsEntData.widthHint = 150;
		Button addArt = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addArt.setLayoutData(buttonsEntData);
		addArt.setText("Add Artista");
		Button addProm = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addProm.setLayoutData(buttonsEntData);
		addProm.setText("Add Promotor");
		Button addLloc = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addLloc.setLayoutData(buttonsEntData);
		addLloc.setText("Add Lloc");
		Button addOfici = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addOfici.setLayoutData(buttonsEntData);
		addOfici.setText("Add Ofici");
		Button addCasa = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addCasa.setLayoutData(buttonsEntData);
		addCasa.setText("Add Casa");
		Button addGenere = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addGenere.setLayoutData(buttonsEntData);
		addGenere.setText("Add Gènere literari");
		Button addInst = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		addInst.setLayoutData(buttonsEntData);
		addInst.setText("Add Instrument");
		
		Button removeEnt = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		removeEnt.setLayoutData(buttonsEntData);
		removeEnt.setText("Delete");
		
		Button associateEnt = new Button(compEntBtns, SWT.PUSH | SWT.CENTER);
		associateEnt.setLayoutData(buttonsEntData);
		associateEnt.setText("Open Entity");

		
		/* SECTION RELATIONS */
		Section sectRel = toolkit.createSection(form.getBody(), 
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectRel.setText("Relations between Entities");
		Composite compRel = toolkit.createComposite(sectRel);
		compRel.setLayout(new GridLayout());
		sectRel.setClient(compRel);
		sectRel.setExpanded(false);
		
		/* Table of relations */
		relations = new ArrayList<>();
		try {
			relations = new AnyRelationDao(conn).select(docEntry);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: could not retrieve relations.");
		}
		relationHelper = new RelationTableViewer(compRel, relations);
		TableViewer relationTV = relationHelper.createTableViewer();
		relationTV.refresh();
		
		/* Label of Relations */
		Label relationLabel = toolkit.createLabel(compRel, "");
		relationLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove relations: 2-column grid */
		Composite compRelBtns = toolkit.createComposite(compRel);
		GridLayout layoutRelBtns = new GridLayout();
		layoutRelBtns.numColumns = 2;
		compRelBtns.setLayout(layoutRelBtns);
		
		GridData gridRel = new GridData();
		gridRel.widthHint = 225;
		Button addArtToOfi = new Button(compRelBtns, SWT.PUSH | SWT.CENTER);
		addArtToOfi.setLayoutData(gridRel);
		addArtToOfi.setText("Add Artista-Ofici Relation");
		Button addPromToCasa = new Button(compRelBtns, SWT.PUSH | SWT.CENTER);
		addPromToCasa.setLayoutData(gridRel);
		addPromToCasa.setText("Add Promotor-Casa Relation");
		Button addArtToProm = new Button(compRelBtns, SWT.PUSH | SWT.CENTER);
		addArtToProm.setLayoutData(gridRel);
		addArtToProm.setText("Add Servei Relation");
		Button addArtToLloc = new Button(compRelBtns, SWT.PUSH | SWT.CENTER);
		addArtToLloc.setLayoutData(gridRel);
		addArtToLloc.setText("Add Residència Relation");
		Button removeRel = new Button(compRelBtns, SWT.PUSH | SWT.CENTER);
		removeRel.setLayoutData(gridRel);
		removeRel.setText("Delete");
		
		
		/* SECTION TRANSCRIPTION */
		transcriptions = new ArrayList<>();
		try {
			transcriptions = new TranscriptionDao(conn).select(docEntry);
		} catch (SQLException e) {
			System.out.println("SQLException: could not retrieve transcriptions.");
		}
		Label titleTranscription = new Label(form.getBody(), SWT.VERTICAL);
		titleTranscription.setText("Transcription:");
		titleTranscription.setFont(fontTitle);
		
		/* GridData for Transcription: wrap behaviour like Regest */
		GridData transcriptionData = new GridData(GridData.FILL_HORIZONTAL);
		transcriptionData.widthHint = 10;

		
		/* Transcription text wraps if too long */
		boolean transcriptionEditable = editMode && transcriptions.size()==0;
		
		transcriptionText = new StyledText(form.getBody(), 
				SWT.BORDER | (transcriptionEditable?0:SWT.READ_ONLY) | SWT.MULTI | SWT.WRAP);
		transcriptionText.setText(docEntry.getTranscriptionText());		
		if(transcriptionEditable) {
			transcriptionText.addModifyListener(modifyListener);
		}
		transcriptionText.setLayoutData(transcriptionData);
		TextStyler transcriptionStyler = new TextStyler(transcriptionText);
		
		/* SECTION TRANSCRIPTION FORMS */
		/* Transcription entities and its table */
		Section sectForms = toolkit.createSection(form.getBody(),
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectForms.setText("Transcription forms");
		Composite compForms = toolkit.createComposite(sectForms);
		compForms.setLayout(new GridLayout());
		sectForms.setClient(compForms);
		sectForms.setExpanded(false);
		
		transcriptionHelper = new TranscriptionTableViewer(compForms,
				transcriptions);
		TableViewer transcriptionTV = transcriptionHelper.createTableViewer();
		transcriptionTV.refresh();
		
		/* Paint transcriptions */
		for (Transcription t: transcriptions) {
			transcriptionStyler.addUpdate(t.getCoords().x, t.getCoords().y);
		}
		
		/* Label of transcriptions */
		Label transcriptionLabel = toolkit.createLabel(compForms, "");
		transcriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove transcription associations: 3-column grid */
		Composite compFormsBtns = toolkit.createComposite(compForms);
		GridLayout layoutFormsBtns = new GridLayout();
		layoutFormsBtns.numColumns = 3;
		compFormsBtns.setLayout(layoutFormsBtns);
		
		GridData gridTrans = new GridData();
		gridTrans.widthHint = 150;
		Button addTransArt = new Button(compFormsBtns, 
				SWT.PUSH | SWT.CENTER);
		addTransArt.setLayoutData(gridTrans);
		addTransArt.setText("Add Artista");
		Button addTransInst = new Button(compFormsBtns, 
				SWT.PUSH | SWT.CENTER);
		addTransInst.setLayoutData(gridTrans);
		addTransInst.setText("Add Instrument");
		Button addTransOfici = new Button(compFormsBtns, 
				SWT.PUSH | SWT.CENTER);
		addTransOfici.setLayoutData(gridTrans);
		addTransOfici.setText("Add Ofici");
		Button addTransGen = new Button(compFormsBtns, 
				SWT.PUSH | SWT.CENTER);
		addTransGen.setLayoutData(gridTrans);
		addTransGen.setText("Add Gènere Literari");
		
		Button removeTrans = new Button(compFormsBtns, 
				SWT.PUSH | SWT.CENTER);
		removeTrans.setLayoutData(gridTrans);
		removeTrans.setText("Delete");
		
		
		/* SECTION METADATA */
		Section sectMeta = toolkit.createSection(form.getBody(),
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectMeta.setText("Metadata");
		Composite compMeta = toolkit.createComposite(sectMeta);
		compMeta.setLayout(new GridLayout());
		sectMeta.setClient(compMeta);
		sectMeta.setExpanded(false);
		
		/* Llengua: combo */
		toolkit.createLabel(compMeta, "Llengua:");
		comboLlengua = new Combo(compMeta, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboLlengua.setItems(Document.LANGS);
		
		/* Set Llengua if already defined */
		selectLlengua();
		
		
		/* Materies: CheckBoxTableViewer */
		class MateriesLabelProvider extends LabelProvider
				implements ITableLabelProvider {
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((Materia) element).getName();
			}
		}
		
		/* Materies checkbox list */
		toolkit.createLabel(compMeta, "Matèries:");
		
		materiesTV = CheckboxTableViewer.newCheckList(compMeta,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		materiesTV.setContentProvider(new ArrayContentProvider());
		materiesTV.setLabelProvider(new MateriesLabelProvider());
		materiesTV.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				Materia m1 = (Materia) e1;
				Materia m2 = (Materia) e2;
				return m1.getName().compareTo(m2.getName());
			}
		});
		
		/* The checkbox list is filled with the entries from Materies table on DB */
		allMateries = new ArrayList<>();
		try {
			allMateries = new MateriaDao(conn).selectAll();
		} catch (SQLException e1) {
			System.out.println("Could not query DB to retrieve Materies");
		}
		materiesTV.setInput(allMateries);
		
		/* Check those Materies already selected in Document */
		checkMateries();
		
		/* Button to save Llengua and Matèries to SQL */
		Button saveMeta = new Button(compMeta, SWT.PUSH | SWT.CENTER);
		saveMeta.setText("Save Llengua and Matèries to DB");
		
		Label metaLabel = toolkit.createLabel(compMeta, "");
		metaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		/* NOTES PART */
		Label titleNotes = new Label(form.getBody(), SWT.VERTICAL);
		titleNotes.setText("Notes:");
		titleNotes.setFont(fontTitle);
		
		String notesStr = "";
		for (Note note : docEntry.getNotes()) {
			notesStr += note.getText() + "\n\n";
		}
		Text notesText = new Text(form.getBody(), 
				SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		notesText.setText(notesStr.trim());
		GridData notesData = new GridData(GridData.FILL_HORIZONTAL);
		notesData.widthHint = 10;
		notesText.setLayoutData(notesData);
		
		/* REFERENCES PART */
		/* References section */
		Section sectRef = toolkit.createSection(form.getBody(),  
				ExpandableComposite.TREE_NODE | ExpandableComposite.CLIENT_INDENT);
		sectRef.setText("References in bibliography");
		Composite compRef = toolkit.createComposite(sectRef);
		compRef.setLayout(new GridLayout());
		sectRef.setClient(compRef);
		sectRef.setExpanded(false);
		
		String rawRefs = "Edició: " + docEntry.getEditions() +
				"\nRegest: " + docEntry.getRegisters() +
				"\nCitació: " + docEntry.getCitations();
		Text rawRefsText = toolkit.createText(compRef, rawRefs, 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		rawRefsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Table of references. Requires bibliography and references */
		bibliography = new ArrayList<>();
		try {
			bibliography = new BibliographyDao(conn).selectAll();
		} catch (SQLException e) {
			System.out.println("SQLException: could not retrieve bibliography.");
		}
		references = new ArrayList<>();
		try {
			references = new ReferenceDao(conn).select(docEntry);
		} catch (SQLException e) {
			System.out.println("SQLException: could not retrieve references.");
			e.printStackTrace();
		}
		referenceHelper = new ReferenceTableViewer(
				compRef, references, bibliography, docEntry);
		TableViewer referenceTV = referenceHelper.createTableViewer();
		referenceTV.refresh();
		
		/* Label of references */
		Label referenceLabel = toolkit.createLabel(compRef, "");
		referenceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove references */
		GridData gridRef = new GridData();
		gridRef.widthHint = 100;
		Button addRef = new Button(compRef, SWT.PUSH | SWT.CENTER);
		addRef.setLayoutData(gridRef);
		addRef.setText("Add");
		Button removeRef = new Button(compRef, SWT.PUSH | SWT.CENTER);
		removeRef.setLayoutData(gridRef);
		removeRef.setText("Delete");
		
		
		/* BUTTON LISTENERS */
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!isDirty() || MessageDialog.openConfirm(null, "Current document was modified", "Do you want to discard changes?")) {					
					editMode=false;
					dirty = false;
					firePropertyChange(PROP_DIRTY);
					refreshBtn.setText("Refresh");
					editSaveBtn.setText("Edit");
					updateDocument();				
					Composite parent = form.getParent();
					form.dispose();
					createPartControl(parent);
				}
			}
		});

		editSaveBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(editMode) { // doSave (and returns no normal mode)
					doSave(null);
				} else {  // Activate edit mode
					editMode = true;
					Composite parent = form.getParent();
					form.dispose();
					createPartControl(parent);
				}
			}
		});


		saveState.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int annotSel = comboStateAnnot.getSelectionIndex();
				int revSel = comboStateRev.getSelectionIndex();
				
				/* Update on model */
				docEntry.setStateAnnotIdx(annotSel);
				docEntry.setStateRevIdx(revSel);
				
				/* Update on DB */
				try {
					new DocumentDao(getConnection()).update(docEntry);
					LabelPrinter.printInfo(stateLabel, 
							"State updated successfully");
					System.out.println("State updated successfully");
				} catch (SQLException e1) {
					if (e1.getSQLState().equals("42000")) {
						System.out.println("Disconnected exception.");
						LabelPrinter.printError(stateLabel, 
								"You must be connected to perform changes to the DB.");
					} else {
						e1.printStackTrace();
						System.out.println("Could not update document in DB.");
					}
				}
			}
		});
		saveMeta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/* Recover items to save */
				
				int llenguaSel = comboLlengua.getSelectionIndex();
				if (llenguaSel>=0) {
					/* Update llengua in model object*/
					docEntry.setLanguage(llenguaSel);
					
					/* Update list of materies in model object */
					Object[] checked = materiesTV.getCheckedElements();
					List<Materia> newMateries = new ArrayList<>();
					for (int i=0; i<checked.length; i++) {
						newMateries.add((Materia) checked[i]);
					}
					docEntry.setSubjects(newMateries);
					
					/* Update in DB */
					try {
						new DocumentDao(getConnection()).update(docEntry);
						LabelPrinter.printInfo(metaLabel, 
								"Llengua and materies added successfully");
						System.out.println("Llengua and materies added successfully");
					} catch (SQLException e1) {
						if (e1.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(metaLabel, 
									"You must be connected to perform changes to the DB.");
						} else {
							e1.printStackTrace();
							System.out.println("Could not update document in DB.");
						}
					}
					
				} else {
					LabelPrinter.printError(metaLabel, 
							"Llengua must be specified");
					System.out.println("Llengua must be specified");
				}
			}
		});

		/* Entity buttons */
		addArt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Artista> artists = new ArrayList<>();
				
				try {
					artists = new ArtistaDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve Artists");
				}
				InstanceDialog<Artista> dialog = new InstanceDialog<Artista>(
						artists, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Artista";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addInst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Instrument> instruments = new ArrayList<>();
				try {
					instruments = new InstrumentDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve instruments");
				}
				InstanceDialog<Instrument> dialog = new InstanceDialog<Instrument>(
						instruments, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Instrument";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addCasa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Casa> cases = new ArrayList<>();
				try {
					cases = new CasaDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve cases");
				}
				InstanceDialog<Casa> dialog = new InstanceDialog<Casa>(
						cases, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Casa";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addProm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Promotor> proms = new ArrayList<>();
				try {
					proms = new PromotorDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve proms");
				}
				InstanceDialog<Promotor> dialog = new InstanceDialog<Promotor>(
						proms, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Promotor";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addOfici.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Ofici> oficis = new ArrayList<>();
				try {
					oficis = new OficiDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve oficis");
				}
				InstanceDialog<Ofici> dialog = new InstanceDialog<Ofici>(
						oficis, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Ofici";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addLloc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Lloc> llocs = new ArrayList<>();
				try {
					llocs = new LlocDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve llocs");
				}
				InstanceDialog<Lloc> dialog = new InstanceDialog<Lloc>(
						llocs, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "Lloc";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		addGenere.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<GenereLiterari> generes = new ArrayList<>();
				try {
					generes = new GenereLiterariDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve generes");
				}
				InstanceDialog<GenereLiterari> dialog = 
						new InstanceDialog<GenereLiterari>(
								generes, parent.getShell()) {
					@Override
					public String getDialogName() {
						return "GenereLiterari";
					}
				};
				runDialog(dialog, entityInstances, regestLabel);
				entityTV.refresh();
			}
		});
		removeEnt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EntityInstance ent = (EntityInstance) 
						((IStructuredSelection) entityTV.getSelection())
						.getFirstElement();
				if (ent==null) {
					System.out.println(
							"Could not remove Entity because none was selected.");
					LabelPrinter.printError(regestLabel, 
							"You must select an entity to delete it.");
				} else {
					/* 
					 * Check if instance is being used by relation.
					 * Our SQL schema cannot provide such constraint because
					 * in the DB relations are linked to entities, not instances.
					 */
					boolean used = false;
					for (Relation rel : relations) {
						if (ent.getItsEntity().getId()
								==rel.getItsEntity1().getId()
							|| ent.getItsEntity().getId()
								==rel.getItsEntity2().getId()) {
							used = true;
							break;
						}
					}
					if (!used) {
						/* OK case */
						try {
							new InstanceDao(conn).delete(ent);
							entityInstances.remove(ent);
							System.out.println("Removing entity - " 
									+ entityInstances.size());
							LabelPrinter.printInfo(regestLabel, 
									"Entity deleted successfully.");
							
							entityTV.refresh();
						} catch (SQLIntegrityConstraintViolationException e1) {
							LabelPrinter.printError(regestLabel, 
									"Cannot delete Entity Instance in use.");
							System.out.println("Could not delete: entity in use.");
						} catch (SQLException e2) {
							if (e2.getSQLState().equals("42000")) {
								System.out.println("Disconnected exception.");
								LabelPrinter.printError(regestLabel, 
										"You must be connected to perform changes to the DB.");
							} else {
								e2.printStackTrace();
								System.out.println("SQLException: could not delete Instance.");
							}
						}
					} else {
						/* Entity in use in some relation of the document */
						LabelPrinter.printError(regestLabel, 
								"Cannot delete Entity Instance in use.");
						System.out.println("Could not delete: entity in use.");
					}
				}
			}
		});
		associateEnt.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				EntityInstance ent = (EntityInstance) 
						((IStructuredSelection) entityTV.getSelection())
						.getFirstElement();
				if (ent==null) {
					System.out.println(
							"Could not open Entity because none was selected.");
					LabelPrinter.printError(regestLabel, 
							"You must select an entity to open it.");
				} else {
					if (ent.getItsEntity().getType().equals("Artista")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.artistaView");
							DeclarativeView<Artista> decView = 
									(DeclarativeView<Artista>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("Casa")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.casaView");
							DeclarativeView<Casa> decView = 
									(DeclarativeView<Casa>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("GenereLiterari")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.genereView");
							DeclarativeView<GenereLiterari> decView = 
									(DeclarativeView<GenereLiterari>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("Instrument")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.instrumentView");
							DeclarativeView<Instrument> decView = 
									(DeclarativeView<Instrument>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("Lloc")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.llocView");
							DeclarativeView<Lloc> decView = 
									(DeclarativeView<Lloc>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("Ofici")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.oficiView");
							DeclarativeView<Ofici> decView = 
									(DeclarativeView<Ofici>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					} else if (ent.getItsEntity().getType().equals("Promotor")) {
						try {
							IViewPart view = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()
									.getActivePage()
									.showView("MiMusEditor.promotorView");
							DeclarativeView<Promotor> decView = 
									(DeclarativeView<Promotor>) view;
							decView.selectEntityInTable(ent.getItsEntity());
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		/* Relation buttons */
		addArtToOfi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationDialog relDialog = 
						new RelationDialog(entityInstances, parent.getShell(),
						"Artista", "Ofici");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addPromToCasa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationDialog relDialog = 
						new RelationDialog(entityInstances, parent.getShell(),
						"Promotor", "Casa");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addArtToProm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationDialog relDialog = 
						new RelationDialog(entityInstances, parent.getShell(),
						"Artista", "Promotor");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addArtToLloc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RelationDialog relDialog = 
						new RelationDialog(entityInstances, parent.getShell(),
						"Artista", "Lloc");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		removeRel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Relation rel = (Relation)
						((IStructuredSelection) relationTV.getSelection())
						.getFirstElement();
				if (rel==null) {
					/* No relation was selected before pressing Delete */
					System.out.println(
							"Could not remove Relation because none was selected.");
					LabelPrinter.printError(relationLabel, 
							"You must select a relation to delete it.");
				} else {
					try {
						/* OK case */
						new AnyRelationDao(conn).delete(rel);
						relations.remove(rel);
						System.out.println("Removing relation - " 
								+ relations.size());
						LabelPrinter.printInfo(relationLabel, 
								"Relation deleted successfully.");
						
						relationTV.refresh();
					} catch (SQLException e1) {
						if (e1.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(relationLabel, 
									"You must be connected to perform changes to the DB.");
						} else {
							System.out.println("SQLException: could not delete relation.");
							e1.printStackTrace();
						}
					}
				}
			}
		});
		
		/* Transcription buttons */
		addTransArt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog dialog = 
						new TranscriptionDialog(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Artista";
					}
				};
				runTranscriptionDialog(dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransInst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog dialog = 
						new TranscriptionDialog(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Instrument";
					}
				};
				runTranscriptionDialog(dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransOfici.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog dialog = 
						new TranscriptionDialog(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Ofici";
					}
				};
				runTranscriptionDialog(dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransGen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog dialog = 
						new TranscriptionDialog(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "GenereLiterari";
					}
				};
				runTranscriptionDialog(dialog,
						transcriptions, entityInstances,
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		removeTrans.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Transcription trans = (Transcription) 
						((IStructuredSelection) transcriptionTV.getSelection())
							.getFirstElement();
				if (trans==null) {
					/* No transcription was selected before pressing Delete */
					System.out.println(
							"Could not remove Transcription because none was selected.");
					LabelPrinter.printError(transcriptionLabel, 
							"You must select a transcription to delete it.");
				} else {
					/* OK case */
					try {
						new TranscriptionDao(conn).delete(trans);
						transcriptions.remove(trans);
						
						/* Undo colour in text */
						Point charCoords = trans.getCoords();
						transcriptionStyler.deleteUpdate(charCoords.x, charCoords.y);
						
						System.out.println("Removing lemma - " 
								+ transcriptions.size());
						LabelPrinter.printInfo(transcriptionLabel, 
								"Transcription deleted successfully.");
						transcriptionTV.refresh();
					} catch (SQLException e1) {
						if (e1.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(transcriptionLabel, 
									"You must be connected to perform changes to the DB.");
						} else {
							System.out.println("SQLException: could not delete trans.");
						}
					}
				}
			}
		});
		
		/* Reference buttons */
		addRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Bibliography> biblio = new ArrayList<>();
				
				try {
					biblio = new BibliographyDao(conn).selectAll();
				} catch (SQLException e1) {
					System.out.println("SQLException: could not retrieve Artists");
				}
				ReferenceDialog dialog = 
						new ReferenceDialog(biblio, docEntry.getNotes(),
						parent.getShell()) {
				};
				runReferenceDialog(dialog, bibliography, referenceLabel);
				referenceTV.refresh();
			}
		});
		removeRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MiMusReference ref = (MiMusReference) 
						((IStructuredSelection) referenceTV.getSelection())
						.getFirstElement();
				if (ref==null) {
					System.out.println(
							"Could not remove Reference because none was selected.");
					LabelPrinter.printError(referenceLabel, 
							"You must select a reference to delete it.");
				} else {
					try {
						new ReferenceDao(conn).delete(ref);
						references.remove(ref);
						
						LabelPrinter.printInfo(referenceLabel, 
								"Reference deleted successfully.");
						System.out.println("Removing Reference " + ref.toString());
						
						referenceTV.refresh();
					} catch (SQLException e1) {
						if (e1.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(referenceLabel, 
									"You must be connected to perform changes to the DB.");
						} else {
							System.out.println("SQLException: could not delete ref");
							e1.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	private void createDocumentForm() {
		
		Composite c = toolkit.createComposite(form.getBody());
		c.setLayout(new GridLayout(2, false));
		
		toolkit.createLabel(c, "Doc ID:");
		toolkit.createLabel(c, docEntry.getIdStr());
		  
		toolkit.createLabel(c, "Numeració antiga:");
		numbering = toolkit.createText(c, docEntry.getNumbering());
		numbering.setLayoutData(GridDataFactory.swtDefaults().hint(100, SWT.DEFAULT).create());
		numbering.addModifyListener(modifyListener);

		toolkit.createLabel(c, "Data:");
		mimusDate = new MimusDateControl(c, toolkit, docEntry.getDate());
		mimusDate.addModifyListener(modifyListener);

		toolkit.createLabel(c, "Lloc:");
		Composite llocComposite = toolkit.createComposite(c);
		llocComposite.setLayout(new GridLayout(4, false));
		toolkit.createLabel(llocComposite, "de");
		lloc1 = toolkit.createText(llocComposite, docEntry.getPlace1());
		lloc1.addModifyListener(modifyListener);
		lloc1.setMessage("lloc d’emissió del document.");
		toolkit.createLabel(llocComposite, "a");
		lloc2 = toolkit.createText(llocComposite, docEntry.getPlace2());
		lloc2.setMessage("(deixar en blanc si no hi ha data2)");
		lloc2.addModifyListener(modifyListener);

		toolkit.createLabel(c, "Signatura A:");
		signatureA = new SignatureControl(c, toolkit, docEntry.getLibrary());
		signatureA.addModifyListener(modifyListener);
		toolkit.createLabel(c, "Signatura B:");
		signatureB = new SignatureControl(c, toolkit, docEntry.getLibrary2());
		signatureB.addModifyListener(modifyListener);
				
	}

	/**
	 * Given the Document which contains certain Materies, it
	 * checks all their checkbox entries in the Editor.
	 */
	private void checkMateries() {
		List<Materia> materies = docEntry.getSubjects();
		for (int i=0; i<allMateries.size(); i++) {
			Materia thisMat = allMateries.get(i);
			for (Materia mat : materies) {
				if (mat.getName().equals(thisMat.getName())) {
					materiesTV.setChecked(thisMat, true);
				}
			}
		}
	}

	/**
	 * Given the Document which contains a certain Llengua, it
	 * selects this option in the Editor.
	 */
	private void selectLlengua() {
		String llengua = docEntry.getLanguageStr();
		for (int i=0; i<comboLlengua.getItems().length; i++) {
			if (llengua.equals(comboLlengua.getItem(i))) {
				comboLlengua.select(i);
				break;
			}
		}
	}

	/**
	 * When a button for adding entities is pressed, an InstanceDialog
	 * is opened to the user. This method processes its result and
	 * performs the insertion to the DB when it's the case.
	 */
	private void runDialog(InstanceDialog<? extends Entity> dialog, 
			List<EntityInstance> entities, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Entity added = dialog.getUnit();
			if (added != null) {
				EntityInstance inst = new EntityInstance(
						added, docEntry);
				try {
					int id = new InstanceDao(conn).insert(inst);
					if (id>0) {
						/* OK case */
						inst.setId(id);
						entities.add(inst);
						System.out.println("Adding selected Entity - " 
								+ entities.size());
						LabelPrinter.printInfo(label, 
								"Entity added successfully.");
					} else {
						System.out.println("DAO: could not insert Instance.");
					}
				} catch (SQLIntegrityConstraintViolationException e1) {
					/* Unique constraint violated when inserting same entity */
					System.out.println("Cannot insert same Instance twice.");
					LabelPrinter.printError(label, 
							"Cannot add the same entity twice.");
				} catch (SQLException e2) {
					if (e2.getSQLState().equals("42000")) {
						System.out.println("Disconnected exception.");
						LabelPrinter.printError(label, 
								"You must be connected to perform changes to the DB.");
					} else {
						e2.printStackTrace();
						System.out.println("SQLException: could not insert Instance.");
					}
				}
			} else {
				/* No entities declared, nothing could be selected */
				System.out.println("No Entity added - " 
						+ entities.size());
				LabelPrinter.printInfo(label, 
						"Nothing was added. Must declare any first.");
			}
		} else {
			/* No entity was selected from the list */
			System.out.println("No Entity added - " 
					+ entities.size());
			LabelPrinter.printInfo(label, 
					"Nothing was added.");
		}
	}
	
	/**
	 * When a button for adding transcriptions is pressed,
	 * a TranscriptionDialog is opened to the user. This method 
	 * processes its result and performs the insertion to the DB 
	 * when it's the case.
	 */
	private void runTranscriptionDialog(TranscriptionDialog dialog, 
			List<Transcription> transcriptions, List<EntityInstance> entities, 
			Label label, TextStyler styler) {
		Point charCoords = transcriptionText.getSelection();
		if (charCoords.x!=charCoords.y) {
			/* Picks transcripted form in String and text coordinates */
//			charCoords = transcription.fromWordToCharCoordinates(
//					transcription.fromCharToWordCoordinates(
//					charCoords));	// Trick to ensure selection of whole words
			transcriptionText.setSelection(charCoords);
			String selectedText = transcriptionText.getSelectionText();
			dialog.setSelectedText(selectedText);
			
			/* Dialog blocks Editor until user closes window */
			int dialogResult = dialog.open();
			if (dialogResult == Window.OK) {
				int selection = dialog.getSelection();
				if (selection>=0) {
					/* User actually selected something */
					EntityInstance inst = dialog.getUnit();
					System.out.println("Selected instance with ID: " + inst.getId() +
							" and type of entity: " + inst.getItsEntity().getType());
					String form = dialog.getTranscription();
					if (form=="") {
						/* User did not add a form, use selection directly */
						form = dialog.getSelectedText();
					}
					/* Selected entity has been marked in this document */
					Transcription trans = new Transcription(
							inst, selectedText, form, charCoords);
					try {
						int id = new TranscriptionDao(conn).insert(trans);
						if (id>0) {
							/* OK case */
							trans.setId(id);
							transcriptions.add(trans);
							LabelPrinter.printInfo(label, 
									"Lemma added successfully.");
							styler.addUpdate(charCoords.x, charCoords.y);
						} else {
							System.out.println("DAO: could not insert transcription.");
						}
					} catch (SQLException e) {
						if (e.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							e.printStackTrace();
							System.out.println("SQLException: could not insert transcription.");
						}
					}
				} else {
					/* User pressed OK but selected nothing */
					System.out.println("No Transcription added - " 
							+ transcriptions.size());
					LabelPrinter.printInfo(label, 
							"Nothing was added. Must add any entity first.");
				}
			} else {
				/* User pressed Cancel, nothing to add */
				System.out.println("No Transcription added - " 
						+ transcriptions.size());
				LabelPrinter.printInfo(label, 
						"Nothing was added.");
			}
		} else {
			/* User marked no transcription in text */
			System.out.println("Could not add Selected Entity"
					+ " because no text was selected");
			LabelPrinter.printError(label, 
					"You must select a part of the text.");
		}
	}
	
	/**
	 * When a button for adding relations is pressed, a RelationDialog
	 * is opened to the user. This method processes its result and
	 * performs the insertion to the DB when it's the case.
	 */
	private void runRelationDialog(RelationDialog dialog,
			List<Relation> relations, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Entity instance1 = dialog.getUnit1();
			Entity instance2 = dialog.getUnit2();
			if (instance1 != null && instance2 != null) {
				/* Two instances correctly selected */
				Relation rel = new Relation(docEntry,
						instance1, instance2, "",
						0, 0);
				try {
					String type1 = dialog.getEntityType1();
					String type2 = dialog.getEntityType2();
					RelationDao dao = null;
					if (type1.equals("Artista") && type2.equals("Ofici")) {
						dao = new TeOficiDao(conn);
					} else if (type1.equals("Promotor") && type2.equals("Casa")) {
						dao = new TeCasaDao(conn);
					} else if (type1.equals("Artista") && type2.equals("Promotor")) {
						dao = new ServeixADao(conn);
					} else if (type1.equals("Artista") && type2.equals("Lloc")) {
						dao = new ResideixADao(conn);
					}
					
					if (dao != null) {
						dialog.setRelType(dao.getTable());
						rel.setType(dialog.getRelType());
						try {
							int id = dao.insert(rel);
							if (id>0) {
								/* OK case */
								relations.clear();
								relations.addAll(new AnyRelationDao(getConnection())
										.select(docEntry));
								
								System.out.println("Adding selected Relation - " 
										+ relations.size());
								LabelPrinter.printInfo(label, 
										"Relation added successfully.");
							} else {
								System.out.println("DAO: could not add relation.");
							}
						} catch (SQLIntegrityConstraintViolationException e1) {
							/* Unique constraint violated */
							System.out.println(
									"Cannot add the same relation twice.");
							LabelPrinter.printError(label, 
									"Cannot add the same relation twice.");
						}
					} else {
						System.out.println("Error: unknown relation in dialog.");
					}
				} catch (SQLException e2) {
					if (e2.getSQLState().equals("42000")) {
						System.out.println("Disconnected exception.");
						LabelPrinter.printError(label, 
								"You must be connected to perform changes to the DB.");
					} else {
						System.out.println("SQLException: could not add relation.");
						e2.printStackTrace();
					}
				}
			} else {
				/* No instances selected by the user */
				System.out.println("No Relation added - " 
						+ relations.size());
				LabelPrinter.printInfo(label, 
						"Nothing was added. Must select two entities.");
			}
		} else {
			/* Operation cancelled by the user */
			System.out.println("No Relation added - " 
					+ relations.size());
			LabelPrinter.printInfo(label, 
					"Nothing was added.");
		}
	}
	
	/**
	 * When a button for adding references is pressed, a ReferenceDialog
	 * is opened to the user. This method processes its result and
	 * performs the insertion to the DB when it's the case.
	 */
	private void runReferenceDialog(ReferenceDialog dialog,
			List<Bibliography> bibliography, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Bibliography added = dialog.getUnit();
			int type = dialog.getType();
			if (added != null) {
				Note note = dialog.getNote();
				if (type<0){
					/* Happens when type has not been set by user */
					System.out.println("No reference added - " 
							+ bibliography.size());
					LabelPrinter.printError(label, "Must select type of reference.");
				} else {
					String pages = dialog.getPages();
					MiMusReference ref = new MiMusReference(added, docEntry,
							note, pages, type, 0);
					try {
						int id = new ReferenceDao(conn).insert(ref);
						if (id>0) {
							/* OK case */
							ref.setId(id);
							references.add(ref);
							
							System.out.println("Adding selected Reference - " 
									+ references.size());
							LabelPrinter.printInfo(label, 
									"Reference added successfully.");
						} else {
							System.out.println("DAO: could not insert reference");
						}
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, 
								"Cannot insert same Reference twice.");
						System.out.println("Cannot insert same Reference twice.");
					} catch (SQLException e2) {
						if (e2.getSQLState().equals("42000")) {
							System.out.println("Disconnected exception.");
							LabelPrinter.printError(label, 
									"You must be connected to perform changes to the DB.");
						} else {
							System.out.println("SQLException: could not insert reference");
						}
					}
				}
			} else {
				/* No bibliography declared, nothing could be selected */
				System.out.println("No Reference added - " 
						+ references.size());
				LabelPrinter.printInfo(label, 
						"Nothing was added. Must declare any first.");
			}
		} else {
			/* Operation cancelled by the user */
			System.out.println("No Reference added - " 
					+ references.size());
			LabelPrinter.printInfo(label, 
					"Nothing was added.");
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}
	
	/**
	 * Saves some document fields, clears the dirty state, and returns editor to normal mode.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {

		try {
			new DocumentDao(getConnection()).update(docEntry.getId(), numbering.getText(), mimusDate.getValue(), 
					("".equals(lloc1.getText())?null:lloc1.getText()), 
					("".equals(lloc2.getText())?null:lloc2.getText()), 
					signatureA.getValue(), signatureB.getValue(), regest.getText(), transcriptionText.getText());
			updateDocument();
			dirty = false;
			editSaveBtn.setEnabled(false);
			firePropertyChange(PROP_DIRTY);
			editMode=false;
			Composite parent = form.getParent();
			form.dispose();
			createPartControl(parent);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(null, "Error", "Could not update the document", new Status(IStatus.ERROR,"MiMusEditor", e.getMessage()));
		}
	}
	
	@Override
	public void doSaveAs() {}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
