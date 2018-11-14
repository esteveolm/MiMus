package model;

public class Artista {
	
	private String name;
	private boolean female;
	
	public Artista() {
		this.name = "";
		this.female = false;
	}
	
	public Artista(String name, boolean female) {
		this.name = name;
		this.female = female;
	}
	
	/* Getters and setters */
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFemale() {
		return female;
	}
	public void setFemale(boolean female) {
		this.female = female;
	}
	public String getGender() {
		return female ? "Female" : "Male";
	}
}
