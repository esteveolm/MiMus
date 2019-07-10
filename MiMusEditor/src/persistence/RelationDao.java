package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Document;
import model.Entity;
import model.Relation;

public abstract class RelationDao extends UnitDao<Relation> {
	
	public RelationDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Relation unit) throws SQLException {
		int commonId = insertCommonRelation(unit);
		if (commonId > 0) {
			int specId = insertSpecificRelation(unit, commonId);
			return updateCommonRelation(commonId, specId);
		}
		return -1;
	}

	private int insertCommonRelation(Relation unit) throws SQLException {
		String sql = "SELECT id from RelationTypes WHERE RelationName=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, getTable());
		ResultSet typeRS = stmt.executeQuery();
		if (typeRS.next()) {
			int typeId = typeRS.getInt(1);
			
			sql = "INSERT INTO Relation "
					+ "(relation_type_id, relation_id, document_id) VALUES (?,?,?)";
			stmt = getConnection().prepareStatement(sql);
			stmt.setInt(1, typeId);
			stmt.setInt(2, 0);	// Don't know relation_id yet, set it to 0
			stmt.setInt(3, unit.getDoc().getId());
			return executeGetId(stmt);
		} 
		return -1;
	}
	
	private int updateCommonRelation(int commonId, int specId) throws SQLException {
		String sql = "UPDATE Relation SET relation_id=? WHERE id=?";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, specId);
		stmt.setInt(2, commonId);
		return executeGetId(stmt);
	}

	protected abstract int insertSpecificRelation(Relation unit, int commonId)
			throws SQLException;

	@Override
	public void update(Relation unit) throws DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}
	
	@Override
	protected Relation make(ResultSet rs) throws SQLException {
		return null;
	}
	
	@Override
	public void delete(Relation relation) throws SQLException {
		/* 1st delete from specific table */
		Statement stmt1 = getConnection().createStatement();
		String sql1 = "DELETE FROM " + getTable() + 
				" WHERE id=" + relation.getSpecificId();
		System.out.println("SQL Specific: " + sql1);
		stmt1.executeUpdate(sql1);
		
		/* Then, delete from Relation  table using ID recovered */
		Statement stmt2 = getConnection().createStatement();
		String sql2 = "DELETE FROM Relation WHERE id=" + relation.getId();
		stmt2.executeUpdate(sql2);
	}

	@Override
	public abstract String getTable();
	
	public Entity getEntity(int i, int id) throws SQLException {
		String sql = "SELECT * FROM " + getTable() + " WHERE relation_id=" + id;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			int entId = rs.getInt(getEntities()[i-1]);
			
			EntityDao<? extends Entity> dao = 
					new AnyEntityDao(getConnection()).getDaoByKey(getEntities()[i-1]);
			return dao.selectOne(entId, true);	/* Foreign key is specific in rels */
		}
		throw new SQLException();
	}

	public List<Entity> getEntities(int id) throws SQLException {
		List<Entity> out = new ArrayList<>();
		for (int i=0; i<countEntities(); i++) {
			out.add(getEntity(i+1,id));
		}
		return out;
	}
	
	public abstract int countEntities();
	
	public abstract String[] getEntities();
}
