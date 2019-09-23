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
					"jdbc:mysql://161.116.21.174:3306/mimus"
					+ "?useUnicode=true&characterEncoding=UTF-8"
					+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
					prop.getProperty("admin.user"), prop.getProperty("admin.pass"));
			MiMusEntryReader reader = new MiMusEntryReader();
			File txtPath = new File("txt/");
			File[] files = txtPath.listFiles();
			Arrays.sort(files);
			for (File f: files) {
				String fName = f.getName();
				if (fName.endsWith(".txt")) {
					try {
						Document doc = reader.read(f.getAbsolutePath());
						if (new DocumentDao(conn).insert(doc) > 0) {
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
}
