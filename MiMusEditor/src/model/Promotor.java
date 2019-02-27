package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.SharedResources;
import util.xml.MiMusXML;

public class Promotor extends Entity {
	
	private String nomComplet;
	private String nom;
	private String cognom;
	private String sobrenom;
	private String distintiu;
	private int genere;
	private Casa casa;
	
	public Promotor() {}

	public Promotor(int id, String nomComplet, String nom, String cognom,
			String sobrenom, String distintiu, int genere, Casa casa) {
		super(id);
		this.nomComplet = nomComplet;
		this.nom = nom;
		this.cognom = cognom;
		this.sobrenom = sobrenom;
		this.setDistintiu(distintiu);
		this.genere = genere;
		this.casa = casa;
	}

	@Override
	public Promotor fromXMLElement(Element elem) {
		String nomComplet = elem.getElementsByTagName("nom_complet")
				.item(0).getTextContent();
		String nom = elem.getElementsByTagName("nom")
				.item(0).getTextContent();
		String cognom = elem.getElementsByTagName("cognom")
				.item(0).getTextContent();
		String sobrenom = elem.getElementsByTagName("sobrenom")
				.item(0).getTextContent();
		String distintiu = elem.getElementsByTagName("distintiu")
				.item(0).getTextContent();
		int genere = Integer.parseInt(
				elem.getElementsByTagName("genere")
				.item(0).getTextContent());
		int casaId = -1;
		try {
			casaId = Integer.parseInt(
					elem.getElementsByTagName("casa_id")
					.item(0).getTextContent());
		} catch (NumberFormatException e) {}
				
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		
		/* If Casa unspecified, its field in Promotor will be null */
		if (casaId == -1)
			return new Promotor(id, nomComplet, nom, cognom, sobrenom, 
					distintiu, genere, null);
		
		Casa casa = (Casa) 
				Unit.findUnit(SharedResources.getInstance().getCases(), casaId);
		return new Promotor(id, nomComplet, nom, cognom, sobrenom, 
				distintiu, genere, casa);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagNomComplet = doc.createElement("nom_complet");
		tagNomComplet.appendChild(doc.createTextNode(getNomComplet()));
		Element tagNom = doc.createElement("nom");
		tagNom.appendChild(doc.createTextNode(getNom()));
		Element tagCognom = doc.createElement("cognom");
		tagCognom.appendChild(doc.createTextNode(getCognom()));
		Element tagSobrenom = doc.createElement("sobrenom");
		tagSobrenom.appendChild(doc.createTextNode(getSobrenom()));
		Element tagDistintiu = doc.createElement("distintiu");
		tagDistintiu.appendChild(doc.createTextNode(getDistintiu()));
		Element tagGenere = doc.createElement("genere");
		tagGenere.appendChild(doc.createTextNode(String.valueOf(getGenere())));
		Element tagCasaId = doc.createElement("casa_id");
		if (getCasa() != null)	/* Otherwise, leave it blank */
			tagCasaId.appendChild(doc.createTextNode(
					String.valueOf(getCasa().getId())));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagNomComplet);
		tagEntry.appendChild(tagNom);
		tagEntry.appendChild(tagCognom);
		tagEntry.appendChild(tagSobrenom);
		tagEntry.appendChild(tagDistintiu);
		tagEntry.appendChild(tagGenere);
		tagEntry.appendChild(tagCasaId);
		tagEntry.appendChild(tagId);
		return tagEntry;
	}
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openPromotor().getDoc();
		NodeList nl = doc.getElementsByTagName("promotor");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Promotor().fromXMLElement(elem));
			}
		}
		return entries;
	}

	@Override
	public String getWritableName() {
		return "promotor";
	}

	@Override
	public String getWritableCategory() {
		return "promotors";
	}

	@Override
	public String getLemma() {
		String str = getNomComplet();
		if (getDistintiu().length()>0)
			str += " / " + getDistintiu();
		return str;
	}
	
	@Override
	public String toString() {
		return getLemma();
	}
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCognom() {
		return cognom;
	}
	public void setCognom(String cognom) {
		this.cognom = cognom;
	}
	public String getSobrenom() {
		return sobrenom;
	}
	public void setSobrenom(String sobrenom) {
		this.sobrenom = sobrenom;
	}
	public String getDistintiu() {
		return distintiu;
	}
	public void setDistintiu(String distintiu) {
		this.distintiu = distintiu;
	}
	public int getGenere() {
		return genere;
	}
	public void setGenere(int genere) {
		this.genere = genere;
	}
	public Casa getCasa() {
		return casa;
	}
	public void setCasa(Casa casa) {
		this.casa = casa;
	}
}
