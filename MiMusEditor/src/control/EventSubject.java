package control;

import java.util.List;

public interface EventSubject {
	
	public void attach(EventObserver o);
	
	public void detach(EventObserver o);
	
	public default void notifyObservers() {
		for (EventObserver o: getObservers()) {
			System.out.println("Notifying " + o.getClass().getName());
			o.update();
		}
	}
	
	public List<EventObserver> getObservers();
}
