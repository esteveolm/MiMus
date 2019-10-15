package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import model.Document;
import model.Materia;
import model.MiMusDate;
import model.MiMusLibraryIdentifier;
import model.Note;

/**
 * Contains the specific implementation of Document queries and statements to
 * the DB.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DocumentDao extends UnitDao<Document> {

	public DocumentDao() throws SQLException {
		super();
	}
	
	public DocumentDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Document unit) throws SQLException {
		String[] insertColumns = {"id", "numeracio", "any1", "any2", "mes1", "mes2",
				"dia1", "dia2", "h_any1", "h_any2", "h_mes1", "h_mes2", "h_dia1",
				"h_dia2","d_any1", "d_any2", "d_mes1", "d_mes2", "d_dia1",
				"d_dia2", "lloc1", "lloc2","regest", "lib1_arxiu", "lib1_serie", 
				"lib1_subserie", "lib1_subserie2","lib1_numero", "lib1_pagina", 
				"lib2_arxiu", "lib2_serie", "lib2_subserie", "lib2_subserie2", 
				"lib2_numero", "lib2_pagina", "edicions", "registres",
				"citacions", "transcripcio", "llengua_id", "state_annot", 
				"state_rev"};
		String sql = "INSERT INTO " + getTable() + " (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += insertColumns[i] + ", ";
		}
		sql += insertColumns[insertColumns.length-1] + ") VALUES (";
		for (int i=0; i<insertColumns.length-1; i++) {
			sql += "?, ";
		}
		sql += "?)";
		PreparedStatement stmt = getConnection().prepareStatement(sql);
		stmt.setInt(1, unit.getId());
		stmt.setString(2, unit.getNumbering());
		stmt.setInt(3, unit.getDate().getYear1());
		stmt.setInt(4, unit.getDate().getYear2());
		stmt.setInt(5, unit.getDate().getMonth1());
		stmt.setInt(6, unit.getDate().getMonth2());
		stmt.setInt(7, unit.getDate().getDay1());
		stmt.setInt(8, unit.getDate().getDay2());
		stmt.setBoolean(9, unit.getDate().ishYear1());
		stmt.setBoolean(10, unit.getDate().ishYear2());
		stmt.setBoolean(11, unit.getDate().ishMonth1());
		stmt.setBoolean(12, unit.getDate().ishMonth2());
		stmt.setBoolean(13, unit.getDate().ishDay1());
		stmt.setBoolean(14, unit.getDate().ishDay2());
		stmt.setBoolean(15, unit.getDate().isuYear1());
		stmt.setBoolean(16, unit.getDate().isuYear2());
		stmt.setBoolean(17, unit.getDate().isuMonth1());
		stmt.setBoolean(18, unit.getDate().isuMonth2());
		stmt.setBoolean(19, unit.getDate().isuDay1());
		stmt.setBoolean(20, unit.getDate().isuDay2());
		stmt.setString(21, unit.getPlace1());
		stmt.setString(22, unit.getPlace2());
		stmt.setString(23, unit.getRegestText());
		stmt.setString(24, unit.getLibrary().getArchive());
		stmt.setString(25, unit.getLibrary().getSeries());
		stmt.setString(26, unit.getLibrary().getSubseries1());
		stmt.setString(27, unit.getLibrary().getSubseries2());
		stmt.setString(28, unit.getLibrary().getNumber());
		stmt.setString(29, unit.getLibrary().getPage());
		stmt.setString(30, unit.getLibrary2().getArchive());
		stmt.setString(31, unit.getLibrary2().getSeries());
		stmt.setString(32, unit.getLibrary2().getSubseries1());
		stmt.setString(33, unit.getLibrary2().getSubseries2());
		stmt.setString(34, unit.getLibrary2().getNumber());
		stmt.setString(35, unit.getLibrary2().getPage());
		stmt.setString(36, unit.getEditions());
		stmt.setString(37, unit.getRegisters());
		stmt.setString(38, unit.getCitations());
		stmt.setString(39, unit.getTranscriptionText());
		stmt.setInt(40, unit.getLanguage());
		stmt.setInt(41, unit.getStateAnnotIdx());
		stmt.setInt(42, unit.getStateRevIdx());
		
		int docResult = stmt.executeUpdate();
		if (docResult>0) {
			boolean notesOk = true;
			for (Note note : unit.getNotes()) {
				int noteResult = new NoteDao(getConnection()).insert(note);
				if (noteResult<1) {
					notesOk = false;
					break;
				}
			}
			if (notesOk) {
				return docResult;
			}
		}
		return -1;
	}

	protected Document make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String numeracio = rs.getString("numeracio");
		int any = rs.getInt("any1");
		int any2 = rs.getInt("any2");
		int mes = rs.getInt("mes1");
		int mes2 = rs.getInt("mes2");
		int dia = rs.getInt("dia1");
		int dia2 = rs.getInt("dia2");
		boolean hany = rs.getBoolean("h_any1");
		boolean hany2 = rs.getBoolean("h_any2");
		boolean hmes = rs.getBoolean("h_mes1");
		boolean hmes2 = rs.getBoolean("h_mes2");
		boolean hdia = rs.getBoolean("h_dia1");
		boolean hdia2 = rs.getBoolean("h_dia2");
		boolean dany = rs.getBoolean("d_any1");
		boolean dany2 = rs.getBoolean("d_any2");
		boolean dmes = rs.getBoolean("d_mes1");
		boolean dmes2 = rs.getBoolean("d_mes2");
		boolean ddia = rs.getBoolean("d_dia1");
		boolean ddia2 = rs.getBoolean("d_dia2");
		String lloc = rs.getString("lloc1");
		String lloc2 = rs.getString("lloc2");
		String regest = rs.getString("regest");
		String lib1Arxiu = rs.getString("lib1_arxiu");
		String lib1Serie = rs.getString("lib1_serie");
		String lib1Subserie = rs.getString("lib1_subserie");
		String lib1Subserie2 = rs.getString("lib1_subserie2");
		String lib1Numero = rs.getString("lib1_numero");
		String lib1Pagina = rs.getString("lib1_pagina");
		String lib2Arxiu = rs.getString("lib2_arxiu");
		String lib2Serie = rs.getString("lib2_serie");
		String lib2Subserie = rs.getString("lib2_subserie");
		String lib2Subserie2 = rs.getString("lib2_subserie2");
		String lib2Numero = rs.getString("lib2_numero");
		String lib2Pagina = rs.getString("lib2_pagina");
		String edicions = rs.getString("edicions");
		String registres = rs.getString("registres");
		String citacions = rs.getString("citacions");
		String transcripcio = rs.getString("transcripcio");
		int llenguaId = rs.getInt("llengua_id");
		int stateAnnot = rs.getInt("state_annot");
		int stateRev = rs.getInt("state_rev");
		System.out.println("Llengua ID:" + llenguaId);
		/* Query to Llengua table to get it from ID */
		String sql = "SELECT llengua_name FROM llengua WHERE id=" + llenguaId;
		Statement stmt = getConnection().createStatement();
		ResultSet llenguaRS = stmt.executeQuery(sql);
		if (llenguaRS.next()) {
			String llengua = llenguaRS.getString("llengua_name");
			
			/* Query to Materia and HasMateria tables */
			MateriaDao matDao = new MateriaDao(getConnection());
			TreeMap<Integer,String> idsToMateries = matDao.selectAllAsIdsToNames();
			System.out.println("TreeMap size: " + idsToMateries.size());
			
			sql = "SELECT materia_id FROM has_materia WHERE document_id=" + id;
			stmt = getConnection().createStatement();
			ResultSet hasMateriaRS = stmt.executeQuery(sql);
			
			List<Materia> materies = new ArrayList<>();
			while (hasMateriaRS.next()) {
				int materiaId = hasMateriaRS.getInt("materia_id");
				Materia newMat = new Materia(idsToMateries.get(materiaId));
				newMat.setId(materiaId);
				materies.add(newMat);
				System.out.println("Materia: " + materiaId);
			}
			
			
			/* Make Document from its parts */
			Document doc = new Document();
			doc.setId(id);
			doc.setNumbering(numeracio);
			
			/* Query to Note table */
			List<Note> notes = new NoteDao(getConnection()).select(doc);
			
			MiMusDate date = new MiMusDate();
			date.setInterval(any2>0 || mes2>0 || dia2>0);
			System.out.println(date.isInterval() + " " + any2 + " " + mes2 + " " + dia2);
			date.setYear1(any);
			date.setMonth1(mes);
			date.setDay1(dia);
			date.setYear2(any2);
			date.setMonth2(mes2);
			date.setDay2(dia2);
			date.sethYear1(hany);
			date.sethMonth1(hmes);
			date.sethDay1(hdia);
			date.sethYear2(hany2);
			date.sethMonth2(hmes2);
			date.sethDay2(hdia2);
			date.setuYear1(dany);
			date.setuMonth1(dmes);
			date.setuDay1(ddia);
			date.setuYear2(dany2);
			date.setuMonth2(dmes2);
			date.setuDay2(ddia2);
			
			doc.setDate(date);
			doc.setPlace1(lloc);
			doc.setPlace2(lloc2);
			doc.setRegestText(regest);
			
			MiMusLibraryIdentifier library = new MiMusLibraryIdentifier();
			library.setArchive(lib1Arxiu);
			library.setSeries(lib1Serie);
			library.setSubseries1(lib1Subserie);
			library.setSubseries2(lib1Subserie2);
			library.setNumber(lib1Numero);
			library.setPage(lib1Pagina);
			
			MiMusLibraryIdentifier library2 = new MiMusLibraryIdentifier();
			library2.setArchive(lib2Arxiu);
			library2.setSeries(lib2Serie);
			library2.setSubseries1(lib2Subserie);
			library2.setSubseries2(lib2Subserie2);
			library2.setNumber(lib2Numero);
			library2.setPage(lib2Pagina);
			
			doc.setLibrary(library);
			doc.setLibrary2(library2);
			doc.setEditions(edicions);
			doc.setRegisters(registres);
			doc.setCitations(citacions);
			doc.setTranscriptionText(transcripcio);
			doc.setNotes(notes);
			doc.setLanguage(llengua);
			doc.setSubjects(materies);
			doc.setStateAnnotIdx(stateAnnot);
			doc.setStateRevIdx(stateRev);
			System.out.println("Materies: " + doc.getSubjects().size());
			return doc;
		}
		throw new SQLException();
	}
	
	@Override
	public void update(Document unit) throws SQLException {
		/* We use transactional mode because the update happens in stages */
		getConnection().setAutoCommit(false);
		
		/* First update state */
		int stateAnnot = unit.getStateAnnotIdx();
		int stateRev = unit.getStateRevIdx();
		String sql = "UPDATE document SET state_annot=?, state_rev=? WHERE id=?";
		PreparedStatement stateStmt = getConnection().prepareStatement(sql);
		stateStmt.setInt(1, stateAnnot);
		stateStmt.setInt(2, stateRev);
		stateStmt.setInt(3, unit.getId());
		int stateRS = stateStmt.executeUpdate();
		boolean ok = false;
		if (stateRS > 0) {
			/* Get llengua_id from Llengua String */
			sql = "SELECT id FROM llengua WHERE llengua_name=?";
			PreparedStatement llenguaStmt = getConnection().prepareStatement(sql);
			llenguaStmt.setString(1, unit.getLanguageStr());
			ResultSet llenguaRS = llenguaStmt.executeQuery();
			if (llenguaRS.next()) {
				int llenguaId = llenguaRS.getInt("id");
				
				sql = "UPDATE document SET llengua_id=? WHERE id=?";
				PreparedStatement stmt1 = getConnection().prepareStatement(sql);
				stmt1.setInt(1, llenguaId);
				stmt1.setInt(2, unit.getId());
				int result1 = stmt1.executeUpdate();
				if (result1 > 0) {
					sql = "DELETE FROM has_materia WHERE document_id=?";
					PreparedStatement stmt2 = getConnection().prepareStatement(sql);
					stmt2.setInt(1, unit.getId());
					stmt2.executeUpdate();
					
					int result2 = 0;
					for (Materia mat: unit.getSubjects()) {
						sql = "INSERT INTO has_materia (materia_id, document_id)"
								+ " VALUES (?,?)";
						PreparedStatement stmt3 = getConnection().prepareStatement(sql);
						stmt3.setInt(1, mat.getId());
						stmt3.setInt(2, unit.getId());
						result2 += stmt3.executeUpdate();
					}
					if (result2 == unit.getSubjects().size()) {
						getConnection().commit();
						System.out.println("Document updated correctly.");
						ok = true;
					} else {
						System.out.println("Could not update Materies properly.");
					}
				} else {
					System.out.println("Could not perform update; Llengua not updated.");
				}
			} else {
				System.out.println("Could not perform update; Llengua not found.");
			}
		} else {
			System.out.println("Could not perform update due to State.");
		}
		if (!ok) {
			/* If didn't finish properly, rollback */
			getConnection().rollback();
		}
		getConnection().setAutoCommit(true);
	}
	
	public List<Document> selectWhereEntity(int id) throws SQLException {
		String sql = "SELECT DISTINCT document.* "
				+ "FROM document, entity_instance, entity "
				+ "WHERE document.id=entity_instance.document_id "
				+ "AND entity_instance.entity_id="+id;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<Document> docs = new ArrayList<>();
		while (rs.next()) {
			docs.add(make(rs));
		}
		return docs;
	}
	
	public List<Document> selectWhereBiblio(int id) throws SQLException {
		String sql = "SELECT DISTINCT document.* "
				+ "FROM document, referencia, bibliografia "
				+ "WHERE document.id=referencia.document_id "
				+ "AND referencia.bibliografia_id=" + id;
		Statement stmt = getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<Document> docs = new ArrayList<>();
		while (rs.next()) {
			docs.add(make(rs));
		}
		return docs;
	}
	
	@Override
	public String getTable() {
		return "document";
	}
}
