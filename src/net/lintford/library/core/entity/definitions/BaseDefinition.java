package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseEntity;

public abstract class BaseDefinition extends BaseEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient short definitionUid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String definitionName() {
		return name;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDefinition() {
		definitionUid = -1;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final short pDefinitionUid) {
		definitionUid = pDefinitionUid;

	}

}
