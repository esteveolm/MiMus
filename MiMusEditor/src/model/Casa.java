package model;

public class Casa extends Entity {
	
	private String nomComplet;
	private String titol;
	private String cort;
	
	public Casa() {}
	
	public Casa(int id, String nomComplet, String titol, String cort) {
		super(id);
		this.nomComplet = nomComplet;
		this.titol = titol;
		this.cort = cort;
	}

	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}

	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getCort() {
		return cort;
	}
	public void setCort(String cort) {
		this.cort = cort;
	}
}
