package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;

public class Artista extends Entity {
	
	private String name;
	private boolean female;
	
	public Artista() {}
	
	public Artista(int id) {
		this(id, "", false);
	}
	
	public Artista(int id, String name, boolean female) {
		super(id, "Artista");
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
	
	public String toString() {
		return getName();
	}

	/* MiMusWritable implementation */
	
	@Override
	public Artista fromXMLElement(Element elem) {
		String name = elem.getElementsByTagName("name")
				.item(0).getTextContent();
		boolean female = elem.getElementsByTagName("gender")
				.item(0).getTextContent()
				.equals("female");
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		return new Artista(id, name, female);
	}
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagName = doc.createElement("name");
		tagName.appendChild(doc.createTextNode(getName()));
		Element tagGender = doc.createElement("gender");
		tagGender.appendChild(doc.createTextNode(getGender()));
		Element tagID = doc.createElement("id");
		tagID.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagName);
		tagEntry.appendChild(tagGender);
		tagEntry.appendChild(tagID);
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
	
	public static ArrayList<Artista> read() {
		ArrayList<Artista> entries = new ArrayList<>();
		Document doc = MiMusXML.openArtista().getDoc();
		NodeList nl = doc.getElementsByTagName("artista");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Artista().fromXMLElement(elem));
			}
		}
		return entries;
	}
}
