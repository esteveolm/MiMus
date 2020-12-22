package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import model.Lloc;
import persistence.LlocDao;
import persistence.UnitDao;
import ui.table.LlocTableViewer;

/**
 * Declarative view for Lloc entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class LlocView extends EntityView<Lloc> {

	/* Form fields */
	private Text textNomComplet;
	private Combo comboArea;
	private Combo comboRegne;
	private Text textObs;
	
	public LlocView() {
		super();
		
		try {
			setUnits(new LlocDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load llocs from DB.");
		}
	}
	
	@Override
	public void developForm(Form form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);

		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		textNomComplet = addTextControl(sectAdd.getParent(), "Nom complet:");
		comboArea = addComboControl(sectAdd.getParent(), "Àrea:", Lloc.AREES);
		/* Default selection at start lets comboRegne know what to load */
		comboArea.select(0);	
		
		/* Regne: option field */
		comboRegne = addComboControl(sectAdd.getParent(), "Regne:", Lloc.REGNES[0]);
		comboRegne.setItems(Lloc.REGNES[0]);	/* At start, comboFamily at 0 */
		comboRegne.select(0);
		
		/* When comboArea changes, update comboRegne options */
		comboArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboRegne.setItems(Lloc.REGNES[comboArea.getSelectionIndex()]);
				comboRegne.select(0);
			}
		});
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		
		/* Table for Llocs creation */
		Section sectTable = new Section(form.getBody(), 0);
				
		LlocTableViewer llocHelper = 
				new LlocTableViewer(sectTable.getParent(), getUnits());
		setTv(llocHelper.createTableViewer());	
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createEditAction();
	}
	
	@Override
	protected Lloc getUnitToSave() {

				/* Selecting nothing == Selection index 0 ("unmarked") */
				if (comboRegne.getSelectionIndex() == -1) {
					comboRegne.select(0);
				} 
				if (comboArea.getSelectionIndex() == -1) {
					comboArea.select(0);
				}
				
				Lloc lloc = new Lloc(
						0, 0,
						textNomComplet.getText(), 
						comboRegne.getSelectionIndex(), 
						comboArea.getSelectionIndex(),
						textObs.getText());
				return lloc;
	}
		
	@Override
	public String getViewName() {
		return "Lloc";
	}

	@Override
	protected void fillFieldsFromSelection(Lloc ent) {
		textNomComplet.setText(ent.getNomComplet());
		comboArea.select(ent.getArea());
		comboRegne.setItems(
				Lloc.REGNES[comboArea.getSelectionIndex()]);
		comboRegne.select(ent.getRegne());
		textObs.setText(ent.getObservacions());
	}

	@Override
	public List<Lloc> retrieveUnits() throws SQLException {
		return new LlocDao().selectAll();
	}

	@Override
	protected UnitDao<Lloc> getDao() throws SQLException {
		return new LlocDao();
	}

}
