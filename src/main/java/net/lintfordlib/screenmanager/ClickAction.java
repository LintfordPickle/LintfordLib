package net.lintfordlib.screenmanager;

public class ClickAction {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int ENTRY_UID_UNASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mEntryUid = ENTRY_UID_UNASSIGNED;
	private boolean mConsumed;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int entryUid() {
		return mEntryUid;
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

	public ClickAction(int entryUid) {
		this();
		mEntryUid = entryUid;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int consume() {
		mConsumed = true;
		return mEntryUid;
	}

	public void setNewClick(int entryUid) {
		mConsumed = false;
		mEntryUid = entryUid;
	}

	public void reset() {
		mConsumed = false;
		mEntryUid = ENTRY_UID_UNASSIGNED;
	}
}
