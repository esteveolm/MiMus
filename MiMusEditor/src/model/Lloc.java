package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.SharedResources;
import util.xml.MiMusXML;

public class Lloc extends Entity {
	
	private String nomComplet;
	private int regne;
	private int area;
	
	public Lloc() {}
	
	public Lloc(int id) {
		this(id, "", 0, 0);
	}
	
	public Lloc(int id, String nomComplet, int regne, int area) {
		this.setId(id);
		this.nomComplet = nomComplet;
		this.regne = regne;
		this.area = area;
	}
	
	@Override
	public Lloc fromXMLElement(Element elem) {
		String nomComplet = elem.getElementsByTagName("nom_complet")
				.item(0).getTextContent();
		int regne = Integer.parseInt(
				elem.getElementsByTagName("regne")
				.item(0).getTextContent());
		int area = Integer.parseInt(
				elem.getElementsByTagName("area")
				.item(0).getTextContent());
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		return new Lloc(id, nomComplet, regne, area);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagNomComplet = doc.createElement("nom_complet");
		tagNomComplet.appendChild(doc.createTextNode(getNomComplet()));
		Element tagRegne = doc.createElement("regne");
		tagRegne.appendChild(doc.createTextNode(String.valueOf(getRegne())));
		Element tagArea = doc.createElement("area");
		tagArea.appendChild(doc.createTextNode(String.valueOf(getArea())));
		Element tagID = doc.createElement("id");
		tagID.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagNomComplet);
		tagEntry.appendChild(tagRegne);
		tagEntry.appendChild(tagArea);
		tagEntry.appendChild(tagID);
		return tagEntry;
	}

	@Override
	public String getWritableName() {
		return "lloc";
	}

	@Override
	public String getWritableCategory() {
		return "llocs";
	}

	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}

	/* Getters and setters */
	
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public int getRegne() {
		return regne;
	}
	public String getRegneStr() {
		return SharedResources.REGNE[regne];
	}
	public void setRegne(int regne) {
		this.regne = regne;
	}
	public int getArea() {
		return area;
	}
	public String getAreaStr() {
		return SharedResources.AREA[area];
	}
	public void setArea(int area) {
		this.area = area;
	}
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openLloc().getDoc();
		NodeList nl = doc.getElementsByTagName("lloc");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Lloc().fromXMLElement(elem));
			}
		}
		return entries;
	}
}
