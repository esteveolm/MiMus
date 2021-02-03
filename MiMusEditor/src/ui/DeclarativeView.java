package ui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import model.Bibliography;
import model.Document;
import model.Entity;
import model.HierarchicalUnit;
import model.Unit;
import persistence.DaoNotImplementedException;
import persistence.DocumentDao;
import persistence.UnitDao;
import util.LabelPrinter;

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

	private boolean isNew;
	private int selectedId;
	private TableViewer tv;
	private Button btnAdd;
	private Button btnEdit;
	private Button btnDel;
	private Button btnRefresh;
	private Button btnSave;
	private Button btnCancel;
	private Text annotationsText;
	private List<U> units;
	
	/**
	 * DeclarativeViews have associated a connection to the DB.
	 */
	public DeclarativeView() {
		super();
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
				
		developForm(form);
						
		setControlsEnabled(false);
	}
	
	/**
	 * Action on button Add
	 * 
	 *  Clears control values to allow input of a new data.
	 *  Disables ADD, EDIT, DELETE and SAVE buttons. Enables CANCEL button.
	 *  Enables form controls and sets a listener on each to enable the SAVE button
	 *  Clears selection in tableViewer
	 *  Binds listeners to controlsList to enable the SAVE button
	 *  
	 *  subclasses may override to extend behavior
	 */
	public void addAction() {
		clearControlValues();
		isNew = true;
		wasModified = false;
		btnAdd.setEnabled(false);
		btnEdit.setEnabled(false);					
		btnDel.setEnabled(false);

		btnSave.setEnabled(false);
		btnCancel.setEnabled(true);
		
		tv.getTable().deselectAll();	/* Add mode = nothing selected */
		setSelectedId(0);
		annotationsText.setText("");
		
		for(Control c: controlsList) {
			c.setEnabled(true);
			if(c instanceof Text) {
				((Text) c).addModifyListener(modifyTextListener);
			} else if(c instanceof Combo) {
				((Combo) c).addSelectionListener(comboSelectionListener);
			} else {
				throw new Error("Unsupported control type "+c.getClass().getSimpleName());
			}
		}		
	}
	
	
	public void editAction() {
		isNew = false;
		wasModified = false;
		btnAdd.setEnabled(false);
		btnEdit.setEnabled(false);
		btnSave.setEnabled(false);
		btnCancel.setEnabled(true);				
		for(Control c: controlsList) {
			c.setEnabled(true);
			if(c instanceof Text) {
				((Text) c).addModifyListener(modifyTextListener);
			} else if(c instanceof Combo) {
				((Combo) c).addSelectionListener(comboSelectionListener);
			} else {
				throw new Error("Unsupported control type "+c.getClass().getSimpleName());
			}
		}		
	}
	
	public void refreshAction() {
		try {
			setUnits(retrieveUnits());
		} catch(SQLException e1) {
			e1.printStackTrace();
		}
		
		tv.setInput(getUnits());
		tv.refresh();
		setSelectedId(0);
		
		btnAdd.setEnabled(true);
		btnEdit.setEnabled(false);
		btnDel.setEnabled(false);
		btnSave.setEnabled(false);
		btnCancel.setEnabled(false);
		
		/* Empty label for document annotations */
		annotationsText.setText("");
		setControlsEnabled(false);
		wasModified=false;
	}
	
	/**
	 * List of View Controls. Subclasses are responsible to fill with Entity widgets. 
	 */
	private List<Control> controlsList = new ArrayList<>();
	
	public List<Control> getControlsList() {
		return controlsList;
	}
	
	/**
	 * Creates and adds a Label and Text widgets to a parent composite.
	 * The created Text is added to controlsList to manage its enabled/disabled status.
	 * 
	 * @param parent
	 * @param label
	 * @return
	 */
	public Text addTextControl(Composite parent, String label) {
		Label widgetLabel = new Label(parent, LABEL_FLAGS);
		widgetLabel.setText(label);
		Text widgetText = new Text(parent, TEXT_FLAGS );
		widgetText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getControlsList().add(widgetText);
		return widgetText;
	}

	public Text addTextAreaControl(Composite parent, String label, int height) {
		Label widgetLabel = new Label(parent, LABEL_FLAGS);
		widgetLabel.setText(label);
		Text widgetText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = height;
		widgetText.setLayoutData(gd);
		getControlsList().add(widgetText);
		return widgetText;
	}

	public Combo addComboControl(Composite parent, String label, String... items) {
		Label widgetLabel = new Label(parent, LABEL_FLAGS);
		widgetLabel.setText(label);
		Combo widgetCombo = new Combo(parent, COMBO_FLAGS);
		widgetCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		widgetCombo.setItems(items);
		getControlsList().add(widgetCombo);
		return widgetCombo;
		
	}
	
	
	
	/** Sets Enabled for all Controls in controlsList */
	public void setControlsEnabled(boolean enabled) {
		for(Control c: controlsList) {
			c.setEnabled(enabled);
		}
		if(!enabled) {
			btnCancel.setEnabled(false);
			btnSave.setEnabled(false);
		}
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
	 * Draws buttons to add entity, clear form and
	 * deselect entity.
	 */
	public void addButtons(Composite parent) {
		/* Form buttons */
		Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setBackground(parent.getBackground());
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttons.setLayout(new GridLayout(6,false));
		
		
		btnAdd = new Button(buttons, BUTTON_FLAGS);
		btnAdd.setText("Add " + getViewName());
		btnEdit = new Button(buttons, BUTTON_FLAGS);
		btnEdit.setText("Edit "+getViewName());
		btnEdit.setEnabled(false);		
		btnDel = new Button(buttons, BUTTON_FLAGS);
		btnDel.setText("Delete " + getViewName());
		btnDel.setEnabled(false);		
		btnRefresh = new Button(buttons, SWT.PUSH | SWT.CENTER);
		btnRefresh.setText("Refresh");
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(wasModified && !MessageDialog.openConfirm(null, "Current entity was modified", "Do you want to discard changes?")) {
					return;
				}
				refreshAction();
				wasModified = false;
			}
		});


		btnSave = new Button(buttons, BUTTON_FLAGS);
		btnSave.setText("Save");
		btnSave.setEnabled(false);
		btnSave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		btnCancel = new Button(buttons, BUTTON_FLAGS);
		btnCancel.setText("Cancel");
		btnCancel.setEnabled(false);

		
		btnEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editAction();
			}			
		});

		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAction();								
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!MessageDialog.openConfirm(null, "Delete "+getViewName(), "Do you want to delete the selected entity?")) {
					return;
				}
				deleteAction();
			}
		});


		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(wasModified) {
					boolean success = saveEntity();
					if(!success) {
						System.out.println("Entity not saved");
						return;
					}
				}
				btnAdd.setEnabled(true);
				btnEdit.setEnabled(true);					
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);				
				for(Control c: controlsList) {
					c.setEnabled(false);
					if(c instanceof Text) {
						((Text) c).removeModifyListener(modifyTextListener);
					} else if(c instanceof Combo) {
						((Combo) c).removeSelectionListener(comboSelectionListener);
					} else {
						throw new Error("Unsupported control type "+c.getClass().getSimpleName());
					}
				}				
				
			}
		});

		
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(wasModified && !MessageDialog.openConfirm(null, "Current entity was modified", "Do you want to discard changes?")) {
					return;
				}
				Object o = getTv().getStructuredSelection().getFirstElement();
				if (o != null) {
					fillFieldsFromSelection((U) o);
				} else {
					clearControlValues();
				}
				btnAdd.setEnabled(true);
				btnEdit.setEnabled(getSelectedId()!=0);					
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);				
				for(Control c: controlsList) {
					c.setEnabled(false);
					if(c instanceof Text) {
						((Text) c).removeModifyListener(modifyTextListener);
					} else if(c instanceof Combo) {
						((Combo) c).removeSelectionListener(comboSelectionListener);
					} else {
						throw new Error("Unsupported control type "+c.getClass().getSimpleName());
					}
				}
				wasModified = false;
				
			}

		});
		
	}
	
	public final boolean saveEntity() {
		U newEntity = getUnitToSave();
		
		if(newEntity==null) {			
			return false;	// Entity has errors: Do not save
		}
		
		if (isNew) {
			/* Add new entity */
			try {
				int id = getDao().insert(newEntity);
				if (id>0) {
					newEntity.setId(id);
					getUnits().clear();
					getUnits().addAll(getDao().selectAll());
					System.out.println(getViewName()+" created successfully.");
					getTv().refresh();
					wasModified = false;
					selectEntityInTable((U) newEntity);
					wasModified = false;
				} else {
					MessageDialog.openError(null, "Save error", "DAO: Could not insert "+getViewName()+" into DB.");
				}
			} catch (SQLException e2) {
				if (e2.getSQLState().equals("42000")) {
					System.out.println("Disconnected exception.");
					MessageDialog.openError(null, "Save error", "You must be connected to perform changes to the DB.");
				} else {
					e2.printStackTrace();
					MessageDialog.openError(null, "Save error", "SQLException: Could not insert "+getViewName()+" into DB. \n"+e2.getMessage());
				}
			} catch (DaoNotImplementedException e1) {
				e1.printStackTrace();
				MessageDialog.openError(null, "Save error", "Insert operation not implemented, this should never happen.");
			}
		} else {
			/* Update values from selected entity */
			try {
				/* Recover ID from selection */
				int currentID = getSelectedId();
				if(newEntity instanceof HierarchicalUnit) {
					((HierarchicalUnit) newEntity).setSpecificId(getSelectedId());					
				} else {
					newEntity.setId(getSelectedId());
				}
				getDao().update(newEntity);
				getUnits().clear();
				getUnits().addAll(getDao().selectAll());
				System.out.println(getViewName()+" updated successfully.");
				getTv().refresh();
				wasModified = false;
				selectEntityInTable((U) newEntity);
				wasModified = false;
				this.setSelectedId(currentID);
			} catch (SQLException e2) {
				if (e2.getSQLState()!=null && e2.getSQLState().equals("42000")) {
					System.out.println("Disconnected exception.");
					MessageDialog.openError(null, "Save error", "You must be connected to perform changes to the DB.");
				} else {
					e2.printStackTrace();
					MessageDialog.openError(null, "Save error", "SQLException: Could not update "+getViewName()+" to DB.\n"+e2.getMessage());
				}
			} catch (DaoNotImplementedException e1) {
				e1.printStackTrace();
				MessageDialog.openError(null, "Save error", "Update operation not implemented, this should never happen.");
			}
		}
		return (wasModified==false);  // success
	};
	
	abstract U getUnitToSave();

	public boolean deleteAction() {
		
		boolean deleted = false;
		U art = (U) ((IStructuredSelection) getTv().getSelection()).getFirstElement();
		if (art==null) {
			MessageDialog.openError(null, "Delete error", "You must select a "+getViewName()+" in order to remove it.");
		} else {
			try {
				getDao().delete(art);
				getUnits().clear();
				getUnits().addAll(getDao().selectAll());
				System.out.println(getViewName()+" deleted successfully.");
				getTv().refresh();
				deleted = true;
			} catch (SQLIntegrityConstraintViolationException e1) {
				MessageDialog.openError(null, "Delete error", "Cannot delete Entity in use.");
			} catch (SQLException e2) {
				if (e2.getSQLState().equals("42000")) {
					MessageDialog.openError(null, "Delete error", "You must be connected to perform changes to the DB.");
				} else {
					e2.printStackTrace();
					MessageDialog.openError(null, "Delete error", "Could not delete "+getViewName()+" from DB: "+e2.getMessage());
				}
			}
		}
		return deleted;
	}


	protected abstract UnitDao<U> getDao() throws SQLException;

	public void clearControlValues() {
		for(Control c: controlsList) {
			c.setEnabled(false);
			if(c instanceof Text) {
				((Text) c).setText("");
			} else if(c instanceof Combo) {
				((Combo) c).select(0);
			} else {
				throw new Error("Unsupported control type "+c.getClass().getSimpleName());
			}
		}				
	}
	
	/** True when the current Unit was modified */
	protected boolean wasModified = false;
	
	private ModifyListener modifyTextListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			wasModified = true;
			btnSave.setEnabled(true);
			btnCancel.setEnabled(true);			
		}
	};
	
	private SelectionListener comboSelectionListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			wasModified = true;
			btnSave.setEnabled(true);
			btnCancel.setEnabled(true);			
		}
		
	};
	
	
	public void createEditAction() {
		/* Select a table row to enter edit mode with the row selected */
		getTv().addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				/* Unchecked warning because selection is Object type */
				Object o = getTv().getStructuredSelection().getFirstElement();
				if (o != null) {
					/* User selected a row from the table */
					if(o instanceof Entity) {
						setSelectedId(((Entity) o).getSpecificId());	/* Save ID */
					} else {
						setSelectedId(((Unit) o).getId());	/* Save ID */
					}
					
					System.out.println("ID is " + getSelectedId());
					fillFieldsFromSelection((U) o);
					
					setControlsEnabled(false);
					btnEdit.setEnabled(true);
					btnDel.setEnabled(true);
					
					/* Tell documents where entity is annotated */
					fillAnnotationsLabel((U) o);
				} else {
					/* After an entity is edited, no row selected */ 
					
					setSelectedId(0);
					clearControlValues();
					
					/* Empty label for document annotations */
					annotationsText.setText("");
				}
				wasModified = false;
			}
		});
	}
	
	/**
	 * Given a unit of the type of the view, fills the fields of
	 * the form with the attributes of the entity.
	 */
	protected abstract void fillFieldsFromSelection(U unit);
	
	/**
	 * Draws label that says what documents have annotations of a
	 * certain unit.
	 */
	protected void fillAnnotationsLabel(U unit) {
		List<Document> docs = new ArrayList<>();
		try {
			if(unit instanceof Bibliography) {
				docs = new DocumentDao().selectWhereBiblio(unit.getId());
			} else {
				docs = new DocumentDao().selectWhereEntity(unit.getId());								
			}
			
		} catch (SQLException e) {
			System.out.println("Could not retrieve documents where entity "+getViewName()+" is.");
			e.printStackTrace();
		}
		String ids = "";
		for (Document doc : docs) {
			ids += doc.getIdStr() + ", ";
		}
		if (ids.length()>2) {
			ids = ids.substring(0, ids.length()-2);
			annotationsText.setText("Used in documents: " + ids);
			annotationsText.getParent().layout();
		} else {
			annotationsText.setText("Used in 0 documents.");
		}
	}
	
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
	public void selectEntityInTable(Unit ent) {
		if(wasModified && !MessageDialog.openConfirm(null, "Current entity was modified", "Changing selected entity you will loose all changes. Do you want to discard changes?")) {
			return;
		}
		for (int i=0; i<getUnits().size(); i++) {
			U thisEnt = (U) getTv().getElementAt(i);
			if (thisEnt.getId() == ent.getId() || ( thisEnt instanceof Entity &&  ent instanceof Entity && ((Entity)thisEnt).getSpecificId() == ((Entity)ent).getSpecificId())) {
				getTv().getTable().select(i);
				fillFieldsFromSelection(thisEnt);
				setControlsEnabled(false);
				btnEdit.setEnabled(true);
				btnDel.setEnabled(true);				
				fillAnnotationsLabel(thisEnt);
				getTv().reveal(thisEnt);
				isNew = false;
				wasModified=false;
				if(thisEnt instanceof Entity) {
					setSelectedId(((Entity) thisEnt).getSpecificId());					
				} else {
					setSelectedId(thisEnt.getId());
				}				
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
	private int getSelectedId() {
		return selectedId;
	}
	private void setSelectedId(int selectedId) {
		this.selectedId = selectedId;
	}
}
