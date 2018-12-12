package model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;
import util.xml.Persistable;

/**
 * 
 * TODO: coords is not model logic. Could come from an interface instead?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends ConcreteUnit 
		implements Persistable {
	
	private EntityInstance itsEntity;
	private String selectedText;
	private String form;
	private Point coords;
	private int id;
	
	public Transcription(List<Unit> allEntities) {
		super(allEntities);
	}
	
	public Transcription() {
		this(null, "", "", null, 0);
	}
	
	public Transcription(EntityInstance itsEntity, String selectedText, String form) {
		this(itsEntity, selectedText, form, null, 0);
	}
	
	public Transcription(EntityInstance itsEntity, String selectedText, 
			String form, int id) {
		this(itsEntity, selectedText, form, null, id);
	}
	
	public Transcription(EntityInstance itsEntity, String selectedText, 
			String form, Point coords, int id) {
		this.itsEntity = itsEntity;
		this.setSelectedText(selectedText);
		this.form = form;
		this.coords = coords;
		this.id = id;
	}
	
	/* Getters and setters */
	
	public EntityInstance getItsEntity() {
		System.out.println("getting " + itsEntity.toString());
		return itsEntity;
	}
	public void setItsEntity(EntityInstance itsEntity) {
		this.itsEntity = itsEntity;
	}
	public String getSelectedText() {
		return selectedText;
	}
	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public Point getCoords() {
		return coords;
	}
	public void setCoords(Point coords) {
		this.coords = coords;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public Transcription fromXMLElement(Element elem) {
		int entId = Integer.parseInt(
				elem.getElementsByTagName("entity_id")
				.item(0).getTextContent());
		EntityInstance ent = null;
		for (int i=0; i<getItsConcepts().size(); i++) {
			Unit u = getItsConcepts().get(i);
			if (u instanceof EntityInstance) {
				EntityInstance thisEnt = ((EntityInstance) u);
				if (thisEnt.getId() == entId) {
					ent = thisEnt;
					break;
				}
			}
		}
		if (ent != null) {
			int id = Integer.parseInt(
					elem.getElementsByTagName("id")
					.item(0).getTextContent());
			String selectedText = elem.getElementsByTagName("selected_text")
					.item(0).getTextContent();
			String form = elem.getElementsByTagName("form")
					.item(0).getTextContent();
			String startCh = elem.getElementsByTagName("start_char")
					.item(0).getTextContent();
			String endCh = elem.getElementsByTagName("end_char")
					.item(0).getTextContent();
			if (startCh.length()>0 && endCh.length()>0) {
				Point coords = new Point(
						Integer.parseInt(startCh), Integer.parseInt(endCh));
				return new Transcription(ent, selectedText, form, coords, id);
			}
			else {
				return new Transcription(ent, selectedText, form, id);
			}
			
			
		}
		return null;
	}
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagTrans = doc.createElement(getWritableName());
		Element tagEntityId = doc.createElement("entity_id");
		tagEntityId.appendChild(doc.createTextNode(
				String.valueOf(itsEntity.getId())));
		Element tagSelectedText = doc.createElement("selected_text");
		tagSelectedText.appendChild(doc.createTextNode(selectedText));
		Element tagForm = doc.createElement("form");
		tagForm.appendChild(doc.createTextNode(form));
		Element tagStartChar = doc.createElement("start_char");
		Element tagEndChar = doc.createElement("end_char");
		if (coords != null) {
			tagStartChar.appendChild(doc.createTextNode(
					String.valueOf(coords.x)));
			tagEndChar.appendChild(doc.createTextNode(
					String.valueOf(coords.y)));
		}
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(getWritableId()));
		tagTrans.appendChild(tagEntityId);
		tagTrans.appendChild(tagSelectedText);
		tagTrans.appendChild(tagForm);
		tagTrans.appendChild(tagStartChar);
		tagTrans.appendChild(tagEndChar);
		tagTrans.appendChild(tagId);
		return tagTrans;
	}

	@Override
	public String getWritableName() {
		return "transcription";
	}

	@Override
	public String getWritableCategory() {
		return "transcriptions";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(getId());
	}
	
	public static List<Unit> read(MiMusEntry entry, List<Unit> entityInstances) {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openDoc(entry).getDoc();
		NodeList nl = doc.getElementsByTagName("transcription");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Transcription(entityInstances)
						.fromXMLElement(elem));
			}
		}
		return entries;
	}
	
	public static boolean containsEntity(List<Unit> list, EntityInstance ent) {
		for (Unit t : list) {
			if (t instanceof Transcription &&
					((Transcription) t).getItsEntity().equals(ent)) {
				return true;
			}
		}
		return false;
	}
}
