package model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.xml.Persistable;
import util.xml.MiMusXML;

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
public class MiMusBibEntry extends Unit implements Persistable {
	
	/*
	 * The number of authors and secondary authors is fixed and reflected
	 * by <NUM_AUTHORS> and <NUM_SECONDARY>.
	 */
	private static final int NUM_AUTHORS = 4;
	private static final int NUM_SECONDARY = 6;
	
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
	private int id;
	private List<Integer> users;
	
	public MiMusBibEntry() {}
	
	/**
	 * Initializes a Bibliography Entry with no values filled.
	 */
	public MiMusBibEntry(int id) {
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
		this.setId(id);
		users = new ArrayList<>();
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
			String pages, String shortReference, int id, List<Integer> users) {
		/* Infers activeAuthors values from "" in <authors> */
		this.authors = authors;
		this.secondaryAuthors = secondaryAuthors;
		System.out.println("Active authors:");
		for (int i=0; i<authors.length; i++) {
			System.out.println(String.valueOf("-" + authors[i] + "-" + authors[i].length()));
		}
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
		this.setId(id);
		this.users = users;
	}
	
	public MiMusBibEntry(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			String pages, String shortReference, int id) {
		this(authors, secondaryAuthors, year, distinction, title, mainTitle,
				volume, place, editorial, series, pages, shortReference, id,
				new ArrayList<>());
	}
	
	public MiMusBibEntry(String[] authors, String[] secondaryAuthors,
			String year, String distinction, String title, String mainTitle,
			String volume, String place, String editorial, String series, 
			String pages, int id, List<Integer> users) {
		this(authors, secondaryAuthors, year, distinction,
				title, mainTitle, volume, place, editorial, series, pages, 
				"", id, users);
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
	 * Where - <authorShort1> only appears if it exists,
	 * and <distinction> only appears if specified.
	 * 
	 * Note that the specification says that no separation should appear
	 * between year and distinction.
	 */
	public String generateShortReference() {
		String str = getAuthorShort(0);
		str += (getAuthor(1) == "") ? "" : " - " + getAuthorShort(1);
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
			str += getAuthor(i).length()>0 ? getAuthor(i) + "; " : "";
		}
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Integer> getUsers() {
		return users;
	}
	public void setUsers(List<Integer> users) {
		this.users = users;
	}
	public void addUser(Integer user) {
		users.add(user);
	}
	public void removeUser(Integer user) {
		users.remove(user);
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
				second[i] = "";
			} else {
				unknownAut[i] = "";
				second[i] = "";
			}
		}
		return new MiMusBibEntry(unknownAut, second, "", "", "", "",
				"", "", "", "", "", 0, new ArrayList<>());
	}
	
	public static String generateShortReference(String author0,
			String author1, String year, String distinction) {
		String str = author0;
		if (author1.length()>0)
			str += " - " + author1;
		str += " " + year + distinction;
		return str;
	}
	
	public MiMusBibEntry fromXMLElement(Element elem) {
		String[] authors = new String[NUM_AUTHORS];
		for (int i=0; i<NUM_AUTHORS; i++) {
			authors[i] = elem.getElementsByTagName("autor"+(i+1))
					.item(0).getTextContent();
		}
		String[] secondaries = new String[NUM_SECONDARY];
		for (int i=0; i<NUM_SECONDARY; i++) {
			secondaries[i] = elem.getElementsByTagName("autor_secundari"+(i+1))
					.item(0).getTextContent();
		}
		String year = elem.getElementsByTagName("any")
				.item(0).getTextContent();
		String distinction = elem.getElementsByTagName("distincio")
				.item(0).getTextContent();
		String title = elem.getElementsByTagName("titol")
				.item(0).getTextContent();
		String mainTitle = elem.getElementsByTagName("titol_principal")
				.item(0).getTextContent();
		String volume = elem.getElementsByTagName("volum")
				.item(0).getTextContent();
		String place = elem.getElementsByTagName("lloc")
				.item(0).getTextContent();
		String editorial = elem.getElementsByTagName("editorial")
				.item(0).getTextContent();
		String series = elem.getElementsByTagName("serie")
				.item(0).getTextContent();
		String pages = elem.getElementsByTagName("pagines")
				.item(0).getTextContent();
		String shortRef = elem.getElementsByTagName("referencia_abreujada")
				.item(0).getTextContent();
		int id = Integer.parseInt(elem.getElementsByTagName("id")
				.item(0).getTextContent());
		ArrayList<Integer> users = new ArrayList<>();
		NodeList nodesDoc = elem.getElementsByTagName("usuari_id");
		for (int i=0; i<nodesDoc.getLength(); i++) {
			users.add(Integer.parseInt(nodesDoc.item(i).getTextContent()));
		}
		return new MiMusBibEntry(authors, secondaries, year, distinction, 
				title, mainTitle, volume, place, editorial, series, pages,
				shortRef, id, users);
	}
	
	/* Implementation of MiMusWritable */
	
	@Override
	public Element toXMLElement(Document doc) {
		Element elem = doc.createElement("entry");
		Element a1 = doc.createElement("autor1");
		a1.appendChild(doc.createTextNode(getAuthor(0)));
		Element a2 = doc.createElement("autor2");
		a2.appendChild(doc.createTextNode(getAuthor(1)));
		Element a3 = doc.createElement("autor3");
		a3.appendChild(doc.createTextNode(getAuthor(2)));
		Element a4 = doc.createElement("autor4");
		a4.appendChild(doc.createTextNode(getAuthor(3)));
		Element s1 = doc.createElement("autor_secundari1");
		s1.appendChild(doc.createTextNode(getSecondaryAuthor(0)));
		Element s2 = doc.createElement("autor_secundari2");
		s2.appendChild(doc.createTextNode(getSecondaryAuthor(1)));
		Element s3 = doc.createElement("autor_secundari3");
		s3.appendChild(doc.createTextNode(getSecondaryAuthor(2)));
		Element s4 = doc.createElement("autor_secundari4");
		s4.appendChild(doc.createTextNode(getSecondaryAuthor(3)));
		Element s5 = doc.createElement("autor_secundari5");
		s5.appendChild(doc.createTextNode(getSecondaryAuthor(4)));
		Element s6 = doc.createElement("autor_secundari6");
		s6.appendChild(doc.createTextNode(getSecondaryAuthor(5)));
		Element year = doc.createElement("any");
		year.appendChild(doc.createTextNode(getYear()));
		Element distinction = doc.createElement("distincio");
		distinction.appendChild(doc.createTextNode(getDistinction()));
		Element title = doc.createElement("titol");
		title.appendChild(doc.createTextNode(getTitle()));
		Element mainTitle = doc.createElement("titol_principal");
		mainTitle.appendChild(doc.createTextNode(getMainTitle()));
		Element volume = doc.createElement("volum");
		volume.appendChild(doc.createTextNode(getVolume()));
		Element place = doc.createElement("lloc");
		place.appendChild(doc.createTextNode(getPlace()));
		Element editorial = doc.createElement("editorial");
		editorial.appendChild(doc.createTextNode(getEditorial()));
		Element series = doc.createElement("serie");
		series.appendChild(doc.createTextNode(getSeries()));
		Element pages = doc.createElement("pagines");
		pages.appendChild(doc.createTextNode(getPages()));
		Element shortRef = doc.createElement("referencia_abreujada");
		shortRef.appendChild(doc.createTextNode(getShortReference()));
		Element id = doc.createElement("id");
		id.appendChild(doc.createTextNode(String.valueOf(getId())));
		
		Element users = doc.createElement("usuaris");
		for (int user: getUsers()) {
			Element userId = doc.createElement("usuari_id");
			userId.appendChild(doc.createTextNode(String.valueOf(user)));
			users.appendChild(userId);
		}
		
		elem.appendChild(id);
		elem.appendChild(a1);
		elem.appendChild(a2);
		elem.appendChild(a3);
		elem.appendChild(a4);
		elem.appendChild(s1);
		elem.appendChild(s2);
		elem.appendChild(s3);
		elem.appendChild(s4);
		elem.appendChild(s5);
		elem.appendChild(s6);
		elem.appendChild(year);
		elem.appendChild(distinction);
		elem.appendChild(title);
		elem.appendChild(mainTitle);
		elem.appendChild(volume);
		elem.appendChild(place);
		elem.appendChild(editorial);
		elem.appendChild(series);
		elem.appendChild(pages);
		elem.appendChild(shortRef);
		elem.appendChild(users);
		return elem;
	}

	@Override
	public String getWritableName() {
		return "entry";
	}

	@Override
	public String getWritableCategory() {
		return "bibliography";
	}
	
	@Override
	public String getWritableId() {
		return String.valueOf(id);
	}
	
	public static ArrayList<MiMusBibEntry> read() {
		ArrayList<MiMusBibEntry> entries = new ArrayList<>();
		Document doc = MiMusXML.openBiblio().getDoc();
		NodeList nl = doc.getElementsByTagName("entry");
		for (int i=0; i<nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				entries.add(new MiMusBibEntry().fromXMLElement(elem));
			}
		}
		return entries;
	}
}
