package ui;

import java.io.IOException;
import java.sql.Connection;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import util.DBUtils;
import util.LabelPrinter;

public class LoginView extends ViewPart {

	private Connection connection;
	
	public LoginView() {
		super();
		
		try {
			setConnection(DBUtils.connect());
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not connect Login to DB.");
		}
	}
	
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
		
		Text infoText = toolkit.createText(form.getBody(), "", 
				SWT.READ_ONLY | SWT.MULTI);
		try {
			Properties prop = DBUtils.readProperties();
			infoText.setText("Connected as: "+ prop.getProperty("editor.user"));
		} catch (IOException e) {
			infoText.setText("Could not identify connected user");
		}
		
		Label resultLabel = toolkit.createLabel(form.getBody(), "");
		resultLabel.setLayoutData(gd);
		
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
						DBUtils.connect(user, pass);
						
						/* If successful, store values in config.properties */
						DBUtils.writeProperties(user, pass);
						
						/* Update UI */
						infoText.setText("Connected as: " + user);
						LabelPrinter.printInfo(resultLabel, 
								"Authentication successful");
					} catch (SQLException e1) {
						/* If failure, rewind to previous login data */
						DBUtils.writeProperties(oldUser, oldPass);
						
						/* Update UI */
						infoText.setText("Connected as: " + oldUser);
						LabelPrinter.printError(resultLabel, 
								"Authentication failed");
					}
				} catch (IOException e2) {
					LabelPrinter.printError(resultLabel, "Unknown error");
				}
			}
		});
	}

	@Override
	public void setFocus() {}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
