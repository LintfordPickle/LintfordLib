package net.lintford.library.core.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static AtomicInteger mAtomicInteger = new AtomicInteger(Integer.MIN_VALUE);

	// --------------------------------------
	// Variables
	// --------------------------------------

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
		name = "";

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// TODO: Need to return an array of possible items/objects to be dropped
	public String getDropItemName() {
		if (outname != null && !outname.isEmpty())
			return outname;

		return name;
	}

}