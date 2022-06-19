
package net.lintford.library.core.entity;

import java.io.Serializable;

public abstract class BaseInstanceData implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 934763865686681475L;

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
		return hashCode() != mLastSaveHash;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseInstanceData() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	/** Called before the game is about to close. Allows sub-classes to arrange their data for serialization. */
	public void beforeSaving() {

	}

	/** Called after the save process has completed */
	public void afterSaving() {

	}

	/** Called after the object has been loaded. Allows sub-classes to arrange their data after deserialization. */
	public void afterLoaded(Object pParent) {

	}
}