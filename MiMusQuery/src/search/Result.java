package search;

import model.Entity;

public class Result {
	
	private Entity entity;
	private String document;
	
	public Result(Entity entity, String document) {
		this.entity = entity;
		this.document = document;
	}
	
	public Entity getEntity() {
		return entity;
	}
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	
	public String toString() {
		return entity.toString() + " - Doc: " + document;
	}
}
