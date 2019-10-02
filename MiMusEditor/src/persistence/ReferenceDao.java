package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Bibliography;
import model.Document;
import model.MiMusReference;
import model.Note;

/**
 * Contains the specific implementation of Reference queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class ReferenceDao extends UnitDao<MiMusReference> {

	public ReferenceDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(MiMusReference unit) throws SQLException {
		String sql = "INSERT INTO referencia "
				+ "(id, ref_type, pages, document_id, bibliografia_id, note_id) "
				+ "VALUES (?,?,?,?,?,?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, unit.getId());
		stmt.setInt(2, unit.getType());
		stmt.setString(3, unit.getPage());
		stmt.setInt(4, unit.getItsDocument().getId());
		stmt.setInt(5, unit.getItsBiblio().getId());
		
		/* note_id foreign key can be null if no note is associated */
		if (unit.getItsNote() == null) {
			stmt.setNull(6, Types.BIGINT);
		} else {
			stmt.setInt(6, unit.getItsNote().getId());
		}
		return executeGetId(stmt);
	}

	@Override
	public void update(MiMusReference unit) throws SQLException, DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	@Override
	protected MiMusReference make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int type = rs.getInt("ref_type");
		String pages = rs.getString("pages");
		int biblioId = rs.getInt("bibliografia_id");
		Bibliography biblio = new BibliographyDao(getConnection())
				.selectOne(biblioId);
		int docId = rs.getInt("document_id");
		Document doc = new DocumentDao(getConnection()).selectOne(docId);
		int noteId = rs.getInt("note_id");
		Note note = null;
		if (noteId>0) {
			/* rs.getInt() returns 0 if field is null */
			note = new NoteDao(getConnection()).selectOne(noteId);
		}
		return new MiMusReference(biblio, doc, note, pages, type, id);
	}

	@Override
	public String getTable() {
		return "referencia";
	}

	public List<MiMusReference> select(Document doc) throws SQLException {
		List<MiMusReference> references = new ArrayList<>();
		String sql = "SELECT * FROM referencia WHERE document_id=" + doc.getId();
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			references.add(make(rs));
		}
		return references;
	}

}
