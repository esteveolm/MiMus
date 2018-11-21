package ui;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import control.EventObserver;
import control.SharedControl;
import control.SharedResources;
import model.EntitiesList;
import model.Entity;
import model.Lemma;
import model.LemmasList;
import model.MiMusBibEntry;
import model.MiMusEntry;
import model.MiMusReference;
import model.MiMusText;
import model.ReferencesList;
import model.Relation;
import model.RelationsList;
//import ui.table.EntityTableViewer;
//import ui.table.LemmaTableViewer;
import ui.table.ReferenceTableViewer;
//import ui.table.RelationTableViewer;
import util.LabelPrinter;
import util.MiMusEntryReader;
import util.TextStyler;
import util.xml.MiMusXML;
import util.xml.MiMusXMLWriter;

public class Editor extends EditorPart implements EventObserver {
	
	protected String txtPath;
	protected String xmlPath;
	private boolean hasXML;
	private SharedResources resources;
	private SharedControl control;
	private MiMusEntry docEntry;
	private MiMusText regest;
	private MiMusText transcription;
	private StyledText regestText;
	private StyledText transcriptionText;
	private String docID;
	private int entityCurrentID;
	private int referenceCurrentID;
	//private EntityTableViewer entityHelper;
	//private RelationTableViewer relationHelper;
	//private LemmaTableViewer lemmaHelper;
	private ReferenceTableViewer referenceHelper;
	
	public Editor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		docID = getEditorInput().getName().substring(0, getEditorInput().getName().indexOf('.'));
		System.out.println("Doc ID: " + docID);
		entityCurrentID = 0;
		referenceCurrentID = 0;
		resources = SharedResources.getInstance();
		control = SharedControl.getInstance();
		control.addEditor(this);
		
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder corpus = project.getFolder("MiMusCorpus");
		IFolder txtFolder = corpus.getFolder("txt");
		IFolder xmlFolder = corpus.getFolder("xml");
		txtPath = txtFolder.getLocation().toString();
		xmlPath = xmlFolder.getLocation().toString();
		
		if (getEditorInput().getName().endsWith(".txt")) {
			txtPath += "/" + getEditorInput().getName();
			xmlPath += "/" + getEditorInput().getName().replace(".txt", ".xml");
			File xmlFile = new File(xmlPath);
			hasXML = xmlFile.exists();
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else if (getEditorInput().getName().endsWith(".xml")) {
			xmlPath += "/" + getEditorInput().getName();
			txtPath += "/" + getEditorInput().getName().replace(".xml", ".txt");
			hasXML = true;
			System.out.println(xmlPath);
			System.out.println(txtPath);
		} else {
			System.out.println("Error: incompatible input file.");
			System.exit(1);
		}
		
		/* Txt must always be present so the text can be loaded */
		docEntry = new MiMusEntryReader().read(txtPath);
		regest = docEntry.getRegest();
		transcription = docEntry.getTranscription();
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
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Annotation");
		form.getBody().setLayout(new GridLayout());
		
		/* Read-only data */
		Text readOnlyText = new Text(form.getBody(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		String readOnlyStr = docEntry.getReadOnlyText();
		readOnlyText.setText(readOnlyStr);
		readOnlyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		readOnlyText.setEditable(false);
		
		/* Regest text */
		regestText = new StyledText(form.getBody(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		regestText.setText(docEntry.getRegestText());
		regestText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
		regestText.setEditable(false);
		TextStyler styler = new TextStyler(regestText);
		
//		/* List of entities */
//		Section sectEnt = toolkit.createSection(form.getBody(), PROP_TITLE);
//		sectEnt.setText("Entities at Regest");
//		
//		/* Table of entities */
//		entityHelper = new EntityTableViewer(sectEnt.getParent(), styler, regest);
//		TableViewer entityTV = entityHelper.createTableViewer();
//		EntitiesList regestEntities = entityHelper.getEntities();
//		
//		/* Label of Regest entities */
//		Label regestLabel = toolkit.createLabel(form.getBody(), "");
//		regestLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		/* Buttons to add/remove entities */
//		GridData gridData = new GridData();
//		gridData.widthHint = 100;
//		Button setEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
//		setEnt.setLayoutData(gridData);
//		setEnt.setText("Add");
//		
//		Button removeEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
//		removeEnt.setLayoutData(gridData);
//		removeEnt.setText("Delete");
		
//		/* List of relations */
//		Section sectRel = toolkit.createSection(form.getBody(), PROP_TITLE);
//		sectRel.setText("Relations at Regest");
//		
//		/* Table of relations */
//		relationHelper = new RelationTableViewer(sectRel.getParent(), styler, regestEntities);
//		TableViewer relationTV = relationHelper.createTableViewer();
//		RelationsList relations = relationHelper.getRelations();
//		
//		/* Label of relations */
//		Label relationsLabel = toolkit.createLabel(form.getBody(), "");
//		relationsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		/* Buttons to add/remove relations */
//		GridData gridRel = new GridData();
//		gridRel.widthHint = 100;
//		Button addRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
//		addRel.setLayoutData(gridRel);
//		addRel.setText("Add");
//		
//		Button removeRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
//		removeRel.setLayoutData(gridRel);
//		removeRel.setText("Delete");
		
//		/* Transcription part of the form */
//		Section sectTrans = toolkit.createSection(form.getBody(), PROP_TITLE);
//		sectTrans.setText("Entities at Transcription");
//		
//		/* Transcription text */
//		transcriptionText = new StyledText(sectTrans.getParent(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
//		transcriptionText.setText(docEntry.getTranscriptionText());
//		transcriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
//		transcriptionText.setEditable(false);
//		TextStyler transcriptionStyler = new TextStyler(transcriptionText);
//		EntitiesList transcriptionEntities = new EntitiesList(transcription.getWords());
//
//		/* Transcription entities & Lemmatizations table */
//		lemmaHelper = new LemmaTableViewer(sectTrans.getParent(), transcriptionStyler, regestEntities, transcriptionEntities);
//		TableViewer lemmaTV = lemmaHelper.createTableViewer();
//		LemmasList lemmas = lemmaHelper.getLemmas();
//		
//		/* Label of transcriptions */
//		Label transcriptionLabel = toolkit.createLabel(sectTrans.getParent(), "");
//		transcriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		/* Buttons to add/remove lemma associations */
//		GridData gridLemma = new GridData();
//		gridLemma.widthHint = 100;
//		Button addLemma = new Button(sectTrans.getParent(), SWT.PUSH | SWT.CENTER);
//		addLemma.setLayoutData(gridLemma);
//		addLemma.setText("Add");
//		
//		Button removeLemma = new Button(sectTrans.getParent(), SWT.PUSH | SWT.CENTER);
//		removeLemma.setLayoutData(gridLemma);
//		removeLemma.setText("Delete");
		
		/* References part of the form */
		Section sectRef = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectRef.setText("References in bibliography");
		
		String rawRefs = "Editions: " + docEntry.getEditions() +
				"\nRegisters: " + docEntry.getRegisters() +
				"\nCitations: " + docEntry.getCitations();
		Label rawRefsLabel = toolkit.createLabel(sectRef.getParent(), rawRefs);
		rawRefsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Table of references, necessary to initialize it with Unknown with initList() */
		ReferencesList references = new ReferencesList(resources.getBibEntries());
		referenceHelper = new ReferenceTableViewer(sectRef.getParent(), references, docID, resources);
		TableViewer referenceTV = referenceHelper.createTableViewer();
		
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
		
		/* Button listeners */
		
//		setEnt.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				Point charCoords = regestText.getSelection();
//				if (charCoords.x!=charCoords.y) {
//					charCoords = regest.fromWordToCharCoordinates(
//							regest.fromCharToWordCoordinates(charCoords));	// Trick to ensure selection of whole words
//					Point wordCoords = regest.fromCharToWordCoordinates(charCoords);
//					regestEntities.addUnit(new TypedEntity(regest.getWords(), wordCoords.x, wordCoords.y, entityCurrentID++));
//					System.out.println("Adding Selected Entity - " + regestEntities.countUnits());
//					LabelPrinter.printInfo(regestLabel, "Entity added successfully.");
//					styler.addUpdate(charCoords.x, charCoords.y);
//				} else {
//					System.out.println("Could not add Selected Entity because no text was selected");
//					LabelPrinter.printError(regestLabel, "You must select a part of text.");
//				}
//			}
//		});
//		removeEnt.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				Entity ent = (Entity) ((IStructuredSelection) entityTV.getSelection())
//						.getFirstElement();
//				if (ent==null) {
//					System.out.println("Could not remove Entity because none was selected.");
//					LabelPrinter.printInfo(regestLabel, "You must select an entity to delete it.");
//				} else if (relations.using(ent)) {
//					System.out.println("Could not remove Entity because it is used in an existing Relation.");
//					LabelPrinter.printError(regestLabel, "You must remove all relations where this entity is used before you can remove it.");
//				} else if (lemmas.using(ent)) {
//					System.out.println("Could not remove Entity because it is used in an existing Lemmatization.");
//					LabelPrinter.printError(regestLabel, "You must remove all lemmas where this entity is used before you can remove it.");
//				} else {
//					System.out.println(ent);
//					Point charCoords = regest.fromWordToCharCoordinates(
//							new Point(ent.getFrom(), ent.getTo()));
//					regestEntities.removeUnit(ent);
//					entityHelper.packColumns();
//					System.out.println("Removing entity - " + regestEntities.countUnits());
//					LabelPrinter.printInfo(regestLabel, "Entity deleted successfully.");
//					styler.deleteUpdate(charCoords.x, charCoords.y);
//				}
//			}
//		});
//		addRel.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				if (regestEntities.countUnits()<2) {
//					LabelPrinter.printError(relationsLabel, "Cannot create a relation if fewer than 2 entities are created.");
//					System.out.println("Cannot create relation with fewer than 2 entities.");
//				} else {
//					relations.addUnit(new Relation(regestEntities));
//					LabelPrinter.printInfo(relationsLabel, "Relation created successfully.");
//					System.out.println("Adding relation - " + relations.countUnits());
//				}
//				
//			}
//		});
//		removeRel.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				Relation rel = (Relation) ((IStructuredSelection) relationTV.getSelection())
//						.getFirstElement();
//				if (rel==null) {
//					System.out.println("Could not remove Relation because none was selected.");
//					LabelPrinter.printError(relationsLabel, "You must select a relation to delete it.");
//				} else {
//					System.out.println(rel);
//					relations.removeUnit(rel);
//					LabelPrinter.printInfo(relationsLabel, "Relation deleted successfully.");
//					System.out.println("Removing relation - " + relations.countUnits());
//				}
//			}
//		});
//		addLemma.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Point charCoords = transcriptionText.getSelection();
//				if (charCoords.x!=charCoords.y) {
//					charCoords = transcription.fromWordToCharCoordinates(
//							transcription.fromCharToWordCoordinates(
//							charCoords));	// Trick to ensure selection of whole words
//					Point wordCoords = transcription.fromCharToWordCoordinates(charCoords);
//					Entity transEnt = new UntypedEntity(transcription.getWords(), wordCoords.x, wordCoords.y, entityCurrentID++);
//					transcriptionEntities.addUnit(transEnt);
//					lemmas.addUnit(new Lemma(regestEntities, transcriptionEntities, regestEntities.getIdAt(0), transEnt.getId()));
//					LabelPrinter.printInfo(transcriptionLabel, "Lemma added successfully.");
//					transcriptionStyler.addUpdate(charCoords.x, charCoords.y);
//				} else {
//					System.out.println("Could not add Selected Entity because no text was selected");
//					LabelPrinter.printError(transcriptionLabel, "You must select a part of the text.");
//				}
//			}
//		});
//		removeLemma.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Lemma lemma = (Lemma) ((IStructuredSelection) lemmaTV.getSelection())
//							.getFirstElement();
//				if (lemma==null) {
//					System.out.println("Could not remove Lemma because none was selected.");
//					LabelPrinter.printError(transcriptionLabel, "You must select a lemma to delete it.");
//				} else {
//					/* First, undo colour using objects */
//					LabelPrinter.printInfo(transcriptionLabel, "Lemma deleted successfully.");
//					Point charCoords = transcription.fromWordToCharCoordinates(new Point(
//							lemma.getTranscriptionEntityObject().getFrom(), 
//							lemma.getTranscriptionEntityObject().getTo()));
//					transcriptionStyler.deleteUpdate(charCoords.x, charCoords.y);
//					
//					/* Then, remove objects from lists */
//					System.out.println(lemma);
//					lemmas.removeUnit(lemma);
//					System.out.println("Removing lemma - " + lemmas.countUnits());
//					transcriptionEntities.removeUnit(lemma.getTranscriptionEntityObject());
//					System.out.println("Removing transcription entity - " + transcriptionEntities.countUnits());
//				}
//			}
//		});
		addRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				references.addUnit(new MiMusReference(references, 0, referenceCurrentID++));
				
				/* Reflect that the entry is being used by a document */
				//references.getBibEntries().get(0).getUsers().add(Integer.parseInt(docID));
				MiMusBibEntry modifiedEntry = references.getBibEntries().get(0);
				modifiedEntry.addUser(Integer.parseInt(docID));
				MiMusXML.openArtista().update(modifiedEntry).write();
				//MiMusBiblioReader.appendUser(resources.getBiblioPath(), 
				//		references.getBibEntries().get(0), docID);
			}
		});
		removeRef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MiMusReference ref = (MiMusReference) ((IStructuredSelection) referenceTV.getSelection())
						.getFirstElement();
				if (ref==null) {
					System.out.println("Could not remove Reference because none was selected.");
					LabelPrinter.printError(referenceLabel, "You must select a reference to delete it.");
				} else {
					references.removeUnit(ref);
					
					/* Remove Editor using this biblio entry */
					int oldId = ref.getBibEntry().getId();
					MiMusBibEntry oldEntry = references.getBibEntries()
							.get(references.getBibEntryIdx(oldId));
					oldEntry.removeUser(new Integer(Integer.parseInt(docID)));
					MiMusXML.openBiblio().update(oldEntry).write();
//					references.getBibEntries()
//							.get(references.getBibEntryIdx(oldId)).getUsers()
//							.remove(new Integer(Integer.parseInt(docID)));
//					MiMusBiblioReader.removeUser(resources.getBiblioPath(), 
//							references.getBibEntries().get(references.getBibEntryIdx(oldId)), 
//							docID);
					LabelPrinter.printInfo(referenceLabel, "Reference deleted successfully.");
					System.out.println("Removing Reference " + ref.toString());
				}
			}
		});
		
		/* XML Button */
		Section sectXML = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectXML.setText("Create XML");
		Label xmlLabel = toolkit.createLabel(sectXML.getParent(), "");
		xmlLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn = new Button(sectXML.getParent(), SWT.PUSH);
		btn.setText("Press to save as XML");
//		btn.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				MiMusXMLWriter xmlWriter = new MiMusXMLWriter(regest, transcription,
//						regestEntities, transcriptionEntities, lemmas, references, docID);
//				if (xmlWriter.create() && xmlWriter.write(xmlPath)) {
//					LabelPrinter.printInfo(xmlLabel, "Saved to XML file successfully.");
//				} else {
//					LabelPrinter.printError(xmlLabel, "Could not save to XML.");
//				}
//			}
//		});
		toolkit.dispose();
		
		/* Load entities that were declared in the XML */
		if (hasXML) {
			try {
				File xmlFile = new File(xmlPath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();
				docID = doc.getElementsByTagName("doc_id").item(0).getTextContent();

				/* Load entities */
//				NodeList nl = doc.getElementsByTagName("entity");
//				for (int i=0; i<nl.getLength(); i++) {
//					Node nEnt = nl.item(i);
//					if (nEnt.getNodeType() == Node.ELEMENT_NODE) {
//						/* Read fields from XML entry <entity> */
//						Element eEnt = (Element) nEnt;
//						
//						/* Discern if <entity> under <regest_entities> or <transcription_entities> */
//						if (nEnt.getParentNode().getNodeName().equals("regest_entities")) {
//							Entity ent = regest.xmlElementToEntity(eEnt, true);
//							entityCurrentID++;
//							regestEntities.addUnit(ent);
//							Point charCoord = regest.fromWordToCharCoordinates(new Point(ent.getFrom(), ent.getTo()));
//							styler.add(charCoord.x, charCoord.y);
//						} else if (nEnt.getParentNode().getNodeName().equals("transcription_entities")) {
//							Entity ent = transcription.xmlElementToEntity(eEnt, false);
//							entityCurrentID++;
//							transcriptionEntities.addUnit(ent);
//							Point charCoord = transcription.fromWordToCharCoordinates(new Point(ent.getFrom(), ent.getTo()));
//							transcriptionStyler.add(charCoord.x, charCoord.y);
//						}					
//					}
//				}
//				styler.update();
//				transcriptionStyler.update();
//				entityHelper.packColumns();
//				
//				/* Load lemmatizations */
//				nl = doc.getElementsByTagName("lemmatization");
//				for (int i=0; i<nl.getLength(); i++) {
//					Node nLem = nl.item(i);
//					if (nLem.getNodeType() == Node.ELEMENT_NODE) {
//						Element eLem = (Element) nLem;
//						String strTranscriptionID = eLem.getElementsByTagName("transcription_id").item(0).getTextContent();
//						String strRegestID = eLem.getElementsByTagName("regest_id").item(0).getTextContent();
//						Entity foundTranscriptionEntity = null;
//						Entity foundRegestEntity = null;
//						
//						/* Find entities whose IDs coincide with the IDs stored in the <lemmatization> entry */
//						NodeList entNL = doc.getElementsByTagName("entity");
//						for (int j=0; (foundTranscriptionEntity==null || foundRegestEntity==null) 
//								&& j<entNL.getLength(); j++) {
//							Node nEnt = entNL.item(j);
//							if (nEnt.getNodeType() == Node.ELEMENT_NODE) {
//								Element eEnt = (Element) nEnt;
//								
//								/* Discern if <entity> entries retrieved are under <transcription_entities> or <regest_entities> */
//								if (nEnt.getParentNode().getNodeName().equals("transcription_entities")
//										&& eEnt.getElementsByTagName("entity_id").item(0).getTextContent().equals(strTranscriptionID)) {
//									foundTranscriptionEntity = transcription.xmlElementToEntity(eEnt, false);
//								} else if (nEnt.getParentNode().getNodeName().equals("regest_entities")
//										&& eEnt.getElementsByTagName("entity_id").item(0).getTextContent().equals(strRegestID)) {
//									foundRegestEntity = regest.xmlElementToEntity(eEnt, true);
//								}
//							}
//						}
//						
//						/* Find index of the entities retrieved in their corresponding EntitiesList */
//						lemmas.addUnit(new Lemma(regestEntities, transcriptionEntities, foundRegestEntity.getId(), foundTranscriptionEntity.getId()));
//					}
//				}
				
				/* Load references */
				NodeList nl = doc.getElementsByTagName("reference");
				for (int i=0; i<nl.getLength(); i++) {
					Node nRef = nl.item(i);
					if (nRef.getNodeType() == Node.ELEMENT_NODE) {
						Element eRef = (Element) nRef;
						
						/* Finds bibEntry looking by id in references */
						MiMusBibEntry foundBibEntry = null;
						int refId = Integer.parseInt(eRef.getElementsByTagName("ref_id").item(0).getTextContent());
						int bibId = Integer.parseInt(eRef.getElementsByTagName("biblio_id").item(0).getTextContent());
						for (int j=0; j<resources.getBibEntries().size(); j++) {
							if (resources.getBibEntries().get(j).getId()==bibId) {
								foundBibEntry = resources.getBibEntries().get(j);
								break;
							}
						}
						/* Checks actually found corresponding bibEntry by id */
						if (foundBibEntry != null) {
							MiMusReference foundRef = new MiMusReference(references,
									foundBibEntry, 
									eRef.getElementsByTagName("pages").item(0).getTextContent(),
									Integer.parseInt(
											eRef.getElementsByTagName("ref_type").item(0).getTextContent()),
									refId);
							references.addUnit(foundRef);
						}
					}
				}
			} catch (ParserConfigurationException pce) {
				System.out.println("Error with DOM parser.");
				pce.printStackTrace();
			} catch (IOException | SAXException ioe) {
				System.out.println("Error parsing document " + xmlPath);
				ioe.printStackTrace();
			}
		}
	}
	
	public ReferencesList getReferences() {
		return referenceHelper.getReferences();
	}
	
	/* Following methods shouldn't be touched */
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setFocus() {
		
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		
	}
	
	@Override
	public void doSaveAs() {
		
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void update() {
		/* Re-creates SharedResources in the same reference */
		resources.refresh();
		
		/* Refresh all table viewers */
		//entityHelper.refresh();
		//relationHelper.refresh();
		//lemmaHelper.refresh();
		referenceHelper.refresh();
	}

}
