package model;

import java.util.Arrays;

import editor.IllegalTextRangeException;

public abstract class Entity extends Unit {
	
	private String[] words;
	private int from;
	private int to;
	private int id;
	
	public Entity(String[] words, int id) {
		this.words = words;
		from = 0;
		to = 0;
		this.setId(id);
	}
	
	public Entity(String[] words, int from, int to, int id) {
		this.words = words;
		this.from = from;
		this.to = to;
		this.setId(id);
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
