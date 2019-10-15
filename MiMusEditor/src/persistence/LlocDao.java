package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Lloc;

/**
 * Contains the specific implementation of Lloc queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class LlocDao extends EntityDao<Lloc> {

	public LlocDao() throws SQLException {
		super();
	}
	
	public LlocDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Lloc unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "regne", "area"};
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
		stmt.setInt(3, unit.getRegne());
		stmt.setInt(4, unit.getArea());
		
		return executeGetId(stmt);
	}

	@Override
	protected Lloc make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		int regne = rs.getInt("regne");
		int area = rs.getInt("area");
		
		return new Lloc(id, specId, nomComplet, regne, area);
	}

	@Override
	public String getTable() {
		return "lloc";
	}

	@Override
	public void update(Lloc unit) throws SQLException {
		String sql = "UPDATE lloc "
				+ "SET nom_complet=?, "
				+ "regne=?, "
				+ "area=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setInt(2, unit.getRegne());
		stmt.setInt(3, unit.getArea());
		stmt.setInt(4, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}

}
