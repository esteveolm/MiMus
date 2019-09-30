package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * This class encapsulates functionality to connect to the MiMus DB.
 * It picks the login data from config.properties file.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class DBUtils {
	
	/**
	 * Returns an open Connection to the MiMus DB using SQL connector.
	 * User and password are read from editor.user and editor.pass fields
	 * in config.properties.
	 */
	public static Connection connect() throws SQLException {
		try {
			Properties prop = readProperties();
			
			return connect(prop.getProperty("editor.user"), 
					prop.getProperty("editor.pass"));
		} catch(IOException e) {
			throw new SQLException();
		}
	}
	
	/**
	 * Returns an open Connection to the MiMus DB using SQL connector.
	 * User and password are passed as parameters.
	 */
	public static Connection connect(String user, String pass) throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mysql://161.116.21.174:3306/mimus"
				+ "?useUnicode=true&characterEncoding=UTF-8"
				+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
				user, pass);
	}
	
	/**
	 * Returns a Properties object with the login data from config.properties.
	 */
	public static Properties readProperties() throws IOException {
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFile file = corpus.getFile("config.properties");
		String path = file.getLocation().toString();
		
		Properties prop = new Properties();
		InputStream is = null;
		
		is = new FileInputStream(path);
		prop.load(is);
		
		return prop;
	}
	
	/**
	 * Updates the config.properties file with user and password
	 * passed as parameters.
	 */
	public static void writeProperties(String user, String pass) 
			throws IOException {
		Properties prop = readProperties();
		prop.setProperty("editor.user", user);
		prop.setProperty("editor.pass", pass);
	}
}
