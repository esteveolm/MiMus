package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.MiMusEntry;

public class DeployDB {

	public static void main(String[] args) {
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/Mimus?serverTimezone=UTC", 
					"mimus01", "colinet19");
			MiMusEntryReader reader = new MiMusEntryReader();
			File txtPath = new File("txt/");
			for (File f: txtPath.listFiles()) {
				String fName = f.getName();
				if (fName.endsWith(".txt")) {
					try {
						int fNum = Integer.parseInt(
								fName.substring(0, fName.lastIndexOf('.')-1));
						MiMusEntry entry = reader.read(f.getAbsolutePath());
						int result = insertEntry(entry, conn);
						if (result>0) {
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
	
	private static int insertEntry(MiMusEntry entry, Connection conn) {
		PreparedStatement stmt = null;
		try {
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
					+ "Citacions, Transcripcio, Notes, Llengua, Materies) "
					+ "VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
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
			stmt.setString(40, entry.getLanguage());
			stmt.setString(41, "");	// TODO: List of subjects?
			return stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error at " + entry.getNumbering());
			e.printStackTrace();
			return -1;
		}
	}
}
