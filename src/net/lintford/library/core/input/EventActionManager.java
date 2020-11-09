package net.lintford.library.core.input;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.debug.Debug;

public class EventActionManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private InputManager mInputManager;
	private final Map<Integer, EventAction> mEventActionMap = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public EventAction getEventActionByUid(int pEventActionUid) {
		return mEventActionMap.get(pEventActionUid);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public EventActionManager(InputManager pInputManager) {
		mInputManager = pInputManager;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void registerNewEventAction(int pEventActionUid, int pDefaultKeyCode) {
		if (mEventActionMap.get(pEventActionUid) != null)
			return; // already taken

		final var lNewEventAction = new EventAction(pEventActionUid, pDefaultKeyCode);
		mEventActionMap.put(pEventActionUid, lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + pEventActionUid + " to key code [" + pDefaultKeyCode + "]");

	}

	public boolean getCurrentControlActionState(int pEventActionUid) {
		final var lEventAction = mEventActionMap.get(pEventActionUid);
		if (lEventAction == null)
			return false;

		return mInputManager.keyboard().isKeyDown(lEventAction.getBoundKeyCode());
	}

}
