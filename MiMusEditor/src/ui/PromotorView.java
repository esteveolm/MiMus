package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Promotor;
import persistence.PromotorDao;
import persistence.UnitDao;
import ui.table.PromotorTableViewer;

/**
 * Declarative view for Promotor entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class PromotorView extends EntityView<Promotor> {

	/* Form fields */
	private Text textNomComplet;
	private Text textNom;
	private Text textCognom;
	private Text textSobrenom;
	private Text textDistintiu;
	private Combo comboGenere;
	private Text textObs;
	
	public PromotorView() {
		super();
		
		try {
			setUnits(new PromotorDao().selectAll());
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load promotors from DB.");
		}
	}
	
	@Override
	public String getViewName() {
		return "Promotor";
	}
	
	@Override
	public void developForm(ScrolledForm form) {
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);

		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		

		textNomComplet = addTextControl(sectAdd.getParent(), "Nom complet:");
		textNom = addTextControl(sectAdd.getParent(), "Nom:");
		textCognom = addTextControl(sectAdd.getParent(), "Cognom:");		
		textSobrenom = addTextControl(sectAdd.getParent(), "Sobrenom:");		
		textDistintiu = addTextControl(sectAdd.getParent(), "Distintiu:");
		comboGenere = addComboControl(sectAdd.getParent(), "Gènere:", "no marcat", "home", "dona");
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());

		addAnnotationsLabel(sectAdd.getParent(), grid);

		/* Table for promotors created */
		Section sectTable = new Section(form.getBody(), 0);
				
		PromotorTableViewer promotorHelper = 
				new PromotorTableViewer(sectTable.getParent(), getUnits());
		setTv(promotorHelper.createTableViewer());	
		
		createEditAction();
	}

	@Override
	protected Promotor getUnitToSave() {
		
				/* Selecting nothing == Selection index 0 ("unmarked") */
				if (comboGenere.getSelectionIndex() == -1) {
					comboGenere.select(0);
				} 
				Promotor prom = new Promotor(
						0, 0,
						textNomComplet.getText(),
						textNom.getText(),
						textCognom.getText(),
						textSobrenom.getText(),
						textDistintiu.getText(),
						comboGenere.getSelectionIndex(),
						textObs.getText());
				return prom;
	}

	@Override
	protected void fillFieldsFromSelection(Promotor ent) {
		textNomComplet.setText(ent.getNomComplet());
		textNom.setText(ent.getNom());
		textCognom.setText(ent.getCognom());
		textSobrenom.setText(ent.getSobrenom());
		textDistintiu.setText(ent.getDistintiu());
		comboGenere.select(ent.getGenere());
		textObs.setText(ent.getObservacions());
	}

	@Override
	public List<Promotor> retrieveUnits() throws SQLException {
		return new PromotorDao().selectAll();
	}

	@Override
	protected UnitDao<Promotor> getDao() throws SQLException {
		return new PromotorDao();
	}

}
