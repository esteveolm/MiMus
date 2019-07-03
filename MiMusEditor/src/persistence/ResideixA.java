package persistence;

import java.sql.Connection;

public class ResideixA extends RelationDao {

	public ResideixA(Connection conn) {
		super(conn);
	}

	@Override
	public String getTable() {
		return "ResideixA";
	}

	@Override
	public int countEntities() {
		return 2;
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"artista_id", "lloc_id"};
		return entities;
	}

}
