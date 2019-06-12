package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Relation;

public class RelationDao extends UnitDao<Relation> {

	public RelationDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Relation unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(Relation unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Relation make(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTable() {
		return "Relation";
	}

}
