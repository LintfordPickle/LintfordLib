package net.lintford.library.core.entity.definitions;

import net.lintford.library.core.entity.BaseEntity;

public abstract class BaseDefinition extends BaseEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient short definitionUid;

	/** used for linking entities */
	protected String outname;

	/** user friendly name for the front end */
	protected String displayName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String definitionName() {
		return name;
	}

	public String displayName() {
		return displayName;
	}

	public String outname() {
		if (outname != null && !outname.isEmpty())
			return outname;

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
