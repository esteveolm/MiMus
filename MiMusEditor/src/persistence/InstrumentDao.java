package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Instrument;

public class InstrumentDao extends UnitDao<Instrument> {

	public InstrumentDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Instrument unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom", "familia", "classe", "part"};
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
		stmt.setString(1, unit.getNom());
		stmt.setInt(2, unit.getFamily());
		stmt.setInt(3, unit.getClasse());
		stmt.setString(4, unit.getPart());
		
		return executeGetId(stmt);
	}

	@Override
	protected Instrument make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");
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

}
