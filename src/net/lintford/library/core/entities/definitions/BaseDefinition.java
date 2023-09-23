package net.lintford.library.core.entities.definitions;

public abstract class BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final short INVALID_DEFINITION_UID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient short mDefinitionUid;

	/** used for linking entities */
	protected String outname;

	/** the name of the definition */
	public String name;
	
	/** user friendly name for the front end */
	public String displayName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public short definitionUid() {
		return mDefinitionUid;
	}

	public String definitionName() {
		return name;
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
		this(INVALID_DEFINITION_UID);
	}

	public BaseDefinition(short definitionUid) {
		mDefinitionUid = definitionUid;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(final short pDefinitionUid) {
		mDefinitionUid = pDefinitionUid;
	}
}
