package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import model.Unit;

public class CasaTableViewer extends DeclarativeTableViewer {

	public CasaTableViewer(Composite parent, List<Unit> cases) {
		// TODO Auto-generated constructor stub
		super(parent);
	}

	@Override
	public CellEditor[] developEditors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void developProviders() {
		// TODO Auto-generated method stub

	}

}
