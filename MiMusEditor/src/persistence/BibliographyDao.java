package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Bibliography;

public class BibliographyDao extends UnitDao<Bibliography> {

	public BibliographyDao(Connection conn) {
		super(conn);
	}

	@Override
	public void insert(Bibliography unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Bibliography unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}
	
	protected Bibliography make(ResultSet rs) throws SQLException {
		return null;
	}

	@Override
	public String getTable() {
		return "Bibliography";
	}

}
