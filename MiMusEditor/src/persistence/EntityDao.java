package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	
	public void delete(E entity) throws SQLException, DaoNotImplementedException {
		/* First find Entity ID */
		Statement selectStmt = getConnection().createStatement();
		String sql = "SELECT ent_id FROM " + getTable() + 
				" WHERE id=" + entity.getId();
		ResultSet rs = selectStmt.executeQuery(sql);
		rs.next();
		int entId = rs.getInt("ent_id");
		
		/* Then delete from specific table */
		super.delete(entity);
		
		/* Finally, delete from Entity table using ID recovered */
		Statement deleteStmt = getConnection().createStatement();
		String deleteSql = "DELETE FROM Entity WHERE id=" + entId;
		deleteStmt.executeUpdate(deleteSql);
	}
}
