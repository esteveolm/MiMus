package model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * MiMusText represents a text in a MiMus Document (either a
 * regest or a transcription). It has methods that enable
 * decoration of the text as in the case of the transcription
 * on the UI.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusText {
	
	private String text;
	private String[] words;	/* Just <text> separated by whitespaces */
	
	public MiMusText(String text) {
		this.text = text;
		this.words = text.split(" ");
	}
	
	/**
	 * Translates from coordinates at the character level
	 * to the word level, returning them in a Point object where
	 * the x component is the start of a text selection and y
	 * is the ending.
	 * 
	 * This helps trimming user selections in the transcription to
	 * the words only, but it is a feature currently disabled in
	 * the project, because it led to some misbehaviors.
	 */
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
	
	/**
	 * Translates from coordinates at the word level
	 * to the character level, returning them in a Point object where
	 * the x component is the start of a text selection and y
	 * is the ending.
	 * 
	 * This helps trimming user selections in the transcription to
	 * the words only, but it is a feature currently disabled in
	 * the project, because it led to some misbehaviors.
	 */
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
	
	/**
	 * Return a list of integers which are the positions of spaces
	 * in the text. The start and ending positions are also added,
	 * i.e. 0 and text.length. Hence, the list is ordered ascending.
	 */
	private List<Integer> getSpacesInText() {
		List<Integer> spaces = new ArrayList<>();
		spaces.add(0);
		for (int idxSpace = text.indexOf(' '); 
				idxSpace>=0;
				idxSpace = text.indexOf(' ', idxSpace+1)) {
			spaces.add(idxSpace);
		}
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
