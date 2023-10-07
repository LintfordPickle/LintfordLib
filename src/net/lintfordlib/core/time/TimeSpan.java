package net.lintfordlib.core.time;

import net.lintfordlib.core.LintfordCore.CoreTime;

public class TimeSpan {

	// --------------------------------------
	// Statics
	// --------------------------------------

	public static final TimeSpan TIME_SPAN_ZERO = new TimeSpan(0);

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
		return mTicks * TimeConstants.MillisecondsPerTick;
	}

	public double seconds() {
		return mTicks * TimeConstants.SecondsPerTick;
	}

	public double minutes() {
		return mTicks * TimeConstants.MinutesPerTick;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public TimeSpan(float milliseconds) {
		mTicks = (long) (milliseconds * TimeConstants.TicksPerMillisecond);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(CoreTime gameTime) {
		mTicks += gameTime.elapsedTimeMilli() * TimeConstants.TicksPerMillisecond;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void interval(double value, int scale) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException("TimeSpan must be a valid number!");
		}

		double num = value * scale;

		mTicks = (long) num * TimeConstants.TicksPerMillisecond;
	}

	/** Reinitializes this TimeSpan Object from the given seconds.
	 * 
	 * @param seconds */
	public void fromSeconds(double seconds) {
		interval(seconds, TimeConstants.MillisPerSecond);
	}

	/** Reinitializes this TimeSpan Object from the given milliseconds.
	 * 
	 * @param milliseconds */
	public void fromMilliseconds(int milliseconds) {
		interval(milliseconds, 1);
	}

	public boolean greaterThan(double seconds) {
		return seconds > mTicks * TimeConstants.SecondsPerTick;
	}

	public boolean equals(TimeSpan timeSpan) {
		return timeSpan.ticks() == mTicks;
	}

	public boolean equals(float milliseconds) {
		return milliseconds * TimeConstants.TicksPerMillisecond == mTicks;
	}

	public boolean equals(long ticks) {
		return ticks == mTicks;
	}

	public static TimeSpan zero() {
		return new TimeSpan(0);
	}
}
