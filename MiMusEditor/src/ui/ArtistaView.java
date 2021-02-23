package ui;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Artista;
import persistence.ArtistaDao;
import persistence.UnitDao;
import ui.table.ArtistaTableViewer;

/**
 * Declarative view for Artista entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ArtistaView extends EntityView<Artista> {
	
	/* Form fields */
	private Text textNombreCompleto;
	private Text textTratamiento;
	private Text textNombre;
	private Text textApellido;
	private Text textSobrenombre;
	private Text textDistintiu;
	private Combo comboGenero;
	private Combo comboReligion;
	private Text textOrigen;
	private Text textObs;
	
	public ArtistaView() {
		super();
		try {
			setUnits(retrieveUnits());
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getViewName() {
		return "Artista";
	}
	
	public void developForm(ScrolledForm form) {		
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		
		
		textNombreCompleto = addTextControl(sectAdd.getParent(), "Nom complet:");
		textTratamiento = addTextControl(sectAdd.getParent(), "Tractament:");
		textNombre = addTextControl(sectAdd.getParent(), "Nom:");
		textApellido = addTextControl(sectAdd.getParent(), "Cognom:");
		textSobrenombre = addTextControl(sectAdd.getParent(), "Sobrenom:");
		textDistintiu = addTextControl(sectAdd.getParent(), "Distintiu:");
		comboGenero = addComboControl(sectAdd.getParent(), "Gènere:", "no marcat", "home", "dona");
		comboReligion = addComboControl(sectAdd.getParent(), "Religió:", "no marcat", "jueu", "musulmà");
		textOrigen = addTextControl(sectAdd.getParent(), "Origen:");
		textObs = addTextAreaControl(sectAdd.getParent(), "Observacions:",60);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());

		addAnnotationsLabel(sectAdd.getParent());

		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
				
		ArtistaTableViewer artistaHelper = new ArtistaTableViewer(sectTable.getParent(), getUnits());
		setTv(artistaHelper.createTableViewer());	
		
		createEditAction();
		
		
	}
	
	
	
	@Override
	protected Artista getUnitToSave() {
		
				/* Selecting nothing == Selection index 0 ("unmarked") */
				if (comboGenero.getSelectionIndex() == -1) {
					comboGenero.select(0);
				} 
				if (comboReligion.getSelectionIndex() == -1) {
					comboReligion.select(0);
				}
				
				Artista art = new Artista(
						0, 0,
						textNombreCompleto.getText(), 
						textTratamiento.getText(), 
						textNombre.getText(), 
						textApellido.getText(), 
						textSobrenombre.getText(),
						textDistintiu.getText(),
						comboGenero.getSelectionIndex(),
						comboReligion.getSelectionIndex(),
						textOrigen.getText(),
						textObs.getText());
				return art;
	}
	
		

	@Override
	protected void fillFieldsFromSelection(Artista ent) {
		textNombreCompleto.setText(ent.getNomComplet());
		textTratamiento.setText(ent.getTractament());
		textNombre.setText(ent.getNom());
		textApellido.setText(ent.getCognom());
		textSobrenombre.setText(ent.getSobrenom());
		textDistintiu.setText(ent.getDistintiu());
		comboGenero.select(ent.getGenere());
		comboReligion.select(ent.getReligio());
		textOrigen.setText(ent.getOrigen());
		textObs.setText(ent.getObservacions());
	}
	
	@Override
	public List<Artista> retrieveUnits() throws SQLException {
		return new ArtistaDao().selectAll();
	}

	@Override
	protected UnitDao<Artista> getDao() throws SQLException {
		return new ArtistaDao();
	}

}
