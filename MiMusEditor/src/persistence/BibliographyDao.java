package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.Bibliography;

public class BibliographyDao extends UnitDao<Bibliography> {

	public BibliographyDao(Connection conn) {
		super(conn);
	}

	@Override
	public List<Bibliography> selectAll() throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bibliography selectOne(String... criteria) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(Bibliography unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Bibliography unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub
		
	}

}
