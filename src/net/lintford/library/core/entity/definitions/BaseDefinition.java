package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseEntity;

public abstract class BaseDefinition extends BaseEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient short definitionID;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String definitionName() {
		return name;
	}

	public int definitionUid() {
		return definitionID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDefinition() {
		definitionID = -1;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final short pDefinitionID) {
		definitionID = pDefinitionID;

	}

}
