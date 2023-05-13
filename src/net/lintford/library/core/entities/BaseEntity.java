package net.lintford.library.core.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// Unique (within the process) resourcegroup id
	private static AtomicInteger mAtomicInteger = new AtomicInteger(Integer.MIN_VALUE);

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** the name of the definition */
	public String name;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public static int getEntityNumber() {
		return mAtomicInteger.incrementAndGet();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseEntity() {
	}

}
