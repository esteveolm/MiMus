package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Casa;

public class CasaDao extends UnitDao<Casa> {

	public CasaDao(Connection conn) {
		super(conn);
	}

	@Override
	public void insert(Casa unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom_complet", "titol", "cort"};
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
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getTitol());
		stmt.setString(3, unit.getCort());
		
		stmt.executeUpdate();
	}

	@Override
	protected Casa make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");
		String nomComplet = rs.getString("nom_complet");
		String titol = rs.getString("titol");
		String cort = rs.getString("cort");
		return new Casa(id, nomComplet, titol, cort);
	}

	@Override
	public String getTable() {
		return "Casa";
	}

}
