package net.lintford.library.core.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper {

	// Defines a date format as DAY MONTH DAY HOUR:MINUTE:SECOND
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MM dd kk:mm:ss");

	// Defines a date format as MONTH_DAY_HOURMINUTE e.g. 01_14_2248
	public static final SimpleDateFormat DATE_FORMAT_FILE_FRIENDLY = new SimpleDateFormat("MM_dd_kkmm");

	public static String getDataAsString(final Date pDate) {
		if(pDate == null) return "";
		return DATE_FORMAT.format(pDate);

	}

	public static String getDateAsStringFileFriendly(final Date pDate) {
		return DATE_FORMAT_FILE_FRIENDLY.format(pDate);

	}

	public static Date getDateFromString(final String pDateAsString) {
		Date result = null;

		try {
			result = DATE_FORMAT.parse(pDateAsString);
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
