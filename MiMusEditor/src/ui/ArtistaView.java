package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

public class ArtistaView extends DeclarativeView {
	
	private List<Artista> artists;
	private Connection conn;
	
	public ArtistaView() {
		super();
		getControl().setArtistaView(this);
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			artists = new ArtistaDao(conn).selectAll();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.out.println("Could not load artists from DB.");
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
		
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		
		/* NombreCompleto: text field */
		Label labelNombreCompleto = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNombreCompleto.setText("Nom complet:");
		Text textNombreCompleto = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNombreCompleto.setLayoutData(grid);

		/* Tratamiento: text field */
		Label labelTratamiento = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelTratamiento.setText("Tractament:");
		Text textTratamiento = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textTratamiento.setLayoutData(grid);

		/* Nombre: text field */
		Label labelNombre = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelNombre.setText("Nom:");
		Text textNombre = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textNombre.setLayoutData(grid);

		/* Apellido: text field */
		Label labelApellido = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelApellido.setText("Cognom:");
		Text textApellido = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textApellido.setLayoutData(grid);

		/* Sobrenombre: text field */
		Label labelSobrenombre = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelSobrenombre.setText("Sobrenom:");
		Text textSobrenombre = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textSobrenombre.setLayoutData(grid);
		
		/* Distintiu: text field */
		Label labelDistintiu = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelDistintiu.setText("Distintiu:");
		Text textDistintiu = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textDistintiu.setLayoutData(grid);
		
		/* Genero: option field */
		Label labelGenero = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelGenero.setText("Gènere:");
		Combo comboGenero = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboGenero.setItems("No marcat", "Home", "Dona");
		
		/* Religion: option field */
		Label labelReligion = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelReligion.setText("Religió:");
		Combo comboReligion = new Combo(sectAdd.getParent(), COMBO_FLAGS);
		comboReligion.setItems("No marcat", "Jueu", "Musulmà");
		
		/* Origen: text field */
		Label labelOrigen = new Label(sectAdd.getParent(), LABEL_FLAGS);
		labelOrigen.setText("Origen:");
		Text textOrigen = new Text(sectAdd.getParent(), TEXT_FLAGS);
		textOrigen.setLayoutData(grid);
		
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
			}
		});
		
		/* Table for artists created */
		Section sectTable = new Section(form.getBody(), 0);
		sectTable.setText("Artists created");
				
		ArtistaTableViewer artistaHelper = 
				new ArtistaTableViewer(sectTable.getParent(), artists);
		setTv(artistaHelper.createTableViewer());	
		
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
						0,
						textNombreCompleto.getText(), 
						textTratamiento.getText(), 
						textNombre.getText(), 
						textApellido.getText(), 
						textSobrenombre.getText(),
						textDistintiu.getText(),
						comboGenero.getSelectionIndex(),
						comboReligion.getSelectionIndex(),
						textOrigen.getText());
				try {
					int id = new ArtistaDao(conn).insert(art);
					if (id>0) {
						artists.clear();
						artists.addAll(new ArtistaDao(conn).selectAll());
						System.out.println("Artist created successfully.");
						LabelPrinter.printInfo(label, "Artist added successfully.");
						notifyObservers();
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
						new ArtistaDao(conn).delete(art);
						artists.clear();
						artists.addAll(new ArtistaDao(conn).selectAll());
						LabelPrinter.printInfo(label, "Artist deleted successfully.");
						notifyObservers();
						getTv().refresh();
					} catch (SQLException e2) {
						e2.printStackTrace();
						System.out.println("Could not delete Artist from DB.");
					} catch (DaoNotImplementedException e1) {
						e1.printStackTrace();
						System.out.println("Delete operation not implemented, this should never happen.");
					}
				}
			}
		});
	}
	
	/**
	 * When ArtistaView is closed, it is unregistered from SharedControl.
	 */
	@Override
	public void dispose() {
		super.dispose();
		getControl().unsetArtistaView();
	}

	@Override
	public void update() {}
	
	public Connection getConnection() {
		return conn;
	}
}
