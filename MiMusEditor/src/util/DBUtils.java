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

public class DBUtils {
	
	public static Connection connect() throws SQLException {
		try {
			Properties prop = readProperties();
			
			return connect(prop.getProperty("editor.user"), 
					prop.getProperty("editor.pass"));
		} catch(IOException e) {
			throw new SQLException();
		}
	}
	
	public static Connection connect(String user, String pass) throws SQLException {
		return DriverManager.getConnection(
				"jdbc:mysql://161.116.21.174:3306/mimus"
				+ "?useUnicode=true&characterEncoding=UTF-8"
				+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
				user, pass);
	}
	
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
	
	public static void writeProperties(String user, String pass) 
			throws IOException {
		Properties prop = readProperties();
		prop.setProperty("editor.user", user);
		prop.setProperty("editor.pass", pass);
	}
}
