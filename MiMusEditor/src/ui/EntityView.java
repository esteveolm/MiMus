package ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import model.Document;
import model.Entity;
import persistence.DocumentDao;

public abstract class EntityView<E extends Entity> extends DeclarativeView<E> {
	
	public abstract String getViewName();

	public abstract void developForm(ScrolledForm form);

	protected abstract void fillFieldsFromSelection(E unit);

	public abstract List<E> retrieveUnits() throws SQLException;
	
	@Override
	protected void fillAnnotationsLabel(E unit) {
		List<Document> docs = new ArrayList<>();
		try {
			docs = new DocumentDao()
					.selectWhereEntity(unit.getId());
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
	
	public void createEditAction() {
		/* Select a table row to enter edit mode with the row selected */
		getTv().addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				/* Unchecked warning because selection is Object type */
				Object o = getTv().getStructuredSelection().getFirstElement();
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
}
