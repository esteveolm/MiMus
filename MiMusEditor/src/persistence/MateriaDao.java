package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

import model.Materia;

/**
 * Contains the specific implementation of Materia queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
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
		Materia mat = new Materia(rs.getString("materia_name"));
		mat.setId(rs.getInt("id"));
		return mat;
	}

	@Override
	public String getTable() {
		return "materia";
	}
	
	public TreeMap<Integer, String> selectAllAsIdsToNames() throws SQLException {
		TreeMap<Integer, String> map = new TreeMap<>();
		List<Materia> materies = selectAll();
		for (Materia mat : materies) {
			map.put(mat.getId(), mat.getName());
		}
		return map;
	}
	
	public TreeMap<String, Integer> selectAllAsNamesToIds() throws SQLException {
		TreeMap<String, Integer> map = new TreeMap<>();
		List<Materia> materies = selectAll();
		for (Materia mat : materies) {
			map.put(mat.getName(), mat.getId());
		}
		return map;
	}

	@Override
	public void update(Materia unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

}
