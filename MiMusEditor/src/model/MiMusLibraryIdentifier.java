package model;

public class MiMusLibraryIdentifier {
	
	private String archive;
	private String series;
	private String subseries1;
	private String subseries2;
	private String number;
	private String page;
	
	public MiMusLibraryIdentifier(String archive, String series, String subseries1, String subseries2, String number,
			String page) {
		super();
		this.archive = archive;
		this.series = series;
		this.subseries1 = subseries1;
		this.subseries2 = subseries2;
		this.number = number;
		this.page = page;
	}

	public MiMusLibraryIdentifier() {
		
	}

	@Override
	public String toString() {
		return archive + ", " + series + ", " + subseries1
				+ ", " + subseries2 + ", " + number + ", " + page;
	}

	public String getArchive() {
		return archive;
	}
	public void setArchive(String archive) {
		this.archive = archive;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getSubseries1() {
		return subseries1;
	}
	public void setSubseries1(String subseries1) {
		this.subseries1 = subseries1;
	}
	public String getSubseries2() {
		return subseries2;
	}
	public void setSubseries2(String subseries2) {
		this.subseries2 = subseries2;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
}
