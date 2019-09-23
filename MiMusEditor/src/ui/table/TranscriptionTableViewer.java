package ui.table;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import model.Transcription;

public class TranscriptionTableViewer extends MiMusTableViewer {
	
	private List<Transcription> transcriptions;
	
	public TranscriptionTableViewer(Composite parent, List<Transcription> initials) {
		super(parent);
		String[] cols = {"Transcripció", "Forma estàndard", "Lema"};
		columnNames = cols;
		transcriptions = initials;
	}

	@Override
	public TableViewer createTableViewer() {
		tv = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		
		for (String h: columnNames) {
			TableColumn col = new TableColumn(tv.getTable(), SWT.LEFT);
			col.setText(h);
		}
		tv.setUseHashlookup(true);
		tv.setColumnProperties(columnNames);
		tv.setContentProvider(ArrayContentProvider.getInstance());
		tv.setLabelProvider(new TranscriptionLabelProvider());
		tv.setInput(transcriptions);
		tv.getTable().setHeaderVisible(true);
		tv.getTable().setLinesVisible(true);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 150;
		tv.getTable().setLayoutData(gd);
		tv.setComparator(new TranscriptionComparator());
		packColumns();
		return tv;
	}
	
	class TranscriptionLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Transcription tra = (Transcription) element;
			switch (columnIndex) {
			case 0:	// Selected Text
				return tra.getSelectedText();
			case 1:	// Transcription
				return tra.getForm();
			case 2:	// Lemma
				return tra.getItsEntity().getItsEntity().getLemma();
			default:	// Should never reach here
				return "";
			}
		}
	}
	
	public class TranscriptionComparator extends ViewerComparator {
		/**
		 * Compares transcriptions alphanumerically, using two criteria:
		 * first, by their lemma; then, ties are resolved by the
		 * transcripted form.
		 */
		public int compare(Viewer viewer, Object e1, Object e2) {
			Transcription tra1 = (Transcription) e1;
			Transcription tra2 = (Transcription) e2;
			int byLemma = tra1.getItsEntity().getItsEntity().getLemma()
					.compareTo(tra2.getItsEntity().getItsEntity().getLemma());
			if (byLemma==0) {
				return tra1.getForm().compareTo(tra2.getForm());
			} else {
				return byLemma;
			}
		}
	}

	public List<Transcription> getTranscriptions() {
		return transcriptions;
	}
	public void setTranscriptions(List<Transcription> transcriptions) {
		this.transcriptions = transcriptions;
	}
}
