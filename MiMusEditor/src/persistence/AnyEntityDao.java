package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import model.Entity;

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
		return "Entity";
	}
	
	public String selectType(int id) throws SQLException {
		Statement selectStmt = getConnection().createStatement();
		String sql = "SELECT EntityName FROM EntityTypes, Entity " + 
				"WHERE Entity.id=" + id + " AND Entity.entity_type_id=EntityName.id";
		ResultSet rs = selectStmt.executeQuery(sql);
		
		if (rs.next()) {
			return rs.getString("EntityName");
		}
		throw new SQLException();
	}
	
	public EntityDao<? extends Entity> getDao(String type) {
		HashMap<String, EntityDao<? extends Entity>> map = new HashMap<>();
		map.put("Artista", new ArtistaDao(getConnection()));
		map.put("Ofici", new OficiDao(getConnection()));
		map.put("Promotor", new PromotorDao(getConnection()));
		map.put("Casa", new CasaDao(getConnection()));
		map.put("Instrument", new InstrumentDao(getConnection()));
		map.put("Lloc", new LlocDao(getConnection()));
		return map.get(type);
	}

}
