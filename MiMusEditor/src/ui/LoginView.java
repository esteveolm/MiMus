package ui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import util.DBUtils;
import util.LabelPrinter;

/**
 * Eclipse View for login of users to the MiMus database.
 * 
 * @author Javier Beltrán Jorba
 *
 */
public class LoginView extends ViewPart {
	
	public LoginView() {
		super();
	}
	
	/**
	 * Draws the view, which consists of a form to introduce the
	 * username and the password, an action button and a label
	 * with the result of the login operation.
	 */
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Login");
		form.getBody().setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		toolkit.createLabel(form.getBody(), "User:");
		
		Text userText = toolkit.createText(form.getBody(), "");
		userText.setLayoutData(gd);
		
		toolkit.createLabel(form.getBody(), "Pass:");
		
		Text passText = toolkit.createText(form.getBody(), "", SWT.PASSWORD);
		passText.setLayoutData(gd);
		
		Button btn = toolkit.createButton(form.getBody(), "Connect", 
				SWT.PUSH | SWT.CENTER);
		
		Label resultText = toolkit.createLabel(form.getBody(), "");
		resultText.setLayoutData(gd);
		
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String user = userText.getText();
				String pass = passText.getText();
				
				/* Save old properties in case a rewind is needed */
				try {
					Properties oldProp = DBUtils.readProperties();
					String oldUser = oldProp.getProperty("editor.user");
					String oldPass = oldProp.getProperty("editor.pass");
					
					/* First, try connection */
					try {
						DBUtils.connect(user, pass, 
								oldProp.getProperty("host.name"));
						
						/* If successful, store values in config.properties */
						DBUtils.writeProperties(user, pass);
						
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage();
							
						/* Declarative views  refreshed to make sure disconnected */
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
						
						/* Update UI */
						LabelPrinter.printInfo(resultText, 
								"Authenticated: " + user);
					} catch (SQLException e1) {
						/* If failure, rewind to previous login data */
						DBUtils.writeProperties(oldUser, oldPass);
						
						/* Update UI */
						LabelPrinter.printError(resultText, 
								"Authentication failed");
					} catch (PartInitException e1) {
						e1.printStackTrace();
					}
				} catch (IOException e2) {
					LabelPrinter.printError(resultText, "Unknown error");
				}
			}
		});
	}
	
	@Override
	public void setFocus() {}

}
