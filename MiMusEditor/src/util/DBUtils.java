package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
//import com.jcraft.jsch.*;

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
	
	private static String user;
	private static String password;
	
	/**
	 * Returns an open Connection to the MiMus DB using SQL connector.
	 * User and password must already be logged in 
	 * host.name is in config.properties.
	 */
	public static Connection connect() throws SQLException {
		try {
			Properties prop = readProperties();
			return connect(user, password,
					prop.getProperty("host.name"));
		} catch(IOException e) {
			e.printStackTrace();
			throw new SQLException("Error reading DB properties");
		}
	}
	
	/**
	 * Returns an open Connection to the MiMus DB using SQL connector.
	 * User and password are passed as parameters.
	 */
	public static Connection connect(String user, String pass, String host)
			throws SQLException {
//		JSch jsch = new JSch();
//		Properties prop;
//		try {
//			prop = readProperties();
//			HostKey hk = new HostKey(host, prop.getProperty("host.key").getBytes());
//			jsch.getHostKeyRepository().add(hk, null);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Session session = jsch.getSession(user, host);
//		session.setPassword(pass);
//		session.connect();
//		int forwardedPort = session.setPortForwardingL(0, host, 3306);
//		
//		
//		String conn = "jdbc:mysql://localhost:" 
//				+ forwardedPort
//				+ "/mimus"
//				+ "?useUnicode=true&characterEncoding=UTF-8"
//				+ "&autoReconnect=true"
//				+ "&failOverReadOnly=false&maxReconnects=10"
//				+ "&useJDBCCompliantTimezoneShift=true"
//				+ "&useLegacyDatetimeCode=false"
//				+ "&serverTimezone=UTC";
		DBUtils.user = user;
		DBUtils.password = pass;
		
		if(user==null || user.length()==0) {
			throw new SQLException("Not logged in","42000");
		}
		
		String conn = "jdbc:mysql://" 
				+ host
				+ ":3306/mimus"
				+ "?useUnicode=true&characterEncoding=UTF-8"
				+ "&autoReconnect=true"
				+ "&failOverReadOnly=false&maxReconnects=10"
				+ "&useJDBCCompliantTimezoneShift=true"
				+ "&useLegacyDatetimeCode=false"
				+ "&serverTimezone=UTC";
		return DriverManager.getConnection(
				conn, user, pass);
	}
	
	/**
	 * Returns a Properties object with the login data from config.properties.
	 */
	public static Properties readProperties() throws IOException {
		String path = getPath();
		
		Properties prop = new Properties();
		InputStream is = null;
		
		is = new FileInputStream(path);
		prop.load(is);
		
		return prop;
	}
	
	
	private static String getPath() {
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFile file = corpus.getFile("config.properties");
		return file.getLocation().toString();
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		DBUtils.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		DBUtils.password = password;
	}
	
	
}
