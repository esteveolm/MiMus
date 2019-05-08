package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class UnitDao<U> {
	
	private Connection conn;
	
	public UnitDao(Connection conn) {
		setConnection(conn);
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public abstract List<U> selectAll() 
			throws SQLException, DaoNotImplementedException;
	
	public abstract U selectOne(String... criteria) 
			throws SQLException, DaoNotImplementedException;
	
	public abstract void insert(U unit) 
			throws SQLException, DaoNotImplementedException;
	
	public abstract void delete(U unit) 
			throws SQLException, DaoNotImplementedException;
}
