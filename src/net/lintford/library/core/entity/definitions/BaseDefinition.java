package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseEntity;

public abstract class BaseDefinition extends BaseEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient short definitionID;

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final short pDefinitionID) {
		definitionID = pDefinitionID;

	}

}
