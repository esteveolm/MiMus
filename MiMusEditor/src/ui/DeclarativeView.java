package ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

import control.EventObserver;
import control.EventSubject;
import control.SharedControl;
import model.Document;
import model.Entity;
import persistence.DocumentDao;
import util.DBUtils;

public abstract class DeclarativeView<E extends Entity> extends ViewPart 
		implements EventSubject, EventObserver {
	
	final int LABEL_FLAGS = SWT.VERTICAL;
	final int TEXT_FLAGS = SWT.SINGLE | SWT.WRAP | SWT.SEARCH;
	final int COMBO_FLAGS = SWT.DROP_DOWN | SWT.READ_ONLY;
	final int BUTTON_FLAGS = SWT.PUSH | SWT.CENTER;
	
	final String STATE_ADD = "Adding a new entity";
	final String STATE_EDIT = "Editing an existing entity";
	
	final String BUTTON_ADD = "Add " + getViewName();
	final String BUTTON_EDIT = "Edit " + getViewName();
	
	private boolean stateAdd;
	private int selectedId;
	private Connection conn;
	private SharedControl control;
	private List<EventObserver> observers;
	private TableViewer tv;
	protected Button btnAdd;
	protected Button btnClr;
	protected Button btnDes;
	protected Label stateLabel;
	protected Text annotationsText;
	
	public DeclarativeView() {
		super();
		setObservers(new ArrayList<>());
		setControl(SharedControl.getInstance());
		setStateAdd(true);
		setSelectedId(0);
		
		try {
			setConnection(DBUtils.connect());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not load entities from DB.");
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledForm form = initForm(parent);
		developForm(form);
	}
	
	public abstract String getViewName();
	
	private ScrolledForm initForm(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Declare " + getViewName() + " Entity");
		form.getBody().setLayout(new GridLayout());
		return form;
	}
	
	public abstract void developForm(ScrolledForm form);
	
	public void addAnnotationsLabel(Composite parent, GridData gd) {
		annotationsText = new Text(parent, 
				SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
		annotationsText.setText("");
		annotationsText.setLayoutData(gd);
	}
	
	public void addStateLabel(Composite parent) {
		/* On creation, view is in ADD mode */
		stateLabel = new Label(parent, LABEL_FLAGS);
		stateLabel.setText(STATE_ADD);
	}
	
	public void addButtons(Composite parent) {
		/* Form buttons */
		btnAdd = new Button(parent, BUTTON_FLAGS);
		btnAdd.setText(BUTTON_ADD);	/* On creation, view is in ADD mode */
		btnClr = new Button(parent, BUTTON_FLAGS);
		btnClr.setText("Clear fields");
		btnDes = new Button(parent, BUTTON_FLAGS);
		btnDes.setText("Back to Insert mode");
	}
	
	public void createTableActions() {
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
		
		/* Select a table row to enter edit mode with the row selected */
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				/* Unchecked warning because selection is Object type */
				Object o = tv.getStructuredSelection().getFirstElement();
				if (o != null) {
					/* User selected a row from the table, move to Edit mode */
					setStateAdd(false);
					setSelectedId(((Entity) o).getSpecificId());	/* Save ID */
					System.out.println("ID is " + getSelectedId());
					stateLabel.setText(STATE_EDIT);
					btnAdd.setText(BUTTON_EDIT);
					fillFieldsFromSelection((E) o);
					
					/* Tell documents where entity is annotated */
					fillAnnotationsLabel((E) o);
				} else {
					/* After an entity is edited, back to add mode */ 
					setStateAdd(true);
					setSelectedId(0);
					stateLabel.setText(STATE_ADD);
					btnAdd.setText(BUTTON_ADD);
					
					/* Empty label for document annotations */
					annotationsText.setText("");
				}
			}
		});
	}
	
	protected abstract void fillFieldsFromSelection(E ent);

	
	private void fillAnnotationsLabel(E entity) {
		System.out.println("Filling.");
		List<Document> docs = new ArrayList<>();
		try {
			docs = new DocumentDao(conn)
					.selectWhereEntity(entity.getId());
		} catch (SQLException e) {
			System.out.println("Could not retrieve documents where entity is.");
			e.printStackTrace();
		}
		String ids = "";
		for (Document doc : docs) {
			ids += doc.getIdStr() + ", ";
		}
		if (ids.length()>2) {
			ids = ids.substring(0, ids.length()-2);
			annotationsText.setText("Used in documents: " + ids);
		} else {
			annotationsText.setText("Used in 0 documents.");
		}
	}
	
	@Override
	public void attach(EventObserver o) {
		getObservers().add(o);
	}
	
	@Override
	public void detach(EventObserver o) {
		getObservers().remove(o);
	}
	
	@Override
	public List<EventObserver> getObservers() {
		return observers;
	}
	
	@Override
	public void setFocus() {}
	
	
	/* Getters and setters */
	public SharedControl getControl() {
		return control;
	}
	public void setControl(SharedControl control) {
		this.control = control;
	}
	public void setObservers(List<EventObserver> observers) {
		this.observers = observers;
	}
	public TableViewer getTv() {
		return tv;
	}
	public void setTv(TableViewer tv) {
		this.tv = tv;
	}
	public Connection getConnection() {
		return conn;
	}
	public void setConnection(Connection conn) {
		this.conn = conn;
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
