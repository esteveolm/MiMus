package model;

import org.eclipse.swt.graphics.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.xml.MiMusWritable;

/**
 * 
 * TODO: coords is not model logic. Could come from an interface instead?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends Unit implements MiMusWritable {
	
	private Entity itsEntity;
	private String form;
	private Point coords;
	private int id;
	
	public Transcription() {
		this(null, "", null, 0);
	}
	
	public Transcription(Entity itsEntity, String form) {
		this(itsEntity, form, null, 0);
	}
	
	public Transcription(Entity itsEntity, String form, Point coords) {
		this(itsEntity, form, null, 0);
	}
	
	public Transcription(Entity itsEntity, String form, Point coords, int id) {
		this.itsEntity = itsEntity;
		this.form = form;
		this.coords = coords;
		this.id = id;
	}
	
	/* Getters and setters */
	
	public Entity getEntity() {
		return itsEntity;
	}
	public void setEntity(Entity itsEntity) {
		this.itsEntity = itsEntity;
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
		Element tagForm = doc.createElement("form");
		tagForm.appendChild(doc.createTextNode(form));
		Element tagStartChar = doc.createElement("start_char");
		tagStartChar.appendChild(doc.createTextNode(
				String.valueOf(coords.x)));
		Element tagEndChar = doc.createElement("end_char");
		tagEndChar.appendChild(doc.createTextNode(
				String.valueOf(coords.y)));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(getWritableId()));
		tagTrans.appendChild(tagEntityId);
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
}
