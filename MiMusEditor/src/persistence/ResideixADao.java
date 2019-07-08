package persistence;

import java.sql.Connection;

public class ResideixADao extends RelationDao {

	public ResideixADao(Connection conn) {
		super(conn);
	}

	@Override
	public String getTable() {
		return "ResideixADao";
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
