package model;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A MiMusReference is an instance of a Bibliography in a
 * certain MiMusEntry document of the corpus. It specifies what
 * bibliography entry the reference is addressed at, as well as
 * the specific pages where the reference happens for such document.
 * 
 * The conceptual relations between these classes can be summarized as:
 * 
 * 1 <Document> has several <MiMusReference>
 * 1 <MiMusReference> has 1 <Bibliography>
 * Hence, 1 <Bibliography> is indirectly linked to several <MiMusEntry>
 * 
 * This allows for definition of information specific to the <MiMusEntry>
 * for each appearance of the same <Bibliography>.
 * 
 * References may have associated a Note or not, in which case attribute
 * <itsNote> is null.
 * 
 * Its String representation is constructed from that from its Bibliography,
 * just by attaching the pages field at the end of it.
 * 
 */
public class MiMusReference extends Unit {
	
	/* <type> attribute can use one of these values */
	public static final String [] TYPES = {"Edició", "Regest", "Citació"};
	
	private Bibliography itsBiblio;
	private Document itsDocument;
	private Note itsNote;
	private String page;
	private int type;
		
	public MiMusReference(Bibliography itsBiblio, Document itsDocument,
			Note itsNote, String page, int type, int id) {
		this.itsBiblio = itsBiblio;
		this.itsDocument = itsDocument;
		this.setItsNote(itsNote);
		this.page = page;
		this.setType(type);
		this.setId(id);
	}
	
	/**
	 * String representation constructed from string representation
	 * of its bibliography, plus the page annotated to this reference
	 * specifically.
	 */
	public String toString() {
		return itsBiblio.toString() + ", p. " + page + ".";
	}
	
	/* Getters and setters */
	
	public Bibliography getItsBiblio() {
		return itsBiblio;
	}
	public void setItsBiblio(Bibliography itsBiblio) {
		this.itsBiblio = itsBiblio;
	}
	public Document getItsDocument() {
		return itsDocument;
	}
	public void setItsDocument(Document itsDocument) {
		this.itsDocument = itsDocument;
	}
	public Note getItsNote() {
		return itsNote;
	}
	public void setItsNote(Note itsNote) {
		this.itsNote = itsNote;
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
}
