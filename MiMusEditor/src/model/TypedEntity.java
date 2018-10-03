package model;

import java.util.Arrays;

import editor.Editor;

public class TypedEntity extends Entity {

	private int type;
	private int subtype;
	
	public TypedEntity(String[] words, int id) {
		super(words, id);
	}
	
	public TypedEntity(String[] words, int from, int to, int id) {
		super(words, from, to, id);
	}
	
	public TypedEntity(String[] words, int from, int to, int type, int subtype, int id) {
		super(words, from, to, id);
		this.type = type;
		this.subtype = subtype;
	}
	
	public TypedEntity(String[] words, int from, int to, String type, String subtype, int id) {
		super(words, from, to, id);
		this.type = Arrays.asList(Editor.ENTITY_TYPES).indexOf(type);
		if (this.type==0) {
			this.subtype = Arrays.asList(Editor.PERSON_TYPES).indexOf(subtype);
		} else if (this.type==2) {
			this.subtype = Arrays.asList(Editor.PLACE_TYPES).indexOf(subtype);
		} else {
			this.subtype = 0;
		}
		this.setId(id);
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
		return "From: " + getFromWord() 
				+ ", To: " + getToWord() 
				+ ", Type: " + getTypeWord().toString() 
				+ ", Subtype: " + getSubtypeWord().toString() 
				+ ", ID: " + String.valueOf(getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getFrom();
		result = prime * result + getId();
		result = prime * result + subtype;
		result = prime * result + getTo();
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypedEntity ent = (TypedEntity) obj;
		return getFrom()==ent.getFrom()
				&& getTo()==ent.getTo()
				&& getType()==ent.getType()
				&& getSubtype()==ent.getSubtype()
				&& getId()==ent.getId();
	}
}
