package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.TreeMap;

import model.Materia;

public class MateriaDAO extends UnitDao<Materia> {

	public MateriaDAO(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Materia unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	protected Materia make(ResultSet rs) throws SQLException {
		String sql = "SELECT MateriaName FROM Materia";
		Statement stmt = getConnection().createStatement();
		ResultSet materiaRS = stmt.executeQuery(sql);
		if (materiaRS.next()) {
			return new Materia(materiaRS.getString(1));
		}
		throw new SQLException();
	}

	@Override
	public String getTable() {
		return "Materia";
	}
	
	public TreeMap<Integer, String> selectAllAsMap() throws SQLException {
		TreeMap<Integer, String> map = new TreeMap<>();
		List<Materia> materies = selectAll();
		for (Materia mat : materies) {
			map.put(mat.getId(), mat.getName());
		}
		return map;
	}

}
