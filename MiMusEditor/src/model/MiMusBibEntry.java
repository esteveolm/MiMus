package model;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A MiMusBibEntry is a Bibliography Entry in the MiMus corpora.
 * MiMus documents have a bibliography associated in annotation,
 * which can also be explored and manipulated through the UI.
 * 
 * It is important to note that MiMusEntry documents are not directly
 * associated to MiMusBibEntry bibliography. Instead, they use
 * MiMusReference, which is an instance of a Bibliography Entry
 * with information of that specific reference (in general, the pages
 * inside the bibliography where the reference happens to such particular
 * document).
 * 
 * A MiMusBibEntry has up to 4 main authors (see field <authors>), and
 * up to 6 main secondary authors (see field <secondaryAuthors>). Each
 * occupies a slot accessed through its index (starting by 0). It is
 * not necessary that the slots are ordered so all authors are in the
 * first positions and the empty positions are at the end. Instead, the
 * user must be prepared to receive a null value from any position when
 * they are empty.
 * 
 */
public class MiMusBibEntry {
	
	/*
	 * The number of authors and secondary authors is fixed and reflected
	 * by <NUM_AUTHORS> and <NUM_SECONDARY>.
	 */
	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	
	private String[] authors;
	private boolean[] activeAuthors;
	private String[] secondaryAuthors;
	private boolean[] activeSecondaryAuthors;
	private int year;
	private String distinction;
	private String title;
	private String mainTitle;
	private int volume;
	private String place;
	private String editorial;
	private String series;
	
	/**
	 * Initializes a Bibliography Entry with no values filled.
	 */
	public MiMusBibEntry() {
		/* Authors are set to null and their flags to false */
		authors = new String[NUM_AUTHORS];
		activeAuthors = new boolean[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			authors[i] = null;
			activeAuthors[i] = false;
		}
		secondaryAuthors = new String[NUM_SECONDARY];
		activeSecondaryAuthors = new boolean[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			secondaryAuthors[i] = null;
			activeSecondaryAuthors[i] = false;
		}
		
		year = -1;
		distinction = "";
		title = "";
		mainTitle = "";
		volume = -1;
		place = "";
		editorial = "";
		series = "";
	}
	
	/**
	 * Creates a Bibliography Entry with all values specified. 
	 * 
	 * The user is expected not to leave any fields blank if this method 
	 * is used, as default values are not considered. If some fields may be 
	 * left blank, use the incremental approach.
	 */
	public MiMusBibEntry(String[] authors, String[] secondaryAuthors,
			int year, String distinction, String title, String mainTitle,
			int volume, String place, String editorial, String series) {
		/* Infers activeAuthors values from nulls in <authors> */
		this.authors = authors;
		for (int i=0; i<NUM_AUTHORS; i++) {
			activeAuthors[i] = (authors[i] != null);
		}
		this.secondaryAuthors = secondaryAuthors;
		for (int i=0; i<NUM_SECONDARY; i++) {
			activeSecondaryAuthors[i] = (secondaryAuthors[i] != null);
		}
		
		this.year = year;
		this.distinction = distinction;
		this.title = title;
		this.mainTitle = mainTitle;
		this.volume = volume;
		this.place = place;
		this.editorial = editorial;
		this.series = series;
	}
	
	/* Special Getters and Setters for accessing authors */
	
	/**
	 * Returns the full name of the main author of index <i>,
	 * or null if such author is not specified.
	 */
	public String getAuthor(int i) {
		return authors[i];
	}
	
	/**
	 * Main authors require retrieval of surname as short.
	 */
	public String getAuthorShort(int i) {
		return authors[i].replaceAll(",", "").split(" ")[0];
	}
	
	/**
	 * Sets the name of one of the main authors, of index <i>.
	 * 
	 * The field <author> may take value null to indicate that
	 * the author previously stored in such position is removed,
	 * and activeAuthors internal variable is updated accordingly.
	 */
	public void setAuthor(int i, String author) {
		authors[i] = author;
		activeAuthors[i] = (author != null);
	}
	
	/**
	 * Returns the full name of the secondary author of index <i>,
	 * or null if such author is not specified.
	 */
	public String getSecondaryAuthor(int i) {
		return secondaryAuthors[i];
	}
	
	/**
	 *  Sets the name of one of the secondary authors, of index <i>.
	 *  
	 *  The field <secondaryAuthor> may take value null to indicate that
	 *  the author previously stored in such position is removed,
	 *  and activeSecondaryAuthors internal variable is updated accordingly.
	 */
	public void setSecondaryAuthor(int i, String secondaryAuthor) {
		secondaryAuthors[i] = secondaryAuthor;
		activeAuthors[i] = (secondaryAuthor != null);
	}
	
	/* Standard Getters and Setters */
	
	public String[] getAuthors() {
		return authors;
	}
	public String[] getSecondaryAuthors() {
		return secondaryAuthors;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDistinction() {
		return distinction;
	}
	public void setDistinction(String distinction) {
		this.distinction = distinction;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMainTitle() {
		return mainTitle;
	}
	public void setMainTitle(String mainTitle) {
		this.mainTitle = mainTitle;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getEditorial() {
		return editorial;
	}
	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
}
