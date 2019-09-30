package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Casa;

/**
 * Contains the specific implementation of Casa queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
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
	public void update(Casa unit) throws SQLException {
		String sql = "UPDATE casa "
				+ "SET nom_complet=?, "
				+ "titol=?, "
				+ "cort=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getTitol());
		stmt.setString(3, unit.getCort());
		stmt.setInt(4, unit.getSpecificId());
		
		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}

}
