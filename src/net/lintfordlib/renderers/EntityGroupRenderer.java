package net.lintfordlib.renderers;

public class EntityGroupRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mEntityGroupUid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityGroupUid() {
		return mEntityGroupUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EntityGroupRenderer(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;
	}
}