package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Promotor;

public class PromotorDao extends UnitDao<Promotor> {

	public PromotorDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Promotor unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom_complet", "nom", "cognom", 
				"sobrenom", "distintiu", "genere"};
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
		stmt.setString(2, unit.getNom());
		stmt.setString(3, unit.getCognom());
		stmt.setString(4, unit.getSobrenom());
		stmt.setString(5, unit.getDistintiu());
		stmt.setInt(6, unit.getGenere());
		
		return executeGetId(stmt);
	}

	@Override
	protected Promotor make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");
		String nomComplet = rs.getString("nom_complet");
		String nom = rs.getString("nom");
		String cognom = rs.getString("cognom");
		String sobrenom = rs.getString("sobrenom");
		String distintiu = rs.getString("distintiu");
		int genere = rs.getInt("genere");
		
		return new Promotor(id, nomComplet, nom, cognom, sobrenom, 
				distintiu, genere);
	}

	@Override
	public String getTable() {
		return "Promotor";
	}

}
