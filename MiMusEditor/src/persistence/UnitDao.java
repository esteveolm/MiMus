package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

	public List<U> selectAll() throws SQLException {
		String sql = "SELECT * FROM " + getTable();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		List<U> elems = new ArrayList<>();
		while (rs.next()) {
			elems.add(make(rs));
		}
		return elems;
	}
	
	public U selectOne(String... criteria) throws SQLException {
		String sql = "SELECT * FROM " + getTable() + " LIMIT 1";
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while (rs.next()) {
			return make(rs);
		}
		return null;
	}
	
	public abstract void insert(U unit) 
			throws SQLException, DaoNotImplementedException;
	
	public abstract void delete(U unit) 
			throws SQLException, DaoNotImplementedException;
	
	protected abstract U make(ResultSet rs) throws SQLException;
	
	public abstract String getTable();
}
