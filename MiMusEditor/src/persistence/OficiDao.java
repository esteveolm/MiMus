package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Ofici;

public class OficiDao extends UnitDao<Ofici> {

	public OficiDao(Connection conn) {
		super(conn);
	}

	@Override
	public void insert(Ofici unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom_complet", "terme", "especialitat",
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
		stmt.setString(1, unit.getNomComplet());
		stmt.setString(2, unit.getTerme());
		stmt.setInt(3, unit.getEspecialitat());
		stmt.setInt(4, unit.getInstrument().getId());
		stmt.executeUpdate();
	}

	@Override
	protected Ofici make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");
		String nomComplet = rs.getString("nom_complet");
		String terme = rs.getString("terme");
		int especialitat = rs.getInt("especialitat");
		int instrumentId = rs.getInt("instrument_id");
		return new Ofici(id, nomComplet, terme, especialitat, null);
	}

	@Override
	public String getTable() {
		return "Ofici";
	}

}
