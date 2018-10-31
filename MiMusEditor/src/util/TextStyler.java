package util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A TextStyler helps managing the styles of the Text elements in the
 * user interface of MiMus Editor. Specifically, it handles the yellow
 * highlighting of every part of the text associated with an entity,
 * ensuring a style that is robust to the changes happening when
 * the user modifies the lists of entities.
 * 
 * This robustness is achieved by a two-stage functionality: first, a
 * list of current styles is modified with the adition or removal of
 * a highlight; then, the whole StyledText SWT element is refreshed
 * and the current highlights are re-painted. That's why the methods
 * distinguish between add and addUpdate, or delete and deleteUpdate.
 * 
 * This allows for a correct presentation when entities overlap with
 * each other, where the removal of an entity would otherwise remove
 * the style of an entity inscribed in its range.
 *
 */
public class TextStyler {
	
	private StyledText text;
	private List<StyleRange> styles;
	
	public TextStyler(StyledText text) {
		this.text = text;
		this.styles = new ArrayList<>();
	}
	
	/**
	 * Refresh SWT StyledText element with current styles.
	 */
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
		/* First, only find element to delete */
		for (int i=0; toDelete==-1 && i<styles.size(); i++) {
			if (styles.get(i).start==from && styles.get(i).length==to-from) {
				toDelete = i;
			}
		}
		
		/* Then, delete such element after loop */
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
}
