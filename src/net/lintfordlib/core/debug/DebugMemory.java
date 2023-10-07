package net.lintfordlib.core.debug;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DebugMemory {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long MEGABYTE_FACTOR = 1024L * 1024L;
	private static final DecimalFormat ROUNDED_DOUBLE_DECIMALFORMAT;
	private static final String MIB = "MiB";

	static {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		ROUNDED_DOUBLE_DECIMALFORMAT = new DecimalFormat("####0.00", otherSymbols);
		ROUNDED_DOUBLE_DECIMALFORMAT.setGroupingUsed(false);
	}

	// --------------------------------------
	// Static Methods
	// --------------------------------------

	public static void dumpMemoryToLog() {
		if (!Debug.debugManager().debugModeEnabled())
			return;

		final var lDebugLogger = Debug.debugManager().logger();

		// Returns the maximum amount of memory that the JVM will attempt to use (VM argument -Xmx512M)
		lDebugLogger.i(DebugMemory.class.getSimpleName(), "Maximum memory: " + getMaxMemoryInMiB());

		// Returns the total amount of memory in the JVM (influenced with VM argument -Xms256M)
		lDebugLogger.i(DebugMemory.class.getSimpleName(), "Total memory: " + getTotalMemoryInMiB());

		// Returns the amount of free memory in the JVM
		lDebugLogger.i(DebugMemory.class.getSimpleName(), "Free memory: " + getFreeMemoryInMiB());

		lDebugLogger.i(DebugMemory.class.getSimpleName(), "Used memory: " + getUsedMemoryInMiB());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private static double bytesToMiB(long bytes) {
		return ((double) bytes / MEGABYTE_FACTOR);
	}

	public static String getTotalMemoryInMiB() {
		double totalMiB = bytesToMiB(getTotalMemory());
		return String.format("%s %s", ROUNDED_DOUBLE_DECIMALFORMAT.format(totalMiB), MIB);
	}

	public static String getFreeMemoryInMiB() {
		double freeMiB = bytesToMiB(getFreeMemory());
		return String.format("%s %s", ROUNDED_DOUBLE_DECIMALFORMAT.format(freeMiB), MIB);
	}

	public static String getUsedMemoryInMiB() {
		double usedMiB = bytesToMiB(getUsedMemory());
		return String.format("%s %s", ROUNDED_DOUBLE_DECIMALFORMAT.format(usedMiB), MIB);
	}

	public static String getMaxMemoryInMiB() {
		double maxMiB = bytesToMiB(getMaxMemory());
		return String.format("%s %s", ROUNDED_DOUBLE_DECIMALFORMAT.format(maxMiB), MIB);
	}

	public static double getPercentageUsed() {
		return ((double) getUsedMemory() / getMaxMemory()) * 100;
	}

	public static String getPercentageUsedFormatted() {
		double usedPercentage = getPercentageUsed();
		return ROUNDED_DOUBLE_DECIMALFORMAT.format(usedPercentage) + "%";
	}

	public static long getMaxMemory() {
		return Runtime.getRuntime().maxMemory();
	}

	public static long getUsedMemory() {
		return getMaxMemory() - getFreeMemory();
	}

	public static long getTotalMemory() {
		return Runtime.getRuntime().totalMemory();
	}

	public static long getFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

}
