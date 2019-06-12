package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.MiMusReference;

public class ReferenceDao extends UnitDao<MiMusReference> {

	public ReferenceDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(MiMusReference unit) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(MiMusReference unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub

	}

	@Override
	protected MiMusReference make(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTable() {
		return "Referencia";
	}

}
