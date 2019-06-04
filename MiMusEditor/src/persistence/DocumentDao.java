package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import model.Document;
import model.MiMusDate;
import model.MiMusLibraryIdentifier;

public class DocumentDao extends UnitDao<Document> {

	public DocumentDao(Connection conn) {
		super(conn);
	}

	@Override
	public int insert(Document unit) throws SQLException {
		String[] insertColumns = {"Numeracio", "Any", "Any2", "Mes", "Mes2",
				"Dia", "Dia2", "hAny", "hAny2", "hMes", "hMes2", "hDia", "hDia2",
				"dAny", "dAny2", "dMes", "dMes2", "dDia", "dDia2", "Lloc", "Lloc2",
				"Regest", "lib1Arxiu", "lib1Serie", "lib1Subserie", "lib1Subserie2",
				"lib1Numero", "lib1Pagina", "lib2Arxiu", "lib2Serie", "lib2Subserie",
				"lib2Subserie2", "lib2Numero", "lib2Pagina", "Edicions", "Registres",
				"Citacions", "Transcripcio", "Notes", "Llengua", "Materies"};
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
		stmt.setString(1, unit.getNumbering());
		stmt.setInt(2, unit.getDate().getYear1());
		stmt.setInt(3, unit.getDate().getYear2());
		stmt.setInt(4, unit.getDate().getMonth1());
		stmt.setInt(5, unit.getDate().getMonth2());
		stmt.setInt(6, unit.getDate().getDay1());
		stmt.setInt(7, unit.getDate().getDay2());
		stmt.setBoolean(8, unit.getDate().ishYear1());
		stmt.setBoolean(9, unit.getDate().ishYear2());
		stmt.setBoolean(10, unit.getDate().ishMonth1());
		stmt.setBoolean(11, unit.getDate().ishMonth2());
		stmt.setBoolean(12, unit.getDate().ishDay1());
		stmt.setBoolean(13, unit.getDate().ishDay2());
		stmt.setBoolean(14, unit.getDate().isuYear1());
		stmt.setBoolean(15, unit.getDate().isuYear2());
		stmt.setBoolean(16, unit.getDate().isuMonth1());
		stmt.setBoolean(17, unit.getDate().isuMonth2());
		stmt.setBoolean(18, unit.getDate().isuDay1());
		stmt.setBoolean(19, unit.getDate().isuDay2());
		stmt.setString(20, unit.getPlace1());
		stmt.setString(21, unit.getPlace2());
		stmt.setString(22, unit.getRegestText());
		stmt.setString(23, unit.getLibrary().getArchive());
		stmt.setString(24, unit.getLibrary().getSeries());
		stmt.setString(25, unit.getLibrary().getSubseries1());
		stmt.setString(26, unit.getLibrary().getSubseries2());
		stmt.setString(27, unit.getLibrary().getNumber());
		stmt.setString(28, unit.getLibrary().getPage());
		stmt.setString(29, unit.getLibrary2().getArchive());
		stmt.setString(30, unit.getLibrary2().getSeries());
		stmt.setString(31, unit.getLibrary2().getSubseries1());
		stmt.setString(32, unit.getLibrary2().getSubseries2());
		stmt.setString(33, unit.getLibrary2().getNumber());
		stmt.setString(34, unit.getLibrary2().getPage());
		stmt.setString(35, unit.getEditions());
		stmt.setString(36, unit.getRegisters());
		stmt.setString(37, unit.getCitations());
		stmt.setString(38, unit.getTranscriptionText());
		stmt.setString(39, String.join("$", unit.getNotes()));
		stmt.setString(40, unit.getLanguage());
		stmt.setString(41, String.join("$", unit.getSubjects()));
		
		return executeGetId(stmt);
	}

	@Override
	public void delete(Document unit) throws DaoNotImplementedException {
		throw new DaoNotImplementedException();
	}

	protected Document make(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String numeracio = rs.getString("Numeracio");
		int any = rs.getInt("Any");
		int any2 = rs.getInt("Any2");
		int mes = rs.getInt("Mes");
		int mes2 = rs.getInt("Mes2");
		int dia = rs.getInt("Dia");
		int dia2 = rs.getInt("Dia2");
		boolean hany = rs.getBoolean("hAny");
		boolean hany2 = rs.getBoolean("hAny2");
		boolean hmes = rs.getBoolean("hMes");
		boolean hmes2 = rs.getBoolean("hMes2");
		boolean hdia = rs.getBoolean("hDia");
		boolean hdia2 = rs.getBoolean("hDia2");
		boolean dany = rs.getBoolean("dAny");
		boolean dany2 = rs.getBoolean("dAny2");
		boolean dmes = rs.getBoolean("dMes");
		boolean dmes2 = rs.getBoolean("dMes2");
		boolean ddia = rs.getBoolean("dDia");
		boolean ddia2 = rs.getBoolean("dDia2");
		String lloc = rs.getString("Lloc");
		String lloc2 = rs.getString("Lloc2");
		String regest = rs.getString("Regest");
		String lib1Arxiu = rs.getString("lib1Arxiu");
		String lib1Serie = rs.getString("lib1Serie");
		String lib1Subserie = rs.getString("lib1Subserie");
		String lib1Subserie2 = rs.getString("lib1Subserie2");
		String lib1Numero = rs.getString("lib1Numero");
		String lib1Pagina = rs.getString("lib1Pagina");
		String lib2Arxiu = rs.getString("lib2Arxiu");
		String lib2Serie = rs.getString("lib2Serie");
		String lib2Subserie = rs.getString("lib2Subserie");
		String lib2Subserie2 = rs.getString("lib2Subserie2");
		String lib2Numero = rs.getString("lib2Numero");
		String lib2Pagina = rs.getString("lib2Pagina");
		String edicions = rs.getString("Edicions");
		String registres = rs.getString("Registres");
		String citacions = rs.getString("Citacions");
		String transcripcio = rs.getString("Transcripcio");
		String notes = rs.getString("Notes");
		int llenguaId = rs.getInt("llengua_id");

		/* Query to Llengua table to get it from ID */
		String sql = "SELECT LlenguaName FROM Llengua WHERE id=" + llenguaId;
		Statement stmt = getConnection().createStatement();
		ResultSet llenguaRS = stmt.executeQuery(sql);
		if (llenguaRS.next()) {
			String llengua = llenguaRS.getString("LlenguaName");
			
			/* Query to Materia and HasMateria tables */
			MateriaDao matDao = new MateriaDao(getConnection());
			TreeMap<Integer,String> idsToMateries = matDao.selectAllAsMap();
			System.out.println("TreeMap size: " + idsToMateries.size());
			
			sql = "SELECT materia_id FROM HasMateria WHERE document_id=" + id;
			stmt = getConnection().createStatement();
			ResultSet hasMateriaRS = stmt.executeQuery(sql);
			
			List<String> materies = new ArrayList<>();
			while (hasMateriaRS.next()) {
				int materiaId = hasMateriaRS.getInt("materia_id");
				materies.add(idsToMateries.get(materiaId));
				System.out.println("Materia id: " + materiaId);
			}
						
			Document doc = new Document();
			doc.setId(id);
			doc.setNumbering(numeracio);
			
			MiMusDate date = new MiMusDate();
			date.setInterval(any2>0 || mes2>0 || dia2>0);
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
			doc.setNotes(Arrays.asList(notes.split("$")));
			doc.setLanguage(llengua);
			doc.setSubjects(materies);
			System.out.println("Materies: " + doc.getSubjects().size());
			return doc;
		}
		throw new SQLException();
	}

	@Override
	public String getTable() {
		return "Document";
	}
}
