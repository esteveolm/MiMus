package model;

/**
 * An Entity is every type of element stored on the
 * MiMus database which has a lemma. That is, it corresponds
 * with the objects being annotated.
 * 
 * A lemma is not a physical attribute, it is specified by
 * any class implementing Entity through method getLemma()
 * and usually corresponds with attributes such as <nomComplet>
 * in the case of people.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public abstract class Entity extends HierarchicalUnit {
	
	/**
	 * Returns the lemma of an entity. How the lemma is
	 * created depends on the specific Entity implemented.
	 */
	public abstract String getLemma();
	
	/**
	 * Returns the type of Entity, understood as the name
	 * of the specific class implementing Entity. See
	 * Class.getSimpleName().
	 */
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
