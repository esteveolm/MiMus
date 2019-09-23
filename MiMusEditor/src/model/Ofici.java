package model;

public class Ofici extends Entity {
	
	public static final String[] ESPECIALITATS = {
			"-",
			"sense especificar", 
			"instrument",
			"veu", 
			"dansa", 
			"artesà", 
			"malabars i altres"
	};
	
	private String nomComplet;
	private String terme;
	private int especialitat;
	private Instrument instrument;

	public Ofici(int id, int specId, String nomComplet, String terme, 
			int especialitat, Instrument instrument) {
		this.setId(id);
		this.setSpecificId(specId);
		this.setNomComplet(nomComplet);
		this.terme = terme;
		this.especialitat = especialitat;
		this.instrument = instrument;
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
}
