package ui.table;

import java.lang.reflect.Method;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

    private String[] columnas;
    
    /**
     * C
     * @param col
     */
    public TableLabelProvider(String... col)
    {
        columnas = col;
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
