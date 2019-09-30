package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import model.Entity;

/**
 * This is a special DAO only in charge of redirecting the application
 * to the specific DAO that is necessary for a certain type of entities.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class AnyEntityDao extends UnitDao<Entity> {

	public AnyEntityDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Entity unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	public void update(Entity unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	protected Entity make(ResultSet rs) throws SQLException {
		return null;
	}

	@Override
	public String getTable() {
		return "entity";
	}
	
	public String selectType(int id) throws SQLException {
		Statement selectStmt = getConnection().createStatement();
		String sql = "SELECT entity_name FROM entity_types, entity " + 
				"WHERE entity.id=" + id + " AND entity.entity_type_id=entity_types.id";
		ResultSet rs = selectStmt.executeQuery(sql);
		
		if (rs.next()) {
			return rs.getString("entity_name");
		}
		throw new SQLException();
	}
	
	public EntityDao<? extends Entity> getDao(String type) {
		HashMap<String, EntityDao<? extends Entity>> map = new HashMap<>();
		map.put("artista", new ArtistaDao(getConnection()));
		map.put("ofici", new OficiDao(getConnection()));
		map.put("promotor", new PromotorDao(getConnection()));
		map.put("casa", new CasaDao(getConnection()));
		map.put("instrument", new InstrumentDao(getConnection()));
		map.put("lloc", new LlocDao(getConnection()));
		map.put("genere_literari", new GenereLiterariDao(getConnection()));
		return map.get(type);
	}

	public EntityDao<? extends Entity> getDaoByKey(String key) {
		HashMap<String, EntityDao<? extends Entity>> map = new HashMap<>();
		map.put("artista_id", new ArtistaDao(getConnection()));
		map.put("ofici_id", new OficiDao(getConnection()));
		map.put("promotor_id", new PromotorDao(getConnection()));
		map.put("casa_id", new CasaDao(getConnection()));
		map.put("instrument_id", new InstrumentDao(getConnection()));
		map.put("lloc_id", new LlocDao(getConnection()));
		map.put("genere_id", new GenereLiterariDao(getConnection()));
		return map.get(key);
	}

}
