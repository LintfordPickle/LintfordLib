
package net.lintford.library.data;

import java.io.Serializable;

public abstract class BaseData implements Serializable {

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
		return getSaveHash() != mLastSaveHash;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseData() {

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
