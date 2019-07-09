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
		int id = rs.getInt("id");
		int relationTypeId = rs.getInt("relation_type_id");
		String type = Relation.TYPES[relationTypeId];
		int documentId = rs.getInt("document_id");
		Document doc = new DocumentDao(getConnection()).selectOne(documentId);
		
		Entity ent1 = getEntity(1, id);
		Entity ent2 = getEntity(2, id);
		
		return new Relation(doc, ent1, ent2, type, id);
	}
	
	@Override
	public void delete(Relation relation) throws SQLException {
		/* First find Relation ID */
		Statement selectStmt = getConnection().createStatement();
		String sql = "SELECT relation_id FROM " + getTable() + 
				" WHERE id=" + relation.getId();
		ResultSet rs = selectStmt.executeQuery(sql);
		rs.next();
		int relId = rs.getInt("relation_id");
		
		/* Then delete from specific table */
		super.delete(relation);
		
		/* Finally, delete from Relation table using ID recovered */
		Statement deleteStmt = getConnection().createStatement();
		String deleteSql = "DELETE FROM Relation WHERE id=" + relId;
		deleteStmt.executeUpdate(deleteSql);
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
