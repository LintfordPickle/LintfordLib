package net.lintfordlib.core.time;

import java.util.concurrent.TimeUnit;

public class TimeConstants {

	public static final int SecondsPerHour = 3600;
	public static final int SecondsPerMinute = 60;
	public static final int MinutesPerHour = 60;
	public static final int MinutesPerDay = 1440;
	public static final double NanoToSecond = 1000000000.0;
	public static final double NanoToMilli = 1000000.0;
	public static final double NanoToMicro = 1000.0;
	public static final long TicksPerMillisecond = 10000;
	public static final double MillisecondsPerTick = 0.0001;
	public static final long TicksPerSecond = 0x989680L;
	public static final double SecondsPerTick = 1E-07;
	public static final long TicksPerMinute = 0x23c34600L;
	public static final double MinutesPerTick = 1.6666666666666667E-09;
	public static final long TicksPerHour = 0x861c46800L;
	public static final double HoursPerTick = 2.7777777777777777E-11;
	public static final long TicksPerDay = 0xc92a69c000L;
	public static final double DaysPerTick = 1.1574074074074074E-12;
	public static final int MillisPerSecond = 0x3e8;
	public static final int MillisPerMinute = 0xea60;
	public static final int MillisPerHour = 0x36ee80;
	public static final int MillisPerDay = 0x5265c00;
	public static final long MaxSeconds = 0xd6bf94d5e5L;
	public static final long MinSeconds = -922337203685L;
	public static final long MaxMilliSeconds = 0x346dc5d638865L;
	public static final long MinMilliSeconds = -922337203685477L;

	public static long minutesFromMilliseconds(long ms) {
		return TimeUnit.MILLISECONDS.toMinutes(ms);

	}

	public static long secondsFromMilliseconds(long ms) {
		return TimeUnit.MILLISECONDS.toSeconds(ms);
	}

}
