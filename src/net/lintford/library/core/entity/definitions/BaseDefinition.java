package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseEntity;

public abstract class BaseDefinition extends BaseEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	// FIXME: I think this should enforce sub-classes to implement an ID system for definition files.
	public transient int definitionID; // Calculated and cached. Re-Calculated in block/mod change detected.

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final int pDefinitionID) {
		definitionID = pDefinitionID;

	}

}
