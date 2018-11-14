package model;

public class Artista extends Entity {
	
	private String name;
	private boolean female;
	
	public Artista(int id) {
		super(id);
		this.name = "";
		this.female = false;
	}
	
	public Artista(int id, String name, boolean female) {
		super(id);
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

	@Override
	public String getLemma() {
		// TODO Auto-generated method stub
		return "";
	}
}
