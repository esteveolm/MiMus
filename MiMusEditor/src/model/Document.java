package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * A Document represents a text document of MiMus corpus. It
 * is associated with the entries from table Document in MiMus
 * database.
 * 
 * Document implements IEditorInput, which means it is a suitable
 * input for an Eclipse Editor such as MiMus Editor.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class Document extends Unit implements IEditorInput {
	
	/* Lists of values Language can take, reflected on langIdx */
	public final static String[] LANGS = 
		{"-", "llatí", "català", "castellà", "aragonès/castellà", "altres"};
	
	/* 
	 * Lists of values for state of annotation and revision, reflected
	 * on stateAnnotIdx and stateRevIdx, respectively.
	 */
	public final static String[] STATES_ANNOT = {"-", "En procés", "Per revisar"};
	public final static String[] STATES_REV = {"-", "En procés", "Revisat"};
	
	/* Document attributes */
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
	private List<Note> notes;
	private int langIdx;
	private List<Materia> subjects;
	private int stateAnnotIdx;
	private int stateRevIdx;
	
	
	public Document() {
		this.numbering = "";
		this.date = new MiMusDate();
		this.setPlace1(null);
		this.setPlace2(null);
		this.regest = "";
		this.library = new MiMusLibraryIdentifier();
		this.library2 = new MiMusLibraryIdentifier();
		this.editions = null;
		this.registers = null;
		this.citations = null;
		this.transcription = "";
		this.notes = new ArrayList<>();
		this.langIdx = -1;
		this.subjects = new ArrayList<>();
		this.stateAnnotIdx = -1;
		this.stateAnnotIdx = -1;
		setLanguage(Document.LANGS[1]);		
	}
	
	/**
	 * String representation of Document which reflects
	 * its ID.
	 */
	public String toString() {
		return "Document " + getIdStr();
	}
	
	/* 
	 * Getters and setters
	 * Some naming variations refer to the fact that String!=MiMusText
	 * Some methods encapsulate access to complex objects like the Language 
	 */
	public String getIdStr() {
		/* As IDs in 001..999, 3-len(id) gives the number of leading zeros */
		String str = String.valueOf(getId());
		int len0 = Math.max(5-str.length(), 0);
		String zeros = "";
		if (len0>0) {
			/* Creates array with null characters and replaces them by 0s */
			zeros = new String(new char[len0]).replace('\0', '0');
		}
		return zeros + str;
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
	public List<Note> getNotes() {
		return notes;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	public int getLanguage() {
		return langIdx;
	}
	public String getLanguageStr() {
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
	public List<Materia> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<Materia> subjects) {
		this.subjects = subjects;
	}
	public void addSubject(Materia subject) {
		this.subjects.add(subject);
	}
	public int getStateAnnotIdx() {
		return stateAnnotIdx;
	}
	public String getStateAnnotStr() {
		return STATES_ANNOT[stateAnnotIdx];
	}
	public void setStateAnnotIdx(int stateAnnotIdx) {
		this.stateAnnotIdx = stateAnnotIdx;
	}
	public int getStateRevIdx() {
		return stateRevIdx;
	}
	public String getStateRevStr() {
		return STATES_REV[stateRevIdx];
	}
	public void setStateRevIdx(int stateRevIdx) {
		this.stateRevIdx = stateRevIdx;
	}
	
	/**
	 * Returns part of the document with static information,
	 * in a string presentation.
	 */
	public String getReadOnlyText() {
		Note nData = null;
		Note nArxiu = null;
		for (Note note: getNotes()) {
			if (note.getType().equals("nota_data")) {
				nData = note;
			}
			if  (note.getType().equals("nota_arxiu")) {
				nArxiu = note;
			}
		}
		String str = "Doc ID: " + getIdStr() + 
				"\nNumeració antiga: " + getNumbering() +
				"\nData: " + getDate().toString();
		if (nData != null) {
			str += " {ndata}\n";
		} else {
			str += "\n";
		}
		if (getPlace2() != null) {
			str += "Lloc: " + getPlace1() + 
					" - " + getPlace2() + "\n";
		} else if (getPlace1() != null) {
			str += "Lloc: " + getPlace1() + "\n";
		} else {
			str += "Lloc: -\n";
		}
		str += "Signatura A: " + getLibrary();
		if (getLibrary2().toString().length()>0) {
			str += "\nSignatura B: " + getLibrary2();
		}
		if (nArxiu != null) {
			str += " {narxiu}\n";
		} else {
			str += "\n";
		}
		return str.substring(0,str.length()-1);
	}

	/* IEditorInput implementation */
	
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.createFromFile(null, "icons/sample.png");
	}

	@Override
	public String getName() {
		return toString();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return toString();
	}
}
