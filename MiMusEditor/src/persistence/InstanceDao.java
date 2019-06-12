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
import model.EntityInstance;

public class InstanceDao extends UnitDao<EntityInstance> {

	public InstanceDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(EntityInstance unit) throws SQLException {
		System.out.println(unit.getItsEntity().getId() + " " + unit.getItsDocument().getId());
		String sql = "INSERT INTO EntityInstance (entity_id, document_id)"
				+ "VALUES (?,?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, unit.getItsEntity().getId());
		stmt.setInt(2, unit.getItsDocument().getId());
		return executeGetId(stmt);
	}

	@Override
	public void update(EntityInstance unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected EntityInstance make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int entityId = rs.getInt("entity_id");
		int documentId = rs.getInt("document_id");
		
		/* To access appropriate DAO we first need to get the type of entity */
		AnyEntityDao anyDao = new AnyEntityDao(getConnection());
		String typeName = anyDao.selectType(entityId);
		System.out.println("Type: " + typeName);
		EntityDao<? extends Entity> specificDao = anyDao.getDao(typeName);
		Entity ent = specificDao.selectOne(entityId);
		
		Document doc = new DocumentDao(getConnection()).selectOne(documentId);
		return new EntityInstance(ent, doc, id);
	}

	@Override
	public String getTable() {
		return "EntityInstance";
	}
	
	public List<EntityInstance> select(Document doc) throws SQLException {
		List<EntityInstance> insts = new ArrayList<>();
		String sql = "SELECT * FROM EntityInstance WHERE document_id=" + doc.getId();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			insts.add(make(rs));
		}
		return insts;
	}

}
