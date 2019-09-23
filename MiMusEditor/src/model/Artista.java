package model;

public class Artista extends Entity {
	
	private String nomComplet;
	private String tractament;
	private String nom;
	private String cognom;
	private String sobrenom;
	private String distintiu;
	private int genere;
	private int religio;
	private String origen;
	private String observacions;
	
	public Artista(int id, int specId, String nomComplet, String tractament,
			String nom, String cognom, String sobrenom, String distintiu,
			int genere, int religio, String origen, String observacions) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.tractament = tractament;
		this.nom = nom;
		this.cognom = cognom;
		this.sobrenom = sobrenom;
		this.setDistintiu(distintiu);
		this.genere = genere;
		this.religio = religio;
		this.origen = origen;
		this.observacions = observacions;
	}
	
	/* Getters and setters */
	public String getTractament() {
		return tractament;
	}
	public void setTractament(String tractament) {
		this.tractament = tractament;
	}
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nom) {
		this.nomComplet = nom;
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
	public String getGenereStr() {
		if (genere==0)
			return "no marcat";
		if (genere==1)
			return "home";
		if (genere==2)
			return "dona";
		return "desconegut";
	}
	public int getReligio() {
		return religio;
	}
	public void setReligio(int religio) {
		this.religio = religio;
	}
	public String getReligioStr() {
		if (religio==0)
			return "no marcat";
		if (religio==1)
			return "jueu";
		if (religio==2)
			return "musulmà";
		return "Desconegut";
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	@Override
	public String getLemma() {
		String str = getNomComplet();
		if (getDistintiu().length()>0)
			str += " / " + getDistintiu();
		return str;
	}
	
	public String toString() {
		return getLemma();
	}
}
