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
	
	private Entity itsEntity;
	private String form;
	private Point coords;
	
	public Transcription() {
		this(null, "", null);
	}
	
	public Transcription(Entity itsEntity, String form) {
		this(itsEntity, form, null);
	}
	
	public Transcription(Entity itsEntity, String form, Point coords) {
		this.itsEntity = itsEntity;
		this.form = form;
		this.coords = coords;
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
}
