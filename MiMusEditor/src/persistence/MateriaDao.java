package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.TreeMap;

import model.Materia;

public class MateriaDao extends UnitDao<Materia> {

	public MateriaDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Materia unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	protected Materia make(ResultSet rs) throws SQLException {
		Materia mat = new Materia(rs.getString("MateriaName"));
		mat.setId(rs.getInt("id"));
		return mat;
	}

	@Override
	public String getTable() {
		return "Materia";
	}
	
	public TreeMap<Integer, String> selectAllAsMap() throws SQLException {
		TreeMap<Integer, String> map = new TreeMap<>();
		List<Materia> materies = selectAll();
		for (Materia m : materies) {
			System.out.println(m.getId() + " " + m.getName());
		}
		System.out.println("List size: " + materies.size());
		for (Materia mat : materies) {
			map.put(mat.getId(), mat.getName());
		}
		return map;
	}

}
