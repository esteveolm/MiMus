package model;

/**
 * Ofici is one of MiMus entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Ofici extends Entity {
	
	/* List of elements that attribute <especialitat> can take */
	public static final String[] ESPECIALITATS = {
			"-",
			"sense especificar", 
			"instrument",
			"veu", 
			"dansa", 
			"artesà", 
			"malabars i altres"
	};
	
	/* Ofici attributes */
	private String nomComplet;
	private String terme;
	private int especialitat;
	private Instrument instrument;
	private String observacions;

	public Ofici(int id, int specId, String nomComplet, String terme, 
			int especialitat, Instrument instrument, String observacions) {
		this.setId(id);
		this.setSpecificId(specId);
		this.setNomComplet(nomComplet);
		this.terme = terme;
		this.especialitat = especialitat;
		this.instrument = instrument;
		this.observacions = observacions;
	}

	/**
	 * Returns the lemma of an Ofici, which coincides
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
	public String getTerme() {
		return terme;
	}
	public void setTerme(String terme) {
		this.terme = terme;
	}
	public int getEspecialitat() {
		return especialitat;
	}
	public void setEspecialitat(int especialitat) {
		this.especialitat = especialitat;
	}
	public Instrument getInstrument() {
		return instrument;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	
}
