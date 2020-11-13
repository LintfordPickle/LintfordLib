package net.lintford.library.screenmanager;

public class ClickAction {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_UID_UNASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mButtonUid = BUTTON_UID_UNASSIGNED;
	private boolean mConsumed;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int buttonUid() {
		return mButtonUid;
	}

	public boolean isConsumed() {
		return mConsumed;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ClickAction() {
		mConsumed = false;
	}

	public ClickAction(int pButtonID) {
		this();
		mButtonUid = pButtonID;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int consume() {
		mConsumed = true;
		return mButtonUid;
	}

	public void setNewClick(int pEntryID) {
		mConsumed = false;
		mButtonUid = pEntryID;

	}

	public void reset() {
		mConsumed = false;
		mButtonUid = BUTTON_UID_UNASSIGNED;
	}

}
