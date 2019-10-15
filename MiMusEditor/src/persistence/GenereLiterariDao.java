package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.GenereLiterari;

/**
 * Contains the specific implementation of GenereLiterari queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class GenereLiterariDao extends EntityDao<GenereLiterari> {

	public GenereLiterariDao() throws SQLException {
		super();
	}
	
	public GenereLiterariDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(GenereLiterari unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "nom_frances",
				"nom_occita", "definicio"};
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
		stmt.setString(3, unit.getNomFrances());
		stmt.setString(4, unit.getNomOccita());
		stmt.setString(5, unit.getDefinicio());
		
		return executeGetId(stmt);
	}

	@Override
	protected GenereLiterari make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String nomFrances = rs.getString("nom_frances");
		String nomOccita = rs.getString("nom_occita");
		String definicio = rs.getString("definicio");
		
		return new GenereLiterari(id, specId, nomComplet, nomFrances,
				nomOccita, definicio);
	}

	@Override
	public String getTable() {
		return "genere_literari";
	}
	
	@Override
	public void update(GenereLiterari unit) throws SQLException {
		String sql = "UPDATE genere_literari "
				+ "SET nom_complet=?, "
				+ "nom_frances=?, "
				+ "nom_occita=?, "
				+ "definicio=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getNomFrances());
		stmt.setString(3, unit.getNomOccita());
		stmt.setString(4, unit.getDefinicio());
		stmt.setInt(5, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}
}
