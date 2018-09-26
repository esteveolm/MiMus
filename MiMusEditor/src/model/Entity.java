package model;

import java.util.Arrays;

import editor.Editor;
import editor.IllegalTextRangeException;

public class Entity extends Unit {
	
	private String[] words;
	private int from;
	private int to;
	private int type;
	private int subtype;
	
	public Entity(String[] words) {
		this.words = words;
		from = 0;
		to = 0;
		type = 0;
		subtype = 0;
	}
	
	public Entity(String[] words, int from, int to) {
		this.words = words;
		this.from = from;
		this.to = to;
		type = 0;
		subtype = 0;
	}
	
	public Entity(String[] words, int from, int to, int type, int subtype) {
		this.words = words;
		this.from = from;
		this.to = to;
		this.type = type;
		this.subtype = subtype;
	}
	
	public Entity(String[] words, int from, int to, String type, String subtype) {
		this.words = words;
		this.from = from;
		this.to = to;
		this.type = Arrays.asList(Editor.ENTITY_TYPES).indexOf(type);
		if (this.type==0) {
			this.subtype = Arrays.asList(Editor.PERSON_TYPES).indexOf(subtype);
		} else if (this.type==2) {
			this.subtype = Arrays.asList(Editor.PLACE_TYPES).indexOf(subtype);
		} else {
			this.subtype = 0;
		}
	}
	
	public int getFrom() {
		return from;
	}
	public String getFromWord() {
		return words[from];
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public String getToWord() {
		return words[to];
	}
	public void setTo(int to) {
		this.to = to;
	}
	public String getFullText() {
		return String.join(" ", words);
	}
	public String getText() throws IllegalTextRangeException {
		if (from>to) {
			throw new IllegalTextRangeException();
		}
		return String.join(" ", Arrays.copyOfRange(words, from, to+1));
	}
	public int getType() {
		return type;
	}
	public String getTypeWord() {
		return Editor.ENTITY_TYPES[type];
	}
	public int getSubtype() {
		return subtype;
	}
	public String getSubtypeWord() {
		if (type==0) {
			return Editor.PERSON_TYPES[subtype];
		} else if (type==2) {
			return Editor.PLACE_TYPES[subtype];
		} else {
			return "";
		}
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}
	
	public String toString() {
		return "From: " + getFromWord() + ", To: " + getToWord() + ", Type: " + getTypeWord().toString() + ", Subtype: " + getSubtypeWord().toString();
	}
}
