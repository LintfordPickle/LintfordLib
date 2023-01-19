package net.lintford.library.core.input;

import java.io.Serializable;

public class KeyEventAction implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3787420369463342303L;

	public static final int UNASSIGNED_KEY_CODE = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mDefaultBoundKeyCode;
	private final int mEventActionUid;
	private int mBoundKeyCode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int defaultBoundKeyCode() {
		return mDefaultBoundKeyCode;
	}

	public int eventActionUid() {
		return mEventActionUid;
	}

	public void boundKeyCode(int newKeyCode) {
		mBoundKeyCode = newKeyCode;
	}

	public int getBoundKeyCode() {
		return mBoundKeyCode > UNASSIGNED_KEY_CODE ? mBoundKeyCode : mDefaultBoundKeyCode;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public KeyEventAction(int eventActionUid, int defaultBoundKeyCode) {
		mEventActionUid = eventActionUid;
		mDefaultBoundKeyCode = defaultBoundKeyCode;
		mBoundKeyCode = UNASSIGNED_KEY_CODE;

	}

	public KeyEventAction(int eventActionUid, int defaultBoundKeyCode, int initialBoundKeyCode) {
		this(eventActionUid, defaultBoundKeyCode);

		mBoundKeyCode = initialBoundKeyCode;
	}
}
