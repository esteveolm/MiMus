package ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class TextStyler {
	
	private StyledText text;
	private List<StyleRange> styles;
	//private int lastSelection;
	
	public TextStyler(StyledText text) {
		this.text = text;
		this.styles = new ArrayList<>();
		//this.lastSelection = -1;
	}
	
	public void update() {
		text.setStyleRange(new StyleRange(0, text.getText().length(), null, null));
		for (StyleRange style: styles) {
			text.setStyleRange(style);
		}
	}
	
	public void add(int from, int to) {
		add(from, to, SWT.NORMAL);
	}
	
	public void add(int from, int to, int font) {
		styles.add(new StyleRange(from, to-from, null, new Color(Display.getCurrent(), 255, 255, 153), font));
	}
	
	public void addUpdate(int from, int to) {
		add(from, to);
		update();
	}
	
	public void delete(int from, int to) {
		int toDelete = -1;
		for (int i=0; i<styles.size(); i++) {
			if (styles.get(i).start==from && styles.get(i).length==to-from) {
				toDelete = i;
			}
		}
		if (toDelete>=0) {
			styles.remove(toDelete);
		}
	}
	
	public void deleteUpdate(int from, int to) {
		delete(from, to);
		update();
	}
	
//	public void select(int from, int to) {
//		if (lastSelection>=0) {
//			deselect();
//		}
//		
//		/* Selection from normal to bold */
//		delete(from, to);
//		add(from, to, SWT.BOLD);
//	}
//	
//	public void selectUpdate(int from, int to) {
//		select(from, to);
//		update();
//	}
//	
//	private void deselect() {
//		StyleRange style = styles.get(lastSelection);
//		
//		/* Selection from bold to normal */
//		delete(style.start, style.start + style.length);
//		add(style.start, style.start + style.length);
//	}
	
	/* Getters and setters */
	
	public StyledText getText() {
		return text;
	}

	public void setText(StyledText text) {
		this.text = text;
	}

	public List<StyleRange> getStyles() {
		return styles;
	}

	public void setStyles(List<StyleRange> styles) {
		this.styles = styles;
	}

//	public int getLastSelection() {
//		return lastSelection;
//	}
//
//	public void setLastSelection(int lastSelection) {
//		this.lastSelection = lastSelection;
//	}

}
