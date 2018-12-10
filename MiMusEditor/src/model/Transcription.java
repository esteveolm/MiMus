package model;

import org.eclipse.swt.graphics.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.Persistable;

/**
 * 
 * TODO: coords is not model logic. Could come from an interface instead?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends Unit implements Persistable {
	
	private Entity itsEntity;
	private String selectedText;
	private String form;
	private Point coords;
	private int id;
	
	public Transcription() {
		this(null, "", "", null, 0);
	}
	
	public Transcription(Entity itsEntity, String selectedText, String form) {
		this(itsEntity, selectedText, form, null, 0);
	}
	
	public Transcription(Entity itsEntity, String selectedText, 
			String form, Point coords) {
		this(itsEntity, selectedText, form, null, 0);
	}
	
	public Transcription(Entity itsEntity, String selectedText, 
			String form, Point coords, int id) {
		this.itsEntity = itsEntity;
		this.setSelectedText(selectedText);
		this.form = form;
		this.coords = coords;
		this.id = id;
	}
	
	/* Getters and setters */
	
	public Entity getItsEntity() {
		System.out.println("getting " + itsEntity.getLemma());
		return itsEntity;
	}
	public void setItsEntity(Entity itsEntity) {
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
	
	public static boolean containsEntity(TranscriptionsList list, Entity ent) {
		for (Transcription t : list.getUnits()) {
			if (((Transcription) t).getItsEntity().equals(ent)) {
				return true;
			}
		}
		return false;
	}
}
