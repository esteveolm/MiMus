package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import model.MiMusEntry;

public class DeployDB {

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			InputStream is = null;
			
			try {
				is = new FileInputStream("config.properties");
				prop.load(is);
			} catch(IOException e) {
				System.out.println(e.toString());
			}
			
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://161.116.21.174:3306/Mimus"
					+ "?useUnicode=true&characterEncoding=UTF-8"
					+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
					prop.getProperty("admin.user"), prop.getProperty("admin.pass"));
			MiMusEntryReader reader = new MiMusEntryReader();
			File txtPath = new File("txt/");
			for (File f: txtPath.listFiles()) {
				String fName = f.getName();
				if (fName.endsWith(".txt")) {
					try {
						MiMusEntry entry = reader.read(f.getAbsolutePath());
						if (insertEntry(entry, conn)) {
							System.out.println("Inserted " + fName);
						} else {
							System.out.println("Could not insert " + fName);
						}
					} catch(NumberFormatException e) {
						System.out.println("Error: unexpected filename " + fName);
					}
				} else {
					System.out.println("Error: unexpected filename " + fName);
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private static boolean insertEntry(MiMusEntry entry, Connection conn) {
		Statement llenguaStmt = null;
		PreparedStatement selectMateriaStmt = null;
		PreparedStatement insertMateriaStmt = null;
		PreparedStatement stmt = null;
		try {
			/* Get llengua_id of associated language */
			llenguaStmt = conn.createStatement();
			ResultSet llenguaRS = llenguaStmt.executeQuery(
					"SELECT id FROM Llengua WHERE LlenguaName='" + 
					entry.getLanguage() + "'");
			if (llenguaRS.next()) {
				int llenguaId = llenguaRS.getInt("id");
				
				/* Insert Document entry using fields from model object + llengua_id */
				stmt = conn.prepareStatement(
						"INSERT INTO Document " +
						"(Numeracio, Any, Any2, Mes, Mes2, Dia, Dia2,"
						+ "hAny, hAny2, hMes, hMes2, hDia, hDia2,"
						+ "dAny, dAny2, dMes, dMes2, dDia, dDia2,"
						+ "Lloc, Lloc2, Regest, lib1Arxiu, lib1Serie, "
						+ "lib1Subserie, lib1Subserie2, lib1Numero, "
						+ "lib1Pagina, lib2Arxiu, lib2Serie, lib2Subserie, "
						+ "lib2Subserie2, lib2Numero, lib2Pagina, "
						+ "Edicions, Registres,"
						+ "Citacions, Transcripcio, Notes, llengua_id,"
						+ "StateAnnot, StateRev) "
						+ "VALUES "
						+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
						);
				stmt.setString(1, entry.getNumbering());
				stmt.setInt(2, entry.getDate().getYear1());
				stmt.setInt(3, entry.getDate().getYear2());
				stmt.setInt(4, entry.getDate().getMonth1());
				stmt.setInt(5, entry.getDate().getMonth2());
				stmt.setInt(6, entry.getDate().getDay1());
				stmt.setInt(7, entry.getDate().getDay2());
				stmt.setBoolean(8, entry.getDate().ishYear1());
				stmt.setBoolean(9, entry.getDate().ishYear2());
				stmt.setBoolean(10, entry.getDate().ishMonth1());
				stmt.setBoolean(11, entry.getDate().ishMonth2());
				stmt.setBoolean(12, entry.getDate().ishDay1());
				stmt.setBoolean(13, entry.getDate().ishDay2());
				stmt.setBoolean(14, entry.getDate().isuYear1());
				stmt.setBoolean(15, entry.getDate().isuYear2());
				stmt.setBoolean(16, entry.getDate().isuMonth1());
				stmt.setBoolean(17, entry.getDate().isuMonth2());
				stmt.setBoolean(18, entry.getDate().isuDay1());
				stmt.setBoolean(19, entry.getDate().isuDay2());
				stmt.setString(20, entry.getPlace1());
				stmt.setString(21, entry.getPlace2());
				stmt.setString(22, entry.getRegest());
				stmt.setString(23, entry.getLibrary().getArchive());
				stmt.setString(24, entry.getLibrary().getSeries());
				stmt.setString(25, entry.getLibrary().getSubseries1());
				stmt.setString(26, entry.getLibrary().getSubseries2());
				stmt.setString(27, entry.getLibrary().getNumber());
				stmt.setString(28, entry.getLibrary().getPage());
				stmt.setString(29, entry.getLibrary2().getArchive());
				stmt.setString(30, entry.getLibrary2().getSeries());
				stmt.setString(31, entry.getLibrary2().getSubseries1());
				stmt.setString(32, entry.getLibrary2().getSubseries2());
				stmt.setString(33, entry.getLibrary2().getNumber());
				stmt.setString(34, entry.getLibrary2().getPage());
				stmt.setString(35, entry.getEditions());
				stmt.setString(36, entry.getRegisters());
				stmt.setString(37, entry.getCitations());
				stmt.setString(38, entry.getTranscription());
				stmt.setString(39, "");	// XXX: Notes
				stmt.setInt(40, llenguaId);
				stmt.setInt(41, 0);
				stmt.setInt(42, 0);
				int documentResult = stmt.executeUpdate();
				
				if (documentResult > 0) {
					/* Get id of last inserted entry (the document) for the foreign key */
					Statement lastStmt = conn.createStatement();
					ResultSet lastRS = lastStmt.executeQuery("SELECT LAST_INSERT_ID()");
					
					if (lastRS.next()) {
						int documentId = lastRS.getInt(1);
						
						/* Insert every subject into HasMateria table */
						selectMateriaStmt = conn.prepareStatement(
								"SELECT id FROM Materia WHERE MateriaName = ?");
						
						for (String mat : entry.getSubjects()) {
							/* First get ID of Materia from its table */
							selectMateriaStmt.setString(1, mat);
							ResultSet materiaRS = selectMateriaStmt.executeQuery();
							if (materiaRS.next()) {
								int materiaId = materiaRS.getInt(1);
								
								/* Then fill HasMateria table with ID of Materia and Document */
								insertMateriaStmt = conn.prepareStatement(
										"INSERT INTO HasMateria (document_id, materia_id) " +
										"VALUES (?,?)");
								insertMateriaStmt.setInt(1, documentId);
								insertMateriaStmt.setInt(2, materiaId);
								int materiaResult = insertMateriaStmt.executeUpdate();
								if (materiaResult == 0) {
									System.out.println(
											"Error inserting HasMateria: " + mat +  
											" - " + documentId);
									return false;
								}
							} else {
								System.out.println("Error searching for Materia: " + mat);
								return false;
							}
						}
						return true;
					} else {
						System.out.println(
								"Error searching for last inserted ID of Document: "
								+ entry.getNumbering());
						return false;
					}
				} else {
					System.out.println("Error inserting Document: " 
							+ entry.getNumbering());
					return false;
				}
			}
			return false;
		} catch (SQLException e) {
			System.out.println("SQL Error at Dpcument " + entry.getNumbering());
			e.printStackTrace();
			return false;
		}
	}
}
