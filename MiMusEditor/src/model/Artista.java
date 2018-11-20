package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Artista extends Entity {
	
	private String name;
	private boolean female;
	
	public Artista(int id) {
		super(id);
		this.name = "";
		this.female = false;
	}
	
	public Artista(int id, String name, boolean female) {
		super(id);
		this.name = name;
		this.female = female;
	}
	
	/* Getters and setters */
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFemale() {
		return female;
	}
	public void setFemale(boolean female) {
		this.female = female;
	}
	public String getGender() {
		return female ? "Female" : "Male";
	}

	@Override
	public String getLemma() {
		return getName();
	}

	/* MiMusWritable implementation */
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagName = doc.createElement("name");
		tagName.appendChild(doc.createTextNode(getName()));
		Element tagGender = doc.createElement("gender");
		tagGender.appendChild(doc.createTextNode(getGender()));
		tagEntry.appendChild(tagName);
		tagEntry.appendChild(tagGender);
		return tagEntry;
	}
	
	@Override
	public String getWritableName() {
		return "artista";
	}
	
	@Override
	public String getWritableCategory() {
		return "artistas";
	}
}
