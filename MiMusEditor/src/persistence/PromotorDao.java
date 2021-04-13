package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Promotor;

/**
 * Contains the specific implementation of Promotor queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class PromotorDao extends EntityDao<Promotor> {

	public PromotorDao() throws SQLException {
		super();
	}
	
	public PromotorDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Promotor unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "nom", "cognom", 
				"sobrenom", "distintiu", "genere", "observacions"};
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
		stmt.setString(3, unit.getNom());
		stmt.setString(4, unit.getCognom());
		stmt.setString(5, unit.getSobrenom());
		stmt.setString(6, unit.getDistintiu());
		stmt.setInt(7, unit.getGenere());
		stmt.setString(8, unit.getObservacions());
		
		return executeGetId(stmt);
	}

	@Override
	protected Promotor make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String nom = rs.getString("nom");
		String cognom = rs.getString("cognom");
		String sobrenom = rs.getString("sobrenom");
		String distintiu = rs.getString("distintiu");
		int genere = rs.getInt("genere");
		String observacions = rs.getString("observacions");
		
		return new Promotor(id, specId, nomComplet, nom, cognom, sobrenom, 
				distintiu, genere, observacions);
	}

	@Override
	public String getTable() {
		return "promotor";
	}

	@Override
	public void update(Promotor unit) throws SQLException {
		String sql = "UPDATE promotor "
				+ "SET nom_complet=?, "
				+ "nom=?, "
				+ "cognom=?, "
				+ "sobrenom=?, "
				+ "distintiu=?, "
				+ "genere=?, "
				+ "observacions=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getNom());
		stmt.setString(3, unit.getCognom());
		stmt.setString(4, unit.getSobrenom());
		stmt.setString(5, unit.getDistintiu());
		stmt.setInt(6, unit.getGenere());
		stmt.setString(7, unit.getObservacions());
		stmt.setInt(8, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}

}
