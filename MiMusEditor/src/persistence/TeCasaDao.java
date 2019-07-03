package persistence;

import java.sql.Connection;

public class TeCasaDao extends RelationDao {

	public TeCasaDao(Connection conn) {
		super(conn);
	}

	@Override
	public String getTable() {
		return "TeCasa";
	}

	@Override
	public int countEntities() {
		return 2;
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"promotor_id", "casa_id"};
		return entities;
	}

}
