package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Entity;

public abstract class EntityDao<E extends Entity> extends UnitDao<E> {

	public EntityDao(Connection conn) {
		super(conn);
	}
	
	public int insert(E entity) throws SQLException, DaoNotImplementedException {
		int entId = insertCommonEntity(entity);
		if (entId > 0) {
			return insertSpecificEntity(entity, entId);
		}
		return -1;
	}
	
	public abstract int insertSpecificEntity(E entity, int entId)
			throws SQLException, DaoNotImplementedException;
	
	public int insertCommonEntity(E entity) throws SQLException {
		String sql = "INSERT INTO Entity (ent_type) VALUES (?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, getTable());
		return executeGetId(stmt);
	}
	

}
