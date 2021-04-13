package util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

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
		
		new UIJob(Display.getDefault(), "Login") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage();
				page.closeAllEditors(false);
				try {
					page.showView("MiMusEditor.loginView");
				} catch (PartInitException e) {
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		}.schedule(1000);		
		
		
//		try {			
//			IWorkbenchPage page = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow()
//				.getActivePage();
//			
//			page.closeAllEditors(false);
//			
//			
//			
//			/* Force that all views are refreshed to make sure disconnected */
//			IViewPart view = page.showView("MiMusEditor.biblioView");
//			((BiblioView) view).refreshAction();
//
//			view = page.showView("MiMusEditor.artistaView");
//			((ArtistaView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.casaView");
//			((CasaView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.genereView");
//			((GenereLiterariView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.instrumentView");
//			((InstrumentView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.llocView");
//			((LlocView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.oficiView");
//			((OficiView) view).refreshAction();
//			
//			view = page.showView("MiMusEditor.promotorView");
//			((PromotorView) view).refreshAction();
//		} catch (PartInitException e) {
//			e.printStackTrace();
//		}
	}

}
