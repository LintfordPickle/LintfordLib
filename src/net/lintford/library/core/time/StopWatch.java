package net.lintford.library.core.time;

public class StopWatch {

	private static long mStartTime;

	/** Starts the stopwatch */
	public static void start() {
		mStartTime = System.nanoTime();
	}

	/** Stops the stopwatch.
	 * 
	 * @return the time in milliseconds since the stopwatch was started. */
	public static long stop() {
		return ((System.nanoTime() - mStartTime) / (long) 1000000);
	}

}
