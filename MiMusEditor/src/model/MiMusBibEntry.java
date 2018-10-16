package model;

public class MiMusBibEntry {
	
	private final static int SHORT_TITLE_LEN = 20;
	private String author;
	private int year;
	private String title;
	
	public MiMusBibEntry(String author, int year, String title) {
		this.author = author;
		this.year = year;
		this.title = title;
	}
	
	public String toString() {
		return getAuthorAsReference() + ". " + getYear() + ". " + getTitleAsReference();
	}
	
	/* Getters and setters */
	
	public String getAuthor() {
		return author;
	}
	public String getAuthorAsReference() {
		String[] words = author.split(" ");
		String name = "";
		for (int i=0; i<words.length-1; i++) {
			/* Iterate n-1 first words in name and get first letter*/
			name += words[i].substring(0, 1) + ". ";
		}
		return words[words.length-1] + ", " + name;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getTitle() {
		return title;
	}
	public String getTitleAsReference() {
		String points = "...";
		String shortened = title;
		if (shortened.length()>SHORT_TITLE_LEN) {
			/* Cut title considering the length of 3 suspension points */
			shortened = shortened
					.substring(0, SHORT_TITLE_LEN-points.length()) + points;
		}
		return "\"" + shortened + "\"";
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
