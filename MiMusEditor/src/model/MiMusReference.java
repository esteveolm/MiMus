package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
public class MiMusReference extends Unit implements Persistable {
	
	private MiMusBibEntry bibEntry;
	private String page;
	private ReferencesList references;
	private int type;
	private int id;
	
	public MiMusReference(ReferencesList references, int type, int id) {
		this.setReferences(references);
		this.bibEntry = references.getBibEntries().get(0);
		this.page = "";
		this.setType(type);
		this.setId(id);
	}
	
	public MiMusReference(ReferencesList references, MiMusBibEntry bibEntry, 
			String page, int type, int id) {
		this.setReferences(references);
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
	public ReferencesList getReferences() {
		return references;
	}
	public void setReferences(ReferencesList references) {
		this.references = references;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	/* Implementation of MiMusWritable */
	
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
}
