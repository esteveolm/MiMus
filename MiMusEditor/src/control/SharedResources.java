package control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import model.Artista;
import model.Casa;
import model.EntityInstance;
import model.Instrument;
import model.Lloc;
import model.Bibliography;
import model.MiMusReference;
import model.Ofici;
import model.Promotor;
import model.Transcription;
import model.Unit;

/**
 * 
 * A SharedResources object encapsulates objects that are common to
 * several components of MiMus that could work separately. It follows
 * a Singleton pattern that assures creation of only one object for the
 * whole application, no matter how many components are in use.
 * 
 * The Singleton forces SharedResources to be constructed using the method
 * SharedResources.getInstance(), which provides lazy initialization.
 * 
 * At the moment, SharedResources only contains the bibliography, which
 * is common to the Editor and the BiblioView, counters and constants.
 * If SharedResources ends up containing more complex objects, a separate 
 * Singleton should be available for every object, assuring that only the 
 * components explicitly demanded by the application are loaded into memory.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public final class SharedResources {
	
	public static final String[] FAMILY = {"-","Cordòfon", 
			"Aeròfon", "Idiòfon/Membranòfon", "Altres"};
	public static final String[] CLASSE = {"-", "amb mànec", 
			"sense mànec", "d'arc", "de tecla", "tipus flauta", "de llengüeta",
			"de dipòsit d'aire", "tipus trompa"};
	public static final String[] REGNE = {"-", 
			"Catalunya, principat de", "València, regne de", "Aragó, regne de", 
			"Mallorca, regne de", "Sardenya, regne de"};
	public static final String[] AREA = {"-", "Corona d'Aragó", 
			"Portugal", "Castella", "Navarra", "França", "Anglaterra", "Escòcia", 
			"Flandes", "Alemanya", "Borgonya", "Itàlia", "Nàpols", "Sicília", 
			"Xipre", "Altres"};
	public static final String [] REF_TYPES = 
		{"Edition", "Register", "Citation"};
	
	private List<Bibliography> bibEntries;
	private List<Unit> artistas;
	private List<Unit> instruments;
	private List<Unit> cases;
	private List<Unit> promotors;
	private List<Unit> oficis;
	private List<Unit> llocs;
	private String repoPath;
	private IProject corpusFolder;
	private String corpusPath;
	private String txtPath;
	private String xmlPath;
	private String biblioPath;
	private String artistaPath;
	private String instrumentPath;
	private String casaPath;
	private String promotorPath;
	private String oficiPath;
	private String llocPath;
	private String remote;
	
	/* local + id make the identification of unique entities possible globally */
	private int id;
	private int local;
	
	/* Singleton instance of SharedResources */
	private static SharedResources instance = null;
	
	/* Constructor made private so the object can only initialized by singleton */
	private SharedResources() {
		setRemote("https://github.com/JavierBJ/MiMusCorpus.git");
		
		/* Set local id */
		setLocal(1);	// This must be read from outside the program
		setId(getLocal()*1000000);
		
		/* Set repository directory in workspace */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		this.repoPath = workspace.getLocation().toString();
		IProject corpus = workspace.getProject("MiMusCorpus");
		this.setCorpusFolder(corpus);
		this.setCorpusPath(corpus.getLocation().toString());
		
		/* Once repo is functional, we can keep loading resources */
		IFolder entitiesFolder = corpus.getFolder("entities");
		IFile biblioFile = entitiesFolder.getFile("bibliography.xml");
		IFile artistaFile = entitiesFolder.getFile("artistas.xml");
		IFile instrumentFile = entitiesFolder.getFile("instruments.xml");
		IFile casaFile = entitiesFolder.getFile("cases.xml");
		IFile promotorFile = entitiesFolder.getFile("promotors.xml");
		IFile oficiFile = entitiesFolder.getFile("oficis.xml");
		IFile llocFile = entitiesFolder.getFile("llocs.xml");
		this.biblioPath = biblioFile.getLocation().toString();
		this.setArtistaPath(artistaFile.getLocation().toString());
		this.setInstrumentPath(instrumentFile.getLocation().toString());
		this.setCasaPath(casaFile.getLocation().toString());
		this.setPromotorPath(promotorFile.getLocation().toString());
		this.setOficiPath(oficiFile.getLocation().toString());
		this.setLlocPath(llocFile.getLocation().toString());
		
		IFolder txts = corpus.getFolder("txt");
		this.setTxtPath(txts.getLocation().toString());
		IFolder xmls = corpus.getFolder("xml");
		this.setXmlPath(xmls.getLocation().toString());
		if (!xmls.exists()) {
			xmls.getLocation().toFile().mkdirs();
			System.out.println("xml folder created because it was not present.");
		}
	}
	
	/**
	 * Creates a new SharedResources object if none has been declared yet,
	 * or returns the existing object if there is an instance created. This
	 * is the only way to instantiate this class, following the Singleton
	 * pattern.
	 */
	public static SharedResources getInstance() {
		if (instance == null) {
			System.out.println("Creating NEW SharedResources");
			instance = new SharedResources();
			instance.globallySetUpdateId();
		}
		return instance;
	}
	
	/**
	 * Constructs a new SharedResources in the same Singleton reference.
	 */
	public void refresh() {
		System.out.println("Creating REFRESHED SharedResources");
		instance = new SharedResources();
		instance.globallySetUpdateId();
	}
	
	/**
	 * Method overridden to always throw CloneNotSupportedException,
	 * avoiding that a copy of SharedResources object is made and the
	 * system ends up with several instances of it.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public List<Bibliography> getBibEntries() {
		if (bibEntries == null) {
			bibEntries = Bibliography.read();
		}
		return bibEntries;
	}
	public void setBibEntries(List<Bibliography> bibEntries) {
		this.bibEntries = bibEntries;
	}
	public List<Unit> getArtistas() {
		if (artistas == null)
			artistas = Artista.read();
		return artistas;
	}
	public void setArtistas(List<Unit> artistas) {
		this.artistas = artistas;
	}
	public List<Unit> getInstruments() {
		if (instruments == null)
			instruments = Instrument.read();
		return instruments;
	}
	public void setInstruments(List<Unit> instruments) {
		this.instruments = instruments;
	}
	public List<Unit> getCases() {
		if (cases == null)
			cases = Casa.read();
		return cases;
	}
	public void setCases(List<Unit> cases) {
		this.cases = cases;
	}
	public List<Unit> getPromotors() {
		if (promotors == null)
			promotors = Promotor.read();
		return promotors;
	}
	public void setPromotors(List<Unit> promotors) {
		this.promotors = promotors;
	}
	public List<Unit> getOficis() {
		if (oficis == null)
			oficis = Ofici.read();
		return oficis;
	}
	public void setOficis(List<Unit> oficis) {
		this.oficis = oficis;
	}
	public List<Unit> getLlocs() {
		if (llocs == null)
			llocs = Lloc.read();
		return llocs;
	}
	public void setLlocs(List<Unit> llocs) {
		this.llocs = llocs;
	}
	public String getRepoPath() {
		return repoPath;
	}
	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}
	public IProject getCorpusFolder() {
		return corpusFolder;
	}
	public void setCorpusFolder(IProject corpusFolder) {
		this.corpusFolder = corpusFolder;
	}
	public String getCorpusPath() {
		return corpusPath;
	}
	public void setCorpusPath(String corpusPath) {
		this.corpusPath = corpusPath;
	}
	public String getTxtPath() {
		return txtPath;
	}
	public void setTxtPath(String txtPath) {
		this.txtPath = txtPath;
	}
	public String getXmlPath() {
		return xmlPath;
	}
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	public String getBiblioPath() {
		return biblioPath;
	}
	public void setBiblioPath(String biblioPath) {
		this.biblioPath = biblioPath;
	}
	public String getArtistaPath() {
		return artistaPath;
	}
	public void setArtistaPath(String artistaPath) {
		this.artistaPath = artistaPath;
	}
	public String getInstrumentPath() {
		return instrumentPath;
	}
	public void setInstrumentPath(String instrumentPath) {
		this.instrumentPath = instrumentPath;
	}
	public String getCasaPath() {
		return casaPath;
	}
	public void setCasaPath(String casaPath) {
		this.casaPath = casaPath;
	}
	public String getPromotorPath() {
		return promotorPath;
	}
	public void setPromotorPath(String promotorPath) {
		this.promotorPath = promotorPath;
	}
	public String getOficiPath() {
		return oficiPath;
	}
	public void setOficiPath(String oficiPath) {
		this.oficiPath = oficiPath;
	}
	public String getLlocPath() {
		return llocPath;
	}
	public void setLlocPath(String llocPath) {
		this.llocPath = llocPath;
	}
	public String getRemote() {
		return remote;
	}
	public void setRemote(String remote) {
		this.remote = remote;
	}
	public int getLocal() {
		return local;
	}
	public void setLocal(int local) {
		this.local = local;
	}
	
	/* ID getters and setters to work as counters */
	
	public int getId() {
		return id;
	}
	
	public int getIncrementId() {
		System.out.println("Get and Increment ID at " + id);
		return ++id-1;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUpdateId(int id) {
		/* Only consider the maximum of ids with same local as yours */
		int old = this.id;
		int itsLocal = id / 1000000;
		if (itsLocal == this.local) {
			this.id = Math.max(this.id, id+1);
		}
		System.out.println("Set and Update ID from " + old + " to " + this.id);
	}
	
	public void globallySetUpdateId() {
		System.out.println("Entering global setupdate");
		for (Unit a : Artista.read()) {
			setUpdateId(a.getId());
		}
		for (Unit i : Instrument.read()) {
			setUpdateId(i.getId());
		}
		for (Unit i : Casa.read()) {
			setUpdateId(i.getId());
		}
		for (Unit i : Promotor.read()) {
			setUpdateId(i.getId());
		}
		for (Unit i : Ofici.read()) {
			setUpdateId(i.getId());
		}
		for (Unit i : Lloc.read()) {
			setUpdateId(i.getId());
		}
		List<Unit> biblio = new ArrayList<>(Bibliography.read());
		for (Unit e : biblio) {
			setUpdateId(e.getId());
		}
		File docs = new File(getXmlPath());
		for (File f : docs.listFiles()) {
			if (f.getName().endsWith(".xml")) {
				try {
					String num = f.getName().substring(0, 3);
					Integer.parseInt(num);	// Catches exception if not a number
					List<Unit> instances = 
							EntityInstance.read(String.valueOf(num));
					List<Unit> transcriptions = 
							Transcription.read(String.valueOf(num), instances);
					List<Unit> references = 
							MiMusReference.read(String.valueOf(num), biblio);
					for (Unit u : instances) {
						setUpdateId(u.getId());
					}
					for (Unit u : transcriptions) {
						setUpdateId(u.getId());
					}
					for (Unit u : references) {
						setUpdateId(u.getId());
					}
				} catch (NumberFormatException e) {}
			}
		}
		System.out.println("Updated ID is: "+ id);
	}
}
