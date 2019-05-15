package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Artista;

public class ArtistaDao extends UnitDao<Artista> {

	public ArtistaDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Artista unit) throws SQLException, DaoNotImplementedException {
		String[] insertColumns = {"nom_complet", "tractament", "nom", "cognom",
				"sobrenom", "distintiu", "genere", "religio", "origen"};
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
		stmt.setString(2, unit.getTractament());
		stmt.setString(3, unit.getNom());
		stmt.setString(4, unit.getCognom());
		stmt.setString(5, unit.getSobrenom());
		stmt.setString(6, unit.getDistintiu());
		stmt.setInt(7, unit.getGenere());
		stmt.setInt(8, unit.getGenere());
		stmt.setString(9, unit.getOrigen());
		
		return executeGetId(stmt);
	}

	@Override
	protected Artista make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entId = rs.getInt("ent_id");	// TODO: EntityInstance
		String nomComplet = rs.getString("nom_complet");
		String tractament = rs.getString("tractament");
		String nom = rs.getString("nom");
		String cognom = rs.getString("cognom");
		String sobrenom = rs.getString("sobrenom");
		String distintiu = rs.getString("distintiu");
		int genere = rs.getInt("genere");
		int religio = rs.getInt("religio");
		int origen = rs.getInt("origen");
		
		return new Artista(id, nomComplet, tractament, nom, cognom, sobrenom,
				distintiu, genere, religio, String.valueOf(origen));
	}

	@Override
	public String getTable() {
		return "Artista";
	}

}
