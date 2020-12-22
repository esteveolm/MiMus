package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Instrument;
import model.Ofici;

/**
 * Contains the specific implementation of Ofici queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class OficiDao extends EntityDao<Ofici> {

	public OficiDao() throws SQLException {
		super();
	}
	
	public OficiDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Ofici unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "terme", "especialitat",
				"instrument_id", "observacions"};
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
		stmt.setString(3, unit.getTerme());
		stmt.setInt(4, unit.getEspecialitat());
		
		/* Instrument is a foreign key that can be null if not specified */
		if (unit.getInstrument() != null) {
			stmt.setInt(5, unit.getInstrument().getSpecificId());
		} else {
			stmt.setNull(5, java.sql.Types.BIGINT);
		}
		stmt.setString(6, unit.getObservacions());
		
		return executeGetId(stmt);
	}

	@Override
	protected Ofici make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String terme = rs.getString("terme");
		int especialitat = rs.getInt("especialitat");
		int instrumentId = rs.getInt("instrument_id");
		String observacions = rs.getString("observacions");
		
		/* 
		 * Instrument foreign key uses specific key, not generic entity key. 
		 * If not present, id retrieved as 0 and should be null on model.
		 * */
		Instrument inst = null;
		if (instrumentId>0) {
			inst = new InstrumentDao(getConnection()).selectOne(instrumentId, true);
		}
		return new Ofici(id, specId, nomComplet, terme, especialitat, inst, observacions);
	}

	@Override
	public String getTable() {
		return "ofici";
	}

	@Override
	public void update(Ofici unit) throws SQLException {
		String sql = "UPDATE ofici "
				+ "SET nom_complet=?, "
				+ "terme=?, "
				+ "especialitat=?, "
				+ "instrument_id=?, "
				+ "observacions=? "
				+ "WHERE id=?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getTerme());
		stmt.setInt(3, unit.getEspecialitat());
		/* Instrument is a foreign key that can be null if not specified */
		if (unit.getInstrument() != null) {
			stmt.setInt(4, unit.getInstrument().getSpecificId());
		} else {
			stmt.setNull(4, java.sql.Types.BIGINT);
		}
		stmt.setString(5, unit.getObservacions());
		stmt.setInt(6, unit.getSpecificId());

		int result = stmt.executeUpdate();
		if (result>0) {
			System.out.println("Update performed successfully.");
		} else {
			System.out.println("Could not execute update.");
		}
	}

}
