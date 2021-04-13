package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.GenereLiterari;
import persistence.GenereLiterariDao;
import persistence.UnitDao;
import ui.table.GenereTableViewer;
import util.DBUtils;

/**
 * Declarative view for GenereLiterari entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class GenereLiterariView extends EntityView<GenereLiterari> {
	
	/* Form fields */
	private Text textNombreCompleto;
	private Text textNomFrances;
	private Text textNomOccita;
	private Text textDefinicio;
	private Text textObs;
	
	
	public GenereLiterariView() {
		super();
		try {
			if(DBUtils.getUser()!=null)
				setUnits(new GenereLiterariDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load generes from DB.");
		}
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);

		textNombreCompleto = addTextControl(sectAdd.getParent(), "Nom complet:");
		textNomFrances = addTextControl(sectAdd.getParent(), "Nom francès:");
		textNomOccita = addTextControl(sectAdd.getParent(), "Nom occità:");
		textDefinicio = addTextControl(sectAdd.getParent(), "Definició:");
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());

		addAnnotationsLabel(sectAdd.getParent());

		/* Table for generes created */
		Section sectTable = new Section(form.getBody(), 0);
				
		GenereTableViewer genereHelper = 
				new GenereTableViewer(sectTable.getParent(), getUnits());
		setTv(genereHelper.createTableViewer());	
		
		createEditAction();
		
	}
	
	@Override
	protected GenereLiterari getUnitToSave() {

		GenereLiterari gen = new GenereLiterari(
						0, 0,
						textNombreCompleto.getText(), 
						textNomFrances.getText(), 
						textNomOccita.getText(), 
						textDefinicio.getText(),
						textObs.getText());
		return gen;
	}
		
	@Override
	public String getViewName() {
		return "Gènere literari";
	}
	
	@Override
	protected void fillFieldsFromSelection(GenereLiterari ent) {
		textNombreCompleto.setText(ent.getNomComplet());
		textNomFrances.setText(ent.getNomFrances());
		textNomOccita.setText(ent.getNomOccita());
		textDefinicio.setText(ent.getDefinicio());
		textObs.setText(ent.getObservacions());
	}

	@Override
	public List<GenereLiterari> retrieveUnits() throws SQLException {
		return new GenereLiterariDao().selectAll();
	}

	@Override
	protected UnitDao<GenereLiterari> getDao() throws SQLException {
		return new GenereLiterariDao();
	}

}
