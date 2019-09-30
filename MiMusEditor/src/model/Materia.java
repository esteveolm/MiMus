package model;

/**
 * Materia is a String stored on MiMus database, which
 * makes the list of Materies associated to Documents.
 * 
 * A class is necessary because it must implement Unit.
 * 
 * @author Javier Beltrán Jorba
 *
 */
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
