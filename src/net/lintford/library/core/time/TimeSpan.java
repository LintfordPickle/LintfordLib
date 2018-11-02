package net.lintford.library.core.time;

import net.lintford.library.core.LintfordCore.GameTime;

public class TimeSpan {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	// --------------------------------------
	// Variables
	// --------------------------------------

	private long mTicks;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long ticks() {
		return mTicks;
	}

	public double milliseconds() {
		return mTicks * MillisecondsPerTick;
	}

	public double seconds() {
		return mTicks * SecondsPerTick;
	}

	public double minutes() {
		return mTicks * MinutesPerTick;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public TimeSpan(float pMilliseconds) {
		mTicks = (long) (pMilliseconds * TicksPerMillisecond);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(GameTime pGameTime) {
		// check for overflow
		mTicks += pGameTime.elapseGameTimeMilli() * TicksPerMillisecond;
		
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void interval(double pValue, int pScale) {

		if (Double.isNaN(pValue)) {
			throw new IllegalArgumentException("TimeSpan NaN!");
		}

		double num = pValue * pScale;

		mTicks = (long) num * TicksPerMillisecond;
	}

	/** Reinitialises this TimeSpan Object from the given seconds.
	 * 
	 * @param pSeconds */
	public void fromSeconds(double pSeconds) {
		interval(pSeconds, MillisPerSecond);
	}

	/** Reinitialises this TimeSpan Object from the given milliseconds.
	 * 
	 * @param pMilliseconds */
	public void fromMilliseconds(int pMilliseconds) {
		interval(pMilliseconds, 1);
	}

	public boolean greaterThan(double pSeconds) {
		return pSeconds > mTicks * SecondsPerTick;
	}

	public boolean equals(TimeSpan pTimeSpan) {
		return pTimeSpan.ticks() == mTicks;
	}

	public boolean equals(float pMilliseconds) {
		return pMilliseconds * TicksPerMillisecond == mTicks;
	}

	public boolean equals(long pTicks) {
		return pTicks == mTicks;
	}

	public static TimeSpan zero() {
		return new TimeSpan(0);
	}
}
