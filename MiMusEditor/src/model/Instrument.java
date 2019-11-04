package model;

/**
 * Artista is one of MiMus entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Instrument extends Entity {
	
	/* 
	 * Tree-like structure of attributes Familia and Classe.
	 * They are associated by index of the following arrays.
	 */
	public static final String[] FAMILIES = {
			"-",
			"cordòfon", 
			"aeròfon", 
			"idiòfon/membranòfon",
			"altres"
	};
	public static final String[][] CLASSES = {
			{"-"},	/* - */
			{		/* Cordofon */
				"amb mànec",
				"sense mànec",
				"d'arc",
				"de tecla"
			},
			{		/* Aerofon */
				"tipus flauta",
				"de llengüeta",
				"de dipòsit d'aire",
				"tipus trompa",
				"de tecla"
			},
			{"-"},	/* Idiofon/Membranofon */
			{"-"}	/* Altres */
	};
	
	/* Instrument attributes */
	private String nom;
	private int family;
	private int classe;
	private String part;
	
	public Instrument(int id, int specId, String nom, 
			int family, int classe, String part) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nom = nom;
		this.family = family;
		this.classe = classe;
		this.part = part;
	}
	
	/**
	 * Returns the lemma of an Instrument, which
	 * coincides with its <nom>.
	 */
	@Override
	public String getLemma() {
		return getNom();
	}
	
	@Override
	public String toString() {
		return getNom();
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getFamily() {
		return family;
	}
	public void setFamily(int family) {
		this.family = family;
	}
	public int getClasse() {
		return classe;
	}
	public void setClasse(int classe) {
		this.classe = classe;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
}
