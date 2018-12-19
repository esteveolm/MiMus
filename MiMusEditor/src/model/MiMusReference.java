package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.MiMusXML;
import util.xml.Persistable;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A MiMusReference is an instance of a MiMusBibEntry in a
 * certain MiMusEntry document of the corpora. It specifies what
 * bibliography entry the reference is addressed at, as well as
 * the specific pages where the reference happens for such document.
 * 
 * The conceptual relations between these classes can be summarized as:
 * 
 * 1 <MiMusEntry> has several <MiMusReference>
 * 1 <MiMusReference> has 1 <MiMusBibEntry>
 * Hence, 1 <MiMusBibEntry> is indirectly linked to several <MiMusEntry>
 * 
 * This allows for definition of information specific to the <MiMusEntry>
 * for each appearance of the same <MiMusBibEntry>.
 * 
 * Its String representation is constructed from that from its MiMusBibEntry,
 * just by attaching the pages field at the end of it.
 * 
 */
public class MiMusReference extends ConcreteUnit implements Persistable {
	
	private MiMusBibEntry bibEntry;
	private String page;
	private int type;
	private int id;
	
	public MiMusReference() {}
	
	public MiMusReference(List<Unit> allBiblio) {
		super(allBiblio);
	}
	
	public MiMusReference(MiMusBibEntry bibEntry, String page, int type, int id) {
		this.bibEntry = bibEntry;
		this.page = page;
		this.setType(type);
		this.setId(id);
	}
	
	public String toString() {
		return bibEntry.toString() + ", p. " + page + ".";
	}
	
	/* Getters and setters */
	
	public MiMusBibEntry getBibEntry() {
		return bibEntry;
	}
	public void setBibEntry(MiMusBibEntry bibEntry) {
		this.bibEntry = bibEntry;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public MiMusReference fromXMLElement(Element elem) {
		int bibId = Integer.parseInt(
				elem.getElementsByTagName("biblio_id")
				.item(0).getTextContent());
		MiMusBibEntry biblio = null;
		for (int i=0; i<getItsConcepts().size(); i++) {
			Unit u = getItsConcepts().get(i);
			if (u instanceof MiMusBibEntry) {
				MiMusBibEntry thisBiblio = (MiMusBibEntry) u;
				if (thisBiblio.getId() == bibId) {
					biblio = thisBiblio;
					break;
				}
			}
		}
		if (biblio != null) {
			String page = elem.getElementsByTagName("page")
					.item(0).getTextContent();
			int type = Integer.parseInt(
					elem.getElementsByTagName("type")
					.item(0).getTextContent());
			int id = Integer.parseInt(
					elem.getElementsByTagName("id")
					.item(0).getTextContent());
			return new MiMusReference(biblio, page, type, id);
		}
		return null;
	}
	
	@Override
	public Element toXMLElement(Document doc) {
		Element tagRef = doc.createElement(getWritableName());
		Element tagBiblioId = doc.createElement("biblio_id");
		tagBiblioId.appendChild(doc.createTextNode(
				String.valueOf(bibEntry.getId())));
		Element tagPage = doc.createElement("page");
		tagPage.appendChild(doc.createTextNode(page));
		Element tagType = doc.createElement("type");
		tagType.appendChild(doc.createTextNode(
				String.valueOf(type)));
		Element tagId = doc.createElement("id");
		tagId.appendChild(doc.createTextNode(
				String.valueOf(getWritableId())));
		tagRef.appendChild(tagBiblioId);
		tagRef.appendChild(tagPage);
		tagRef.appendChild(tagType);
		tagRef.appendChild(tagId);
		return tagRef;
	}

	@Override
	public String getWritableName() {
		return "reference";
	}

	@Override
	public String getWritableCategory() {
		return "references";
	}

	@Override
	public String getWritableId() {
		return String.valueOf(getId());
	}
	
	public static List<Unit> read(String docIdStr, List<Unit> bibEntries) {
		ArrayList<Unit> entries = new ArrayList<>();
		Document doc = MiMusXML.openDoc(docIdStr).getDoc();
		NodeList nl = doc.getElementsByTagName("reference");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new MiMusReference(bibEntries).fromXMLElement(elem));
			}
		}
		System.out.println(entries.size() + " references read");
		return entries;
	}
}
