package util;

import java.io.IOException;

import org.eclipse.ui.IStartup;

/**
 * MiMusStartup is code executed at startup of the plugin, and it
 * prepares the environment to work properly with the plugin.
 * 
 * Currently its only use is to rewrite the config.properties file
 * to the disconnected user. This allows that the application is
 * disconnected from the DB at startup, for security.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class MiMusStartup implements IStartup {

	/**
	 * Startup code of the plugin goes here.
	 */
	@Override
	public void earlyStartup() {
		try {
			DBUtils.writeProperties("disconnected", "disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
