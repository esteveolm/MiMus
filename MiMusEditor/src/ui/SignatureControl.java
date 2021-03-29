package ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import model.MiMusLibraryIdentifier;

public class SignatureControl {

	Composite c;
	Text arxiu, serie, subserie1, subserie2, registre, foli;
	
	public SignatureControl(Composite parent, FormToolkit toolkit, MiMusLibraryIdentifier library) {
		c = toolkit.createComposite(parent);
		c.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
		c.setLayout(new GridLayout(6, false));		
		createPartControl(c, toolkit, library);		
	}

	private void createPartControl(Composite parent, FormToolkit toolkit, MiMusLibraryIdentifier library) {
		arxiu = toolkit.createText(parent, library.getArchive(), SWT.CENTER);
		arxiu.setLayoutData(GridDataFactory.swtDefaults().hint(50, SWT.DEFAULT).create());
		arxiu.setMessage("arxiu");
		serie = toolkit.createText(parent, library.getSeries(), SWT.CENTER);
		serie.setLayoutData(GridDataFactory.swtDefaults().hint(140, SWT.DEFAULT).create());
		serie.setMessage("serie");
		subserie1 = toolkit.createText(parent, library.getSubseries1(), SWT.CENTER);
		subserie1.setLayoutData(GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).create());
		subserie1.setMessage("subserie1");
		subserie2 = toolkit.createText(parent, library.getSubseries2(), SWT.CENTER);
		subserie2.setLayoutData(GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).create());
		subserie2.setMessage("subserie2");
		registre = toolkit.createText(parent, library.getNumber(), SWT.CENTER);
		registre.setLayoutData(GridDataFactory.swtDefaults().hint(70, SWT.DEFAULT).create());
		registre.setMessage("registre");
		foli = toolkit.createText(parent, library.getPage(), SWT.CENTER);
		foli.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(160, SWT.DEFAULT).create());
		foli.setMessage("foli/carta/pergamí");
		
	}

	public void addModifyListener(ModifyListener modifyListener) {
		arxiu.addModifyListener(modifyListener);
		serie.addModifyListener(modifyListener);
		subserie1.addModifyListener(modifyListener);
		subserie2.addModifyListener(modifyListener);
		registre.addModifyListener(modifyListener);
		foli.addModifyListener(modifyListener);		
	}

	public MiMusLibraryIdentifier getValue() {
		MiMusLibraryIdentifier lib = new MiMusLibraryIdentifier();
		lib.setArchive(nullify(arxiu.getText()));
		lib.setSeries(nullify(serie.getText()));
		lib.setSubseries1(nullify(subserie1.getText()));
		lib.setSubseries2(nullify(subserie2.getText()));
		lib.setNumber(nullify(registre.getText()));
		lib.setPage(nullify(foli.getText()));
		return lib;
	}
	
	/**
	 * Converts empty String to null
	 * @param val input value
	 * @return null if the input string is empty, and the input value otherwise.
	 */
	private String nullify(String val) {
		if("".equals(val)) {
			return null;
		} else {
			return val;
		}
	}
	

}
