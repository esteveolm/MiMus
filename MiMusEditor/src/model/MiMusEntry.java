package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MiMusEntry {
	private final static String LANGS_PATH = "strings/languages.txt";
	private final static String SUBJECTS_PATH = "strings/subjects.txt";
	private final static String[] LANGS;
	private final static String[] SUBJECTS;

	private int numbering;
	private MiMusDate date;
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
		List<String> langLines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(LANGS_PATH))) {
			langLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load language names from " + LANGS_PATH);
		}
		LANGS = langLines.stream().toArray(String[]::new);
		
		List<String> subjectLines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(SUBJECTS_PATH))) {
			subjectLines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load language names from " + SUBJECTS_PATH);
		}
		SUBJECTS = subjectLines.stream().toArray(String[]::new);
	}
	
	public MiMusEntry() {
		this.numbering = -1;
		this.date = null;
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
	
	public int getNumbering() {
		return numbering;
	}
	public void setNumbering(int numbering) {
		this.numbering = numbering;
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
}
