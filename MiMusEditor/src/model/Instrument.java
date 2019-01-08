package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;

public class Instrument extends Entity {
	
	private String name;
	private int family;
	private int classe;
	
	public Instrument() {}
	
	public Instrument(int id) {
		this(id, "", 0, 0);
	}
	
	public Instrument(int id, String name, int family, int classe) {
		super(id, "Instrument");
		this.name = name;
		this.family = family;
		this.classe = classe;
	}
	
	@Override
	public String getLemma() {
		return getName();
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFamily() {
		return family;
	}
	public void setFamily(int family) {
		this.family = family;
	}
	public int getClasse() {
		return classe;
	}
	public void setClasse(int classe) {
		this.classe = classe;
	}
	
	public Instrument fromXMLElement(Element elem) {
		String name = elem.getElementsByTagName("nom")
				.item(0).getTextContent();
		int family = Integer.parseInt(
				elem.getElementsByTagName("familia")
				.item(0).getTextContent());
		int classe = Integer.parseInt(
				elem.getElementsByTagName("classe")
				.item(0).getTextContent());
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		return new Instrument(id, name, family, classe);
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public Element toXMLElement(Document doc) {
		Element entry = doc.createElement(getWritableName());
		Element tagName = doc.createElement("nom");
		tagName.appendChild(doc.createTextNode(getName()));
		Element tagFamily = doc.createElement("familia");
		tagFamily.appendChild(doc.createTextNode(String.valueOf(getFamily())));
		Element tagClasse = doc.createElement("classe");
		tagClasse.appendChild(doc.createTextNode(String.valueOf(getClasse())));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(String.valueOf(getId())));
		entry.appendChild(tagName);
		entry.appendChild(tagFamily);
		entry.appendChild(tagClasse);
		entry.appendChild(tagId);
		return entry;
	}

	@Override
	public String getWritableName() {
		return "instrument";
	}

	@Override
	public String getWritableCategory() {
		return "instruments";
	}
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openInstrument().getDoc();
		NodeList nl = doc.getElementsByTagName("instrument");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Instrument().fromXMLElement(elem));
			}
		}
		return entries;
	}
}
