package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Document;
import model.EntityInstance;
import model.Relation;

public abstract class RelationDao extends UnitDao<Relation> {
	
	public RelationDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Relation unit) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

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
		
		EntityInstance ent1 = getEntity(1, id);
		EntityInstance ent2 = getEntity(2, id);
		
		return new Relation(doc, ent1, ent2, type, id);
	}

	@Override
	public abstract String getTable();
	
	public EntityInstance getEntity(int i, int id) throws SQLException {
		String sql = "SELECT id FROM " + getTable() + " WHERE relation_id=" + id;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			int entId = rs.getInt(getEntities()[i-1]);
			return new InstanceDao(getConnection()).selectOne(entId);
		}
		throw new SQLException();
	}

	public List<EntityInstance> getEntities(int id) throws SQLException {
		List<EntityInstance> out = new ArrayList<>();
		for (int i=0; i<countEntities(); i++) {
			out.add(getEntity(i+1,id));
		}
		return out;
	}
		
	public abstract int countEntities();
	
	public abstract String[] getEntities();
}
