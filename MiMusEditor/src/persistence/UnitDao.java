package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.JSchException;

import model.Unit;
import util.DBUtils;

/**
 * Main class of the persistence package. All DAO patterns
 * in the project inherit from UnitDao, because they are
 * specific DAOs for different objects reflected in different
 * tables of the database.
 * 
 * @author Javier Beltrán Jorba
 *
 * @param <U> the specific Unit
 */
public abstract class UnitDao<U extends Unit> {
	
	/* DAO requires a Connection to DB to perform queries and changes */
	private Connection conn;
	
	public UnitDao() throws SQLException {
		setConnection(DBUtils.connect());
	}
	
	public UnitDao(Connection conn) {
		setConnection(conn);
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Selects all DB entries in table associated to class U.
	 */
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
	
	/**
	 * Selects one DB entry in table associated to class U,
	 * arbitrarily.
	 */
	public U selectOne() throws SQLException {
		String sql = "SELECT * FROM " + getTable();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return make(rs);
		}
		throw new SQLException();
	}
	
	/**
	 * Selects the DB entry in the table associated to class U
	 * that has ID <id>, or null if none has.
	 */
	public U selectOne(int id) throws SQLException {
		String sql = "SELECT * FROM " + getTable() + " WHERE id=" + id;
		System.out.println("SQL: " + sql);
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
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
	
	/**
	 * Given a SQL statement ready to be executed, it executes it and
	 * returns the last ID inserted by the user. This means that, if the
	 * statement was an INSERT, this method returns the ID the DB chose
	 * for the element inserted.
	 */
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
	
	/**
	 * Deletes unit <unit> from the table based on its ID.
	 */
	public void delete(U unit) throws SQLException {
		Statement stmt = getConnection().createStatement();
		String sql = "DELETE FROM " + getTable() + 
				" WHERE id=" + unit.getId();
		stmt.executeUpdate(sql);
	}
	
	/**
	 * Given a ResultSet from executing a query, transforms it
	 * into a model object of class U.
	 */
	protected abstract U make(ResultSet rs) throws SQLException;
	
	/**
	 * Returns the name of the DB table this DAO is using.
	 */
	public abstract String getTable();

	/**
	 * Given unit <unit> with a certain ID, this method finds an
	 * entry with the same ID and replaces it with the data from <unit>.
	 */
	public abstract void update(U unit) 
			throws SQLException, DaoNotImplementedException;
}
