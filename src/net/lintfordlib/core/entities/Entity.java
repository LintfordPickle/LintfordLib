package net.lintfordlib.core.entities;

public abstract class Entity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int uid;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Entity(int uid) {
		this.uid = uid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Called before the game is about to close. Allows sub-classes to arrange their data for serialization. */
	public void beforeSaving() {

	}

	/** Called after the save process has completed */
	public void afterSaving() {

	}

	/** Called before the object is loaded. Allows sub-classes to arrange their data before deserialization. */
	public void beforeLoading() {

	}

	/** Called after the object has been loaded. Allows sub-classes to arrange their data after deserialization. */
	public void afterLoading(Object pParent) {

	}
}