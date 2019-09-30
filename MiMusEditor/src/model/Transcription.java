package model;

import org.eclipse.swt.graphics.Point;

/**
 * Transcription is an annotation on the Transcription text of
 * a MiMus Document which relates a portion of the text with
 * an entity previously annotated in the document, and optionally
 * adds a standard form to it.
 * 
 * Because transcriptions paint the Transcription text where the
 * annotations occured, they have coordinates associated with
 * the start and ending position in Point object <coords>.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends Unit {
	
	private EntityInstance itsEntity;
	private String selectedText;
	private String form;
	private Point coords;
	
	public Transcription(EntityInstance itsEntity, String selectedText, 
			String form, Point coords) {
		this(itsEntity, selectedText, form, coords, 0);
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
