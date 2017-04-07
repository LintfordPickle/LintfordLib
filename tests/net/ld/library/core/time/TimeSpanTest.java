package net.ld.library.core.time;

import org.junit.Test;

public class TimeSpanTest {

	/**
	 * Test the TimeSpan maintains the expected time when pass into the
	 * constructor
	 */
	@Test
	public void timeSpanStartTest() {
		// Assign
		TimeSpan lTimeSpan = new TimeSpan(500);

		// Test
		double expectedStartTicks = 500 * TimeSpan.TicksPerMillisecond;
		assert (lTimeSpan.milliseconds() == expectedStartTicks
				* TimeSpan.MillisecondsPerTick) : "The TimeSpan did not maintain the initial value";

	}

	/**
	 * Test that the TimeSpan maintains the correct time over the period of a
	 * frame (update iteration).
	 */
	@Test
	public void timeSpanUpdateTest() {
		// Assign
		TimeSpan lTimeSpan = new TimeSpan(500);
		GameTime lGameTime = new GameTime();
		double expectedStartTicks = 500 * TimeSpan.TicksPerMillisecond;

		// Act
		lGameTime.update();
		lTimeSpan.update(lGameTime);

		// Assert
		// Test if the TimeSpan tracks time properly over a frame
		double expectedEndTicks = expectedStartTicks + lGameTime.elapseGameTime() * TimeSpan.TicksPerMillisecond;
		assert (lTimeSpan.milliseconds() == expectedEndTicks
				* TimeSpan.MillisecondsPerTick) : "The TimeSpan did not maintain the expected elapsed";

	}

}
