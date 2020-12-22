package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Instrument;

/**
 * Contains the specific implementation of Instrument queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class InstrumentDao extends EntityDao<Instrument> {

	public InstrumentDao() throws SQLException {
		super();
	}
	
	public InstrumentDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Instrument unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom", "familia", "classe", "part","observacions"};
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
		stmt.setString(2, unit.getNom());
		stmt.setInt(3, unit.getFamily());
		stmt.setInt(4, unit.getClasse());
		stmt.setString(5, unit.getPart());
		stmt.setString(6, unit.getObservacions());
		
		return executeGetId(stmt);
	}

	@Override
	protected Instrument make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nom = rs.getString("nom");
		int familia = rs.getInt("familia");
		int classe = rs.getInt("classe");
		String part = rs.getString("part");
		String observacions = rs.getString("observacions");
		
		return new Instrument(id, specId, nom, familia, classe, part, observacions);
	}

	@Override
	public String getTable() {
		return "instrument";
	}

	@Override
	public void update(Instrument unit) throws SQLException {
		String sql = "UPDATE instrument "
				+ "SET nom=?, "
				+ "familia=?, "
				+ "classe=?, "
				+ "part=?, "
				+ "observacions=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNom());
		stmt.setInt(2, unit.getFamily());
		stmt.setInt(3, unit.getClasse());
		stmt.setString(4, unit.getPart());
		stmt.setString(5,  unit.getObservacions());
		stmt.setInt(6, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}

}
