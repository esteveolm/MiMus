package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Unit;

public abstract class UnitDao<U extends Unit> {
	
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
		for (U u : elems) {
			System.out.println(getTable() + " id: " + u.getId());
		}
		return elems;
	}
	
	public U selectOne() throws SQLException {
		String sql = "SELECT * FROM " + getTable();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return make(rs);
		}
		throw new SQLException();
	}
	
	public U selectOne(int id) throws SQLException {
		String sql = "SELECT * FROM " + getTable() + " WHERE id=" + id;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return make(rs);
		}
		throw new SQLException();
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
	
	/**
	 * Inserts a model object of type U into its corresponding DB as specified
	 * by the DAO implementation. Returns the auto-incremental ID of the
	 * inserted row in the table, if the data was successfully inserted, or
	 * -1 otherwise.
	 */
	public abstract int insert(U unit) 
			throws SQLException, DaoNotImplementedException;
	
	protected int executeGetId(PreparedStatement stmt) throws SQLException {
		int success = stmt.executeUpdate();
		if (success == 1) {
			Statement selectStmt = getConnection().createStatement();
			ResultSet id = selectStmt.executeQuery("SELECT LAST_INSERT_ID()");
			id.next();
			return id.getInt(1);
		}
		return -1;
	}
	
	public void delete(U unit) throws SQLException {
		Statement stmt = getConnection().createStatement();
		String sql = "DELETE FROM " + getTable() + 
				" WHERE id=" + unit.getId();
		stmt.executeUpdate(sql);
	}
	
	public abstract void update(U unit) 
			throws SQLException, DaoNotImplementedException;
	
	protected abstract U make(ResultSet rs) throws SQLException;
	
	public abstract String getTable();
}
