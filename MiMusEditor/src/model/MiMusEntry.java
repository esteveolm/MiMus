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
	private final static String LANGS_PATH = "strings/languages.txt";
	private final static String SUBJECTS_PATH = "strings/subjects.txt";
	private final static String[] LANGS;
	private final static String[] SUBJECTS;

	private int id;
	private String numbering;
	private MiMusDate date;
	private String place1;
	private String place2;
	private String regest;
	private MiMusLibraryIdentifier library;
	private MiMusLibraryIdentifier library2;
	private List<MiMusReference> editions;
	private List<MiMusReference> registers;
	private List<MiMusReference> citations;
	private String transcription;
	private List<String> notes;		// TODO: change to real Notes structure when we define it
	private int langIdx;
	private List<Integer> subjectIdxs;
	
	/* Load languages array from file only once for all entries */
	static {
		/* Stream all lines and convert to array */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspace.getProject("MiMus");
		IFolder stringsFolder = project.getFolder("strings");
		IFile langsFile = stringsFolder.getFile("languages");
		
		List<String> langLines = new ArrayList<>();
		String langsPath = langsFile.getLocation().toString() + ".txt";
		try (Stream<String> stream = Files.lines(Paths.get(langsPath))) {
			langLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load language names from " + LANGS_PATH);
		}
		LANGS = langLines.stream().toArray(String[]::new);
		
		IFile subjectsFile = stringsFolder.getFile("subjects");
		String subjectsPath = subjectsFile.getLocation().toString() + ".txt";
		List<String> subjectLines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(subjectsPath))) {
			subjectLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load language names from " + SUBJECTS_PATH);
		}
		SUBJECTS = subjectLines.stream().toArray(String[]::new);
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
		this.editions = new ArrayList<>();
		this.registers = new ArrayList<>();
		this.citations = new ArrayList<>();
		this.transcription = null;
		this.notes = new ArrayList<>();
		this.langIdx = -1;
		this.subjectIdxs = new ArrayList<>();
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
	
	public List<MiMusReference> getEditions() {
		return editions;
	}
	public void setEditions(List<MiMusReference> editions) {
		this.editions = editions;
	}
	public void addEdition(MiMusReference edition) {
		editions.add(edition);
	}
	public void removeEdition(MiMusReference edition) {
		editions.remove(edition);
	}
	
	public List<MiMusReference> getRegisters() {
		return registers;
	}
	public void setRegisters(List<MiMusReference> registers) {
		this.registers = registers;
	}
	public void addRegister(MiMusReference register) {
		registers.add(register);
	}
	public void removeRegister(MiMusReference register) {
		registers.remove(register);
	}
	
	public List<MiMusReference> getCitations() {
		return citations;
	}
	public void setCitations(List<MiMusReference> citations) {
		this.citations = citations;
	}
	public void addCitation(MiMusReference citation) {
		citations.add(citation);
	}
	public void removeCitation(MiMusReference citation) {
		citations.remove(citation);
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
		List<String> subjects = new ArrayList<String>();
		for (Integer idx: subjectIdxs) {
			subjects.add(SUBJECTS[idx]);
		}
		return subjects;
	}
	public void setSubjects(List<Integer> subjectIdxs) {
		this.subjectIdxs = subjectIdxs;
	}
	public void addSubject(int subjectIdx) {
		this.subjectIdxs.add(subjectIdx);
	}
	public void addSubject(String subjectName) {
		int subjectIdx = IntStream.range(0, SUBJECTS.length)
				.filter(i -> subjectName.equals(SUBJECTS[i]))
				.findFirst()
				.orElse(-1);
		if (subjectIdx>-1) 
			addSubject(subjectIdx);
	}
	public void removeSubject(int subjectIdx) {
		this.subjectIdxs.remove(new Integer(subjectIdx));
	}
	public void removeSubject(String subjectName) {
		int subjectIdx = IntStream.range(0, SUBJECTS.length)
				.filter(i -> subjectName.equals(SUBJECTS[i]))
				.findFirst()
				.orElse(-1);
		if (subjectIdx>-1) 
			removeSubject(subjectIdx);
	}
	
	public String getReadOnlyText() {
		String str = "Doc ID: " + getIdStr() + 
				"\nNumeració antiga: " + getNumbering() +
				"\nData: " + getDate().toString() + "\n";
		if (getPlace2() != null) {
			str += "Lloc: de " + getPlace1() + 
					" a " + getPlace2() + "\n";
		} else {
			str += "Lloc: " + getPlace1() + "\n";
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
