package control;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import model.MiMusBibEntry;
import util.MiMusBiblioReader;

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
 * is common to the Editor and the BiblioView, and an array of Strings.
 * If SharedResources ends up containing more complex objects, a separate 
 * Singleton should be available for every object, assuring that only the 
 * components explicitly demanded by the application are loaded into memory.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public final class SharedResources {
	
	private List<MiMusBibEntry> bibEntries;
	private String[] referenceTypes = {"Edition", "Register", "Citation"};
	private String biblioPath;

	/* Singleton instance of SharedResources */
	private static SharedResources instance = null;
	
	/* Constructor made private so the object can only initialized by singleton */
	private SharedResources() {
		/* Load stored entries from path */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder stringsFolder = project.getFolder("strings");
		IFile biblioFile = stringsFolder.getFile("bibliography.xml");
		biblioPath = biblioFile.getLocation().toString();
		
		bibEntries = MiMusBiblioReader.read(biblioPath);
	}
	
	/**
	 * Creates a new SharedResources object if none has been declared yet,
	 * or returns the existing object if there is an instance created. This
	 * is the only way to instantiate this class, following the Singleton
	 * pattern.
	 */
	public static SharedResources getInstance() {
		if (instance == null) {
			instance = new SharedResources();
		}
		return instance;
	}
	
	/**
	 * Constructs a new SharedResources in the same Singleton reference.
	 */
	public void refresh() {
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
	
	public List<MiMusBibEntry> getBibEntries() {
		return bibEntries;
	}
	public void setBibEntries(List<MiMusBibEntry> bibEntries) {
		this.bibEntries = bibEntries;
	}
	public String[] getReferenceTypes() {
		return referenceTypes;
	}
	public void setReferenceTypes(String[] referenceTypes) {
		this.referenceTypes = referenceTypes;
	}
	public String getBiblioPath() {
		return biblioPath;
	}
	public void setBiblioPath(String biblioPath) {
		this.biblioPath = biblioPath;
	}
}
