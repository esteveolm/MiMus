package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;

public class Artista extends Entity {
	
	private String nomComplet;
	private String tractament;
	private String nom;
	private String cognom;
	private String sobrenom;
	private String distintiu;
	private int genere;
	private int religio;
	private String origen;
	
	public Artista() {}

	public Artista(int id) {
		this(id, "", "", "", "", "", "", 0, 0, "");
	}
	
	public Artista(int id, String nomComplet, String tractament,
			String nom, String cognom, String sobrenom, String distintiu,
			int genere, int religio, String origen) {
		this.setId(id);
		this.nomComplet = nomComplet;
		this.tractament = tractament;
		this.nom = nom;
		this.cognom = cognom;
		this.sobrenom = sobrenom;
		this.setDistintiu(distintiu);
		this.genere = genere;
		this.religio = religio;
		this.origen = origen;
	}
	
	/* Getters and setters */
	public String getTractament() {
		return tractament;
	}
	public void setTractament(String tractament) {
		this.tractament = tractament;
	}
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nom) {
		this.nomComplet = nom;
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
	public String getGenereStr() {
		if (genere==0)
			return "No marcat";
		if (genere==1)
			return "Home";
		if (genere==2)
			return "Dona";
		return "Desconegut";
	}
	public int getReligio() {
		return religio;
	}
	public void setReligio(int religio) {
		this.religio = religio;
	}
	public String getReligioStr() {
		if (religio==0)
			return "No marcat";
		if (religio==1)
			return "Jueu";
		if (religio==2)
			return "Musulmà";
		return "Desconegut";
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}

	@Override
	public String getLemma() {
		String str = getNomComplet();
		if (getDistintiu().length()>0)
			str += " / " + getDistintiu();
		return str;
	}
	
	public String toString() {
		return getNomComplet();
	}

	/* MiMusWritable implementation */
	
	@Override
	public Artista fromXMLElement(Element elem) {
		String nombreCompleto = elem.getElementsByTagName("nombre_completo")
				.item(0).getTextContent();
		String tratamiento = elem.getElementsByTagName("tratamiento")
				.item(0).getTextContent();
		String nombre = elem.getElementsByTagName("nombre")
				.item(0).getTextContent();
		String apellido = elem.getElementsByTagName("apellido")
				.item(0).getTextContent();
		String sobrenombre = elem.getElementsByTagName("sobrenombre")
				.item(0).getTextContent();
		String distintiu = elem.getElementsByTagName("distintiu")
				.item(0).getTextContent();
		int genero = Integer.parseInt(
				elem.getElementsByTagName("genero")
				.item(0).getTextContent());
		int religion = Integer.parseInt(
				elem.getElementsByTagName("religion")
				.item(0).getTextContent());
		String origen = elem.getElementsByTagName("origen")
				.item(0).getTextContent();
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		
		return new Artista(id, nombreCompleto, tratamiento, nombre, apellido,
				sobrenombre, distintiu, genero, religion, origen);
	}
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagNombreCompleto = doc.createElement("nombre_completo");
		tagNombreCompleto.appendChild(doc.createTextNode(getNomComplet()));
		Element tagTratamiento = doc.createElement("tratamiento");
		tagTratamiento.appendChild(doc.createTextNode(getTractament()));
		Element tagNombre = doc.createElement("nombre");
		tagNombre.appendChild(doc.createTextNode(getNom()));
		Element tagApellido = doc.createElement("apellido");
		tagApellido.appendChild(doc.createTextNode(getCognom()));
		Element tagSobrenombre = doc.createElement("sobrenombre");
		tagSobrenombre.appendChild(doc.createTextNode(getSobrenom()));
		Element tagDistintiu = doc.createElement("distintiu");
		tagDistintiu.appendChild(doc.createTextNode(getDistintiu()));
		Element tagGenero = doc.createElement("genero");
		tagGenero.appendChild(doc.createTextNode(String.valueOf(getGenere())));
		Element tagReligion = doc.createElement("religion");
		tagReligion.appendChild(doc.createTextNode(String.valueOf(getReligio())));
		Element tagOrigen = doc.createElement("origen");
		tagOrigen.appendChild(doc.createTextNode(getOrigen()));
		Element tagID = doc.createElement("id");
		tagID.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagNombreCompleto);
		tagEntry.appendChild(tagTratamiento);
		tagEntry.appendChild(tagNombre);
		tagEntry.appendChild(tagApellido);
		tagEntry.appendChild(tagSobrenombre);
		tagEntry.appendChild(tagDistintiu);
		tagEntry.appendChild(tagGenero);
		tagEntry.appendChild(tagReligion);
		tagEntry.appendChild(tagOrigen);
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
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
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
