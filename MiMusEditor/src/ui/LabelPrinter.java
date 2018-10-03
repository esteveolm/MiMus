package ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class LabelPrinter {
	
	public static void print(Label label, String color, String msg) {
		if (color.equals("red")) {
			label.setForeground(new Color(Display.getDefault(), 255, 0, 0));
		} else {
			label.setForeground(new Color(Display.getDefault(), 0, 0, 0));
		}
		label.setText(msg);
	}
	
	public static void printInfo(Label label, String msg) {
		print(label, "black", msg);
	}
	
	public static void printError(Label label, String msg) {
		print(label, "red", msg);
	}

}
