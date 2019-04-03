package ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import model.MiMusBibEntry;
import model.MiMusEntry;
import model.MiMusReference;
import model.MiMusText;
import model.Relation;
import model.Transcription;
import model.Unit;
import ui.table.EntityTableViewer;
import ui.table.ReferenceTableViewer;
import ui.table.RelationTableViewer;
import ui.table.TranscriptionTableViewer;
import util.LabelPrinter;
import util.MiMusEntryReader;
import util.TextStyler;
import util.xml.MiMusXML;

public class Editor extends EditorPart implements EventObserver {
	
	protected String txtPath;
	protected String xmlPath;
	private SharedResources resources;
	private SharedControl control;
	private MiMusEntry docEntry;
	private String docIdStr;
	private MiMusText regest;
	private MiMusText transcription;
	private StyledText regestText;
	private StyledText transcriptionText;
	private String docID;
	private EntityTableViewer entityHelper;
	private RelationTableViewer relationHelper;
	private TranscriptionTableViewer transcriptionHelper;
	private ReferenceTableViewer referenceHelper;
	private InstanceDialog dialog;
	private RelationDialog relDialog;
	private List<Unit> entityInstances;
	private List<Unit> relations;
	private List<Unit> transcriptions;
	private List<Unit> references;
	private FormToolkit toolkit;
	
	public Editor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		docID = getEditorInput().getName()
				.substring(0, getEditorInput().getName().indexOf('.'));
		System.out.println("Doc ID: " + docID);
		resources = SharedResources.getInstance();
		//resources.globallySetUpdateId();
		control = SharedControl.getInstance();
		control.addEditor(this);
		
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFolder txtFolder = corpus.getFolder("txt");
		IFolder xmlFolder = corpus.getFolder("xml");
		txtPath = txtFolder.getLocation().toString();
		xmlPath = xmlFolder.getLocation().toString();
		
		if (getEditorInput().getName().endsWith(".txt")) {
			txtPath += "/" + getEditorInput().getName();
			xmlPath += "/" + getEditorInput().getName().replace(".txt", ".xml");
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else if (getEditorInput().getName().endsWith(".xml")) {
			xmlPath += "/" + getEditorInput().getName();
			txtPath += "/" + getEditorInput().getName().replace(".xml", ".txt");
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else {
			System.out.println("Error: incompatible input file.");
			System.exit(1);
		}
		
		/* Txt must always be present so the text can be loaded */
		docEntry = new MiMusEntryReader().read(txtPath);
		docIdStr = docEntry.getIdStr();
		regest = docEntry.getRegest();
		transcription = docEntry.getTranscription();
		
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
		/* Create XML schema if not present */
		MiMusXML.openDoc(docIdStr).write();
		
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
		comboLlengua.setItems(MiMusEntry.LANGS);
		
		/* Set Llengua if already defined */
		String llengua = MiMusXML.openDoc(docIdStr).readLlengua();
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
				return (String) element;
			}
		}
		
		CheckboxTableViewer materiesTV = CheckboxTableViewer.newCheckList(form.getBody(),
				SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		materiesTV.setContentProvider(new ArrayContentProvider());
		materiesTV.setLabelProvider(new MateriesLabelProvider());
		materiesTV.setInput(MiMusEntry.MATERIES);
		
		/* Set Materies if already defined */
		List<String> materies = MiMusXML.openDoc(docIdStr).readMateries();
		System.out.println("Read " + materies.size());
		for (int i=0; i<MiMusEntry.MATERIES.length; i++) {
			String thisMat = MiMusEntry.MATERIES[i];
			for (String mat : materies) {
				if (mat.equals(thisMat)) {
					materiesTV.setChecked(thisMat, true);
				}
			}
		}
		
		/* Button to save Llengua and Matèries to XML */
		GridData gd = new GridData();
		gd.widthHint = 250;
		Button saveMeta = new Button(form.getBody(), SWT.PUSH | SWT.CENTER);
		saveMeta.setLayoutData(gd);
		saveMeta.setText("Save Llengua and Materies to XML");
		
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
		entityHelper = new EntityTableViewer(sectEnt.getParent(), 
				EntityInstance.read(docIdStr),
				styler, regest);
		TableViewer entityTV = entityHelper.createTableViewer();
		entityInstances = entityHelper.getEntities();
		
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
		relationHelper = new RelationTableViewer(sectRel.getParent(), 
				Relation.read(docIdStr),
				styler);
		TableViewer relationTV = relationHelper.createTableViewer();
		relations = relationHelper.getRelations();
		
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
		transcriptionHelper = new TranscriptionTableViewer(sectTrans.getParent(),
				Transcription.read(docIdStr, entityInstances));
		TableViewer transcriptionTV = transcriptionHelper.createTableViewer();
		transcriptions = transcriptionHelper.getTranscriptions();
		
		/* Paint transcriptions */
		for (Unit u: transcriptions) {
			Transcription t = (Transcription) u;
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
		
		/* Table of references */
		List<Unit> bibEntries = new ArrayList<>(resources.getBibEntries());
		referenceHelper = new ReferenceTableViewer(
				sectRef.getParent(), 
				MiMusReference.read(docIdStr, bibEntries), 
				bibEntries, docEntry, resources);
		TableViewer referenceTV = referenceHelper.createTableViewer();
		references = referenceHelper.getReferences();
		
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
					String llengua = comboLlengua.getItem(llenguaSel);
					Object[] materies = materiesTV.getCheckedElements();
					String[] materiesStr = new String[materies.length];
					for (int i=0; i<materies.length; i++) {
						materiesStr[i] = (String) materies[i];
					}
					MiMusXML.openDoc(docIdStr).updateMeta(llengua, materiesStr).write();
					LabelPrinter.printInfo(metaLabel, 
							"Llengua and materies added successfully");
					System.out.println("Llengua and materies added successfully");
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
				System.out.println(resources.getArtistas().size() + "ARTISTS");
				InstanceDialog dialog = new InstanceDialog(
						resources.getArtistas(), parent.getShell()) {
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
				dialog = new InstanceDialog(
						resources.getInstruments(), parent.getShell()) {
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
				dialog = new InstanceDialog(
						resources.getCases(), parent.getShell()) {
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
				dialog = new InstanceDialog(
						resources.getPromotors(), parent.getShell()) {
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
				dialog = new InstanceDialog(
						resources.getOficis(), parent.getShell()) {
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
				dialog = new InstanceDialog(
						resources.getLlocs(), parent.getShell()) {
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
					System.out.println(ent);
					entityInstances.remove(ent);
					MiMusXML.openDoc(docIdStr).remove(ent).write();
					entityTV.refresh();
					entityHelper.packColumns();
					System.out.println("Removing entity - " 
							+ entityInstances.size());
					LabelPrinter.printInfo(regestLabel, 
							"Entity deleted successfully.");
				}
			}
		});
		
		/* Relation buttons */
		relDialog = null;
		addArtToOfi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relDialog = new RelationDialog(entityInstances, parent.getShell(),
						"artista", "ofici");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addPromToCasa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relDialog = new RelationDialog(entityInstances, parent.getShell(),
						"promotor", "casa");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addArtToProm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relDialog = new RelationDialog(entityInstances, parent.getShell(),
						"artista", "promotor");
				runRelationDialog(relDialog, relations, relationLabel);
				relationTV.refresh();
			}
		});
		addArtToLloc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				relDialog = new RelationDialog(entityInstances, parent.getShell(),
						"artista", "lloc");
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
					System.out.println(
							"Could not remove Relation because none was selected.");
					LabelPrinter.printError(relationLabel, 
							"You must select a relation to delete it.");
				} else {
					relations.remove(rel);
					MiMusXML.openDoc(docIdStr).remove(rel).write();
					relationTV.refresh();
					relationHelper.packColumns();
					System.out.println("Removing relation - " 
							+ relations.size());
					LabelPrinter.printInfo(relationLabel, 
							"Relation deleted successfully.");
				}
			}
		});
		
		/* Transcription buttons */
		addTransArt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getArtistas(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Artista";
					}
				};
				runTranscriptionDialog((TranscriptionDialog) dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransInst.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getInstruments(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Instrument";
					}
				};
				runTranscriptionDialog((TranscriptionDialog)dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransCasa.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getCases(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Casa";
					}
				};
				runTranscriptionDialog((TranscriptionDialog)dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransProm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getPromotors(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Promotor";
					}
				};
				runTranscriptionDialog((TranscriptionDialog)dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransOfici.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getOficis(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Ofici";
					}
				};
				runTranscriptionDialog((TranscriptionDialog)dialog, 
						transcriptions, entityInstances, 
						transcriptionLabel, transcriptionStyler);
				transcriptionTV.refresh();
			}
		});
		addTransLloc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog = new TranscriptionDialog(
						resources.getLlocs(), parent.getShell(), "") {
					@Override
					public String getDialogName() {
						return "Lloc";
					}
				};
				runTranscriptionDialog((TranscriptionDialog) dialog,
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
					System.out.println(
							"Could not remove Transcription because none was selected.");
					LabelPrinter.printError(transcriptionLabel, 
							"You must select a transcription to delete it.");
				} else {
					/* Undo colour in text */
					Point charCoords = trans.getCoords();
					transcriptionStyler.deleteUpdate(charCoords.x, charCoords.y);
					
					/* Remove transcription */
					transcriptions.remove(trans);
					MiMusXML.openDoc(docIdStr).remove(trans).write();
					transcriptionTV.refresh();
					System.out.println("Removing lemma - " 
							+ transcriptions.size());
					LabelPrinter.printInfo(transcriptionLabel, 
							"Transcription deleted successfully.");
				}
			}
		});
		
		/* Reference buttons */
		addRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MiMusReference ref = new MiMusReference(
						SharedResources.getInstance().getBibEntries().get(0),
						"",
						0, 
						resources.getIncrementId());
				references.add(ref);
				
				/* Reflect document is user of entry, in model and xml */
				MiMusBibEntry modifiedEntry = 
						SharedResources.getInstance().getBibEntries().get(0);
				modifiedEntry.addUser(Integer.parseInt(docID));
				MiMusXML.openBiblio().update(modifiedEntry).write();
				MiMusXML.openDoc(docIdStr).append(ref).write();
				referenceTV.refresh();
				
				LabelPrinter.printInfo(referenceLabel, 
						"Reference added successfully.");
				System.out.println("Reference added successfully.");
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
					references.remove(ref);
					
					/* 
					 * Reflect document is not user of entry anymore, 
					 * in model and xml 
					 */
					MiMusBibEntry oldEntry = ref.getBibEntry();
					oldEntry.removeUser(new Integer(Integer.parseInt(docID)));
					MiMusXML.openBiblio().update(oldEntry).write();
					MiMusXML.openDoc(docIdStr).remove(ref).write();
					referenceTV.refresh();
					
					LabelPrinter.printInfo(referenceLabel, 
							"Reference deleted successfully.");
					System.out.println("Removing Reference " + ref.toString());
				}
			}
		});
	}
	
	private void runDialog(InstanceDialog dialog, List<Unit> entities,
			Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			Entity added = (Entity) dialog.getEntity();
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
							added,
							resources.getIncrementId());
					entities.add(inst);
					MiMusXML.openDoc(docIdStr).append(inst).write();
					System.out.println("Adding selected Entity - " 
							+ entities.size());
					LabelPrinter.printInfo(label, 
							"Entity added successfully.");
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
	
	private void runTranscriptionDialog(TranscriptionDialog dialog, 
			List<Unit> transcriptions, List<Unit> entities, 
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
					Entity ent = (Entity) dialog.getEntity();
					String form = dialog.getTranscription();
					if (form=="")
						/* User did not add a form, use selection directly */
						form = dialog.getSelectedText();
					if (EntityInstance.containsEntity(entities, ent)) {
						/* Selected entity has been marked in this document */
						Transcription trans = new Transcription(
								EntityInstance.getInstanceWithEntity(entities, ent),
								selectedText, form, charCoords, 
								resources.getIncrementId());
						transcriptions.add(trans);
						MiMusXML.openDoc(docIdStr).append(trans).write();
						System.out.println("Adding selected Transcription - " 
								+ transcriptions.size());
						LabelPrinter.printInfo(label, 
								"Lemma added successfully.");
						styler.addUpdate(charCoords.x, charCoords.y);
					} else {
						/* Entity not in document, don't add transcription */
						System.out.println(
								"Entity of Transcription not in document - "
								+ transcriptions.size());
						LabelPrinter.printError(label, 
								"Entity selected must have been marked in"
								+ " this document.");
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
			List<Unit> relations, Label label) {
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			EntityInstance instance1 = (EntityInstance) dialog.getInstance1();
			EntityInstance instance2 = (EntityInstance) dialog.getInstance2();
			if (instance1 != null && instance2 != null) {
				/* Two instances correctly selected */
				Relation rel = new Relation(
						instance1, instance2, dialog.getRelType(),
						resources.getIncrementId());
				if (Relation.containsRelation(relations, rel)) {
					/* This Relation has been declared already */
					System.out.println("No Relation added, already there - " 
							+ relations.size());
					LabelPrinter.printError(label, 
							"Cannot add the same relation twice.");
				} else {
					/* OK case */
					relations.add(rel);
					MiMusXML.openDoc(docIdStr).append(rel).write();
					System.out.println("Adding selected Relation - " 
							+ relations.size());
					LabelPrinter.printInfo(label, 
							"Relation added successfully.");
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
	
	public List<Unit> getReferences() {
		return referenceHelper.getReferences();
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
