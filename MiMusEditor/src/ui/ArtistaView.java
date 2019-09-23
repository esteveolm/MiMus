package ui;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import model.Artista;
import persistence.ArtistaDao;
import persistence.DaoNotImplementedException;
import ui.table.ArtistaTableViewer;
import util.LabelPrinter;

public class ArtistaView extends DeclarativeView<Artista> {
	
	private List<Artista> artists;
	
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
			artists = retrieveEntities();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getViewName() {
		return "Artista";
	}
	
	public String getAddPattern() {
		return "/artista.xml";
	}
	
	public void developForm(ScrolledForm form) {		
		/* Form for introduction of new entities */
		Section sectAdd = new Section(form.getBody(), 0);
		sectAdd.setText("Add a new " + getViewName());
		
		addStateLabel(sectAdd.getParent());
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* NombreCompleto: text field */
		Label labelNombreCompleto = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNombreCompleto.setText("Nom complet:");
		textNombreCompleto = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNombreCompleto.setLayoutData(grid);

		/* Tratamiento: text field */
		Label labelTratamiento = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTratamiento.setText("Tractament:");
		textTratamiento = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTratamiento.setLayoutData(grid);

		/* Nombre: text field */
		Label labelNombre = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNombre.setText("Nom:");
		textNombre = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNombre.setLayoutData(grid);

		/* Apellido: text field */
		Label labelApellido = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelApellido.setText("Cognom:");
		textApellido = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textApellido.setLayoutData(grid);

		/* Sobrenombre: text field */
		Label labelSobrenombre = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSobrenombre.setText("Sobrenom:");
		textSobrenombre = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSobrenombre.setLayoutData(grid);
		
		/* Distintiu: text field */
		Label labelDistintiu = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistintiu.setText("Distintiu:");
		textDistintiu = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistintiu.setLayoutData(grid);
		
		/* Genero: option field */
		Label labelGenero = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelGenero.setText("Gènere:");
		comboGenero = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboGenero.setItems("no marcat", "home", "dona");
		
		/* Religion: option field */
		Label labelReligion = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelReligion.setText("Religió:");
		comboReligion = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboReligion.setItems("no marcat", "jueu", "musulmà");
		
		/* Origen: text field */
		Label labelOrigen = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelOrigen.setText("Origen:");
		textOrigen = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textOrigen.setLayoutData(grid);
		
		/* Observacions: text field */
		Label labelObs = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelObs.setText("Observacions:");
		textObs = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textObs.setLayoutData(grid);
		
		/* Form buttons */
		addButtons(sectAdd.getParent());
		btnClr.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textNombreCompleto.setText("");
				textTratamiento.setText("");
				textNombre.setText("");
				textApellido.setText("");
				textSobrenombre.setText("");
				textDistintiu.setText("");
				comboGenero.deselectAll();
				comboReligion.deselectAll();
				textOrigen.setText("");
				textObs.setText("");
			}
		});
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Artists created");
				
		ArtistaTableViewer artistaHelper = 
				new ArtistaTableViewer(sectTable.getParent(), artists);
		setTv(artistaHelper.createTableViewer());	
		
		addAnnotationsLabel(sectTable.getParent(), grid);
		createTableActions();
		
		/* Label for user feedback */
		Label label = new Label(sectAdd.getParent(), LABEL_FLAGS);
		label.setLayoutData(grid);
		
		Button btnDel = new Button(sectTable.getParent(), BUTTON_FLAGS);
		btnDel.setText("Delete artist");
		
		/* Button listeners */
		
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
				if (isStateAdd()) {
					/* Add new entity */
					try {
						int id = new ArtistaDao(getConnection()).insert(art);
						if (id>0) {
							artists.clear();
							artists.addAll(new ArtistaDao(getConnection()).selectAll());
							System.out.println("Artist created successfully.");
							LabelPrinter.printInfo(label, "Artist added successfully.");
							getTv().refresh();
						} else {
							System.out.println("DAO: Could not insert Artist into DB.");
						}
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not insert Artist into DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Insert operation not implemented, this should never happen.");
					}
				} else {
					/* Update values from selected entity */
					try {
						/* Recover ID from selection */
						art.setSpecificId(getSelectedId());
						new ArtistaDao(getConnection()).update(art);
						artists.clear();
						artists.addAll(new ArtistaDao(getConnection()).selectAll());
						System.out.println("Artist updated successfully.");
						LabelPrinter.printInfo(label, "Artist updated successfully.");
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("SQLException: Could not update Artist to DB.");
					}
				}
			}
		});
		
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Artista art = (Artista) 
						((IStructuredSelection) getTv().getSelection())
						.getFirstElement();
				if (art==null) {
					System.out.println("Could not remove Artist because none was selected.");
					LabelPrinter.printError(label, "You must select an Artist in order to remove it.");
				} else {
					try {
						new ArtistaDao(getConnection()).delete(art);
						artists.clear();
						artists.addAll(new ArtistaDao(getConnection()).selectAll());
						LabelPrinter.printInfo(label, "Artist deleted successfully.");
						getTv().refresh();
					} catch (SQLIntegrityConstraintViolationException e1) {
						LabelPrinter.printError(label, "Cannot delete Entity in use.");
						System.out.println("Could not delete: entity in use.");
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Artist from DB.");
					}
				}
			}
		});
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
	public List<Artista> getEntities() {
		return artists;
	}
	
	@Override
	public void setEntities(List<Artista> entities) {
		artists = entities;
	}

	@Override
	public List<Artista> retrieveEntities() throws SQLException {
		return new ArtistaDao(getConnection()).selectAll();
	}
}
