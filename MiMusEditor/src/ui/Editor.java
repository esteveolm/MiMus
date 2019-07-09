package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;
import control.EventObserver;
import control.SharedControl;
import control.SharedResources;
import model.Entity;
import model.EntityInstance;
import model.Instrument;
import model.Lloc;
import model.Materia;
import model.Artista;
import model.Bibliography;
import model.Casa;
import model.Document;
import model.MiMusReference;
import model.MiMusText;
import model.Ofici;
import model.Promotor;
import model.Relation;
import model.Transcription;
import persistence.AnyRelationDao;
import persistence.ArtistaDao;
import persistence.BibliographyDao;
import persistence.CasaDao;
import persistence.DocumentDao;
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
import util.LabelPrinter;
import util.TextStyler;

public class Editor extends EditorPart implements EventObserver {
	
	private SharedResources resources;
	private SharedControl control;
	private Connection conn;
	private Document docEntry;
	private String docIdStr;
	private MiMusText regest;
	private StyledText regestText;
	private StyledText transcriptionText;
	private String docID;
	private EntityTableViewer entityHelper;
	private RelationTableViewer relationHelper;
	private TranscriptionTableViewer transcriptionHelper;
	private ReferenceTableViewer referenceHelper;
	private List<EntityInstance> entityInstances;
	private List<Relation> relations;
	private List<Transcription> transcriptions;
	private List<Bibliography> bibliography;
	private List<MiMusReference> references;
	private FormToolkit toolkit;
	
	public Editor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
		} catch (SQLException e) {
			throw new PartInitException("Could not connect to SQL database");
		}
		
		docEntry = (Document) getEditorInput();
		docID = docEntry.getNumbering();
		System.out.println("Doc ID: " + docID);
		resources = SharedResources.getInstance();
		//resources.globallySetUpdateId();
		control = SharedControl.getInstance();
		control.addEditor(this);
		
		/* Txt must always be present so the text can be loaded */
		docIdStr = docEntry.getIdStr();
		regest = docEntry.getRegest();
		
		this.setPartName("Doc. " + docIdStr);
	}
	
	/**
	 * When Editor is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		control.removeEditor(this);
		toolkit.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Annotation");
		form.getBody().setLayout(new GridLayout());
		
		/* Read-only data */
		Text readOnlyText = new Text(form.getBody(), 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		String readOnlyStr = docEntry.getReadOnlyText();
		readOnlyText.setText(readOnlyStr);
		readOnlyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		readOnlyText.setEditable(false);
		
		/* DOC METADATA PART */
		/* Llengua: combo */
		Label labelLlengua = new Label(form.getBody(), SWT.VERTICAL);
		labelLlengua.setText("Llengua:");
		Combo comboLlengua = new Combo(form.getBody(), SWT.DROP_DOWN | SWT.READ_ONLY);
		comboLlengua.setItems(Document.LANGS);
		
		/* Set Llengua if already defined */
		String llengua = docEntry.getLanguage();
		for (int i=0; i<comboLlengua.getItems().length; i++) {
			if (llengua.equals(comboLlengua.getItem(i))) {
				comboLlengua.select(i);
				break;
			}
		}
		
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
		CheckboxTableViewer materiesTV = CheckboxTableViewer.newCheckList(form.getBody(),
				SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		materiesTV.setContentProvider(new ArrayContentProvider());
		materiesTV.setLabelProvider(new MateriesLabelProvider());
		
		/* The checkbox list is filled with the entries from Materies table on DB */
		List<Materia> allMateries = new ArrayList<>();
		try {
			allMateries = new MateriaDao(conn).selectAll();
		} catch (SQLException e1) {
			System.out.println("Could not query DB to retrieve Materies");
		}
		materiesTV.setInput(allMateries);
		
		/* Check those Materies already selected in Document */
		List<Materia> materies = docEntry.getSubjects();
		System.out.println("Read " + materies.size());
		for (int i=0; i<allMateries.size(); i++) {
			Materia thisMat = allMateries.get(i);
			for (Materia mat : materies) {
				if (mat.getName().equals(thisMat.getName())) {
					materiesTV.setChecked(thisMat, true);
				}
			}
		}
		
		/* Button to save Llengua and Matèries to XML */
		GridData gd = new GridData();
		gd.widthHint = 250;
		Button saveMeta = new Button(form.getBody(), SWT.PUSH | SWT.CENTER);
		saveMeta.setLayoutData(gd);
		saveMeta.setText("Save Llengua and Materies to DB");
		
		Label metaLabel = toolkit.createLabel(form.getBody(), "");
		metaLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* ENTITIES PART */
		/* Regest text */
		regestText = new StyledText(form.getBody(), 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		regestText.setText(docEntry.getRegestText());
		regestText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		regestText.setEditable(false);
		TextStyler styler = new TextStyler(regestText);
		
		/* List of entities */
		Section sectEnt = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectEnt.setText("Entities at Regest");
		
		/* Table of entities */
		entityInstances = new ArrayList<>();
		try {
			entityInstances = new InstanceDao(conn).select(docEntry);
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("SQLException: could not retrieve instances.");
		}
		entityHelper = new EntityTableViewer(sectEnt.getParent(), 
				entityInstances,
				styler, regest);
		TableViewer entityTV = entityHelper.createTableViewer();
		entityTV.refresh();
		
		/* Label of Regest entities */
		Label regestLabel = toolkit.createLabel(form.getBody(), "");
		regestLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove entities */
		GridData gridData = new GridData();
		gridData.widthHint = 125;
		Button addArt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addArt.setLayoutData(gridData);
		addArt.setText("Add Artist");
		Button addInst = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addInst.setLayoutData(gridData);
		addInst.setText("Add Instrument");
		Button addCasa = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addCasa.setLayoutData(gridData);
		addCasa.setText("Add Casa");
		Button addProm = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addProm.setLayoutData(gridData);
		addProm.setText("Add Promotor");
		Button addOfici = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addOfici.setLayoutData(gridData);
		addOfici.setText("Add Ofici");
		Button addLloc = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addLloc.setLayoutData(gridData);
		addLloc.setText("Add Lloc");
		
		Button removeEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		removeEnt.setLayoutData(gridData);
		removeEnt.setText("Delete");

		/* List of relations */
		Section sectRel = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectRel.setText("Relations between Entities");
		
		/* Table of relations */
		relations = new ArrayList<>();
		try {
			relations = new AnyRelationDao(conn).select(docEntry);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException: could not retrieve relations.");
		}
		relationHelper = new RelationTableViewer(sectRel.getParent(), 
				relations,
				styler);
		TableViewer relationTV = relationHelper.createTableViewer();
		relationTV.refresh();
		
		/* Label of Relations */
		Label relationLabel = toolkit.createLabel(form.getBody(), "");
		relationLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove relations */
		GridData gridRel = new GridData();
		gridRel.widthHint = 225;
		Button addArtToOfi = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addArtToOfi.setLayoutData(gridRel);
		addArtToOfi.setText("Add Artista-Ofici Relation");
		Button addPromToCasa = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addPromToCasa.setLayoutData(gridRel);
		addPromToCasa.setText("Add Promotor-Casa Relation");
		Button addArtToProm = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addArtToProm.setLayoutData(gridRel);
		addArtToProm.setText("Add Servei Relation");
		Button addArtToLloc = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		addArtToLloc.setLayoutData(gridRel);
		addArtToLloc.setText("Add Residencia Relation");
		Button removeRel = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		removeRel.setLayoutData(gridRel);
		removeRel.setText("Delete");
		
		/* TRANSCRIPTIONS PART */
		/* Transcription section */
		Section sectTrans = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectTrans.setText("Transcriptions of Entities");
		
		/* Transcription text */
		transcriptionText = new StyledText(sectTrans.getParent(), 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		transcriptionText.setText(docEntry.getTranscriptionText());
		transcriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		transcriptionText.setEditable(false);
		TextStyler transcriptionStyler = new TextStyler(transcriptionText);

		/* Transcription entities and its table */
		transcriptions = new ArrayList<>();
		try {
			transcriptions = new TranscriptionDao(conn).select(docEntry);
		} catch (SQLException e) {
			System.out.println("SQLException: could not retrieve transcriptions.");
		}
		transcriptionHelper = new TranscriptionTableViewer(sectTrans.getParent(),
				transcriptions);
		TableViewer transcriptionTV = transcriptionHelper.createTableViewer();
		transcriptionTV.refresh();
		
		/* Paint transcriptions */
		for (Transcription t: transcriptions) {
			transcriptionStyler.addUpdate(t.getCoords().x, t.getCoords().y);
		}
		
		/* Label of transcriptions */
		Label transcriptionLabel = toolkit.createLabel(sectTrans.getParent(), "");
		transcriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove transcription associations */
		GridData gridTrans = new GridData();
		gridTrans.widthHint = 125;
		Button addTransArt = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransArt.setLayoutData(gridTrans);
		addTransArt.setText("Add Artist");
		Button addTransInst = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransInst.setLayoutData(gridTrans);
		addTransInst.setText("Add Instrument");
		Button addTransCasa = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransCasa.setLayoutData(gridTrans);
		addTransCasa.setText("Add Casa");
		Button addTransProm = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransProm.setLayoutData(gridTrans);
		addTransProm.setText("Add Promotor");
		Button addTransOfici = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransOfici.setLayoutData(gridTrans);
		addTransOfici.setText("Add Ofici");
		Button addTransLloc = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		addTransLloc.setLayoutData(gridTrans);
		addTransLloc.setText("Add Lloc");
		
		Button removeTrans = new Button(sectTrans.getParent(), 
				SWT.PUSH | SWT.CENTER);
		removeTrans.setLayoutData(gridTrans);
		removeTrans.setText("Delete");
		
		
		/* REFERENCES PART */
		/* References section */
		Section sectRef = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectRef.setText("References in bibliography");
		
		String rawRefs = "Editions: " + docEntry.getEditions() +
				"\nRegisters: " + docEntry.getRegisters() +
				"\nCitations: " + docEntry.getCitations();
		Text rawRefsText = toolkit.createText(sectRef.getParent(), rawRefs, 
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
				sectRef.getParent(), references, bibliography, docEntry, resources);
		TableViewer referenceTV = referenceHelper.createTableViewer();
		referenceTV.refresh();
		
		/* Label of references */
		Label referenceLabel = toolkit.createLabel(sectRef.getParent(), "");
		referenceLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Buttons to add/remove references */
		GridData gridRef = new GridData();
		gridRef.widthHint = 100;
		Button addRef = new Button(sectRef.getParent(), SWT.PUSH | SWT.CENTER);
		addRef.setLayoutData(gridRef);
		addRef.setText("Add");
		Button removeRef = new Button(sectRef.getParent(), SWT.PUSH | SWT.CENTER);
		removeRef.setLayoutData(gridRef);
		removeRef.setText("Delete");
		
		/* BUTTON LISTENERS */
		saveMeta.addSelectionListener(new SelectionAdapter() {
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
						e1.printStackTrace();
						System.out.println("Could not update document in DB.");
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
		removeEnt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityInstance ent = (EntityInstance) 
						((IStructuredSelection) entityTV.getSelection())
						.getFirstElement();
				if (ent==null) {
					System.out.println(
							"Could not remove Entity because none was selected.");
					LabelPrinter.printError(regestLabel, 
							"You must select an entity to delete it.");
				} else if (Relation.containsEntity(relations, ent)) {
					System.out.println(
							"Could not remove Entity because being used by Relation.");
					LabelPrinter.printError(regestLabel, 
							"You must remove all relations using this entity before.");
				} else if (Transcription.containsEntity(
						transcriptions, ent)) {
					System.out.println(
							"Could not remove Entity because being used by Transcription.");
					LabelPrinter.printError(regestLabel, 
							"You must remove all transcriptions using this entity before.");
				} else {
					try {
						new InstanceDao(conn).delete(ent);
						entityInstances.remove(ent);
						entityHelper.packColumns();
						System.out.println("Removing entity - " 
								+ entityInstances.size());
						LabelPrinter.printInfo(regestLabel, 
								"Entity deleted successfully.");
						
						entityTV.refresh();
					} catch (SQLException e1) {
						e1.printStackTrace();
						System.out.println("SQLException: could not delete Instance.");
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
						relationHelper.packColumns();
						System.out.println("Removing relation - " 
								+ relations.size());
						LabelPrinter.printInfo(relationLabel, 
								"Relation deleted successfully.");
						
						relationTV.refresh();
					} catch (SQLException e1) {
						System.out.println("SQLException: could not delete relation.");
						e1.printStackTrace();
					}
				}
			}
		});
		
		/* Transcription buttons */
		addTransArt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog<Artista> dialog = 
						new TranscriptionDialog<Artista>(
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
				TranscriptionDialog<Instrument> dialog = 
						new TranscriptionDialog<Instrument>(
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
		addTransCasa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog<Casa> dialog = 
						new TranscriptionDialog<Casa>(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Casa";
					}
				};
				runTranscriptionDialog(dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransProm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog<Promotor> dialog = 
						new TranscriptionDialog<Promotor>(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Promotor";
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
				TranscriptionDialog<Ofici> dialog = 
						new TranscriptionDialog<Ofici>(
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
		addTransLloc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TranscriptionDialog<Lloc> dialog = 
						new TranscriptionDialog<Lloc>(
						entityInstances, parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Lloc";
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
						transcriptionHelper.packColumns();
						
						/* Undo colour in text */
						Point charCoords = trans.getCoords();
						transcriptionStyler.deleteUpdate(charCoords.x, charCoords.y);
						
						System.out.println("Removing lemma - " 
								+ transcriptions.size());
						LabelPrinter.printInfo(transcriptionLabel, 
								"Transcription deleted successfully.");
						
						transcriptionTV.refresh();
					} catch (SQLException e1) {
						System.out.println("SQLException: could not delete trans.");
					}
				}
			}
		});
		
		/* Reference buttons */
		addRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ReferenceDialog dialog = 
						new ReferenceDialog(bibliography,
						parent.getShell()) {
				};
				runReferenceDialog(dialog,bibliography, referenceLabel);
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
						System.out.println("SQLException: could not delete ref");
					}
				}
			}
		});
	}
	
	private void runDialog(InstanceDialog<? extends Entity> dialog, 
			List<EntityInstance> entities, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Entity added = dialog.getUnit();
			if (added != null) {
				if (EntityInstance.containsEntity(entities, added)) {
					/* Trying to add entity already added */
					System.out.println("No Entity added - "
							+ entities.size());
					LabelPrinter.printError(label, 
							"Cannot add the same entity twice.");
				} else {
					/* OK case */
					EntityInstance inst = new EntityInstance(
							added, docEntry);
					try {
						int id = new InstanceDao(conn).insert(inst);
						if (id>0) {
							inst.setId(id);
							entities.add(inst);
							System.out.println("Adding selected Entity - " 
									+ entities.size());
							LabelPrinter.printInfo(label, 
									"Entity added successfully.");
						} else {
							System.out.println("DAO: could not insert Instance.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
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
	
	private void runTranscriptionDialog(TranscriptionDialog<? extends Entity> dialog, 
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
						e.printStackTrace();
						System.out.println("SQLException: could not insert transcription.");
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
	
	private void runRelationDialog(RelationDialog dialog,
			List<Relation> relations, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Entity instance1 = dialog.getUnit1();
			Entity instance2 = dialog.getUnit2();
			if (instance1 != null && instance2 != null) {
				/* Two instances correctly selected */
				Relation rel = new Relation(docEntry,
						instance1, instance2, dialog.getRelType(),
						0);
				if (Relation.containsRelation(relations, rel)) {
					/* This Relation has been declared already */
					System.out.println("No Relation added, already there - " 
							+ relations.size());
					LabelPrinter.printError(label, 
							"Cannot add the same relation twice.");
				} else {
					/* OK case */
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
							int id = dao.insert(rel);
							if (id>0) {
								rel.setId(id);
								relations.add(rel);
								
								System.out.println("Adding selected Relation - " 
										+ relations.size());
								LabelPrinter.printInfo(label, 
										"Relation added successfully.");
							} else {
								System.out.println("DAO: could not add relation.");
							}
						} else {
							System.out.println("Error: unknown relation in dialog.");
						}
					} catch (SQLException e) {
						System.out.println("SQLException: could not add relation.");
						e.printStackTrace();
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
	
	private void runReferenceDialog(ReferenceDialog dialog,
			List<Bibliography> bibliography, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Bibliography added = dialog.getUnit();
			if (added != null) {
				if (MiMusReference.containsBibliography(references, added)) {
					/* Trying to add bibliography already added */
					System.out.println("No Reference added - "
							+ bibliography.size());
					LabelPrinter.printError(label, 
							"Cannot add the same reference twice.");
				} else {
					int type = dialog.getType();
					String pages = dialog.getPages();
					MiMusReference ref = new MiMusReference(added, docEntry,
							pages, type, 0);
					
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
					} catch (SQLException e) {
						System.out.println("SQLException: could not insert reference");
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
	
	/* Following methods shouldn't be touched */
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setFocus() {}
	
	@Override
	public void doSave(IProgressMonitor monitor) {}
	
	@Override
	public void doSaveAs() {}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void update() {		
		/* Refresh all table viewers */
		entityHelper.refresh();
		transcriptionHelper.refresh();
		//relationHelper.refresh();
		referenceHelper.refresh();
	}
}
