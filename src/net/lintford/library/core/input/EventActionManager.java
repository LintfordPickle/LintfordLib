package net.lintford.library.core.input;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.options.reader.IniFile;

public class EventActionManager extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

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

	public EventActionManager(InputManager pInputManager, String pConfigFilename) {
		super(pConfigFilename);

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

	// --------------------------------------
	// Ini-File
	// --------------------------------------

	final String lSectionName = "KEY_BINDING";

	@Override
	public void saveConfig() {
		clearEntries();

		for (var lKeyBindingEntry : mEventActionMap.entrySet()) {
			setValue(lSectionName, Integer.toString(lKeyBindingEntry.getKey()), Integer.toString(lKeyBindingEntry.getValue().getBoundKeyCode()));

		}

		super.saveConfig();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		// if no file exists, then do nothing
		if (isEmpty()) {

		} else {
			for (var lKeyBindingEntry : mEventActionMap.entrySet()) {
				final var lValue = getInt(lSectionName, Integer.toString(lKeyBindingEntry.getValue().eventActionUid), lKeyBindingEntry.getValue().defaultBoundKeyCode);
				lKeyBindingEntry.getValue().boundKeyCode = lValue;

			}

		}

	}

}
