package editor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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

import model.EntitiesList;
import model.Entity;
import model.Relation;
import model.RelationsList;
import ui.TextStyler;

public class Editor extends EditorPart {

	public static final String[] ENTITY_TYPES = {"Person", "Payment", "Place"};
	public static final String[] PERSON_TYPES = {"Trobador", "Rey"};
	public static final String[] PLACE_TYPES = {"Town", "Country"};
	public static final String[] RELATION_TYPES = {"Same as", "Pays", "Goes"};
	
	protected String txtPath;
	protected String xmlPath;
	private boolean hasXML;
	private String fullText;
	private String[] words;
	private String docID;
	private StyledText text;
	
	public Editor() {
		super();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		docID = getEditorInput().getName().substring(0, getEditorInput().getName().indexOf('.'));
		System.out.println("Doc ID: " + docID);
		
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder txtFolder = project.getFolder("txt");
		IFolder xmlFolder = project.getFolder("xml");
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
		try {
			fullText = new String(Files.readAllBytes(Paths.get(txtPath)), StandardCharsets.UTF_8);
			words = fullText.split(" ");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Annotation");
		form.getBody().setLayout(new GridLayout());
		
		/* Raw text */
		text = new StyledText(form.getBody(), SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		text.setText(fullText);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	// Necessary for wrapping
		text.setEditable(false);
		TextStyler styler = new TextStyler(text);
		
		/* List of entities */
		Section sectEnt = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectEnt.setText("Entities");
		
		/* Table of entities */
		EntityTableViewer entityHelper = new EntityTableViewer(sectEnt.getParent(), styler, words);
		TableViewer entityTV = entityHelper.createTableViewer();
		EntitiesList entities = entityHelper.getEntities();
		
		/* Load entities that were declared in the XML */
		if (hasXML) {
			try {
				File xmlFile = new File(xmlPath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();
				
				docID = doc.getElementsByTagName("doc_id").item(0).getTextContent();
				NodeList nl = doc.getElementsByTagName("entity");
				for (int i=0; i<nl.getLength(); i++) {
					Node nEnt = nl.item(i);
					if (nEnt.getNodeType() == Node.ELEMENT_NODE) {
						Element eEnt = (Element) nEnt;
						int from = Integer.parseInt(eEnt.getElementsByTagName("from").item(0).getTextContent());
						int to = Integer.parseInt(eEnt.getElementsByTagName("to").item(0).getTextContent());
						String type = eEnt.getElementsByTagName("type").item(0).getTextContent();
						String subtype = eEnt.getElementsByTagName("subtype").item(0).getTextContent();
						Entity ent = new Entity(words, from, to, type, subtype);
						entities.addUnit(ent);
						
						System.out.println("Words from " + from + " to " + to);
						Point charCoord = fromWordToCharCoordinates(new Point(from, to));
						System.out.println("Painting from " + charCoord.x + " to " + charCoord.y);
						styler.add(charCoord.x, charCoord.y);
					}
				}
				styler.update();
				entityHelper.packColumns();
			} catch (ParserConfigurationException pce) {
				System.out.println("Error with DOM parser.");
				pce.printStackTrace();
			} catch (IOException | SAXException ioe) {
				System.out.println("Error parsing document " + xmlPath);
				ioe.printStackTrace();
			}
		}
		
		/* Buttons to add/remove entities */
		GridData gridData = new GridData();
		gridData.widthHint = 300;
		Button setEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		setEnt.setLayoutData(gridData);
		setEnt.setText("Add Entity from Text Selection");
		
		Button removeEnt = new Button(sectEnt.getParent(), SWT.PUSH | SWT.CENTER);
		removeEnt.setLayoutData(gridData);
		removeEnt.setText("Delete");
		
		/* List of relations */
		Section sectRel = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectRel.setText("Relations");
		
		/* Table of relations */
		RelationTableViewer relationHelper = new RelationTableViewer(sectRel.getParent(), styler, entities);
		TableViewer relationTV = relationHelper.createTableViewer();
		RelationsList relations = relationHelper.getRelations();
		
		/* Buttons to add/remove relations */
		GridData gridRel = new GridData();
		gridRel.widthHint = 300;
		Button addRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
		addRel.setLayoutData(gridRel);
		addRel.setText("Add");
		
		Button removeRel = new Button(sectRel.getParent(), SWT.PUSH | SWT.CENTER);
		removeRel.setLayoutData(gridRel);
		removeRel.setText("Delete");
		
		Label info = toolkit.createLabel(form.getBody(), "");
		info.setForeground(new Color(Display.getCurrent(), 255,0,0));
		info.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setEnt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Point charCoords = text.getSelection();
				if (charCoords.x!=charCoords.y) {
					charCoords = fromWordToCharCoordinates(
							fromCharToWordCoordinates(
							charCoords));	// Trick to ensure selection of whole words
					Point wordCoords = fromCharToWordCoordinates(charCoords);
					entities.addUnit(new Entity(words, wordCoords.x, wordCoords.y));
					entityHelper.packColumns();
					System.out.println("Adding Selected Entity - " + entities.countUnits());
					printAddedInfo(info);
					styler.addUpdate(charCoords.x, charCoords.y);
				} else {
					System.out.println("Could not add Selected Entity because no text was selected");
					printNotAddedInfo(info);
				}
			}
		});
		removeEnt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Entity ent = (Entity) ((IStructuredSelection) entityTV.getSelection())
						.getFirstElement();
				if (ent==null) {
					System.out.println("Could not remove Entity because none was selected.");
					printNotDeletedBecauseNullInfo(info);
				} else if (relations.using(ent)) {
					System.out.println("Could not remove Entity because it is used in an existing Relation.");
					printNotDeletedBecauseUsedInfo(info);
				} else {
					System.out.println(ent);
					Point charCoords = fromWordToCharCoordinates(new Point(ent.getFrom(), ent.getTo()));
					entities.removeUnit(ent);
					entityHelper.packColumns();
					System.out.println("Removing entity - " + entities.countUnits());
					printDeletedInfo(info);
					styler.deleteUpdate(charCoords.x, charCoords.y);
				}
			}
		});
		addRel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (entities.countUnits()<2) {
					printRelationNotAddedInfo(info);
					System.out.println("Cannot create relation with fewer than 2 entities.");
				} else {
					relations.addUnit(new Relation(entities, 0, 1));
					printRelationAddedInfo(info);
					System.out.println("Adding relation - " + relations.countUnits());
				}
				
			}
		});
		removeRel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Relation rel = (Relation) ((IStructuredSelection) relationTV.getSelection())
						.getFirstElement();
				if (rel==null) {
					System.out.println("Could not remove Relation because none was selected.");
					printRelationNotDeletedInfo(info);
				} else {
					System.out.println(rel);
					relations.removeUnit(rel);
					printRelationDeletedInfo(info);
					System.out.println("Removing relation - " + relations.countUnits());
				}
			}
		});

		/* XML Button */
		Section sectXML = toolkit.createSection(form.getBody(), PROP_TITLE);
		sectXML.setText("Create XML");
		Button btn = new Button(sectXML.getParent(), SWT.PUSH);
		btn.setText("Press to save as XML");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// TODO: move construction of Document out of here
					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = docBuilder.newDocument();
					
					Element tagDocument = doc.createElement("document");
					doc.appendChild(tagDocument);
					
					Element tagID = doc.createElement("doc_id");
					tagID.appendChild(doc.createTextNode(docID));
					tagDocument.appendChild(tagID);
					
					Element tagFullText = doc.createElement("full_text");
					tagFullText.appendChild(doc.createTextNode(fullText));
					tagDocument.appendChild(tagFullText);
					
					Element tagEntities = doc.createElement("entities");
					tagDocument.appendChild(tagEntities);
					for (Entity ent: entities.getUnits()) {
						Element tagFromEnt = doc.createElement("from");
						tagFromEnt.appendChild(doc.createTextNode(String.valueOf(ent.getFrom())));
						Element tagToEnt = doc.createElement("to");
						tagToEnt.appendChild(doc.createTextNode(String.valueOf(ent.getTo())));
						Element tagTextEnt = doc.createElement("text");
						tagTextEnt.appendChild(doc.createTextNode(ent.getText()));
						Element tagTypeEnt = doc.createElement("type");
						tagTypeEnt.appendChild(doc.createTextNode(ent.getTypeWord()));
						Element tagSubtypeEnt = doc.createElement("subtype");
						tagSubtypeEnt.appendChild(doc.createTextNode(ent.getSubtypeWord()));
						Element tagEntity = doc.createElement("entity");
						tagEntity.appendChild(tagFromEnt);
						tagEntity.appendChild(tagToEnt);
						tagEntity.appendChild(tagTextEnt);
						tagEntity.appendChild(tagTypeEnt);
						tagEntity.appendChild(tagSubtypeEnt);
						tagEntities.appendChild(tagEntity);
					}
					
					/* Converts Java XML Document to file-system XML */
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
			        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			        DOMSource source = new DOMSource(doc);
			        
			        File f = new File(xmlPath);
			        StreamResult console = new StreamResult(f);
			        transformer.transform(source, console);
			        
			        printSavedInfo(info);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		toolkit.dispose();
	}

	private Point fromCharToWordCoordinates(Point old) {
		List<Integer> spaces = getSpacesInText();
		
		/*
		 * Looks for between which spaces our selection indexes fall,
		 * which corresponds with a certain word whose index is returned.
		 */
		int from = 0;
		int to = 0;
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.x>=spaces.get(i) && old.x<spaces.get(i+1)) {
				from = i;
				break;
			}
		}
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.y-1>=spaces.get(i) && old.y-1<=spaces.get(i+1)) {
				to = i;
				break;
			}
		}
		return new Point(from, to);
	}
	
	private Point fromWordToCharCoordinates(Point old) {
		int charIdx=0;
		int wordIdx=0;
		while (wordIdx++<old.x) {	// Advance charIdx until start of first word
			charIdx += words[wordIdx-1].length() + 1;	// +1 for the space
		}
		int newX = new Integer(charIdx);	// Fix start of first word
		charIdx += words[wordIdx-1].length() + 1;	// Advance first word
		while (wordIdx++<old.y) {	// Advance charIdx until start of last word
			charIdx += words[wordIdx-1].length() + 1;
		}
		
		if (old.y-old.x>0) {
			int newY = charIdx + words[wordIdx-1].length();	// Advance charIdx until end of last word
			return new Point(newX, newY);
		} else {
			return new Point(newX, charIdx-1);	// In this case we already advanced before
		}	
	}
	
	private List<Integer> getSpacesInText() {
		/* 
		 * Spaces contains the index of every space, besides the start
		 * and ending index of the full text, in ascending order.
		 */
		List<Integer> spaces = new ArrayList<>();
		spaces.add(0);
		for (int idxSpace = fullText.indexOf(' '); 
				idxSpace>=0;
				idxSpace = fullText.indexOf(' ', idxSpace+1)) {
			spaces.add(idxSpace);
		}
		spaces.add(fullText.length());
		return spaces;
	}
	
	private void printAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Entity added successfully.");
	}
	
	private void printNotAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a part of text.");
	}
	
	private void printDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Entity deleted successfully.");
	}
	
	private void printNotDeletedBecauseNullInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select an entity to delete it.");
	}
	
	private void printNotDeletedBecauseUsedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must remove all relations where this entity is used before you can remove it.");
	}
	
	private void printSavedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Saved to XML file successfully.");
	}
	
	private void printRelationAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Relation created successfully.");
	}
	
	private void printRelationNotAddedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("Cannot create a relation if fewer than 2 entities are created.");
	}
	
	private void printRelationDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		label.setText("Relation deleted successfully.");
	}
	
	private void printRelationNotDeletedInfo(Label label) {
		label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		label.setText("You must select a relation to delete it.");
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

}
