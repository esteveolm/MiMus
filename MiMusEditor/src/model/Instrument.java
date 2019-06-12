package model;

public class Instrument extends Entity {
	
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
