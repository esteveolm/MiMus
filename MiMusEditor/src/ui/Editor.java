package ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import model.Transcription;
import model.Unit;
import ui.table.EntityTableViewer;
import ui.table.ReferenceTableViewer;
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
	private TranscriptionTableViewer transcriptionHelper;
	private ReferenceTableViewer referenceHelper;
	private InstanceDialog dialog;
	private List<Unit> entityInstances;
	private List<Unit> transcriptions;
	private List<Unit> references;
	
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
	}
	
	@Override
	public void createPartControl(Composite parent) {
		/* Create XML schema if not present */
		MiMusXML.openDoc(docIdStr).write();
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
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
		gridData.widthHint = 100;
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
		
		Button removeEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		removeEnt.setLayoutData(gridData);
		removeEnt.setText("Delete");

		
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
		gridTrans.widthHint = 100;
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
		Label rawRefsLabel = toolkit.createLabel(sectRef.getParent(), rawRefs);
		rawRefsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
//				} else if (relations.using(ent)) {
//					System.out.println("Could not remove Entity because it is used in an existing Relation.");
//					LabelPrinter.printError(regestLabel, "You must remove all relations where this entity is used before you can remove it.");
//				} else if (lemmas.using(ent)) {
//					System.out.println("Could not remove Entity because it is used in an existing Lemmatization.");
//					LabelPrinter.printError(regestLabel, "You must remove all lemmas where this entity is used before you can remove it.");
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
		toolkit.dispose();
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
