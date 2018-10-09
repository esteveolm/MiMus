package ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * LabelPrinter facilitates the creation of information messages in
 * the SWT Label objects of the user interface, based on the idea
 * that all such messages are different only in three variables: the
 * label used, the color of the font and the message itself.
 *
 */
public class LabelPrinter {
	
	/**
	 * Prints a message <msg> in SWT Label <label> using the font color
	 * <color>.
	 */
	public static void print(Label label, String color, String msg) {
		if (color.equals("red")) {
			label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		} else {
			label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		}
		label.setText(msg);
	}
	
	/**
	 * Prints a message of type Info, which in the context of MiMus Editor
	 * means that the font color is black.
	 */
	public static void printInfo(Label label, String msg) {
		print(label, "black", msg);
	}
	
	/**
	 * Prints a message of type Error, which in the context of MiMus Editor
	 * means that the font color is red.
	 */
	public static void printError(Label label, String msg) {
		print(label, "red", msg);
	}

}
