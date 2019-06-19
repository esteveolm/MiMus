package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;

import model.Document;
import model.EntityInstance;
import model.Transcription;

public class TranscriptionDao extends UnitDao<Transcription> {

	public TranscriptionDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Transcription unit) throws SQLException {
		String sql = "INSERT INTO Transcription "
				+ "(entity_instance_id, selected_text, form, coords_from, coords_to)"
				+ "VALUES (?,?,?,?,?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, unit.getItsEntity().getId());
		stmt.setString(2, unit.getSelectedText());
		stmt.setString(3, unit.getForm());
		stmt.setInt(4, unit.getCoords().x);
		stmt.setInt(5, unit.getCoords().y);
		return executeGetId(stmt);
	}

	@Override
	public void update(Transcription unit) throws SQLException, DaoNotImplementedException {
		// TODO Auto-generated method stub

	}

	@Override
	protected Transcription make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int instanceID = rs.getInt("entity_instance_id");
		String selectedText = rs.getString("selected_text");
		String form = rs.getString("form");
		Point coords = new Point(rs.getInt("coords_from"), rs.getInt("coords_to"));
		
		EntityInstance instance = 
				new InstanceDao(getConnection()).selectOne(instanceID);
		return new Transcription(instance, selectedText, form, coords, id);
	}

	@Override
	public String getTable() {
		return "Transcription";
	}

	public List<Transcription> select(Document doc) throws SQLException {
		List<Transcription> transcriptions = new ArrayList<>();
		String sql = "SELECT * FROM Transcription, EntityInstance "
				+ "WHERE Transcription.entity_instance_id=EntityInstance.id "
				+ "AND EntityInstance.document_id=" + doc.getId();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			transcriptions.add(make(rs));
		}
		return transcriptions;
	}

}
