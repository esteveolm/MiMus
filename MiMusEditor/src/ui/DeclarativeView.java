package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import model.Entity;
import model.Unit;

/**
 * DeclarativeView is any Eclipse View of MiMus application that allows
 * to declare MiMus Entities. In principle, there is one of these views
 * for every entity declared. All of these must be implemented by
 * inheriting from this class.
 * 
 * DeclarativeViews contain a form that allows to fill the fields that
 * make an Entity of that type. They also contain a table showing all
 * entities of that type, and control buttons.
 * 
 * DeclarativeViews have two functions: to insert new entities and to
 * update existing entities. Hence, sometimes we talk of a state of the
 * view, which can be ADD or EDIT.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <E>
 */
public abstract class DeclarativeView<U extends Unit> extends ViewPart {
	
	/* SWT flags grouped together based on use cases of DeclarativeViews */
	final int LABEL_FLAGS = SWT.VERTICAL;
	final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
	final int COMBO_FLAGS = SWT.DROP_DOWN | SWT.READ_ONLY;
	final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
	final int REFERENCE_FLAGS = SWT.MULTI | SWT.WRAP | SWT.VERTICAL;

	/* Texts of state */
	final String STATE_ADD = "Adding a new entity";
	final String STATE_EDIT = "Editing an existing entity";
	final String BUTTON_ADD = "Add " + getViewName();
	final String BUTTON_EDIT = "Edit " + getViewName();
	
	private boolean stateAdd;
	private int selectedId;
	private TableViewer tv;
	protected Button btnAdd;
	protected Button btnClr;
	protected Button btnDes;
	protected Label stateLabel;
	protected Text annotationsText;
	private List<U> units;
	
	/**
	 * DeclarativeViews have associated a connection to the DB.
	 */
	public DeclarativeView() {
		super();
		setStateAdd(true);
		setSelectedId(0);
	}

	/**
	 * Creates view, consisting of a Refresh button, a form
	 * for introduction of new entities, and a table of
	 * declared entities.
	 */
	@Override
	public void createPartControl(Composite parent) {
		ScrolledForm form = initForm(parent);
		
		Button refreshBtn = new Button(form.getBody(), SWT.PUSH | SWT.CENTER);
		refreshBtn.setText("Refresh");
		
		developForm(form);
		
		refreshBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshAction();
			}
		});
	}
	
	public void refreshAction() {
		try {
			setUnits(retrieveUnits());
		} catch(SQLException e1) {
			e1.printStackTrace();
		}
		
		tv.setInput(getUnits());
		tv.refresh();
		setStateAdd(true);
		setSelectedId(0);
		stateLabel.setText(STATE_ADD);
		btnAdd.setText(BUTTON_ADD);
		
		/* Empty label for document annotations */
		annotationsText.setText("");
	}
	
	/**
	 * Returns the entity name associated to this view. 
	 */
	public abstract String getViewName();
	
	/**
	 * Initializes a ScrolledForm object that facilitates view
	 * creation.
	 */
	private ScrolledForm initForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Declare " + getViewName() + " Entity");
		form.getBody().setLayout(new GridLayout());
		return form;
	}
	
	/**
	 * Implementation of the form for a specific entity must go in
	 * this method.
	 */
	public abstract void developForm(ScrolledForm form);
	
	/**
	 * Draws annotations label in the declarative views.
	 */
	public void addAnnotationsLabel(Composite parent, GridData gd) {
		annotationsText = new Text(parent, 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		annotationsText.setText("");
		annotationsText.setLayoutData(gd);
	}
	
	/**
	 * Draws the label for state in the declarative views.
	 */
	public void addStateLabel(Composite parent) {
		/* On creation, view is in ADD mode */
		stateLabel = new Label(parent, LABEL_FLAGS);
		stateLabel.setText(STATE_ADD);
	}
	
	/**
	 * Draws buttons to add entity, clear form and
	 * deselect entity.
	 */
	public void addButtons(Composite parent) {
		/* Form buttons */
		btnAdd = new Button(parent, BUTTON_FLAGS);
		btnAdd.setText(BUTTON_ADD);	/* On creation, view is in ADD mode */
		btnClr = new Button(parent, BUTTON_FLAGS);
		btnClr.setText("Clear fields");
		btnDes = new Button(parent, BUTTON_FLAGS);
		btnDes.setText("Back to Insert mode");
	}
	
	/**
	 * Contains listeners with actions related to the buttons.
	 */
	public void createDeselectAction() {
		/* Click deselect button to go back to insert mode */
		btnDes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!stateAdd) {
					tv.getTable().deselectAll();	/* Add mode = nothing selected */
					setStateAdd(true);
					setSelectedId(0);
					stateLabel.setText(STATE_ADD);
					btnAdd.setText(BUTTON_ADD);
					annotationsText.setText("");
				}
			}
		});
	}
	
	public abstract void createEditAction();
	
	/**
	 * Given a unit of the type of the view, fills the fields of
	 * the form with the attributes of the entity.
	 */
	protected abstract void fillFieldsFromSelection(U unit);
	
	/**
	 * Draws label that says what documents have annotations of a
	 * certain unit.
	 */
	protected abstract void fillAnnotationsLabel(U unit);
	
	/**
	 * Downloads from DB the list of units declared in this view.
	 */
	public abstract List<U> retrieveUnits() throws SQLException;
	
	/**
	 * Returns the list of units declared in this view, which is
	 * not necessarily updated wrt the DB.
	 */
	public List<U> getUnits() {
		return units;
	}
	
	/**
	 * Updates the list of units declared in this view.
	 */
	public void setUnits(List<U> units) {
		this.units = units;
	}
	
	/**
	 * Given the table in the view, selects the row corresponding
	 * to Entity <ent>.
	 */
	@SuppressWarnings("unchecked")
	public void selectEntityInTable(Entity ent) {
		for (int i=0; i<getUnits().size(); i++) {
			Entity thisEnt = (Entity) getTv().getElementAt(i);
			if (thisEnt.getId() == ent.getId()) {
				getTv().getTable().select(i);
				fillFieldsFromSelection((U) getTv().getElementAt(i));
				break;
			}
		}
	}
	
	@Override
	public void setFocus() {}
	
	/* Getters and setters */
	public TableViewer getTv() {
		return tv;
	}
	public void setTv(TableViewer tv) {
		this.tv = tv;
	}
	public boolean isStateAdd() {
		return stateAdd;
	}
	public void setStateAdd(boolean state) {
		this.stateAdd = state;
	}
	public int getSelectedId() {
		return selectedId;
	}
	public void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
	}
}
