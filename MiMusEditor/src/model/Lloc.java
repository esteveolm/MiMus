package model;

import control.SharedResources;

public class Lloc extends Entity {
	
	private String nomComplet;
	private int regne;
	private int area;
	
	public Lloc() {}
	
	public Lloc(int id) {
		this(id, "", 0, 0);
	}
	
	public Lloc(int id, String nomComplet, int regne, int area) {
		this.setId(id);
		this.nomComplet = nomComplet;
		this.regne = regne;
		this.area = area;
	}

	@Override
	public String getLemma() {
		return getNomComplet();
	}
	
	@Override
	public String toString() {
		return getLemma();
	}

	/* Getters and setters */
	
	public String getNomComplet() {
		return nomComplet;
	}
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	public int getRegne() {
		return regne;
	}
	public String getRegneStr() {
		return SharedResources.REGNE[regne];
	}
	public void setRegne(int regne) {
		this.regne = regne;
	}
	public int getArea() {
		return area;
	}
	public String getAreaStr() {
		return SharedResources.AREA[area];
	}
	public void setArea(int area) {
		this.area = area;
	}
}
