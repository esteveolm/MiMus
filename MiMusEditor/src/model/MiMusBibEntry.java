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
 * not necessary that the slots are sorted such that all authors are in the
 * first positions and the empty positions are at the end, but highly
 * recommended for the String representations to work properly.
 * 
 * TODO: guarantee sequentiality of authors.
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
	private String year;
	private String distinction;
	private String title;
	private String mainTitle;
	private String volume;
	private String place;
	private String editorial;
	private String series;
	private String shortReference;
	private int id;
	
	/**
	 * Initializes a Bibliography Entry with no values filled.
	 */
	public MiMusBibEntry(int id) {
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
		
		year = "";
		distinction = "";
		title = "";
		mainTitle = "";
		volume = "";
		place = "";
		editorial = "";
		series = "";
		shortReference = "";
		this.setId(id);
	}
	
	/**
	 * Creates a Bibliography Entry with all values specified. 
	 * 
	 * The user is expected not to leave any fields blank if this method 
	 * is used, as default values are not considered. If some fields may be 
	 * left blank, use the incremental approach.
	 */
	public MiMusBibEntry(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			String shortReference, int id) {
		/* Infers activeAuthors values from nulls in <authors> */
		activeAuthors = new boolean[NUM_AUTHORS];
		this.authors = authors;
		for (int i=0; i<NUM_AUTHORS; i++) {
			activeAuthors[i] = (authors[i] != null);
		}
		activeSecondaryAuthors = new boolean[NUM_SECONDARY];
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
		this.shortReference = shortReference;
		this.setId(id);
	}
	
	public MiMusBibEntry(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			int id) {
		this(authors, secondaryAuthors, year, distinction,
				title, mainTitle, volume, place, editorial, series, "",
				id);
		setShortReference(generateShortReference());
	}
	
	/**
	 * Returns a String representation of the entry, which coincides
	 * with its short reference.
	 */
	public String toString() {
		return getShortReference();
	}
	
	public String getShortReference() {
		return shortReference;
	}
	public void setShortReference(String shortReference) {
		this.shortReference = shortReference;
	}
	
	/**
	 * Returns an abbreviated textual reference of the bibliography entry,
	 * consisting of:
	 * 
	 * <authorShort0> - <authorShort1> <year><distinction>
	 * 
	 * Where <authorShort1> only appears if it exists,
	 * and <distinction> only appears if specified.
	 * 
	 * Note that the specification says that no separation should appear
	 * between year and distinction.
	 */
	public String generateShortReference() {
		String str = getAuthorShort(0) + " - ";
		str += (getAuthor(1) == null) ? "" : getAuthorShort(1);
		str += " " + getYear() + getDistinction();
		return str;
	}
	
	/**
	 * Returns a complete textual reference of the bibliography entry, 
	 * with all fields that aren't empty.
	 */
	public String getFullReference() {
		String str = "";
		for (int i=0; i<NUM_AUTHORS; i++) {
			str += activeAuthors[i] ? getAuthor(i) + "; " : "";
		}
		str += year + ". \"" + getTitle() + "\", " + getMainTitle() + ", " +
				getVolume() + ", ";
		for (int i=0; i<NUM_SECONDARY; i++) {
			str += activeSecondaryAuthors[i] ? getSecondaryAuthor(i) + ", " : "";
		}
		str += getPlace() + ": " + getEditorial() + " (" + getSeries() + ")";
		return str;
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
		String author = getAuthor(i);
		if (author == null)
			return "";
		return author.replaceAll(",", "").split(" ")[0];
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
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
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
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Creates an entity of this class that works as placeholder
	 * when there are no bibliography entries created yet. It
	 * contains a main author called "unknown reference" so one
	 * can see this value and understand what this entry means
	 * in the bibliography.
	 */
	public static MiMusBibEntry createUnknownEntry() {
		String[] unknownAut = new String[NUM_AUTHORS];
		String[] second = new String[NUM_SECONDARY];
		for (int i=0; i<NUM_AUTHORS; i++) {
			if (i==0) {
				unknownAut[i] = "unknown reference";
				second[i] = null;
			} else {
				unknownAut[i] = null;
				second[i] = null;
			}
		}
		return new MiMusBibEntry(unknownAut, second, "", "", "", "",
				"", "", "", "", 0);
	}
	
	public static String generateShortReference(String author0,
			String author1, String year, String distinction) {
		String str = author0 + " - " + author1;
		str += " " + year + distinction;
		return str;
	}
}
