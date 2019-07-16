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
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFile file = corpus.getFile("config.properties");
		String path = file.getLocation().toString();
		
		Properties prop = new Properties();
		InputStream is = null;
		
		try {
			is = new FileInputStream(path);
			prop.load(is);
		} catch(IOException e) {
			System.out.println(e.toString());
		}
		
		return DriverManager.getConnection(
				"jdbc:mysql://161.116.21.174:3306/Mimus"
				+ "?useUnicode=true&characterEncoding=UTF-8"
				+ "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
				prop.getProperty("editor.user"), prop.getProperty("editor.pass"));
	}
	
}
