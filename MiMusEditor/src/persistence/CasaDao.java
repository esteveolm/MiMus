package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Casa;

public class CasaDao extends EntityDao<Casa> {

	public CasaDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Casa unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "titol", "cort"};
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
		stmt.setInt(1, entId);
		stmt.setString(2, unit.getNomComplet());
		stmt.setString(3, unit.getTitol());
		stmt.setString(4, unit.getCort());
		
		return executeGetId(stmt);
	}

	@Override
	protected Casa make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String titol = rs.getString("titol");
		String cort = rs.getString("cort");
		
		return new Casa(id, specId, nomComplet, titol, cort);
	}

	@Override
	public String getTable() {
		return "casa";
	}

	@Override
	public void update(Casa unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

}
