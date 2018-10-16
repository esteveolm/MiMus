package model;

public class MiMusReference {
	
	private MiMusBibEntry bibEntry;
	private String page;
	
	public MiMusReference(MiMusBibEntry bibEntry, String page) {
		this.bibEntry = bibEntry;
		this.page = page;
	}
	
	public String toString() {
		return bibEntry.toString() + " " + page;
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
