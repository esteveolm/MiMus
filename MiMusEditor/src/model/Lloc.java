package model;

/**
 * Lloc is one of MiMus entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Lloc extends Entity {
	
	/*
	 * Tree-like structure of attributes Area and Regne.
	 * They are associated by index of the following arrays.
	 */
	public static final String[] AREES = {
			"-",
			"Corona d'Aragó",
			"Portugal", "Castella", "Navarra", "França", 
			"Anglaterra", "Escòcia", "Flandes", "Alemanya", 
			"Borgonya", "Itàlia", "Nàpols", "Sicília", 
			"Xipre", "Altres"
	};
	public static final String[][] REGNES = {
			{"-"},	/* Area "-" */
			{		/* Corona d'Aragó */
				"Catalunya, principat de", 
				"València, regne de", 
				"Aragó, regne de", 
				"Mallorca, regne de", 
				"Sardenya, regne de"
			},		/* Others */
			{"-"}, {"-"}, {"-"}, {"-"}, {"-"}, {"-"}, {"-"}, 
			{"-"}, {"-"}, {"-"}, {"-"}, {"-"}, {"-"}, {"-"}		
	};
	
	/* Lloc attributes */
	private String nomComplet;
	private int regne;
	private int area;
	private String observacions;
	
	public Lloc(int id, int specId, String nomComplet, int regne, int area, String observacions) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.regne = regne;
		this.area = area;
		this.observacions = observacions;
	}

	/**
	 * Returns the lemma of a Lloc, which coincides
	 * with its <nomComplet>.
	 */
	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}

	/* Getters and setters */
	
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public int getRegne() {
		return regne;
	}
	public void setRegne(int regne) {
		this.regne = regne;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	
}
