package model;

/**
 * A Unit is any object stored on the MiMus database. Its
 * attribute <id> corresponds with the ID given by the
 * database, which should be its primary key.
 * 
 * Several classes implement Unit and correspond with specific
 * tables of the MiMus database.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Unit {
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
