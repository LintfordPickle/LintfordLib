package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.options.reader.IniFile;

public class GameInputActionManager extends IniFile {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BindableInputActionMap mBindableInputActions;

	private IInputProcessor mInputProcessor;

	// TODO: Need to recheck this - don't think I need separate update list anymore.
	private final Map<Integer, GameInputAction> mGameInputActions = new HashMap<>();
	private final List<GameInputAction> mUpdateActionList = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setInputProcessor(IInputProcessor inputProcessor) {
		mInputProcessor = inputProcessor;
	}

	public void clearInputProcessor() {
		mInputProcessor = null;
	}

	public GameInputAction getGameInputActionByUid(int inputActionUid) {
		return mGameInputActions.get(inputActionUid);
	}

	/**
	 * Returns the GameKeyActions instance which was created at the start of the game.
	 */
	public BindableInputActionMap bindableInputActions() {
		return mBindableInputActions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameInputActionManager(String configFilename) {
		super(configFilename);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void addGameKeyActions(BindableInputActionMap gameKeyActions) {
		mBindableInputActions = gameKeyActions;

		// Gets the 'registered' KeyMap from the game specific 'GameKeyActions' subtype
		mBindableInputActions.registerEventActions(this);
	}

	public void update(LintfordCore core) {

		// TODO: The idea of the action list needs to be thought out a little more. I think we need to have an action list per player maybe (See (1) below))?
		// This is because, there could potentially be 2 or more controllers connected, each controlling a different player.
		// The below polling methods do accept controller indices, so there is that

		// we poll the keyboard once for each of the registered key action events,
		// this way the individual action players don't separately poll the keyboard and consume the key timers.
		// we pass the optional input process, which controls if the event manager should be listening to (keyboard) events.
		final int lNumEventActions = mUpdateActionList.size();
		for (int i = 0; i < lNumEventActions; i++) {
			final var lAction = mUpdateActionList.get(i);
			lAction.reset();
			lAction.update(core);

		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public GameInputAction getOrCreateGameEventAction(int eventActionUid) {

		// NOTE: each eventaction can have several bound input types (i.e. `Jump` = 'KEYBOARD:Space' 'KEYBOARD:X', 'GAMEPAD:Y' and 'GAMEPAD:X')
		final var existingInputAction = mGameInputActions.get(eventActionUid);
		if (existingInputAction != null)
			return existingInputAction;

		final var newEventAction = new GameInputAction(eventActionUid);
		mGameInputActions.put(eventActionUid, newEventAction);
		mUpdateActionList.add(newEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Created new input action Id:" + eventActionUid);

		return newEventAction;
	}

	// --------------------------------------
	// TODO: These polling methods are simply not finished

	public boolean getCurrentControlActionState(int eventActionUid) {
		var actionState = false;
		if (mInputProcessor != null) {
			final var gameIntputAction = mGameInputActions.get(eventActionUid);
			if (gameIntputAction != null) {

				// keyboard mapping
				if (mInputProcessor.allowKeyboardInput()) {

				}

				if (mInputProcessor.allowGamepadInput()) {

				}

				// these can be any input device
				actionState |= gameIntputAction.isDown();
				actionState |= gameIntputAction.value() > 0;
			}
		}

		return actionState;
	}

	public boolean getCurrentControlActionStateTimed(int eventActionUid) {

		var actionStateReturnValue = false;
		final var gameIntputAction = mGameInputActions.get(eventActionUid);
		if (gameIntputAction != null) {

			// keyboard mapping
			if (mInputProcessor.allowKeyboardInput()) {

			}

			if (mInputProcessor.allowGamepadInput()) {

			}

			actionStateReturnValue |= gameIntputAction.isDownTimed();

		}

		return actionStateReturnValue;
	}

	// --------------------------------------
	// Ini-File
	// --------------------------------------

	final String KeyBindingSectionName = "KEY_BINDING";
	final String GamepadBindingSectionName = "GAMEPAD_BINDING";

	@Override
	public void saveConfig() {
		clearEntries();

		for (var keybindEntry : mGameInputActions.entrySet()) {
			setValue(KeyBindingSectionName, Integer.toString(keybindEntry.getKey()), Integer.toString(keybindEntry.getValue().getBoundKeyCode()));
			setValue(GamepadBindingSectionName, Integer.toString(keybindEntry.getKey()), Integer.toString(keybindEntry.getValue().getBoundGamepadCode()));
		}

		// TODO: Save Gamepad too?

		super.saveConfig();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		if (isEmpty())
			return;

		final var NO_BINDING_FOUND_VALUE = -1;

		for (var inputActionBindingEntry : mGameInputActions.entrySet()) {
			final var keyBindingValue = getInt(KeyBindingSectionName, Integer.toString(inputActionBindingEntry.getValue().eventActionUid()), NO_BINDING_FOUND_VALUE);
			if (keyBindingValue > NO_BINDING_FOUND_VALUE)
				inputActionBindingEntry.getValue().boundKeyCode(keyBindingValue);

			final var gamepadValue = getInt(GamepadBindingSectionName, Integer.toString(inputActionBindingEntry.getValue().eventActionUid()), NO_BINDING_FOUND_VALUE);
			if (gamepadValue > NO_BINDING_FOUND_VALUE)
				inputActionBindingEntry.getValue().boundGamepadCode(gamepadValue);
		}

		// TODO: Load Gamepad too?
	}
}
