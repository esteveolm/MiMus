package model;

public class UntypedEntity extends Entity {
	public UntypedEntity(String[] words, int id) {
		super(words, id);
	}
	
	public UntypedEntity(String[] words, int from, int to, int id) {
		super(words, from, to, id);
	}
	
	public String toString() {
		return "From: " + getFromWord() 
				+ ", To: " + getToWord() 
				+ ", ID: " + String.valueOf(getId());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getFrom();
		result = prime * result + getId();
		result = prime * result + getTo();
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
		UntypedEntity ent = (UntypedEntity) obj;
		return getFrom()==ent.getFrom()
				&& getTo()==ent.getTo()
				&& getId()==ent.getId();
	}
}
