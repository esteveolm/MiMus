package server;

//import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.Arrays;
import java.util.Properties;

//import model.Document;
import persistence.DocumentDao;

/**
 * Script to deploy the MiMus server. It populates the MiMus DB
 * with all MiMus documents in txt format found in directory "txt".
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DBDocumentDelete {

	public static void main(String[] args) {
		System.out.println("Document Delete starts...");
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
			
			int idDocument = 0;
			
			if (args.length > 0) {
				String cadena;
				cadena = args[0];
		        try {
		        	idDocument = Integer.parseInt(cadena);
		            System.out.println("Document ID: " + idDocument);
		        } catch (NumberFormatException excepcion) {
		            System.out.println(cadena + " no es un id valid");	        	
		        }				
			}
			

			if (idDocument > 0) {
				System.out.println("Document ID: " + idDocument + " deleting...");
				DocumentDao myDocument = new DocumentDao(conn);
				myDocument.deleteWithDependencies(idDocument);
				System.out.println("Document ID: " + idDocument + " deleted");
			}
			
			
		} catch (SQLException e1) {
			System.out.println("Document Delete Stopped");
			System.out.println("SQL Error:");
            System.out.println("SQLState: " + e1.getSQLState());
            System.out.println("Error Code: " + e1.getErrorCode());
            System.out.println("Message: " + e1.getMessage());
			e1.printStackTrace();
		}
		System.out.println("Document Delete has finished");
	}
}
