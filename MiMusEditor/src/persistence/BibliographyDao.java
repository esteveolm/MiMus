package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Bibliography;

public class BibliographyDao extends UnitDao<Bibliography> {

	public BibliographyDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Bibliography unit) throws SQLException {
		String[] insertColumns = {"Autor1", "Autor2", "Autor3", "Autor4", 
				 "AutorSecondari1", "AutorSecondari2", "AutorSecondari3", 
				 "AutorSecondari4", "AutorSecondari5", "AutorSecondari6", 
				 "Any", "Distincio", "Titol", "TitolPrincipal", "Volum", 
				 "Lloc", "Editorial", "Serie", "Pagines", "ReferenciaCurta"};
		String sql = "INSERT INTO " + getTable() + " (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += insertColumns[i] + ", ";
		}
		sql += insertColumns[insertColumns.length-1] + ") VALUES (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += "?, ";
		}
		sql += "?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getAuthor(0));
		stmt.setString(2, unit.getAuthor(1));
		stmt.setString(3, unit.getAuthor(2));
		stmt.setString(4, unit.getAuthor(3));
		stmt.setString(5, unit.getSecondaryAuthor(0));
		stmt.setString(6, unit.getSecondaryAuthor(1));
		stmt.setString(7, unit.getSecondaryAuthor(2));
		stmt.setString(8, unit.getSecondaryAuthor(3));
		stmt.setString(9, unit.getSecondaryAuthor(4));
		stmt.setString(10, unit.getSecondaryAuthor(5));
		stmt.setInt(11, Integer.parseInt(unit.getYear()));
		stmt.setString(12, unit.getDistinction());
		stmt.setString(13, unit.getTitle());
		stmt.setString(14, unit.getMainTitle());
		stmt.setString(15, unit.getVolume());
		stmt.setString(16, unit.getPlace());
		stmt.setString(17, unit.getEditorial());
		stmt.setString(18, unit.getSeries());
		stmt.setString(19, unit.getPages());
		stmt.setString(20, unit.getShortReference());
		
		return executeGetId(stmt);
	}
	
	protected Bibliography make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String autor1 = denullify(rs.getString("Autor1"));
		String autor2 = denullify(rs.getString("Autor2"));
		String autor3 = denullify(rs.getString("Autor3"));
		String autor4 = denullify(rs.getString("Autor4"));
		String autorSecondari1 = denullify(rs.getString("AutorSecondari1"));
		String autorSecondari2 = denullify(rs.getString("AutorSecondari2"));
		String autorSecondari3 = denullify(rs.getString("AutorSecondari3"));
		String autorSecondari4 = denullify(rs.getString("AutorSecondari4"));
		String autorSecondari5 = denullify(rs.getString("AutorSecondari5"));
		String autorSecondari6 = denullify(rs.getString("AutorSecondari6"));
		String any = denullify(rs.getString("Any"));
		String distincio = denullify(rs.getString("Distincio"));
		String titol = denullify(rs.getString("Titol"));
		String titolPrincipal = denullify(rs.getString("TitolPrincipal"));
		String volum = denullify(rs.getString("Volum"));
		String lloc = denullify(rs.getString("Lloc"));
		String editorial = denullify(rs.getString("Editorial"));
		String serie = denullify(rs.getString("Serie"));
		String pagines = denullify(rs.getString("Pagines"));
		String referenciaCurta = denullify(rs.getString("ReferenciaCurta"));
		
		String[] autors = {autor1, autor2, autor3, autor4};
		String[] autorsSecondaris = {autorSecondari1, autorSecondari2, 
				autorSecondari3, autorSecondari4, autorSecondari5, autorSecondari6};
		return new Bibliography(autors, autorsSecondaris, any, distincio, titol,
				titolPrincipal, volum, lloc, editorial, serie, pagines,
				referenciaCurta, id, new ArrayList<Integer>());
	}

	private String denullify(String value) {
		return (value == null) ? "" : value;
	}
	
	@Override
	public String getTable() {
		return "Bibliografia";
	}

	@Override
	public void update(Bibliography unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}
}
