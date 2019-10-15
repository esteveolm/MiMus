package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Artista;

/**
 * Contains the specific implementation of Artista queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ArtistaDao extends EntityDao<Artista> {

	public ArtistaDao() throws SQLException {
		super();
	}
	
	public ArtistaDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Artista unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "tractament", "nom", 
				"cognom", "sobrenom", "distintiu", "genere", "religio", "origen",
				"observacions"};
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
		stmt.setString(3, unit.getTractament());
		stmt.setString(4, unit.getNom());
		stmt.setString(5, unit.getCognom());
		stmt.setString(6, unit.getSobrenom());
		stmt.setString(7, unit.getDistintiu());
		stmt.setInt(8, unit.getGenere());
		stmt.setInt(9, unit.getReligio());
		stmt.setString(10, unit.getOrigen());
		stmt.setString(11, unit.getObservacions());
		
		return executeGetId(stmt);
	}

	@Override
	protected Artista make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String tractament = rs.getString("tractament");
		String nom = rs.getString("nom");
		String cognom = rs.getString("cognom");
		String sobrenom = rs.getString("sobrenom");
		String distintiu = rs.getString("distintiu");
		int genere = rs.getInt("genere");
		int religio = rs.getInt("religio");
		String origen = rs.getString("origen");
		String observacions = rs.getString("observacions");
		
		return new Artista(id, specId, nomComplet, tractament, nom, cognom, sobrenom,
				distintiu, genere, religio, origen, observacions);
	}

	@Override
	public String getTable() {
		return "artista";
	}

	@Override
	public void update(Artista unit) throws SQLException {
		String sql = "UPDATE artista "
				+ "SET nom_complet=?, "
				+ "tractament=?, "
				+ "nom=?, "
				+ "cognom=?, "
				+ "sobrenom=?, "
				+ "distintiu=?, "
				+ "genere=?, "
				+ "religio=?, "
				+ "origen=?, "
				+ "observacions=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getTractament());
		stmt.setString(3, unit.getNom());
		stmt.setString(4, unit.getCognom());
		stmt.setString(5, unit.getSobrenom());
		stmt.setString(6, unit.getDistintiu());
		stmt.setInt(7, unit.getGenere());
		stmt.setInt(8, unit.getReligio());
		stmt.setString(9, unit.getOrigen());
		stmt.setString(10, unit.getObservacions());
		stmt.setInt(11, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}
}
