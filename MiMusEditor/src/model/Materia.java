package model;

public class Materia extends Unit {

	private String name;
	
	public Materia(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
