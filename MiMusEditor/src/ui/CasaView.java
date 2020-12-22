package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import model.Casa;
import persistence.CasaDao;
import persistence.UnitDao;
import ui.table.CasaTableViewer;

/**
 * Declarative view for Casa entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class CasaView extends EntityView<Casa> {
	
	/* Form fields */
	private Text textNomComplet;
	private Text textTitol;
	private Text textCort;
	private Text textObs;
	
	public CasaView() {
		super();
		try {
			setUnits(new CasaDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load cases from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Casa";
	}
	
	@Override
	public void developForm(Form form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		textNomComplet = addTextControl(sectAdd.getParent(), "Nom complet:");
		textTitol = addTextControl(sectAdd.getParent(), "Títol:");
		textCort = addTextControl(sectAdd.getParent(), "Cort:");
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		
		CasaTableViewer casaHelper =
				new CasaTableViewer(sectTable.getParent(), getUnits());
		setTv(casaHelper.createTableViewer());
		
		addAnnotationsLabel(sectAdd.getParent(), grid);
		createEditAction();
		
	}

	@Override
	protected Casa getUnitToSave() {

				Casa casa = new Casa(0, 0,
						textNomComplet.getText(),
						textTitol.getText(), 
						textCort.getText(),
						textObs.getText());
				return casa;
	}
	
	@Override
	protected void fillFieldsFromSelection(Casa ent) {
		textNomComplet.setText(ent.getNomComplet());
		textTitol.setText(ent.getTitol());
		textCort.setText(ent.getCort());
		textObs.setText(ent.getObservacions());
	}

	@Override
	public List<Casa> retrieveUnits() throws SQLException {
		return new CasaDao().selectAll();
	}

	@Override
	protected UnitDao<Casa> getDao() throws SQLException {
		return new CasaDao();
	}

}
