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
	
	public E selectOne(int id) throws SQLException {
		return selectOne(id, false);
	}
	
	public E selectOne(int id, boolean specific) throws SQLException {
		String key = specific ? "id" : "entity_id";
		String sql = "SELECT * FROM " + getTable() + " WHERE " + key + "=" + id;
		System.out.println("SQL: " + sql);
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return make(rs);
		}
		throw new SQLException();
	}
	
	public int insert(E entity) throws SQLException, DaoNotImplementedException {
		int commonId = insertCommonEntity(entity);
		if (commonId > 0) {
			int specId = insertSpecificEntity(entity, commonId);
			return updateCommonEntity(commonId, specId);
		}
		return -1;
	}
	
	public abstract int insertSpecificEntity(E entity, int entId) throws SQLException;
	
	public int insertCommonEntity(E entity) throws SQLException {
		String sql = "SELECT id from EntityTypes WHERE EntityName=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, getTable());
		ResultSet typeRS = stmt.executeQuery();
		if (typeRS.next()) {
			int typeId = typeRS.getInt(1);
			
			sql = "INSERT INTO Entity (entity_type_id, entity_id) VALUES (?,?)";
			stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, typeId);
			stmt.setInt(2, 0);	// Don't know entity_id yet, set it to 0
			return executeGetId(stmt);
		} 
		return -1;
	}
	
	public int updateCommonEntity(int commonId, int specId) throws SQLException {
		String sql = "UPDATE Entity SET entity_id=? WHERE id=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, specId);
		stmt.setInt(2, commonId);
		return executeGetId(stmt);
	}
	
	@Override
	public void delete(E entity) throws SQLException {
		/* First find Entity ID */
		Statement selectStmt = getConnection().createStatement();
		String sql = "SELECT entity_id FROM " + getTable() + 
				" WHERE id=" + entity.getId();
		ResultSet rs = selectStmt.executeQuery(sql);
		rs.next();
		int entId = rs.getInt("entity_id");
		
		/* Then delete from specific table */
		super.delete(entity);
		
		/* Finally, delete from Entity table using ID recovered */
		Statement deleteStmt = getConnection().createStatement();
		String deleteSql = "DELETE FROM Entity WHERE id=" + entId;
		deleteStmt.executeUpdate(deleteSql);
	}
}
