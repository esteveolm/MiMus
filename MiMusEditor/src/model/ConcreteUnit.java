package model;

import java.util.List;

public abstract class ConcreteUnit extends Unit {

	private List<? extends Unit> itsConcepts;
	
	public ConcreteUnit() {
		itsConcepts = null;
	}
	
	public ConcreteUnit(List<? extends Unit> itsConcepts) {
		this.setItsConcepts(itsConcepts);
	}

	public List<? extends Unit> getItsConcepts() {
		return itsConcepts;
	}

	public void setItsConcepts(List<? extends Unit> itsConcepts) {
		this.itsConcepts = itsConcepts;
	}
}
