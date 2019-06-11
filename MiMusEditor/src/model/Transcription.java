package model;

import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * 
 * TODO: coords is not model logic. Could come from an interface instead?
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Transcription extends ConcreteUnit {
	
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
