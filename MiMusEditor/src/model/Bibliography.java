package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Bibliography is a Bibliography Entry in the MiMus corpus.
 * MiMus documents have a bibliography associated in annotation,
 * which can also be explored and manipulated through the UI.
 * 
 * It is important to note that MiMus Documents are not directly
 * associated to Bibliography. Instead, they use
 * MiMusReference, which is an instance of a Bibliography entry
 * with information of that specific reference (in general, the pages
 * inside the bibliography where the reference happens to such particular
 * document).
 * 
 * A Bibliography has up to 4 main authors (see field <authors>), and
 * up to 6 main secondary authors (see field <secondaryAuthors>). Each
 * occupies a slot accessed through its index (starting by 0). It is
 * not necessary that the slots are sorted such that all authors are in the
 * first positions and the empty positions are at the end, but highly
 * recommended for the String representations to work properly.
 * 
 * @author Javier Beltrán Jorba
 * 
 */
public class Bibliography extends Unit {
	
	/*
	 * The number of authors and secondary authors is fixed and reflected
	 * by <NUM_AUTHORS> and <NUM_SECONDARY>.
	 */
	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	
	/* Bibliography attributes */
	private String[] authors;
	private String[] secondaryAuthors;
	private String year;
	private String distinction;
	private String title;
	private String mainTitle;
	private String volume;
	private String place;
	private String editorial;
	private String series;
	private String pages;
	private String shortReference;
	private String observacions;
	
	public Bibliography() {}
	
	/**
	 * Initializes a Bibliography Entry with no values filled.
	 */
	public Bibliography(int id) {
		/* Authors are set to "" and their flags to false */
		authors = new String[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			authors[i] = "";
		}
		secondaryAuthors = new String[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			secondaryAuthors[i] = "";
		}
		
		year = "";
		distinction = "";
		title = "";
		mainTitle = "";
		volume = "";
		place = "";
		editorial = "";
		series = "";
		pages = "";
		shortReference = "";
		observacions = "";
		this.setId(id);
	}
	
	/**
	 * Creates a Bibliography Entry with all values specified. 
	 * 
	 * The user is expected not to leave any fields blank if this method 
	 * is used, as default values are not considered. If some fields may be 
	 * left blank, use the incremental approach.
	 */
	public Bibliography(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			String pages, String shortReference, String observacions, int id, List<Integer> users) {
		/* Infers activeAuthors values from "" in <authors> */
		this.authors = authors;
		this.secondaryAuthors = secondaryAuthors;
		this.year = year;
		this.distinction = distinction;
		this.title = title;
		this.mainTitle = mainTitle;
		this.volume = volume;
		this.place = place;
		this.editorial = editorial;
		this.series = series;
		this.pages = pages;
		this.shortReference = shortReference;
		this.observacions = observacions;
		this.setId(id);
	}
	
	public Bibliography(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			String pages, String shortReference, String observacions, int id) {
		this(authors, secondaryAuthors, year, distinction, title, mainTitle,
				volume, place, editorial, series, pages, shortReference, observacions, id,
				new ArrayList<>());
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
	 * Returns a complete textual reference of the bibliography entry, 
	 * with all fields that aren't empty.
	 */
	public String getFullReference() {
		String str = "";
		for (int i=0; i<NUM_AUTHORS; i++) {
			str += getAuthor(i).length()>0 ? getAuthor(i) + "; " : "";
		}
		if (str.length()>0) {
			if (str.length()>=2)
				str = str.substring(0, str.length()-2) + ", ";
			if (getYear().length()>0)
				str += getYear() + getDistinction() + ". ";
			if (getTitle().length()>0)
				str += "\"" + getTitle() + "\", ";
			if (getMainTitle().length()>0)
				str += getMainTitle() + ", ";
			if (getVolume().length()>0)
				str += getVolume() + ", ";
			for (int i=0; i<NUM_SECONDARY; i++) {
				str += getSecondaryAuthor(i).length()>0 ? getSecondaryAuthor(i) + ", " : "";
			}
			if (getPlace().length()>0)
				str += getPlace() + ", ";
			if (getPlace().length()>0 && getEditorial().length()>0)
				str = str.substring(0, str.length()-2) + ": ";
			if (getEditorial().length()>0)
				str += getEditorial() + ", ";
			if (getSeries().length()>0) {
				if (str.endsWith(", "))
					str = str.substring(0, str.length()-2);
				str += " (" + getSeries() + "), ";
			}
			if (getPages().length()>0)
				str += "p. " + getPages() + ", ";
			if (str.endsWith(", "))
				str = str.substring(0, str.length()-2) + ".";
			return str;
		}
		/* If no authors, return short reference */
		return getShortReference();
	}
	
	/* Special Getters and Setters for accessing authors */
	
	/**
	 * Returns the full name of the main author of index <i>,
	 * or "" if such author is not specified.
	 */
	public String getAuthor(int i) {
		return authors[i];
	}
	
	/**
	 * Main authors require retrieval of surname as short.
	 */
	public String getAuthorShort(int i) {
		String author = getAuthor(i);
		return author.replaceAll(",", "").split(" ")[0];
	}
	
	/**
	 * Sets the name of one of the main authors, of index <i>.
	 * 
	 * The field <author> may take value "" to indicate that
	 * the author previously stored in such position is removed,
	 * and activeAuthors internal variable is updated accordingly.
	 */
	public void setAuthor(int i, String author) {
		authors[i] = author;
	}
	
	/**
	 * Returns the full name of the secondary author of index <i>,
	 * or "" if such author is not specified.
	 */
	public String getSecondaryAuthor(int i) {
		return secondaryAuthors[i];
	}
	
	/**
	 *  Sets the name of one of the secondary authors, of index <i>.
	 *  
	 *  The field <secondaryAuthor> may take value "" to indicate that
	 *  the author previously stored in such position is removed,
	 *  and activeSecondaryAuthors internal variable is updated accordingly.
	 */
	public void setSecondaryAuthor(int i, String secondaryAuthor) {
		secondaryAuthors[i] = secondaryAuthor;
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
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}	
	
	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	/**
	 * Generates an abbreviated textual reference of the bibliography entry,
	 * consisting of:
	 * 
	 * <author0> - <author1> <year><distinction>
	 * 
	 * Where - <author1> only appears if it exists,
	 * and <distinction> only appears if specified.
	 * 
	 * Note that the specification says that no separation should appear
	 * between year and distinction.
	 */
	public static String generateShortReference(String author0,
			String author1, String year, String distinction) {
		String str = author0;
		if (author1.length()>0)
			str += " - " + author1;
		str += " " + year + distinction;
		return str;
	}
}
