package model;

import org.eclipse.swt.graphics.Point;

/**
 * 
 * TODO: coords is not model logic. Could come from an interface instead?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends Unit {
	
	private EntityInstance itsEntity;
	private String selectedText;
	private String form;
	private Point coords;
	
	public Transcription() {
		this(null, "", "", null, 0);
	}
	
	public Transcription(EntityInstance itsEntity, String selectedText, String form) {
		this(itsEntity, selectedText, form, null, 0);
	}
	
	public Transcription(EntityInstance itsEntity, String selectedText, 
			String form, Point coords) {
		this(itsEntity, selectedText, form, coords, 0);
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
		setId(id);
	}
	
	/* Getters and setters */
	
	public EntityInstance getItsEntity() {
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
}
