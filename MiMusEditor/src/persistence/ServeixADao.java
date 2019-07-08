package persistence;

import java.sql.Connection;

public class ServeixADao extends RelationDao {

	public ServeixADao(Connection conn) {
		super(conn);
	}

	@Override
	public String getTable() {
		return "ServeixA";
	}

	@Override
	public int countEntities() {
		return 2;
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"artista_id", "promotor_id"};
		return entities;
	}

}
