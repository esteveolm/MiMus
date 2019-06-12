package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Ofici;

public class OficiDao extends EntityDao<Ofici> {

	public OficiDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Ofici unit, int entId) 
			throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"entity_id", "nom_complet", "terme", "especialitat",
				"instrument_id"};
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
		stmt.setInt(5, 0);	// TODO: foreign key with Instrument
		
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
		
		return new Ofici(id, specId, nomComplet, terme, especialitat, null);
	}

	@Override
	public String getTable() {
		return "Ofici";
	}

	@Override
	public void update(Ofici unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

}
