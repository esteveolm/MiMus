package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import model.MiMusDate;

public class MimusDateControl {
	
	Composite c;
	Text d1,d2,m1,m2,y1,y2;
	Button hd1,hm1,hy1,hd2,hm2,hy2;
	
	private VerifyListener onlyNumbers = new VerifyListener() {		
		@Override
		public void verifyText(VerifyEvent e) {
			if(!e.text.matches("[0-9]*")) {
				e.doit = false;
			}			
		}
	};

	public MimusDateControl(Composite parent, FormToolkit toolkit, MiMusDate date) {
		c = toolkit.createComposite(parent);
		c.setLayout(new GridLayout(20, false));		
		createPartControl(c, toolkit, date);		
	}
	
	private void createPartControl(Composite c, FormToolkit toolkit, MiMusDate date) {

		
		d1 = createInputField(toolkit, c, 2, date.getDay1(), date.isuDay1());
		d1.setMessage("dd");
		d1.addVerifyListener(onlyNumbers);
		hd1 = toolkit.createButton(c, "", SWT.CHECK);
		hd1.setSelection(date.ishDay1());
		hd1.setToolTipText("valor hipotètic");

		toolkit.createLabel(c, "/");

		m1 = createInputField(toolkit, c, 2, date.getMonth1(), date.isuMonth1());
		m1.setMessage("mm");
		m1.addVerifyListener(onlyNumbers);
		hm1 = toolkit.createButton(c, "", SWT.CHECK);
		hm1.setSelection(date.ishMonth1());
		hm1.setToolTipText("valor hipotètic");

		toolkit.createLabel(c, "/");
		
		y1 = createInputField(toolkit, c, 4, date.getYear1(), date.isuYear1());
		y1.setMessage("yyyy");
		y1.addVerifyListener(onlyNumbers);
		hy1 = toolkit.createButton(c, "", SWT.CHECK);
		hy1.setSelection(date.ishYear1());
		hy1.setToolTipText("valor hipotètic");
				
		toolkit.createLabel(c, " - ");
		
		d2 = createInputField(toolkit, c, 2, date.getDay2(), date.isuDay2());
		d2.setMessage("dd");
		d2.addVerifyListener(onlyNumbers);
		hd2 = toolkit.createButton(c, "", SWT.CHECK);
		hd2.setSelection(date.ishDay2());
		hd2.setToolTipText("valor hipotètic");

		toolkit.createLabel(c, "/");

		m2 = createInputField(toolkit, c, 2, date.getMonth2(), date.isuMonth2());
		m2.setMessage("mm");
		m2.addVerifyListener(onlyNumbers);
		hm2 = toolkit.createButton(c, "", SWT.CHECK);
		hm2.setSelection(date.ishMonth2());
		hm2.setToolTipText("valor hipotètic");
		
		toolkit.createLabel(c, "/");
		
		y2 = createInputField(toolkit, c, 4, date.getYear2(), date.isuYear2());
		y2.setMessage("yyyy");
		y2.addVerifyListener(onlyNumbers);
		hy2 = toolkit.createButton(c, "", SWT.CHECK);	
		hy2.setSelection(date.ishYear2());
		hy2.setToolTipText("valor hipotètic");
		
	}
	
	private Text createInputField(FormToolkit toolkit, Composite parent, int size, int value, boolean unknown) {		
		Text text = toolkit.createText(parent, unknown|| value==0?"":value+"", SWT.CENTER);
		text.setTextLimit(size);
		GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gd.widthHint = size*14;
		text.setLayoutData(gd);
		return text;		
	}

	public void addModifyListener(ModifyListener modifyListener) {
		d1.addModifyListener(modifyListener);
		d2.addModifyListener(modifyListener);
		m1.addModifyListener(modifyListener);
		m2.addModifyListener(modifyListener);
		y1.addModifyListener(modifyListener);
		y2.addModifyListener(modifyListener);
		SelectionListener listener = new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				modifyListener.modifyText(null);				
			}
		};
		hd1.addSelectionListener(listener);
		hm1.addSelectionListener(listener);
		hy1.addSelectionListener(listener);
		hd2.addSelectionListener(listener);
		hm2.addSelectionListener(listener);
		hy2.addSelectionListener(listener);
		
	}

	public MiMusDate getValue() {
		MiMusDate date = new MiMusDate();
		date.setYear1("0"+y1.getText());
		date.setMonth1(Integer.parseInt("0"+m1.getText()));
		date.setDay1("0"+d1.getText());
		date.setYear2("0"+y2.getText());
		date.setMonth2(Integer.parseInt("0"+m2.getText()));
		date.setDay2("0"+d2.getText());
		
		date.sethYear1(hy1.getSelection());
		date.sethMonth1(hm1.getSelection());
		date.sethDay1(hd1.getSelection());
		date.sethYear2(hy2.getSelection());
		date.sethMonth2(hm2.getSelection());
		date.sethDay2(hd2.getSelection());
		
		date.setuYear1(y1.getText().trim().equals(""));
		date.setuMonth1(m1.getText().trim().equals(""));
		date.setuDay1(d1.getText().trim().equals(""));
		date.setuYear2(y2.getText().trim().equals(""));
		date.setuMonth2(m2.getText().trim().equals(""));
		date.setuDay2(d2.getText().trim().equals(""));
		
		date.setInterval(date.getYear2()>0 || date.getMonth2()>0 || date.getDay2()>0);

		return date;
	}

}
