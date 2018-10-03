package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiMusEntryReader {
	
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
		
		/* A: regest */
		MiMusEntry entry = new MiMusEntry();
		if (!lines.get(0).startsWith("A:")) {
			throw new MiMusFormatException("First line does not contain <A:> entry.");
		}
		String regest = "";
		int i;
		for (i=0; i<lines.size() && !lines.get(i).startsWith("B:"); i++) {
			if (i==0) {
				regest += lines.get(0).substring(2);
			} else {
				regest += lines.get(i);
			}
			regest += "\n";
		}
		entry.setRegest(regest);
		
		/* B: transcription */
		if (!lines.get(i).startsWith("B:")) {
			throw new MiMusFormatException("Entry <B:> not found in their place.");
		}
		String transcription = lines.get(i++).substring(2);
		while(i<lines.size() && !lines.get(i).startsWith("C:")) {
			transcription += lines.get(i++) + "\n";
		}
		entry.setTranscription(transcription);
		return entry;
	}
	
}
