package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Document;
import model.MiMusDate;
import model.MiMusLibraryIdentifier;
import model.Note;

/**
 * MiMusEntryReader reads a MiMus document in the txt format declared
 * by the specification, and translates it to a MiMus Document model
 * object.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusEntryReader {
	
	/* Headers and their field name associated */
	private static final String[] STARTERS = {"a:","b:","b2:","c:","c2:",
			"d:","d2:","e:","e2:","f:","g:","h:","i:","j:","k:","l:","g2:",
			"h2:","i2:","j2:","k2:","l2:","m:","n:","o:","p:","q:","r:","s:"};
	private static final String[] FIELD_NAMES = {"Numbering","Year1","Year2",
			"Month1","Month2","Day1","Day2","Place1","Place2","Regest",
			"Archive","Series","Subseries1","Subseries2","Number","Page1",
			"Archive2","Series2","Subseries12","Subseries22","Number12","Page12",
			"Edition","Register","Citation","Transcription","Notes","NotesData",
			"NotesSignatura"};
	
	/**
	 * Reads a MiMus txt document from a <path> and returns a MiMus
	 * Document object with all information from the txt transferred to
	 * the model.
	 */
	public Document read(String path) {
		/* Read lines of text file with Java 8 Streams */
		List<String> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not read MiMus document.");
		}
		
		Document entry = new Document();
		MiMusDate date = new MiMusDate();
		MiMusLibraryIdentifier ident1 = new MiMusLibraryIdentifier();
		MiMusLibraryIdentifier ident2 = new MiMusLibraryIdentifier();
		String editions = "";
		String registers = "";
		String citations = "";
		List<Note> notes = new ArrayList<>();
		int regestIdx = -1;
		int transcriptionIdx = -1;
		int notesIdx = -1;
		for (int i=0; i<lines.size(); i++) {
			if (lines.get(i).length()>=2) {		// Prevents from empty lines
				String content = lines.get(i).substring(lines.get(i).indexOf(':')+1).trim();
				String start = lines.get(i).substring(0, lines.get(i).indexOf(':')+1);
				for (int j=0; j<STARTERS.length; j++) {
					if (start.equals(STARTERS[j])) {
						try {
							switch (j) {
							case 0:
								entry.setNumbering(content);
								break;
							case 1:
								date.setYear1(content);
								break;
							case 2:
								date.setYear2(content);
								date.setInterval(true);
								break;
							case 3:
								date.setMonth1(content);
								break;
							case 4:
								date.setMonth2(content);
								date.setInterval(true);
								break;
							case 5:
								date.setDay1(content);
								break;
							case 6:
								date.setDay2(content);
								date.setInterval(true);
								break;
							case 7:
								entry.setPlace1(content);
								break;
							case 8:
								entry.setPlace2(content);
								break;
							case 9:
								regestIdx = i;
							case 10:
								ident1.setArchive(content);
								break;
							case 11:
								ident1.setSeries(content);
								break;
							case 12:
								ident1.setSubseries1(content);
								break;
							case 13:
								ident1.setSubseries2(content);
								break;
							case 14:
								ident1.setNumber(content);
								break;
							case 15:
								ident1.setPage(content);
								break;
							case 16:
								ident2.setArchive(content);
								break;
							case 17:
								ident2.setSeries(content);
								break;
							case 18:
								ident2.setSubseries1(content);
								break;
							case 19:
								ident2.setSubseries2(content);
								break;
							case 20:
								ident2.setNumber(content);
								break;
							case 21:
								ident2.setPage(content);
								break;
							case 22:
								editions = content;
								break;
							case 23:
								registers = content;
								break;
							case 24:
								citations = content;
								break;
							case 25:
								transcriptionIdx = i;
								break;
							case 26:
								notesIdx = i;
								break;
							case 27:
								if (notesIdx==-1) {
									notesIdx = i;
									break;
								}
							case 28:
								if (notesIdx==-1) {
									notesIdx = i;
									break;
								}
							}
						} catch (NumberFormatException e) {
							System.out.println("Could not read field " + FIELD_NAMES[j] + " properly, this field will be empty.");
						}
					}
				}
			}
		}
		entry.setDate(date);
		entry.setLibrary(ident1);
		entry.setLibrary2(ident2);
		entry.setEditions(editions);
		entry.setRegisters(registers);
		entry.setCitations(citations);
		
		String regest = "";
		for (int i=regestIdx; i<lines.size(); i++) {
			if (lines.get(i).startsWith(STARTERS[9])) {
				regest += lines.get(i).substring(3) + "\n";
			} else if (lines.get(i).startsWith(STARTERS[10])) {
				break;
			} else {
				regest +=lines.get(i) + "\n";
			}
		}
		entry.setRegestText(regest);
		
		String transcription = "";
		for (int i=transcriptionIdx; i<lines.size(); i++) {
			if (lines.get(i).startsWith(STARTERS[25])) {
				transcription += lines.get(i).substring(2) + "\n";
			} else if (lines.get(i).startsWith(STARTERS[26]) 
					|| lines.get(i).startsWith(STARTERS[27])
					|| lines.get(i).startsWith(STARTERS[28])) {
				/* Terminate if notes (q: / r: / s:) show up */
				break;
			} else {
				transcription +=lines.get(i) + "\n";
			}
		}
		entry.setTranscriptionText(transcription);
		
		/* Notes (q, r, s) just as a single note containing all text */
		if (notesIdx!=-1) {
			for (int i=notesIdx; i<lines.size(); i++) {
				if (lines.get(i).startsWith(STARTERS[26])
						|| lines.get(i).startsWith(STARTERS[27])
						|| lines.get(i).startsWith(STARTERS[28])) {
					notes.add(toNote(lines.get(i).substring(2), entry));
				} else {
					notes.add(toNote(lines.get(i), entry));
				}
			}
		}
		entry.setNotes(notes);
		
		entry.setStateAnnotIdx(0);
		entry.setStateRevIdx(0);
		entry.setLanguage(1);
		
		/* Read ID from path */
		String[] parts = path.split("/");
		String idStr = parts[parts.length-1].split("\\.")[0];	// This is regex, need to escape point
		entry.setId(Integer.parseInt(idStr));
		
		return entry;
	}
	
	/**
	 * Transforms a line of text inside the notes field into a
	 * Note object, preprocessing its type prequalifier (e.g. {nota}).
	 */
	private Note toNote(String text, Document entry) {
		String type = "nota";
		if (text.contains("{nota")) {
			type = "nota";
		} else if (text.contains("{ref")) {
			type = "referencia";
		} else if (text.contains("{nbib")) {
			type = "nota_bibliografica";
		} else if (text.contains("{ndata")) {
			type = "nota_data";
		} else if (text.contains("{narxiu")) {
			type = "nota_arxiu";
		}
		
		String note = text.substring(text.indexOf("}")+1);
		return new Note(0, type, note, entry);
	}
	
}
