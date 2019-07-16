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
		/* Insert is 2-step, make it transactional */
		getConnection().setAutoCommit(false);
		
		int commonId = insertCommonEntity(entity);
		System.out.println("Common ID Of gen: " + commonId);
		if (commonId > 0) {
			int specId = insertSpecificEntity(entity, commonId);
			int result = updateCommonEntity(commonId, specId);
			if (result>0) {
				/* If insert succeeded, commit and leave transactional mode */
				getConnection().commit();
				getConnection().setAutoCommit(true);
				return result;
			}
		}
		/* If anything failed, rollback and leave transactional mode */
		getConnection().rollback();
		getConnection().setAutoCommit(true);
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
		/* Delete is a two-step operation, we do it in transactional mode */
		getConnection().setAutoCommit(false);
		
		try {
			/* 1st delete from specific table */
			Statement stmt1 = getConnection().createStatement();
			String sql1 = "DELETE FROM " + getTable() + 
					" WHERE id=" + entity.getSpecificId();
			stmt1.executeUpdate(sql1);
			
			/* Then, delete from Entity table using ID recovered */
			Statement stmt2 = getConnection().createStatement();
			String sql2 = "DELETE FROM Entity WHERE id=" + entity.getId();
			stmt2.executeUpdate(sql2);
			
			getConnection().commit();
		} catch (SQLException e) {
			/* If any step fails, rollback and throw exception to UI */
			getConnection().rollback();
			throw e;
		}
		
		/* Finish transactional mode */
		getConnection().setAutoCommit(true);
	}
}
