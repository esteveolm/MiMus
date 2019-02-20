package model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

public class MiMusText {
	
	private String text;
	private String[] words;
	
	public MiMusText(String text) {
		this.text = text;
		this.words = text.split(" ");
	}
	
//	public Entity xmlElementToEntity(Element elem, boolean typed) {
//		int from = Integer.parseInt(elem.getElementsByTagName("from").item(0).getTextContent());
//		int to = Integer.parseInt(elem.getElementsByTagName("to").item(0).getTextContent());
//		int id = Integer.parseInt(elem.getElementsByTagName("entity_id").item(0).getTextContent());
//		if (typed) {
//			String type = elem.getElementsByTagName("type").item(0).getTextContent();
//			String subtype = elem.getElementsByTagName("subtype").item(0).getTextContent();
//			return new TypedEntity(words, from, to, type, subtype, id);
//		}
//		return new UntypedEntity(words, from, to, id);
//	}
	
	public Point fromCharToWordCoordinates(Point old) {
		List<Integer> spaces = getSpacesInText();
		/*
		 * Looks for between which spaces our selection indexes fall,
		 * which corresponds with a certain word whose index is returned.
		 */
		int from = 0;
		int to = 0;
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.x>=spaces.get(i) && old.x<spaces.get(i+1)) {
				from = i;
				break;
			}
		}
		for (int i=0; i<spaces.size()-1; i++) {
			if (old.y-1>=spaces.get(i) && old.y-1<=spaces.get(i+1)) {
				to = i;
				break;
			}
		}
		return new Point(from, to);
	}
	
	public Point fromWordToCharCoordinates(Point old) {
		int charIdx=0;
		int wordIdx=0;
		while (wordIdx++<old.x) {	// Advance charIdx until start of first word
			charIdx += words[wordIdx-1].length() + 1;	// +1 for the space
		}
		int newX = new Integer(charIdx);	// Fix start of first word
		charIdx += words[wordIdx-1].length() + 1;	// Advance first word
		while (wordIdx++<old.y) {	// Advance charIdx until start of last word
			charIdx += words[wordIdx-1].length() + 1;
		}
		
		if (old.y-old.x>0) {
			int newY = charIdx + words[wordIdx-1].length();	// Advance charIdx until end of last word
			return new Point(newX, newY);
		} else {
			return new Point(newX, charIdx-1);	// In this case we already advanced before
		}	
	}
	
	public List<Integer> getSpacesInText() {
		/* 
		 * Spaces contains the index of every space, besides the start
		 * and ending index of the full text, in ascending order.
		 */
		List<Integer> spaces = new ArrayList<>();
		spaces.add(0);
		for (int idxSpace = text.indexOf(' '); 
				idxSpace>=0;
				idxSpace = text.indexOf(' ', idxSpace+1)) {
			spaces.add(idxSpace);
		}
//		for (int idxSpace = 1;
//				idxSpace < text.length();
//				idxSpace++) {
//			if (text.charAt(idxSpace)==' ' || text.charAt(idxSpace)==',' || 
//					text.charAt(idxSpace)=='.')
//				spaces.add(idxSpace);
//		}
		spaces.add(text.length());
		return spaces;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String[] getWords() {
		return words;
	}
	public void setWords(String[] words) {
		this.words = words;
	}
}
