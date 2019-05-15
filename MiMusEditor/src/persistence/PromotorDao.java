package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Promotor;

public class PromotorDao extends EntityDao<Promotor> {

	public PromotorDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insertSpecificEntity(Promotor unit, int entId) 
			throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"ent_id", "nom_complet", "nom", "cognom", 
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
		stmt.setInt(1, entId);
		stmt.setString(2, unit.getNomComplet());
		stmt.setString(3, unit.getNom());
		stmt.setString(4, unit.getCognom());
		stmt.setString(5, unit.getSobrenom());
		stmt.setString(6, unit.getDistintiu());
		stmt.setInt(7, unit.getGenere());
		
		return executeGetId(stmt);
	}

	@Override
	protected Promotor make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
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
