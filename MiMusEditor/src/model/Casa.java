package model;

/**
 * Casa is one of MiMus entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Casa extends Entity {
	
	/* Casa attributes */
	private String nomComplet;
	private String titol;
	private String cort;
	private String observacions;
	
	
	public Casa(int id, int specId, String nomComplet, String titol, String cort, String observacions) {
		this.setId(id);
		this.setSpecificId(specId);
		this.nomComplet = nomComplet;
		this.titol = titol;
		this.cort = cort;
		this.observacions = observacions;
	}

	/**
	 * Returns the lemma of a Casa, which coincides
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

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	
}
