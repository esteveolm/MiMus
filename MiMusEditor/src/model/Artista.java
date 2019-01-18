package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.SharedResources;
import util.xml.MiMusXML;

public class Artista extends Entity {
	
	private String nombreCompleto;
	private String tratamiento;
	private String nombre;
	private String apellido;
	private String sobrenombre;
	private int genero;
	private int religion;
	private String origen;
	private Ofici ofici;
	
	public Artista() {}

	public Artista(int id) {
		this(id, "", "", "", "", "", 0, 0, "", null);
	}
	
	public Artista(int id, String nombreCompleto, String tratamiento,
			String nombre, String apellido, String sobrenombre,
			int genero, int religion, String origen, Ofici ofici) {
		this.setId(id);
		this.nombreCompleto = nombreCompleto;
		this.tratamiento = tratamiento;
		this.nombre = nombre;
		this.apellido = apellido;
		this.sobrenombre = sobrenombre;
		this.genero = genero;
		this.religion = religion;
		this.origen = origen;
		this.ofici = ofici;
	}
	
	/* Getters and setters */
	public String getTratamiento() {
		return tratamiento;
	}
	public void setTratamiento(String tratamiento) {
		this.tratamiento = tratamiento;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String name) {
		this.nombreCompleto = name;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getSobrenombre() {
		return sobrenombre;
	}
	public void setSobrenombre(String sobrenombre) {
		this.sobrenombre = sobrenombre;
	}
	public int getGenero() {
		return genero;
	}
	public void setGenero(int genero) {
		this.genero = genero;
	}
	public String getGeneroStr() {
		if (genero==0)
			return "No marcat";
		if (genero==1)
			return "Home";
		if (genero==2)
			return "Dona";
		return "Desconegut";
	}
	public int getReligion() {
		return religion;
	}
	public void setReligion(int religion) {
		this.religion = religion;
	}
	public String getReligionStr() {
		if (religion==0)
			return "No marcat";
		if (religion==1)
			return "Jueu";
		if (religion==2)
			return "Musulmà";
		return "Desconegut";
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public Ofici getOfici() {
		return ofici;
	}
	public void setOfici(Ofici ofici) {
		this.ofici = ofici;
	}

	@Override
	public String getLemma() {
		return getNombreCompleto();
	}
	
	public String toString() {
		return getNombreCompleto();
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
		int genero = Integer.parseInt(
				elem.getElementsByTagName("genero")
				.item(0).getTextContent());
		int religion = Integer.parseInt(
				elem.getElementsByTagName("religion")
				.item(0).getTextContent());
		String origen = elem.getElementsByTagName("origen")
				.item(0).getTextContent();
		int oficiId = -1;
		try {	/* Unspecified Ofici takes value -1 in this method */
			oficiId = Integer.parseInt(
					elem.getElementsByTagName("ofici_id")
					.item(0).getTextContent());
		} catch (NumberFormatException e) {}
		
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		
		/* If Ofici unspecified, its field in Artista will be null */
		if (oficiId == -1)
			return new Artista(id, nombreCompleto, tratamiento, nombre, apellido,
					sobrenombre, genero, religion, origen, null); 
		
		Ofici ofici = (Ofici) Unit.findUnit(
				SharedResources.getInstance().getOficis(), oficiId);
		return new Artista(id, nombreCompleto, tratamiento, nombre, apellido,
				sobrenombre, genero, religion, origen, ofici);
	}
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagNombreCompleto = doc.createElement("nombre_completo");
		tagNombreCompleto.appendChild(doc.createTextNode(getNombreCompleto()));
		Element tagTratamiento = doc.createElement("tratamiento");
		tagTratamiento.appendChild(doc.createTextNode(getTratamiento()));
		Element tagNombre = doc.createElement("nombre");
		tagNombre.appendChild(doc.createTextNode(getNombre()));
		Element tagApellido = doc.createElement("apellido");
		tagApellido.appendChild(doc.createTextNode(getApellido()));
		Element tagSobrenombre = doc.createElement("sobrenombre");
		tagSobrenombre.appendChild(doc.createTextNode(getSobrenombre()));
		Element tagGenero = doc.createElement("genero");
		tagGenero.appendChild(doc.createTextNode(String.valueOf(getGenero())));
		Element tagReligion = doc.createElement("religion");
		tagReligion.appendChild(doc.createTextNode(String.valueOf(getReligion())));
		Element tagOrigen = doc.createElement("origen");
		tagOrigen.appendChild(doc.createTextNode(getOrigen()));
		Element tagOficiId = doc.createElement("ofici_id");
		if (getOfici() != null)		/* Otherwise, leave it blank */
			tagOficiId.appendChild(doc.createTextNode(
					String.valueOf(getOfici().getId())));
		Element tagID = doc.createElement("id");
		tagID.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagNombreCompleto);
		tagEntry.appendChild(tagTratamiento);
		tagEntry.appendChild(tagNombre);
		tagEntry.appendChild(tagApellido);
		tagEntry.appendChild(tagSobrenombre);
		tagEntry.appendChild(tagGenero);
		tagEntry.appendChild(tagReligion);
		tagEntry.appendChild(tagOrigen);
		tagEntry.appendChild(tagOficiId);
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
