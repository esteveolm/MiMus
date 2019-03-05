package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * 
 * @author Javier Beltrán Jorba
 * 
 * A MiMusDate represents a date in the MiMus corpus, which has the
 * following characteristics:
 * 
 * - It may be an interval between two dates, or a unique date.
 * - Each of these dates consists of a day, a month and a year.
 * - Any of these components may be flagged as hypothetical, which means
 * lack of sufficient evidence about the date. This situation must
 * be represented when informing about the date in the UI.
 * - Any these components may be flagged as unknown, which means its
 * value is unknown. This situation must be represented when informing
 * about the date in the UI.
 * 
 * TODO: improve code by reusing part of day1 for day2.
 * TODO: control for impossible values in dates with a calendar.
 * TODO: control for date1<date2.
 * 
 */
public class MiMusDate {
	
	private static final Map<String, Integer> MONTH_TO_NUMBER;
	
	/* Utility for month-to-number assignment that only loads once */
	static {
		/* Stream all lines and convert to array */
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IProject corpus = workspace.getProject("MiMusCorpus");
		IFolder stringsFolder = corpus.getFolder("strings");
		IFile monthsFile = stringsFolder.getFile("months");
		String monthsPath = monthsFile.getLocation().toString() + ".txt";
		
		Map<String, Integer> auxMap = new HashMap<>();
		try (Stream<String> stream = Files.lines(Paths.get(monthsPath))) {
			Iterator<String> it = stream.iterator();
			for (int i=0; it.hasNext(); i++) {
				auxMap.put(it.next(), i+1);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load months from file.");
		}
		MONTH_TO_NUMBER = new HashMap<>(auxMap);
	}
	
	/**
	 *  If interval is true, date1 is the actual date and date2 is not used 
	 */
	private boolean interval;
	
	/** 
	 *  Date1 corresponds with start of the date interval, if interval is true;
	 *  or the actual date, if interval is false.
	 */
	private int day1;
	private int month1;
	private int year1;
	
	/**
	 *  Date2 corresponds with end of the date interval, if interval is true;
	 *  or is useless and should not be accessed, if interval is false, as it
	 *  may be storing any meaningless values.
	 */
	private int day2;
	private int month2;
	private int year2;
	
	/**
	 *  Hypothetical flags: if true, the corresponding part of the date is
	 *  hypothetical, hence its actual value stored is presented but represents
	 *  its hypothetical state accordingly.
	 *  If interval is false, hypothetical flags of date2 can
	 *  store any meaningless value and should not be used.
	 */
	private boolean hDay1;
	private boolean hMonth1;
	private boolean hYear1;
	private boolean hDay2;
	private boolean hMonth2;
	private boolean hYear2;
	
	/**
	 *  Unknown flags: if true, the corresponding part of the date is
	 *  unknown, hence its actual value stored is meaningless and not
	 *  presented.
	 *  If interval is false, hypothetical flags of date2 can
	 *  store any meaningless value and should not be used.
	 *  Unknown flags have precedence over hypothetical flags, because it
	 *  makes no sense that an unknown part of the date is hypothetical.
	 */
	private boolean uDay1;
	private boolean uMonth1;
	private boolean uYear1;
	private boolean uDay2;
	private boolean uMonth2;
	private boolean uYear2;
	
	/**
	 * A date can be created empty. In such case, all its attributes are
	 * unknown (that is, all unknown flags are true and all hypothetical flags
	 * are false), the actual dates store 0s because it's a meaningless value
	 * for date, month and year; and interval is set to false.
	 * 
	 * Users of this method can update the date with the actual values as they 
	 * obtain them, using the setter methods.
	 */
	public MiMusDate() {
		this.interval = false;
		this.day1 = 0;
		this.month1 = 0;
		this.year1 = 0;
		this.day2 = 0;
		this.month2 = 0;
		this.year2 = 0;
		this.hDay1 = false;
		this.hMonth1 = false;
		this.hYear1 = false;
		this.hDay2 = false;
		this.hMonth2 = false;
		this.hYear2 = false;
		this.uDay1 = true;
		this.uMonth1 = true;
		this.uYear1 = true;
		this.uDay2 = true;
		this.uMonth2 = true;
		this.uYear2 = true;
	}
	
	/**
	 * A date can be created by specifying all its attributes, if they are known.
	 * 
	 * No consistency checks are applied to these fields; instead, the precedence rules
	 * of this class will work as always. This means that if date2 is specified and
	 * interval is false, then the values used for date2 are useless but stored in
	 * the object.
	 */
	public MiMusDate(boolean interval, int day1, int month1, int year1, int day2, int month2, int year2, boolean hDay1,
			boolean hMonth1, boolean hYear1, boolean hDay2, boolean hMonth2, boolean hYear2, boolean uDay1,
			boolean uMonth1, boolean uYear1, boolean uDay2, boolean uMonth2, boolean uYear2) {
		this.interval = interval;
		this.day1 = day1;
		this.month1 = month1;
		this.year1 = year1;
		this.day2 = day2;
		this.month2 = month2;
		this.year2 = year2;
		this.hDay1 = hDay1;
		this.hMonth1 = hMonth1;
		this.hYear1 = hYear1;
		this.hDay2 = hDay2;
		this.hMonth2 = hMonth2;
		this.hYear2 = hYear2;
		this.uDay1 = uDay1;
		this.uMonth1 = uMonth1;
		this.uYear1 = uYear1;
		this.uDay2 = uDay2;
		this.uMonth2 = uMonth2;
		this.uYear2 = uYear2;
	}
	
	public String toString() {
		String str = "";
		
		/* First, write date1 */
		if (uDay1) {
			str += "-";
		} else if (hDay1) {
			str += "[" + day1 + "]";
		} else {
			str += day1;
		}
		str += " / ";
		if (uMonth1) {
			str += "-";
		} else if (hMonth1) {
			str += "[" + month1 + "]";
		} else {
			str += month1;
		}
		str += " / ";
		if (uYear1) {
			str += "-";
		} else if (hYear1) {
			str += "[" + year1 + "]";
		} else {
			str += year1;
		}
		
		/* Then, write date2 if interval is true */
		if (interval) {
			str += " - ";
			if (uDay2) {
				str += "-";
			} else if (hDay2) {
				str += "[" + day2 + "]";
			} else {
				str += day2;
			}
			str += " / ";
			if (uMonth2) {
				str += "-";
			} else if (hMonth2) {
				str += "[" + month2 + "]";
			} else {
				str += month2;
			}
			str += " / ";
			if (uYear2) {
				str += "-";
			} else if (hYear2) {
				str += "[" + year2 + "]";
			} else {
				str += year2;
			}
		}
		
		return str;
	}
	
	/* Getters and setters */
	
	public boolean isInterval() {
		return interval;
	}
	public void setInterval(boolean interval) {
		this.interval = interval;
	}
	public int getDay1() {
		return day1;
	}
	public void setDay1(int day1) {
		this.day1 = day1;
		setuDay1(false);
	}
	public void setDay1(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethDay1(true);
		}
		setDay1(Integer.parseInt(text.replace("[", "").replace("]","")));
	}
	public int getMonth1() {
		return month1;
	}
	public void setMonth1(int month1) {
		this.month1 = month1;
		setuMonth1(false);
	}
	public void setMonth1(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethMonth1(true);
			text = text.replace("[", "").replaceAll("]", "");
		}
		if (MONTH_TO_NUMBER.containsKey(text)) {
			setMonth1(MONTH_TO_NUMBER.get(text));
		} else {
			setMonth1(0);
		}
	}
	public int getYear1() {
		return year1;
	}
	public void setYear1(int year1) {
		this.year1 = year1;
		setuYear1(false);
	}
	public void setYear1(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethYear1(true);
		}
		setYear1(Integer.parseInt(text.replace("[", "").replace("]","")));
	}
	public int getDay2() {
		return day2;
	}
	public void setDay2(int day2) {
		this.day2 = day2;
		setuDay2(false);
	}
	public void setDay2(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethDay2(true);
		}
		setDay2(Integer.parseInt(text.replace("[", "").replace("]","")));
	}
	public int getMonth2() {
		return month2;
	}
	public void setMonth2(int month2) {
		this.month2 = month2;
		setuMonth2(false);
	}
	public void setMonth2(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethMonth2(true);
			text = text.replace("[", "").replaceAll("]", "");
		}
		if (MONTH_TO_NUMBER.containsKey(text)) {
			setMonth2(MONTH_TO_NUMBER.get(text));
		} else {
			setMonth2(0);
		}
	}
	public int getYear2() {
		return year2;
	}
	public void setYear2(int year2) {
		this.year2 = year2;
		setuYear2(false);
	}
	public void setYear2(String text) {
		if (text.startsWith("[") && text.endsWith("]")) {
			sethYear2(true);
		}
		setYear2(Integer.parseInt(text.replace("[", "").replace("]","")));
	}
	public boolean ishDay1() {
		return hDay1;
	}
	public void sethDay1(boolean hDay1) {
		this.hDay1 = hDay1;
	}
	public boolean ishMonth1() {
		return hMonth1;
	}
	public void sethMonth1(boolean hMonth1) {
		this.hMonth1 = hMonth1;
	}
	public boolean ishYear1() {
		return hYear1;
	}
	public void sethYear1(boolean hYear1) {
		this.hYear1 = hYear1;
	}
	public boolean ishDay2() {
		return hDay2;
	}
	public void sethDay2(boolean hDay2) {
		this.hDay2 = hDay2;
	}
	public boolean ishMonth2() {
		return hMonth2;
	}
	public void sethMonth2(boolean hMonth2) {
		this.hMonth2 = hMonth2;
	}
	public boolean ishYear2() {
		return hYear2;
	}
	public void sethYear2(boolean hYear2) {
		this.hYear2 = hYear2;
	}
	public boolean isuDay1() {
		return uDay1;
	}
	public void setuDay1(boolean uDay1) {
		this.uDay1 = uDay1;
	}
	public boolean isuMonth1() {
		return uMonth1;
	}
	public void setuMonth1(boolean uMonth1) {
		this.uMonth1 = uMonth1;
	}
	public boolean isuYear1() {
		return uYear1;
	}
	public void setuYear1(boolean uYear1) {
		this.uYear1 = uYear1;
	}
	public boolean isuDay2() {
		return uDay2;
	}
	public void setuDay2(boolean uDay2) {
		this.uDay2 = uDay2;
	}
	public boolean isuMonth2() {
		return uMonth2;
	}
	public void setuMonth2(boolean uMonth2) {
		this.uMonth2 = uMonth2;
	}
	public boolean isuYear2() {
		return uYear2;
	}
	public void setuYear2(boolean uYear2) {
		this.uYear2 = uYear2;
	}
}
