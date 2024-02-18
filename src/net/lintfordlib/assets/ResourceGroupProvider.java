package net.lintfordlib.assets;

import java.util.concurrent.atomic.AtomicInteger;

public class ResourceGroupProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static AtomicInteger mAtomicInteger = new AtomicInteger(Integer.MIN_VALUE);

	// --------------------------------------
	// Properties
	// --------------------------------------

	public static int getRollingEntityNumber() {
		return mAtomicInteger.incrementAndGet();
	}
}
