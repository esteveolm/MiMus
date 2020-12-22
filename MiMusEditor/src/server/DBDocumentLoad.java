package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import model.Document;
import persistence.DocumentDao;

/**
 * Script to deploy the MiMus server. It populates the MiMus DB
 * with all MiMus documents in txt format found in directory "txt".
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DBDocumentLoad {

	public static void main(String[] args) {
		System.out.println("Document Load starts...");
		try {
			/* Read DB user-pass from config.properties */
			Properties prop = new Properties();
			InputStream is = null;
			
			try {
				is = new FileInputStream("config.properties");
				prop.load(is);
			} catch(IOException e) {
				System.out.println(e.toString());
			}
			
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://" 
					+ prop.getProperty("host.name") 
					+ ":3306/mimus"
					+ "?useUnicode=true&characterEncoding=UTF-8"
					+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10"
					+ "&useJDBCCompliantTimezoneShift=true"
					+ "&useLegacyDatetimeCode=false"
					+ "&serverTimezone=UTC",
					prop.getProperty("admin.user"), prop.getProperty("admin.pass"));
			
			/* Reads all txt files in txt folder */
			MiMusEntryReader reader = new MiMusEntryReader();
			File txtPath = new File("txt/");
			File[] files = txtPath.listFiles();
			Arrays.sort(files);
			for (File f: files) {
				String fName = f.getName();
				System.out.println(fName + "loading...");
				if (fName.endsWith(".txt")) {
					try {
						/* Transform txt to Document, and insert to DB */
						Document doc = reader.read(f.getAbsolutePath());
						DocumentDao docDAO = new DocumentDao(conn);
						int docId = docDAO.insertGetNextId();
						doc.setId(docId);
						if (docDAO.insert(doc) > 0) {
							System.out.println(fName + "loaded");
						} else {
							System.out.println(fName + "could not be loaded");
						}
					} catch(NumberFormatException e) {
						System.out.println("Error: unexpected filename " + fName);
					}
				} else {
					System.out.println("Error: unexpected filename " + fName);
				}
			}
		} catch (SQLException e1) {
			System.out.println("Document Load Stopped");
			System.out.println("SQL Error:");
            System.out.println("SQLState: " + e1.getSQLState());
            System.out.println("Error Code: " + e1.getErrorCode());
            System.out.println("Message: " + e1.getMessage());
			e1.printStackTrace();
		}
		System.out.println("Document Load has finished");
	}
}
