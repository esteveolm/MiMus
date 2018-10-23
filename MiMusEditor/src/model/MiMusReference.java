package model;

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
public class MiMusReference {
	
	private MiMusBibEntry bibEntry;
	private String page;
	
	public MiMusReference(MiMusBibEntry bibEntry, String page) {
		this.bibEntry = bibEntry;
		this.page = page;
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
}
