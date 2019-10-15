package util;

import java.io.IOException;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ui.BiblioView;
import ui.ArtistaView;
import ui.CasaView;
import ui.GenereLiterariView;
import ui.InstrumentView;
import ui.LlocView;
import ui.OficiView;
import ui.PromotorView;

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
			System.out.println("Resetted user properties.");
			
			IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage();
			
			/* Force that all views are refreshed to make sure disconnected */
			IViewPart view = page.showView("MiMusEditor.biblioView");
			((BiblioView) view).refreshAction();

			view = page.showView("MiMusEditor.artistaView");
			((ArtistaView) view).refreshAction();
			
			view = page.showView("MiMusEditor.casaView");
			((CasaView) view).refreshAction();
			
			view = page.showView("MiMusEditor.genereView");
			((GenereLiterariView) view).refreshAction();
			
			view = page.showView("MiMusEditor.instrumentView");
			((InstrumentView) view).refreshAction();
			
			view = page.showView("MiMusEditor.llocView");
			((LlocView) view).refreshAction();
			
			view = page.showView("MiMusEditor.oficiView");
			((OficiView) view).refreshAction();
			
			view = page.showView("MiMusEditor.promotorView");
			((PromotorView) view).refreshAction();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
