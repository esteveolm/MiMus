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
	public int insertSpecificEntity(Promotor unit, int entId) throws SQLException {
		String[] insertColumns = {"entity_id", "nom_complet", "nom", "cognom", 
				"sobrenom", "distintiu", "genere", "observacions"};
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
		stmt.setString(8, unit.getObservacions());
		
		return executeGetId(stmt);
	}

	@Override
	protected Promotor make(ResultSet rs) throws SQLException {
		int id = rs.getInt("entity_id");
		int specId = rs.getInt("id");
		String nomComplet = rs.getString("nom_complet");
		String nom = rs.getString("nom");
		String cognom = rs.getString("cognom");
		String sobrenom = rs.getString("sobrenom");
		String distintiu = rs.getString("distintiu");
		int genere = rs.getInt("genere");
		String observacions = rs.getString("observacions");
		
		return new Promotor(id, specId, nomComplet, nom, cognom, sobrenom, 
				distintiu, genere, observacions);
	}

	@Override
	public String getTable() {
		return "Promotor";
	}

	@Override
	public void update(Promotor unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

}
