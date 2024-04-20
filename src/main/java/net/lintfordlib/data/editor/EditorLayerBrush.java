package net.lintfordlib.data.editor;

public class EditorLayerBrush {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final int NO_OWNER_HASH = -1;

	public static final int NO_ACTION_UID = -1;
	public static final int NO_LAYER_UID = -1;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mEditorLayerUid = NO_LAYER_UID;
	private int mEditorActionUid = NO_ACTION_UID;

	private int mOwnerHash;

	private int mLayerIndexCounter = 0;

	public int getNewLayerUid() {
		return mLayerIndexCounter++;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public EditorLayerBrush() {
		mOwnerHash = NO_OWNER_HASH;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void clearLayer() {
		mEditorLayerUid = NO_LAYER_UID;
		mEditorActionUid = NO_ACTION_UID;

		mOwnerHash = NO_OWNER_HASH;
	}

	public void clearAction() {
		mEditorActionUid = NO_ACTION_UID;
	}

	// Owner ---------------------------------------

	public boolean isOwnerSet() {
		return mOwnerHash != NO_OWNER_HASH;
	}

	public boolean isOwner(int ownerhash) {
		return mOwnerHash == ownerhash;
	}

	public boolean isOwnerOrNoOwner(int ownerhash) {
		return mOwnerHash == NO_OWNER_HASH || mOwnerHash == ownerhash;
	}

	// Layers --------------------------------------

	public int brushLayer() {
		return mEditorLayerUid;
	}

	public void brushLayer(int layerId, int ownerHash) {
		if (isOwnerOrNoOwner(ownerHash) == false) {
			return; // not owner
		}

		if (layerId == NO_LAYER_UID) {
			clearLayer();
			return;
		}

		mOwnerHash = ownerHash;

		mEditorLayerUid = layerId;
	}

	public boolean isBrushLayer(int layerId) {
		return mEditorLayerUid == layerId;
	}

	// Actons --------------------------------------

	public boolean isActionSet() {
		return mEditorActionUid != NO_ACTION_UID;
	}

	public boolean isActionModeSet(int actionUid) {
		return mEditorActionUid == actionUid;
	}

	public int brushActionUid() {
		return mEditorActionUid;
	}

	public void brushActionUid(int actionUid) {
		mEditorActionUid = actionUid;
	}
}
