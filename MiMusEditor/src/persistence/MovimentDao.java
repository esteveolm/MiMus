package persistence;

import java.sql.Connection;
import java.sql.SQLException;

import model.Relation;

public class MovimentDao extends RelationDao {

	public MovimentDao(Connection conn) {
		super(conn);
	}

	@Override
	public String getTable() {
		return "Moviment";
	}

	@Override
	public int countEntities() {
		return 3;
	}

	@Override
	public String[] getEntities() {
		String[] entities = {"artista_id", "origen_id", "destino_id"};
		return entities;
	}

	@Override
	protected int insertSpecificRelation(Relation unit, int commonId) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
