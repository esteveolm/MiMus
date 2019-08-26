package model;

public class Lloc extends Entity {
	
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
	
	private String nomComplet;
	private int regne;
	private int area;
	
	public Lloc(int id, int specId, String nomComplet, int regne, int area) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.regne = regne;
		this.area = area;
	}

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
}
