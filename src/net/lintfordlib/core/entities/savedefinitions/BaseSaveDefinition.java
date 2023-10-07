
package net.lintfordlib.core.entities.savedefinitions;

import java.io.Serializable;

public abstract class BaseSaveDefinition implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 1468021648702818814L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient int mLastSaveHash;

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

	public BaseSaveDefinition() {

	}

}