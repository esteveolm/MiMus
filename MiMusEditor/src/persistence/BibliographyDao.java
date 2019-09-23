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
		String[] insertColumns = {"autor1", "autor2", "autor3", "autor4", 
				 "autor_secondari1", "autor_secondari2", "autor_secondari3", 
				 "autor_secondari4", "autor_secondari5", "autor_secondari6", 
				 "any_", "distincio", "titol", "titol_principal", "volum", 
				 "lloc", "editorial", "serie", "pagines", "referencia_curta"};
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
		stmt.setString(11, unit.getYear());
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
		String autor1 = denullify(rs.getString("autor1"));
		String autor2 = denullify(rs.getString("autor2"));
		String autor3 = denullify(rs.getString("autor3"));
		String autor4 = denullify(rs.getString("autor4"));
		String autorSecondari1 = denullify(rs.getString("autor_secondari1"));
		String autorSecondari2 = denullify(rs.getString("autor_secondari2"));
		String autorSecondari3 = denullify(rs.getString("autor_secondari3"));
		String autorSecondari4 = denullify(rs.getString("autor_secondari4"));
		String autorSecondari5 = denullify(rs.getString("autor_secondari5"));
		String autorSecondari6 = denullify(rs.getString("autor_secondari6"));
		String any = denullify(rs.getString("any_"));
		String distincio = denullify(rs.getString("distincio"));
		String titol = denullify(rs.getString("titol"));
		String titolPrincipal = denullify(rs.getString("titol_principal"));
		String volum = denullify(rs.getString("volum"));
		String lloc = denullify(rs.getString("lloc"));
		String editorial = denullify(rs.getString("editorial"));
		String serie = denullify(rs.getString("serie"));
		String pagines = denullify(rs.getString("pagines"));
		String referenciaCurta = denullify(rs.getString("referencia_curta"));
		
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
		return "bibliografia";
	}

	@Override
	public void update(Bibliography unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}
}
