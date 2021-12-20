package net.lintford.library.core.entity;

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

	// although the name is a fundamental part of the definition, it isn't final becauase many definitions will be
	// deserialized from file at runtime and their names are not available during object creation.

	public String name;
	public String outname;

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

	// --------------------------------------
	// Methods
	// --------------------------------------

	public String getDropItemName() {
		if (outname != null && !outname.isEmpty())
			return outname;

		return name;
	}

}
