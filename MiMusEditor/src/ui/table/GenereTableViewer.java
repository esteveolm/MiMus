package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import model.GenereLiterari;

public class GenereTableViewer extends DeclarativeTableViewer {

	public GenereTableViewer(Composite parent, List<GenereLiterari> generes) {
		super(parent);
		this.entities = generes;
		String[] aux = {"Nom Complet", "Nom Frances", "Nom Occita", "Definicio"};
		this.columnNames = aux;
	}

	@Override
	public CellEditor[] developEditors() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[1] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[2] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		editors[3] = new TextCellEditor(tv.getTable(), SWT.SINGLE);
		return editors;
	}

	@Override
	public void developProviders() {
		tv.setCellModifier(new GenereCellModifier());
		tv.setLabelProvider(new GenereLabelProvider());
		tv.setComparator(new GenereComparator());
	}
	
	class GenereCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			return true;	/* All fields can be modified */
		}

		@Override
		public Object getValue(Object element, String property) {
			GenereLiterari gen = (GenereLiterari) element;
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Nom Complet	(Text)
				return gen.getNomComplet();
			case 1:	// Nom Frances	(Text)
				return gen.getNomFrances();
			case 2:	// Nom Occita	(Text)
				return gen.getNomOccita();
			case 3:	// Definicio	(Text)
				return gen.getDefinicio();
			default:	// Shouldn't reach here
				return "";
			}
		}

		@Override
		public void modify(Object element, String property, Object value) {
			GenereLiterari gen = (GenereLiterari) ((TableItem) element).getData();
			int colIdx = getColumnNames().indexOf(property);
			switch(colIdx) {
			case 0:	// Nom Complet (Text)
				gen.setNomComplet((String) value);
				break;
			case 1:	// Nom Frances (Text)
				gen.setNomFrances((String) value);
				break;
			case 2:	// Nom Occita (Text)
				gen.setNomOccita((String) value);
				break;
			case 3:	// Definicio (Text)
				gen.setDefinicio((String) value);
				break;
			}
		}
	}
	
	class GenereLabelProvider extends LabelProvider 
		implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	
		@Override
		public String getColumnText(Object element, int columnIndex) {
			GenereLiterari gen = (GenereLiterari) element;
			switch(columnIndex) {
			case 0:	// Nom Complet (Text)
				return gen.getNomComplet();
			case 1:	// Nom Frances (Text)
				return gen.getNomFrances();
			case 2:	// Nom Occita (Text)
				return gen.getNomOccita();
			case 3:	// Definicio (Text)
				return gen.getDefinicio();
			default:	// Shouldn't reach here
				return "";
			}
		}
	}
	
	class GenereComparator extends ViewerComparator {
		public int compare(Viewer viewer, Object e1, Object e2) {
			GenereLiterari gen1 = (GenereLiterari) e1;
			GenereLiterari gen2 = (GenereLiterari) e2;
			return gen1.getId()-gen2.getId();
		}
	}
}
