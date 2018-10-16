package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiMusEntryReader {
	
	private static final String[] STARTERS = {"a:","b:","b2:","c:","c2:",
			"d:","d2:","e:","e2:","f:","g:","h:","i:","j:","k:","l:","g2:",
			"h2:","i2:","j2:","k2:","l2:","m:","n:","o:","p:","q:","r:","s"};
	private static final String[] FIELD_NAMES = {"Numbering","Year1","Year2",
			"Month1","Month2","Day1","Day2","Place1","Place2","Regest",
			"Archive","Series","Subseries1","Subseries2","Number","Page1",
			"Archive2","Series2","Subseries12","Subseries22","Number12","Page12",
			"Edition","Register","Citation","Transcription","Notes","Language",
			"Subjects"};
	
	public MiMusEntryReader() {
		
	}
	
	public MiMusEntry read(String path) throws MiMusFormatException {
		/* Read lines of text file with Java 8 Streams */
		List<String> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(path))) {
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not read MiMus document.");
		}
		
		MiMusEntry entry = new MiMusEntry();
		MiMusDate date = new MiMusDate();
		MiMusLibraryIdentifier ident1 = new MiMusLibraryIdentifier();
		MiMusLibraryIdentifier ident2 = new MiMusLibraryIdentifier();
		int regestIdx = -1;
		int editionsIdx = -1;
		int registersIdx = -1;
		int citationsIdx = -1;
		int transcriptionIdx = -1;
		int notesIdx = -1;
		int subjectsIdx = -1;
		for (int i=0; i<lines.size(); i++) {
			String content = lines.get(i).substring(2);
			String start = lines.get(i).substring(0, 2);
			for (int j=0; j<STARTERS.length; j++) {
				if (start.equals(STARTERS[j])) {
					try {
						switch (j) {
						case 0:
							entry.setNumbering(Integer.parseInt(content));
							break;
						case 1:
							date.setYear1(Integer.parseInt(content));
							break;
						case 2:
							date.setYear2(Integer.parseInt(content));
							break;
						case 3:
							date.setMonth1(Integer.parseInt(content));
							break;
						case 4:
							date.setMonth2(Integer.parseInt(content));
							break;
						case 5:
							date.setDay1(Integer.parseInt(content));
							break;
						case 6:
							date.setDay2(Integer.parseInt(content));
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
							editionsIdx = i;
							break;
						case 23:
							registersIdx = i;
							break;
						case 24:
							citationsIdx = i;
							break;
						case 25:
							transcriptionIdx = i;
							break;
						case 26:
							notesIdx = i;
							break;
						case 27:
							entry.setLanguage(content);
							break;
						case 28:
							subjectsIdx = i;
							break;
						}
					} catch (NumberFormatException e) {
						System.out.println("Could not read field " + FIELD_NAMES[j] + " properly, this field will be empty.");
					}
				}
			}
		}
		String regest = "";
		for (int i=regestIdx; i<lines.size(); i++) {
			if (lines.get(i).startsWith(STARTERS[9])) {
				regest += lines.get(i).substring(2);
			} else if (lines.get(i).startsWith(STARTERS[10])) {
				break;
			} else {
				regest +=lines.get(i);
			}
		}
		entry.setRegestText(regest);
		
		String transcription = "";
		for (int i=transcriptionIdx; i<lines.size(); i++) {
			if (lines.get(i).startsWith(STARTERS[25])) {
				transcription += lines.get(i).substring(2);
			} else if (lines.get(i).startsWith(STARTERS[26])) {
				break;
			} else {
				transcription +=lines.get(i);
			}
		}
		entry.setTranscriptionText(transcription);
		
		// TODO: read multiline fields
		entry.setDate(date);
		entry.setLibrary(ident1);
		entry.setLibrary2(ident2);
		
		// TODO: empty fields. what to do
		
//		/* f: regest */
//		String regest = "";
//		for (int j=i+1; j<lines.size() && !lines.get(j).startsWith("g:"); j++) {
//			if (j==i+1) {
//				regest += lines.get(j).substring(2);
//			} else {
//				regest += lines.get(j);
//			}
//			regest += "\n";
//		}
//		entry.setRegestText(regest);
//		
//		
//		/* p: transcription */
//		if (!lines.get(i).startsWith("p:")) {
//			throw new MiMusFormatException("Entry <p:> not found in their place.");
//		}
//		String transcription = lines.get(i++).substring(2);
//		while(i<lines.size() && !lines.get(i).startsWith("q:")) {
//			transcription += lines.get(i++) + "\n";
//		}
//		entry.setTranscriptionText(transcription);
		
		return entry;
	}
	
}
