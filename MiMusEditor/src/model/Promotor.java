package model;

public class Promotor extends Entity {
	
	private String nomComplet;
	private String nom;
	private String cognom;
	private String sobrenom;
	private String distintiu;
	private int genere;

	public Promotor(int id, int specId, String nomComplet, String nom, String cognom,
			String sobrenom, String distintiu, int genere) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.nom = nom;
		this.cognom = cognom;
		this.sobrenom = sobrenom;
		this.setDistintiu(distintiu);
		this.genere = genere;
	}
	
	@Override
	public String getLemma() {
		String str = getNomComplet();
		if (getDistintiu().length()>0)
			str += " / " + getDistintiu();
		return str;
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
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCognom() {
		return cognom;
	}
	public void setCognom(String cognom) {
		this.cognom = cognom;
	}
	public String getSobrenom() {
		return sobrenom;
	}
	public void setSobrenom(String sobrenom) {
		this.sobrenom = sobrenom;
	}
	public String getDistintiu() {
		return distintiu;
	}
	public void setDistintiu(String distintiu) {
		this.distintiu = distintiu;
	}
	public int getGenere() {
		return genere;
	}
	public void setGenere(int genere) {
		this.genere = genere;
	}
}
