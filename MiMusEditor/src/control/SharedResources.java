package control;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import model.MiMusBibEntry;
//import util.MiMusBiblioReader;

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
	
	private List<MiMusBibEntry> bibEntries;
	private String[] referenceTypes = {"Edition", "Register", "Citation"};
	private String repoPath;
	private IFolder corpusFolder;
	private String corpusPath;
	private String biblioPath;
	private String artistaPath;
	private String remote;
	private Git git;
	private int entityCurrentID;
	/* Singleton instance of SharedResources */
	private static SharedResources instance = null;
	
	/* Constructor made private so the object can only initialized by singleton */
	private SharedResources() {
		setRemote("https://github.com/JavierBJ/MiMusCorpus.git");
		
		/* Set repository directory in workspace */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		this.repoPath = project.getLocation().toString();
		IFolder corpus = project.getFolder("MiMusCorpus");
		this.setCorpusFolder(corpus);
		this.setCorpusPath(corpus.getLocation().toString());
		
		/* Set git adapter */
		try {
			/* Check if directory is a git repo already */
			System.out.println(corpus.getLocation().toString());
			if (!corpus.exists()) {
				/* If not, clone it from github */
				Git git = Git.cloneRepository()
						.setURI(remote)
						.setDirectory(new File(corpusPath))
						.call();
				this.git = git;
				System.out.println("Cloned repo from remote as could not be found in local.");
			} else {
				this.git = Git.open(new File(corpusPath));
				System.out.println("Opened existing repo.");
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Once repo is functional, we can keep loading resources */
		IFolder strings = corpus.getFolder("strings");
		IFile biblioFile = strings.getFile("bibliography.xml");
		IFile artistaFile = strings.getFile("artistas.xml");
		this.biblioPath = biblioFile.getLocation().toString();
		this.setArtistaPath(artistaFile.getLocation().toString());
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
	
	public List<MiMusBibEntry> readBiblio() {
		bibEntries = MiMusBibEntry.read();
		setEntityCurrentID(0);
		return bibEntries;
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
		if (bibEntries == null) {
			return readBiblio();
		}
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
	public String getRepoPath() {
		return repoPath;
	}
	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}
	public IFolder getCorpusFolder() {
		return corpusFolder;
	}
	public void setCorpusFolder(IFolder corpusFolder) {
		this.corpusFolder = corpusFolder;
	}
	public String getCorpusPath() {
		return corpusPath;
	}
	public void setCorpusPath(String corpusPath) {
		this.corpusPath = corpusPath;
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
	public String getRemote() {
		return remote;
	}
	public void setRemote(String remote) {
		this.remote = remote;
	}
	public Git getGit() {
		return git;
	}
	public void setGit(Git git) {
		this.git = git;
	}
	public int getEntityCurrentID() {
		return entityCurrentID;
	}
	public int getIncrementCurrentID() {
		++entityCurrentID;
		return entityCurrentID-1;
	}
	public void setEntityCurrentID(int entityCurrentID) {
		this.entityCurrentID = entityCurrentID;
	}
}
