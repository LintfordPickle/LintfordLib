package net.lintford.library.core.input;

import java.io.Serializable;

public class EventAction implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3787420369463342303L;

	public static final int UNASSIGNED_KEY_CODE = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int defaultBoundKeyCode;
	public final int eventActionUid;
	public int boundKeyCode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int getBoundKeyCode() {
		return boundKeyCode > UNASSIGNED_KEY_CODE ? boundKeyCode : defaultBoundKeyCode;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public EventAction(int pEventActionUid, int pDefaultBoundKeyCode) {
		eventActionUid = pEventActionUid;
		defaultBoundKeyCode = pDefaultBoundKeyCode;
		boundKeyCode = UNASSIGNED_KEY_CODE;

	}

	public EventAction(int pEventActionUid, int pDefaultBoundKeyCode, int pInitialBoundKeyCode) {
		this(pEventActionUid, pDefaultBoundKeyCode);

		boundKeyCode = pInitialBoundKeyCode;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
