package net.lintford.library.core.input;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.options.reader.IniFile;

public class KeyEventActionManager extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	private InputManager mInputManager;
	private final Map<Integer, KeyEventAction> mEventActionMap = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public KeyEventAction getEventActionByUid(int eventActionUid) {
		return mEventActionMap.get(eventActionUid);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public KeyEventActionManager(InputManager inputManager, String configFilename) {
		super(configFilename);

		mInputManager = inputManager;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void registerNewEventAction(int eventActionUid, int defaultKeyCode) {
		if (mEventActionMap.get(eventActionUid) != null)
			return; // already taken

		final var lNewEventAction = new KeyEventAction(eventActionUid, defaultKeyCode);
		mEventActionMap.put(eventActionUid, lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + eventActionUid + " to key code [" + defaultKeyCode + "]");
	}

	public boolean getCurrentControlActionState(int eventActionUid) {
		final var lEventAction = mEventActionMap.get(eventActionUid);
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

		if (isEmpty())
			return;

		// TODO: Does this work if the order of the keybinds changes (either in the file or in LintfordCore.onInitializeInputActions()) ?
		for (var lKeyBindingEntry : mEventActionMap.entrySet()) {
			final var lValue = getInt(lSectionName, Integer.toString(lKeyBindingEntry.getValue().eventActionUid()), lKeyBindingEntry.getValue().defaultBoundKeyCode());
			lKeyBindingEntry.getValue().boundKeyCode(lValue);
		}
	}
}
