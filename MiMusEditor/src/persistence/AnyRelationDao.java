package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import model.Document;
import model.EntityInstance;
import model.Relation;

public class AnyRelationDao extends UnitDao<Relation> {

	public AnyRelationDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Relation unit) throws SQLException, DaoNotImplementedException {
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
		int typeId = rs.getInt("relation_type_id");
		String sqlType = "SELECT * FROM RelationTypes WHERE id=" + typeId;
		Statement stmtType = getConnection().createStatement();
		ResultSet rsType = stmtType.executeQuery(sqlType);
		if (rsType.next()) {
			String type = rsType.getString("EntityName");
			
			int docId = rs.getInt("document_id");
			Document doc = new DocumentDao(getConnection()).selectOne(docId);
			
			HashMap<Integer, RelationDao> typeToDao = getDaoDict();
			RelationDao specificDao = typeToDao.get(typeId);
			List<EntityInstance> ents = specificDao.getEntities(id);
			return new Relation(doc, ents.get(0), ents.get(1), type, id);
		}
		throw new SQLException();
	}
	
	private HashMap<Integer, RelationDao> getDaoDict() {
		HashMap<Integer, RelationDao> dict = new HashMap<>();
		dict.put(1, new TeOficiDao(getConnection()));
		return dict;
	}

	@Override
	public String getTable() {
		return "Relation";
	}
}
