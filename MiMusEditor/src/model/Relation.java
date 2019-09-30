package model;

/**
 * Relation is a relation between two Entities in the MiMus corpus.
 * There are categories of relations, each of which are specified for a
 * certain type of entities. For instance "serveix_a" is between Artista
 * and Promotor.
 * 
 * Note that Relations are actually RelationInstances, because there are
 * no Relation concepts (the types are already predefined), but just
 * appearances of these in the documents. Hence, it contains a foreign
 * key to Document.
 * 
 * In the MiMus database, Relations are implemented hierarchically, i.e.
 * with a table for the data common to all relations, and many subtables
 * with the data specific to the implementations.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Relation extends HierarchicalUnit {

	public static final String[] TYPES = 
			{"te_ofici", "te_casa", "serveix_a", "resideix_a", "moviment"};
	
	private Entity itsEntity1;
	private Entity itsEntity2;
	private String type;
	private Document doc;
	
	public Relation(Document doc, Entity ent1, Entity ent2, 
			String type, int id, int specId) {
		this.itsEntity1 = ent1;
		this.itsEntity2 = ent2;
		this.type = type;
		this.doc = doc;
		this.setId(id);
		this.setSpecificId(specId);
 	}

	public Entity getItsEntity1() {
		return itsEntity1;
	}
	public void setItsEntity1(Entity itsEntity1) {
		this.itsEntity1 = itsEntity1;
	}
	public Entity getItsEntity2() {
		return itsEntity2;
	}
	public void setItsEntity2(Entity itsEntity2) {
		this.itsEntity2 = itsEntity2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	/**
	 * A Relation is represented as the string representation of
	 * both entities related, with a hyphen " - " in between.
	 */
	@Override
	public String toString() {
		return getItsEntity1().toString() + " - " + getItsEntity2().toString();
	}
}
