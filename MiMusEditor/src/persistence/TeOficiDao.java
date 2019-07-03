package persistence;

import java.sql.Connection;

public class TeOficiDao extends RelationDao {
	
	public TeOficiDao(Connection conn) {
		super(conn);
	}
	
	@Override
	public int countEntities() {
		return 2;
	}

	@Override
	public String getTable() {
		return "TeOfici";
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"artista_id", "ofici_id"};
		return entities;
	}

}
