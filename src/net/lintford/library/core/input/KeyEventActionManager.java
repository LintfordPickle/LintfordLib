package net.lintford.library.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintford.library.core.LintfordCore;
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
	private final List<KeyEventAction> mUpdateActionList = new ArrayList<>();

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
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		// we pol the keyboard once, so the individual action players don't separately poll the keyboard (and consume the timers etc.)
		final int lNumEventActions = mUpdateActionList.size();
		for (int i = 0; i < lNumEventActions; i++) {
			final var lAction = mUpdateActionList.get(i);
			final var lIsKeyDown = mInputManager.keyboard().isKeyDown(lAction.getBoundKeyCode());

			lAction.incDownTimer(lDeltaTime);
			lAction.isDown(lIsKeyDown);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void registerNewEventAction(int eventActionUid, int defaultKeyCode) {
		if (mEventActionMap.get(eventActionUid) != null)
			return; // already taken

		final var lNewEventAction = new KeyEventAction(eventActionUid, defaultKeyCode);
		mEventActionMap.put(eventActionUid, lNewEventAction);
		mUpdateActionList.add(lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + eventActionUid + " to key code [" + defaultKeyCode + "]");
	}

	public boolean getCurrentControlActionState(int eventActionUid) {
		final var lEventAction = mEventActionMap.get(eventActionUid);
		if (lEventAction == null)
			return false;

		return lEventAction.isDown();
	}

	public boolean getCurrentControlActionStateTimed(int eventActionUid) {
		final var lEventAction = mEventActionMap.get(eventActionUid);
		if (lEventAction == null)
			return false;

		return lEventAction.isDownTimed();
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
