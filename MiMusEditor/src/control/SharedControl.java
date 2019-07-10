package control;

import java.util.ArrayList;
import java.util.List;

import ui.ArtistaView;
import ui.BiblioView;
import ui.CasaView;
import ui.Editor;
import ui.GenereLiterariView;
import ui.InstrumentView;
import ui.LlocView;
import ui.OficiView;
import ui.PromotorView;

/**
 * 
 * A SharedControl object registers which MiMus UI components are
 * running. It follows a Singleton pattern that assures creation 
 * of only one object for the whole application, no matter how many 
 * components are in use.
 * 
 * The Singleton forces SharedControl to be constructed using the method
 * SharedControl.getInstance(), which provides lazy initialization.
 * 
 * By centralizing the references to these components, SharedControl
 * allows them to communicate with each other when they perform 
 * changes of state that affect others. This is achieved with an 
 * observer pattern that is defined in <EventSubject> and <EventObserver>.
 * 
 * TODO: factorize views into single array + indexes informing of each.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public final class SharedControl {
	
	private BiblioView biblioView;
	private ArtistaView artistaView;
	private InstrumentView instrumentView;
	private CasaView casaView;
	private PromotorView promotorView;
	private OficiView oficiView;
	private LlocView llocView;
	private GenereLiterariView genereView;
	private List<Editor> editors;
	
	/* Singleton instance of SharedResources */
	private static SharedControl instance = null;
	
	private SharedControl() {
		this.biblioView = null;
		this.artistaView = null;
		this.instrumentView = null;
		this.casaView = null;
		this.setPromotorView(null);
		this.llocView = null;
		this.editors = new ArrayList<>();
	}
	
	/**
	 * Creates a new SharedControl object if none has been declared yet,
	 * or returns the existing object if there is an instance created. This
	 * is the only way to instantiate this class, following the Singleton
	 * pattern.
	 */
	public static SharedControl getInstance() {
		if (instance == null) {
			instance = new SharedControl();
			System.out.println("created instance singleton of control.");
		}
		return instance;
	}
	
	/**
	 * Method overridden to always throw CloneNotSupportedException,
	 * avoiding that a copy of SharedResources object is made and the
	 * system ends up with several instances of it.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public BiblioView getBiblioView() {
		return biblioView;
	}
	
	/**
	 * When the BiblioView is opened, all existing Editors
	 * are subscribed to it, so they are informed of future
	 * changes in the global state.
	 */
	public void setBiblioView(BiblioView biblioView) {
		this.biblioView = biblioView;
		
		/* Subscribe all Editors to BiblioView */
		if (editors != null) {
			for (Editor e: editors) {
				this.biblioView.attach(e);
			}
		}
		System.out.println("prepared biblio for subscribers.");
	}
	
	public void unsetBiblioView() {
		this.biblioView = null;
	}
	
	public void setArtistaView(ArtistaView artistaView) {
		this.artistaView = artistaView;
		
		/* Subscribe all Editors to ArtistaView */
		if (editors != null) {
			for (Editor e: editors) {
				this.artistaView.attach(e);
			}
		}
		if (this.oficiView != null)
			this.oficiView.attach(this.artistaView);
		System.out.println("prepared ArtistaView for subscribers.");
	}
	
	public void unsetArtistaView() {
		if (this.oficiView != null)
			this.oficiView.detach(this.artistaView);
		this.artistaView = null;
	}
	
	public void setInstrumentView(InstrumentView instrumentView) {
		this.instrumentView = instrumentView;
		
		/* Subscribe all Editors to InstrumentView */
		if (editors != null) {
			for (Editor e: editors) {
				this.instrumentView.attach(e);
			}
		}
		if (this.oficiView != null)
			this.instrumentView.attach(this.oficiView);
		System.out.println("Prepared InstrumentView for subscribers.");
	}
	
	public void unsetInstrumentView() {
		this.instrumentView = null;
	}
	
	public void setCasaView(CasaView casaView) {
		this.casaView = casaView;
		
		/* Subscribe all Editors to CasaView */
		if (editors != null) {
			for (Editor e: editors) {
				this.casaView.attach(e);
			}
		}
		if (this.promotorView != null)
			this.casaView.attach(this.promotorView);
		System.out.println("Prepared CasaView for subscribers.");
	}

	public void unsetCasaView() {
		this.casaView = null;
	}
	
	public void setPromotorView(PromotorView promotorView) {
		this.promotorView = promotorView;
		
		/* Subscribe all Editors to PromotorView */
		if (editors != null) {
			for (Editor e: editors) {
				this.promotorView.attach(e);
			}
		}
		if (this.casaView != null)
			this.casaView.attach(this.promotorView);
		System.out.println("Prepared PromotorView for subscribers.");
	}
	
	public void unsetPromotorView() {
		if (this.casaView != null)
			this.casaView.detach(this.promotorView);
		this.promotorView = null;
	}
	
	public void setOficiView(OficiView oficiView) {
		this.oficiView = oficiView;
		
		/* Subscribe all Editors to OficiView */
		if (editors != null) {
			for (Editor e: editors) {
				this.oficiView.attach(e);
			}
		}
		if (artistaView != null)
			this.oficiView.attach(artistaView);
		if (instrumentView != null)
			this.instrumentView.attach(this.oficiView);
		System.out.println("Prepared OficiView for subscribers.");
	}
	
	public void unsetOficiView() {
		if (this.instrumentView != null)
			this.instrumentView.detach(this.oficiView);
		this.oficiView = null;
	}

	public void setLlocView(LlocView llocView) {
		this.llocView = llocView;
		
		/* Subscribe all Editors to LlocView */
		if (editors != null) {
			for (Editor e: editors) {
				this.llocView.attach(e);
			}
		}
		System.out.println("Prepared LlocView for subscribers.");
	}
	
	public void unsetLlocView() {
		this.llocView = null;
	}

	public void setGenereView(GenereLiterariView genereView) {
		this.genereView = genereView;
		
		/* Subscribe all Editors to GenereView */
		if (editors != null) {
			for (Editor e: editors) {
				this.genereView.attach(e);
			}
		}
		System.out.println("Prepared GenereLiterariView for subscribers.");
	}
	
	public void unsetGenereView() {
		this.genereView = null;
	}

	public List<Editor> getEditors() {
		return editors;
	}
	
	/**
	 * If the BiblioView is opened, when a new Editor is opened
	 * it is subscribed to the BiblioView, so it can inform the
	 * Editor of future changes in the global state.
	 */
	public void addEditor(Editor editor) {
		this.editors.add(editor);
		
		/* Subscribe new Editor to BiblioView */
		if (biblioView != null) {
			biblioView.attach(editor);
			System.out.println("Subscribed editor to BiblioView.");
		}
		if (artistaView != null) {
			artistaView.attach(editor);
			System.out.println("Subscribed editor to ArtistaView.");
		}
		if (instrumentView != null) {
			instrumentView.attach(editor);
			System.out.println("Subscribed editor to InstrumentView.");
		}
		if (casaView != null) {
			casaView.attach(editor);
			System.out.println("Subscribed editor to CasaView.");
		}
		if (promotorView != null) {
			promotorView.attach(editor);
			System.out.println("Subscribed editor to PromotorView.");
		}
		if (llocView != null) {
			llocView.attach(editor);
			System.out.println("Subscribed editor to LlocView");
		}
		if (genereView != null) {
			genereView.attach(editor);
			System.out.println("Subscribed editor to GenereLiterariView");
		}
	}
	
	/**
	 * When an Editor is removed, it is also unsubscribed from
	 * the BiblioView.
	 */
	public void removeEditor(Editor editor) {
		this.editors.remove(editor);
		
		/* Unsubscribe removed Editor from BiblioView */
		if (biblioView != null) {
			biblioView.detach(editor);
		}
		if (artistaView != null) {
			artistaView.detach(editor);
		}
		if (instrumentView != null) {
			instrumentView.detach(editor);
		}
		if (casaView != null) {
			casaView.detach(editor);
		}
		if (promotorView != null) {
			promotorView.detach(editor);
		}
		if (llocView != null) {
			llocView.detach(editor);
		}
		if (genereView != null) {
			genereView.detach(editor);
		}
	}
}
