package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class MiMusEntry {
	public final static String LANGS_PATH = "strings/languages.txt";
	public final static String[] LANGS;
	
	public final static String MATERIES_PATH = "strings/languages.txt";
	public final static String[] MATERIES;
	

	private int id;
	private String numbering;
	private MiMusDate date;
	private String place1;
	private String place2;
	private String regest;
	private MiMusLibraryIdentifier library;
	private MiMusLibraryIdentifier library2;
	private String editions;
	private String registers;
	private String citations;
	private String transcription;
	private List<String> notes;		// TODO: change to real Notes structure when we define it
	private int langIdx;
	private List<String> subjects;
	
	/* Load languages array from file only once for all entries */
	static {
		/* Stream all lines and convert to array */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFolder stringsFolder = corpus.getFolder("strings");
		IFile langsFile = stringsFolder.getFile("languages");
		IFile matFile = stringsFolder.getFile("materies");
		
		List<String> langLines = new ArrayList<>();
		String langsPath = langsFile.getLocation().toString() + ".txt";
		try (Stream<String> stream = Files.lines(Paths.get(langsPath))) {
			langLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load language names from " + LANGS_PATH);
		}
		LANGS = langLines.stream().toArray(String[]::new);
		
		List<String> matLines = new ArrayList<>();
		String matPath = matFile.getLocation().toString() + ".txt";
		try (Stream<String> stream = Files.lines(Paths.get(matPath))) {
			matLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load materies names from " + LANGS_PATH);
		}
		MATERIES = matLines.stream().toArray(String[]::new);
	}
	
	public MiMusEntry() {
		this.id = -1;
		this.numbering = null;
		this.date = null;
		this.setPlace1(null);
		this.setPlace2(null);
		this.regest = null;
		this.library = null;
		this.library2 = null;
		this.editions = null;
		this.registers = null;
		this.citations = null;
		this.transcription = null;
		this.notes = new ArrayList<>();
		this.langIdx = -1;
		this.subjects = new ArrayList<>();
	}
	
	public MiMusEntry(String regest, String body) {
		this.regest = regest;
		this.transcription = body;
	}
	
	public String toString() {
		return "Regest: " + regest + "\n\nTranscription: " + transcription;
	}
	
	/* 
	 * Getters and setters
	 * Some naming variations refer to the fact that String!=MiMusText
	 * Some methods encapsulate access to complex objects like the Language 
	 */
	
	public int getId() {
		return id;
	}
	public String getIdStr() {
		/* As IDs in 001..999, 3-len(id) gives the number of leading zeros */
		String str = String.valueOf(id);
		int len0 = Math.max(3-str.length(), 0);
		String zeros = "";
		if (len0>0) {
			/* Creates array with null characters and replaces them by 0s */
			zeros = new String(new char[len0]).replace('\0', '0');
		}
		return zeros + str;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNumbering() {
		return numbering;
	}
	public void setNumbering(String numbering) {
		this.numbering = numbering;
	}
	
	public MiMusDate getDate() {
		return date;
	}
	public void setDate(MiMusDate date) {
		this.date = date;
	}
	
	public String getPlace1() {
		return place1;
	}
	public void setPlace1(String place1) {
		this.place1 = place1;
	}
	public String getPlace2() {
		return place2;
	}
	public void setPlace2(String place2) {
		this.place2 = place2;
	}
	
	public MiMusText getRegest() {
		return new MiMusText(regest);
	}
	public String getRegestText() {
		return regest;
	}
	public void setRegestText(String regest) {
		this.regest = regest;
	}
	
	public MiMusLibraryIdentifier getLibrary() {
		return library;
	}
	public void setLibrary(MiMusLibraryIdentifier library) {
		this.library = library;
	}
	public MiMusLibraryIdentifier getLibrary2() {
		return library2;
	}
	public void setLibrary2(MiMusLibraryIdentifier library2) {
		this.library2 = library2;
	}
	public String getFullLibraryIdentifier() {
		return library.toString() + "; " + library2.toString();
	}
	
	public String getEditions() {
		return editions;
	}
	public void setEditions(String editions) {
		this.editions = editions;
	}
	
	public String getRegisters() {
		return registers;
	}
	public void setRegisters(String registers) {
		this.registers = registers;
	}
	
	public String getCitations() {
		return citations;
	}
	public void setCitations(String citations) {
		this.citations = citations;
	}
	
	public MiMusText getTranscription() {
		return new MiMusText(transcription);
	}
	public String getTranscriptionText() {
		return transcription;
	}
	public void setTranscriptionText(String transcription) {
		this.transcription = transcription;
	}
	
	// TODO: change to real Notes behavior when we define it
	public List<String> getNotes() {
		return notes;
	}
	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public String getLanguage() {
		return LANGS[langIdx];
	}
	public void setLanguage(int langIdx) {
		this.langIdx = langIdx;
	}
	public void setLanguage(String langName) {
		/* Stream languages array to find entry */
		int langIdx = IntStream.range(0, LANGS.length)
				.filter(i -> langName.equals(LANGS[i]))
				.findFirst()
				.orElse(-1);
		setLanguage(langIdx);
	}
	
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public void addSubject(String subject) {
		this.subjects.add(subject);
	}
	
	public String getReadOnlyText() {
		String str = "Doc ID: " + getIdStr() + 
				"\nNumeració antiga: " + getNumbering() +
				"\nData: " + getDate().toString() + "\n";
		if (getPlace2() != null) {
			str += "Lloc: de " + getPlace1() + 
					" a " + getPlace2() + "\n";
		} else if (getPlace1() != null) {
			str += "Lloc: " + getPlace1() + "\n";
		} else {
			str += "Lloc: desconegut\n";
		}
		str += "Signatura A: " + getLibrary() + "\n";
		if (getLibrary2().toString().length()>0) {
			str += "Signatura B: " + getLibrary2() + "\n";
		}
		str += "Llengua: " + getLanguage() +
				"\nMatèries: " + String.join(", ", getSubjects());
		return str;
	}
}
