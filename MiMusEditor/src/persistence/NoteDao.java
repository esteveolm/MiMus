package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Document;
import model.Note;
import model.NoteType;

/**
 * Contains the specific implementation of Note queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class NoteDao extends UnitDao<Note> {

	public NoteDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Note unit) throws SQLException {
		String sql = "INSERT INTO note(document_id, note_type_id, note_text) "
				+ "VALUES (?,?,?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, unit.getDoc().getId());
		
		/* Translate type text to type id */
		String typeSQL = "SELECT id FROM note_types WHERE note_name=?";
		PreparedStatement typeStmt = getConnection().prepareStatement(typeSQL);
		typeStmt.setString(1, unit.getType());
		ResultSet typeRS = typeStmt.executeQuery();
		if (typeRS.next()) {
			stmt.setInt(2, typeRS.getInt(1));
			stmt.setString(3, unit.getText());
			return executeGetId(stmt);
		}
		return -1;
	}
	
	public List<Note> select(Document doc) throws SQLException {
		String sql = "SELECT * FROM note WHERE document_id=" + doc.getId();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		List<Note> notes = new ArrayList<>();
		while (rs.next()) {
			Note n = make(rs);
			n.setDoc(doc);
			notes.add(n);
		}
		return notes;
	}

	public List<NoteType> getNoteTypes() throws SQLException {
		String sql = "SELECT id, note_name FROM note_types";
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		List<NoteType> note_types = new ArrayList<>();
		while (rs.next()) {
			note_types.add(new NoteType(rs.getInt(1),rs.getString(2)));
		}
		return note_types;
	}

	
	@Override
	protected Note make(ResultSet rs) throws SQLException {
		/* Get type name from ID */
		int id = rs.getInt(1);
		int typeId = rs.getInt(3);
		String sql = "SELECT note_name FROM note_types WHERE id=" + typeId;
		Statement stmt = getConnection().createStatement();
		ResultSet typeRS = stmt.executeQuery(sql);
		
		if (typeRS.next()) {
			String type = typeRS.getString(1);
			String text = rs.getString(4);
			return new Note(id, type, text, null);
		}
		throw new SQLException();
	}

	@Override
	public String getTable() {
		return "note";
	}

	/**
	 * Updates text in note
	 */
	@Override
	public void update(Note unit) throws SQLException {
		String sql = "UPDATE note SET note_text=? WHERE id = ?";
		
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setString(1, unit.getText());
		stmt.setInt(2,  unit.getId());
		int n = stmt.executeUpdate();
		if(n!=1) {
			throw new SQLException("Note was not updated");
		}		
	}
}
