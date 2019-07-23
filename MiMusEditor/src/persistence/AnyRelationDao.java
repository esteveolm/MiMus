package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.Document;
import model.Entity;
import model.Relation;

public class AnyRelationDao extends UnitDao<Relation> {

	public AnyRelationDao(Connection conn) {
		super(conn);
	}
	
	public List<Relation> select(Document doc) throws SQLException {
		List<Relation> rels = new ArrayList<>();
		String sql = "SELECT * FROM relation WHERE document_id=" + doc.getId();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			rels.add(make(rs));
		}
		return rels;
	}

	@Override
	public int insert(Relation unit) throws DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	public void update(Relation unit) throws DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}
	
	@Override
	public void delete(Relation unit) throws SQLException {
		/* Choose specific DAO based on type of Relation */
		int typeId = Arrays.asList(Relation.TYPES).indexOf(unit.getType())+1;
		RelationDao dao = getDaoDict().get(typeId);
		
		dao.delete(unit);
	}

	@Override
	protected Relation make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int specId = rs.getInt("relation_id");
		int typeId = rs.getInt("relation_type_id");
		String sqlType = "SELECT * FROM relation_types WHERE id=" + typeId;
		Statement stmtType = getConnection().createStatement();
		ResultSet rsType = stmtType.executeQuery(sqlType);
		if (rsType.next()) {
			String type = rsType.getString("relation_name");
			
			int docId = rs.getInt("document_id");
			Document doc = new DocumentDao(getConnection()).selectOne(docId);
			
			HashMap<Integer, RelationDao> typeToDao = getDaoDict();
			RelationDao specificDao = typeToDao.get(typeId);
			List<Entity> ents = specificDao.getEntities(id);
			return new Relation(doc, ents.get(0), ents.get(1), type, id, specId);
		}
		throw new SQLException();
	}
	
	private HashMap<Integer, RelationDao> getDaoDict() {
		HashMap<Integer, RelationDao> dict = new HashMap<>();
		dict.put(1, new TeOficiDao(getConnection()));
		dict.put(2, new TeCasaDao(getConnection()));
		dict.put(3, new ServeixADao(getConnection()));
		dict.put(4, new ResideixADao(getConnection()));
		dict.put(5, new MovimentDao(getConnection()));
		return dict;
	}

	@Override
	public String getTable() {
		return "relation";
	}
}
