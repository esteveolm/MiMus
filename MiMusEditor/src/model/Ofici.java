package model;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import control.SharedResources;
import util.xml.MiMusXML;

public class Ofici extends Entity {
	
	private String terme;
	private String especialitat;
	private Instrument instrument;
	
	public Ofici() {}
	
	public Ofici(int id, String terme, String especialitat, Instrument instrument) {
		super(id);
		this.terme = terme;
		this.especialitat = especialitat;
		this.instrument = instrument;
	}

	@Override
	public Ofici fromXMLElement(Element elem) {
		String terme = elem.getElementsByTagName("terme")
				.item(0).getTextContent();
		String especialitat = elem.getElementsByTagName("especialitat")
				.item(0).getTextContent();
		int instrumentId = -1;
		try {	/* Unspecified Instrument takes auxiliary value -1 in this method */
			instrumentId = Integer.parseInt(
					elem.getElementsByTagName("instrument_id")
					.item(0).getTextContent());
		} catch (NumberFormatException e) {}
		
		int id = Integer.parseInt(
				elem.getElementsByTagName("id")
				.item(0).getTextContent());
		
		/* If instrument unspecified, its field in Ofici will be null */
		if (instrumentId == -1)
			return new Ofici(id, terme, especialitat, null);

		Instrument inst = (Instrument) Unit.findUnit(
				SharedResources.getInstance().getInstruments(), instrumentId);
		return new Ofici(id, terme, especialitat, inst);
	}

	@Override
	public Element toXMLElement(Document doc) {
		Element tagEntry = doc.createElement(getWritableName());
		Element tagTerme = doc.createElement("terme");
		tagTerme.appendChild(doc.createTextNode(getTerme()));
		Element tagEspecialitat = doc.createElement("especialitat");
		tagEspecialitat.appendChild(doc.createTextNode(getEspecialitat()));
		Element tagInstrumentId = doc.createElement("instrument_id");
		if (getInstrument() != null)	/* Otherwise, leave it blank */
			tagInstrumentId.appendChild(doc.createTextNode(
					String.valueOf(getInstrument().getId())));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(String.valueOf(getId())));
		tagEntry.appendChild(tagTerme);
		tagEntry.appendChild(tagEspecialitat);
		tagEntry.appendChild(tagInstrumentId);
		tagEntry.appendChild(tagId);
		return tagEntry;
	}
	
	public static ArrayList<Unit> read() {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openOfici().getDoc();
		NodeList nl = doc.getElementsByTagName("ofici");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new Ofici().fromXMLElement(elem));
			}
		}
		return entries;
	}

	@Override
	public String getWritableName() {
		return "ofici";
	}

	@Override
	public String getWritableCategory() {
		return "oficis";
	}

	@Override
	public String getLemma() {
		String lemma = getTerme() + " " + getEspecialitat();
		if (instrument != null)
			lemma += " " + getInstrument().getLemma();
		return lemma;
	}
	
	@Override
	public String toString() {
		return getLemma();
	}
	
	public String getTerme() {
		return terme;
	}
	public void setTerme(String terme) {
		this.terme = terme;
	}
	public String getEspecialitat() {
		return especialitat;
	}
	public void setEspecialitat(String especialitat) {
		this.especialitat = especialitat;
	}
	public Instrument getInstrument() {
		return instrument;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
}
