package net.lintfordlib.core.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {

	// --------------------------------------
	// Statics
	// --------------------------------------

	// Defines a date format as DAY MONTH DAY HOUR:MINUTE:SECOND
	public static final SimpleDateFormat DateFormat = new SimpleDateFormat("EEE MM dd kk:mm:ss");

	// Defines a date format wihtout spaces or special characters (MONTH_DAY_HOURMINUTE e.g. 01142022)
	public static final SimpleDateFormat DateFormatForFiles = new SimpleDateFormat("MMddkkmm");

	// --------------------------------------
	// Methods
	// --------------------------------------

	public static String getDataAsString(final Date pDate) {
		if (pDate == null)
			return "";
		return DateFormat.format(pDate);
	}

	/** Returns a {@link Date} object in string format for use in file names (no spaces or special characters).*/
	public static String getDateAsStringFileFriendly(final Date pDate) {
		return DateFormatForFiles.format(pDate);
	}

	/**
	 * Parses a date in string format (as 'EEE MM dd kk:mm:ss') to a {@link Date} object.
	 * */
	public static Date getDateFromString(final String dateAsString) {
		Date result = null;

		try {
			result = DateFormat.parse(dateAsString);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (result == null) {
				result = new GregorianCalendar(2013, 0, 31).getTime();
			}
		}

		return result;
	}
}
