package net.lintford.library.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.input.mouse.IInputProcessor;
import net.lintford.library.options.reader.IniFile;

public class KeyEventActionManager extends IniFile {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private InputManager mInputManager;
	private IInputProcessor mInputProcessor;

	private final Map<Integer, KeyEventAction> mKeyboardEventActionMap = new HashMap<>();
	private final Map<Integer, KeyEventAction> mGamepadEventActionMap = new HashMap<>();

	private final List<KeyEventAction> mUpdateActionList = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setInputProcessor(IInputProcessor inputProcessor) {
		mInputProcessor = inputProcessor;
	}

	public void clearInputProcessor() {
		mInputProcessor = null;
	}

	public KeyEventAction getEventActionByUid(int eventActionUid) {
		return mKeyboardEventActionMap.get(eventActionUid);
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

		// we poll the keyboard once for each of the registered key action events,
		// this way the individual action players don't separately poll the keyboard and consume the key timers.
		// we pass the optional input process, which controls if the event manager should be listening to (keyboard) events.
		final int lNumEventActions = mUpdateActionList.size();
		for (int i = 0; i < lNumEventActions; i++) {
			final var lAction = mUpdateActionList.get(i);
			final var lIsKeyDown = mInputManager.keyboard().isKeyDown(lAction.getBoundKeyCode(), mInputProcessor);

			lAction.incDownTimer(lDeltaTime);
			lAction.isDown(lIsKeyDown);
		}

		// TODO: Missing Gamepad action updates
		// and this needs to be across players ...

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void registerNewKeyboardEventAction(int eventActionUid, int defaultKeyCode) {
		if (mKeyboardEventActionMap.get(eventActionUid) != null)
			return; // already taken

		final var lNewEventAction = new KeyEventAction(eventActionUid, defaultKeyCode);
		mKeyboardEventActionMap.put(eventActionUid, lNewEventAction);
		mUpdateActionList.add(lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + eventActionUid + " to key code [" + defaultKeyCode + "]");
	}

	public void registerNewGamepadEventAction(int eventActionUid, int defaultKeyCode) {
		if (mGamepadEventActionMap.get(eventActionUid) != null)
			return; // already taken

		final var lNewEventAction = new KeyEventAction(eventActionUid, defaultKeyCode);
		mGamepadEventActionMap.put(eventActionUid, lNewEventAction);
		mUpdateActionList.add(lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + eventActionUid + " to key code [" + defaultKeyCode + "]");
	}

	public void registerNewGamepadAxisAction(int eventActionUid, int defaultKeyCode) {

	}

	public boolean getCurrentControlActionState(int eventActionUid) {
		var actionState = false;
		if (mInputProcessor == null || mInputProcessor.allowKeyboardInput()) {
			final var lKeyboardEventAction = mKeyboardEventActionMap.get(eventActionUid);
			if (lKeyboardEventAction != null) {
				actionState |= lKeyboardEventAction.isDown();
			}
		}

		if (mInputProcessor == null || mInputProcessor.allowGamepadInput()) {
			final var lGamepadEventAction = mGamepadEventActionMap.get(eventActionUid);
			if (lGamepadEventAction != null) {
				actionState |= lGamepadEventAction.isDown();
			}
		}

		return actionState;
	}

	public boolean getCurrentControlActionStateTimed(int eventActionUid) {
		var actionState = false;
		if (mInputProcessor == null || mInputProcessor.allowKeyboardInput()) {
			final var lKeyboardEventAction = mKeyboardEventActionMap.get(eventActionUid);
			if (lKeyboardEventAction != null) {
				actionState |= lKeyboardEventAction.isDownTimed();
			}
		}

		if (mInputProcessor == null || mInputProcessor.allowGamepadInput()) {
			final var lGamepadEventAction = mGamepadEventActionMap.get(eventActionUid);
			if (lGamepadEventAction != null) {
				actionState |= lGamepadEventAction.isDownTimed();
			}
		}

		return actionState;
	}

	// --------------------------------------
	// Ini-File
	// --------------------------------------

	final String lSectionName = "KEY_BINDING";

	@Override
	public void saveConfig() {
		clearEntries();

		for (var lKeyBindingEntry : mKeyboardEventActionMap.entrySet()) {
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
		for (var lKeyBindingEntry : mKeyboardEventActionMap.entrySet()) {
			final var lValue = getInt(lSectionName, Integer.toString(lKeyBindingEntry.getValue().eventActionUid()), lKeyBindingEntry.getValue().defaultBoundKeyCode());
			lKeyBindingEntry.getValue().boundKeyCode(lValue);
		}
	}
}
