package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Lloc;

public class LlocDao extends UnitDao<Lloc> {

	public LlocDao(Connection conn) {
		super(conn);
	}

	@Override
	public void insert(Lloc unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom_complet", "regne", "area"};
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
		stmt.setInt(2, unit.getRegne());
		stmt.setInt(3, unit.getArea());
		
		stmt.executeUpdate();
	}

	@Override
	protected Lloc make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");
		String nomComplet = rs.getString("nom_complet");
		int regne = rs.getInt("regne");
		int area = rs.getInt("area");
		return new Lloc(id, nomComplet, regne, area);
	}

	@Override
	public String getTable() {
		return "Lloc";
	}

}
