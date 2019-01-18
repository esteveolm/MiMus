package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;

public class Instrument extends Entity {
	
	private String nom;
	private int family;
	private int classe;
	private String part;
	
	public Instrument() {}
	
	public Instrument(int id) {
		this(id, "", 0, 0, "");
	}
	
	public Instrument(int id, String nom, int family, int classe, String part) {
		super(id, "Instrument");
		this.nom = nom;
		this.family = family;
		this.classe = classe;
		this.part = part;
	}
	
	@Override
	public String getLemma() {
		return getNom();
	}
	
	@Override
	public String toString() {
		return getNom();
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
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
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}

	public Instrument fromXMLElement(Element elem) {
		String nom = elem.getElementsByTagName("nom")
				.item(0).getTextContent();
		int family = Integer.parseInt(
				elem.getElementsByTagName("familia")
				.item(0).getTextContent());
		int classe = Integer.parseInt(
				elem.getElementsByTagName("classe")
				.item(0).getTextContent());
		String part = elem.getElementsByTagName("part")
				.item(0).getTextContent();
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		return new Instrument(id, nom, family, classe, part);
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public Element toXMLElement(Document doc) {
		Element entry = doc.createElement(getWritableName());
		Element tagNom = doc.createElement("nom");
		tagNom.appendChild(doc.createTextNode(getNom()));
		Element tagFamily = doc.createElement("familia");
		tagFamily.appendChild(doc.createTextNode(String.valueOf(getFamily())));
		Element tagClasse = doc.createElement("classe");
		tagClasse.appendChild(doc.createTextNode(String.valueOf(getClasse())));
		Element tagPart = doc.createElement("part");
		tagPart.appendChild(doc.createTextNode(getPart()));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(String.valueOf(getId())));
		entry.appendChild(tagNom);
		entry.appendChild(tagFamily);
		entry.appendChild(tagClasse);
		entry.appendChild(tagPart);
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
