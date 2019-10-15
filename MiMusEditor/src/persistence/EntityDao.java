package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Entity;

/**
 * Common DAO for all Entity tables. Contains implementation specific
 * to these units.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <E> the Entity class.
 */
public abstract class EntityDao<E extends Entity> extends UnitDao<E> {

	public EntityDao() throws SQLException {
		super();
	}
	
	public EntityDao(Connection conn) {
		super(conn);
	}
	
	/**
	 * Selects an entity based on its common id.
	 */
	public E selectOne(int id) throws SQLException {
		return selectOne(id, false);
	}
	
	/**
	 * Entities are hierarchical units which have two IDs, the common
	 * and the specific one. Boolean parameter <specific> defines
	 * where to look parameter <id> from.
	 */
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
	
	/**
	 * Inserts an Entity. This requires making an insertion in the
	 * common Entity table and in the specific E table. To avoid
	 * failures during the process that lead to unstable state, this
	 * procedure is executed without auto-commit mode in the DB. That is,
	 * if something fails, state is reversed to how it was before this
	 * method was executed.
	 */
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
	
	/**
	 * This method contains the specific implementation of a certain type of
	 * Entity E, that is, the columns stored in the DB. It is also passed the
	 * common ID <entId> to make the foreign key.
	 */
	public abstract int insertSpecificEntity(E entity, int entId) throws SQLException;
	
	/**
	 * This method contains the insertion of Entity in the common table.
	 * Because it is linked to a specific entity with a foreign key but
	 * this is unknown at this moment, it is set to 0 and updated later.
	 * This is possible because the DB schema is not specifying this
	 * foreign key, we're only using it as such without the constraint 
	 * of a certain table.
	 */
	public int insertCommonEntity(E entity) throws SQLException {
		String sql = "SELECT id from entity_types WHERE entity_name=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, getTable());
		ResultSet typeRS = stmt.executeQuery();
		if (typeRS.next()) {
			int typeId = typeRS.getInt(1);
			
			sql = "INSERT INTO entity (entity_type_id, entity_id) VALUES (?,?)";
			stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, typeId);
			stmt.setInt(2, 0);	// Don't know entity_id yet, set it to 0
			return executeGetId(stmt);
		} 
		return -1;
	}
	
	/**
	 * Updates foreign key on common entity table, finding the common
	 * entry with <commonId> and setting the foreign key as <specId>.
	 */
	public int updateCommonEntity(int commonId, int specId) throws SQLException {
		String sql = "UPDATE entity SET entity_id=? WHERE id=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, specId);
		stmt.setInt(2, commonId);
		return executeGetId(stmt);
	}
	
	/**
	 * Deletes an entity from its table. This requires performing a delete
	 * on the common and the specific table, hence it is done without
	 * auto-commit mode. This means that, if something fails, state is
	 * reversed to how it was before this method was executed.
	 */
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
			String sql2 = "DELETE FROM entity WHERE id=" + entity.getId();
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
