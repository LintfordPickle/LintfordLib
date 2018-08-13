package net.lintford.library.core.debug;

public class DebugMemory {

	public static void dumpMemoryToLog() {
		if (!Debug.debugManager().debugModeEnabled())
			return;
		
		final DebugLogger LOGGER = Debug.debugManager().logger();

		// Returns the maximum amount of memory that the JVM will attempt to use
		LOGGER.i(DebugMemory.class.getSimpleName(), "Maximum memory (bytes): " + Runtime.getRuntime().maxMemory());

		// Returns the total amount of memory in the JVM
		LOGGER.i(DebugMemory.class.getSimpleName(), "Total memory (bytes): " + Runtime.getRuntime().totalMemory());

		// Returns the amount of free memory in the JVM
		LOGGER.i(DebugMemory.class.getSimpleName(), "Free memory (bytes): " + Runtime.getRuntime().freeMemory());

	}

}
