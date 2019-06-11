package control;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

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
	
	private IProject corpusFolder;
	private String corpusPath;
	
	/* Singleton instance of SharedResources */
	private static SharedResources instance = null;
	
	/* Constructor made private so the object can only initialized by singleton */
	private SharedResources() {		
		/* Set repository directory in workspace */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		this.setCorpusFolder(corpus);
		this.setCorpusPath(corpus.getLocation().toString());
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
		}
		return instance;
	}
	
	/**
	 * Constructs a new SharedResources in the same Singleton reference.
	 */
	public void refresh() {
		System.out.println("Creating REFRESHED SharedResources");
		instance = new SharedResources();
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
}
