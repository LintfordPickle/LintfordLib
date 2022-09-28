package net.lintford.library.renderers;

public class EntityGroupRenderer {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mEntityGroupUid;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entityGroupID() {
		return mEntityGroupUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EntityGroupRenderer(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;
	}
}