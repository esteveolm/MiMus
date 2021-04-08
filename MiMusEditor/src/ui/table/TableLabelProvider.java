package ui.table;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

public class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

    private String[] columnas;
    
    /**
     * Creates columns in the viewer and returns the TableLablelProvider
     * Must be set to the viewer:
     *    tableviewer.setLabelProvider(new TableLabelProvider(tableviewer, "col1","col2",... ));
     * @param col
     */
    public TableLabelProvider(TableViewer tableViewer, String... col)
    {
        columnas = col;
		for(String colName : columnas) {
			TableColumn column = new TableColumn(tableViewer.getTable(), SWT.NONE );
			column.setWidth(100);
			column.setText(colName);
		}
    }

    @Override
    public String getColumnText(Object obj, int index)  {

        String field = columnas[index];
		try {
		    Method getter = obj.getClass().getMethod("get"+field.substring(0,1).toUpperCase() + field.substring(1)); //$NON-NLS-1$
		    Object result = getter.invoke(obj);
	        return result.toString();
		} catch (Exception e) {
			return "Could not read property "+field;
		}
    }

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	
}
