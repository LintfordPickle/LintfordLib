
package net.lintford.library.core.entity;

import java.io.Serializable;

public abstract class BaseInstanceData implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 934763865686681475L;

	public static final int NOT_ASSIGNED_UID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mLastSaveHash;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int lastSaveHash() {
		return mLastSaveHash;

	}

	public boolean isDirty() {
		return getSaveHash() != mLastSaveHash;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseInstanceData() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Called after the save has been loaded. Allows sub-classes to arrange their data after deserialization. */
	public void initialize(Object pParent) {

	}

	/** Called before the game is about to close. Allows sub-classes to arrange their data for serialization. */
	public void beforeSerialization() {

	}

	public int getSaveHash() {
		return hashCode();

	}

}
