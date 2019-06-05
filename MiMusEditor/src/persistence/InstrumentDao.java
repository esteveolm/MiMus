package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Instrument;

public class InstrumentDao extends EntityDao<Instrument> {

	public InstrumentDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Instrument unit, int entId) 
			throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"entity_id", "nom", "familia", "classe", "part"};
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
		
		return executeGetId(stmt);
	}

	@Override
	protected Instrument make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String nom = rs.getString("nom");
		int familia = rs.getInt("familia");
		int classe = rs.getInt("classe");
		String part = rs.getString("part");
		return new Instrument(id, nom, familia, classe, part);
	}

	@Override
	public String getTable() {
		return "Instrument";
	}

	@Override
	public void update(Instrument unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

}
