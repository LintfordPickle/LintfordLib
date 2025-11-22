package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.gamepad.GamepadManager;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.options.reader.IniFile;

public class ActionManager extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int PLAYER_INDEX_ALL = -1;

	public static final int MAX_NUM_PLAYERS = 4;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BindableActionMap mBindableMenuActions;
	private BindableActionMap mBindableGameActions;

	/** This is a map of all the action sets that are available for each player. There is a generic action set that is player agnostic (PLAYER_INDEX_ALL). */
	private final Map<Integer, InputActionSet> mActionSets = new HashMap<>(); // <playerIndex, InputActionSet>
	private final List<InputActionSet> mActionSetUpdateList = new ArrayList<>();

	/**
	 * A list of distinct actions that have been registered and have bound key/gamepad codes associated with them).
	 * These actions are where the actual gamepad/keyboard bindings are held, and each InputActionState references one of these 
	 * to know which gamepad/keyboard bind to use. This way, we are only checking for each input once, and the states are tracked for
	 * each player (via the ActionSets).
	 */
	private final List<InputAction> mGlobalInputActionsList = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the GameKeyActions instance which was created at the start of the game.
	 */
	public BindableActionMap bindableGameActions() {
		return mBindableGameActions;
	}

	public BindableActionMap bindableMenuActions() {
		return mBindableMenuActions;
	}

	/**
	 * Gets a InputActionBinding with the specified action uid, if it has been registered. Otherwise, NULL is returned.
	 */
	public InputAction getGlobalInputAction(int actionUid) {
		final var numGlobalInputActions = mGlobalInputActionsList.size();
		for (int i = 0; i < numGlobalInputActions; i++) {
			if (mGlobalInputActionsList.get(i).actionUid == actionUid)
				return mGlobalInputActionsList.get(i);

		}

		return null;
	}

	public InputActionState getActionState(int actionUid) {
		return getActionState(actionUid, PLAYER_INDEX_ALL);
	}

	public InputActionState getActionState(int actionUid, int actionSetUid) {
		final var actionSet = getInputActionSet(actionSetUid);

		if (actionSet == null)
			return null;

		return actionSet.getGameInputActionByUid(actionUid);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ActionManager(String configFilename) {
		super(configFilename);

		setupDefaultPlayer();
	}

	private void setupDefaultPlayer() {
		final var defaultPlayerSet = getOrCreateInputActionSet(PLAYER_INDEX_ALL);

		// default player gets keyboad and all pads
		defaultPlayerSet.isKeyboardEnabled(true);
		defaultPlayerSet.gamepadIndex(GamepadManager.GAMEPAD_INDEX_ANY);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void setupPlayerActionSet(int playerIndex, int gamepadIndex, boolean isKeyboardEnabled) {

		var playerActionSet = getInputActionSet(playerIndex);
		if (playerActionSet == null) {
			// Cretae a new action set and add the game actions to it
			playerActionSet = createActionSet(playerIndex);
			addIfNotExistToActionSet(mBindableGameActions, playerActionSet);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating a new player action set for GameActions (player id: " + playerIndex + ")");
		}

		playerActionSet.isKeyboardEnabled(isKeyboardEnabled);
		playerActionSet.gamepadIndex(gamepadIndex);

	}

	/**
	 * Creates the menu keys/gamepad input
	 */
	public void addMenuActions(BindableActionMap menuKeyActions) {
		mBindableMenuActions = menuKeyActions;

		final var menuActionSet = getOrCreateInputActionSet(PLAYER_INDEX_ALL);
		addIfNotExistToActionSet(mBindableMenuActions, menuActionSet);
	}

	public void addGameActions(BindableActionMap gameKeyActions) {
		mBindableGameActions = gameKeyActions;

		final var defaultPlayerSet = getOrCreateInputActionSet(PLAYER_INDEX_ALL);
		addIfNotExistToActionSet(mBindableGameActions, defaultPlayerSet);
	}

	// TODO: remove the 'custom' action sets for players (The InputActionStates).
	public void cleanGameActionsForPlayers() {

		// Just remove all the player action states, they'll be re-added in the next game screen again
		for (var i = 0; i < MAX_NUM_PLAYERS; i++) {
			final var playerActionSet = mActionSets.remove(i);
			if (playerActionSet == null)
				continue;

			playerActionSet.cleanUp();
		}
	}

	private void addIfNotExistToActionSet(BindableActionMap actionMap, InputActionSet actionSet) {

		final var bindableActions = actionMap.bindableActions();
		final var numBindableActions = bindableActions.size();
		for (int i = 0; i < numBindableActions; i++) {
			final var inputActionBinding = bindableActions.get(i);

			// each action needs to be registered 'globally' once.
			if (!mGlobalInputActionsList.contains(inputActionBinding))
				mGlobalInputActionsList.add(inputActionBinding);

			createInputActionStateInSet(inputActionBinding, actionSet);
		}

	}

	public void update(LintfordCore core) {

		// update the event action sets
		final var numActionSetsToUpdate = mActionSetUpdateList.size();
		for (int i = 0; i < numActionSetsToUpdate; i++) {
			mActionSetUpdateList.get(i).update(core);
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public InputActionSet getInputActionSet(int playerIndex) {
		return mActionSets.get(playerIndex);
	}

	public InputActionSet getOrCreateInputActionSet(int playerIndex) {
		final var actionSet = getInputActionSet(playerIndex);
		if (actionSet != null)
			return actionSet;

		return createActionSet(playerIndex);
	}

	private InputActionSet createActionSet(int playerIndex) {
		final var newInputActionSet = new InputActionSet(playerIndex);
		mActionSets.put(playerIndex, newInputActionSet);
		mActionSetUpdateList.add(newInputActionSet);

		return newInputActionSet;
	}

	// Adds the given InputAction as a tracked state to the given actionSet. If the actionSet already contains a matching InputActionState, it will be skipped.
	private void createInputActionStateInSet(InputAction action, InputActionSet actionSet) {
		if (action == null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot create InputActionState from NULL InputActionBinding object!");
			return;
		}

		final var existingInputAction = actionSet.getGameInputActionByUid(action.actionUid);
		if (existingInputAction != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "InputActionState already addded to InputActionSet: " + actionSet.playerIndex);
			return;
		}

		final var inputActionState = new InputActionState(action);
		actionSet.addActionToSet(inputActionState);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Created new InputActionState Id:" + action.actionUid + " for InputActionSet: " + actionSet.playerIndex);
	}

	// --------------------------------------
	// TODO: These polling methods are simply not finished

	public boolean getCurrentControlActionState(int actionUid, IInputProcessor inputProcessor) {
		return getCurrentControlActionState(PLAYER_INDEX_ALL, actionUid, inputProcessor);
	}

	public boolean getCurrentControlActionState(int playerIndex, int actionUid, IInputProcessor inputProcessor) {
		var actionState = false;
		final var actionSet = getInputActionSet(playerIndex);
		if (actionSet == null)
			throw new RuntimeException("You have requested the action set for a player that has not been registered!");

		final var inputAction = actionSet.getGameInputActionByUid(actionUid);
		if (inputAction != null) {

			if (inputProcessor.allowKeyboardInput()) {
				// TODO: Need to differentiate between input methods, and mask them out if disallowed.
			}

			if (inputProcessor.allowGamepadInput()) {
				// TODO: Need to differentiate between input methods, and mask them out if disallowed.
			}

			// The mapping from input device to LintfordInputCode is done inside the GameInputAction class.
			// Here we just record if the event is 'hit'.
			actionState |= inputAction.isDown();
			actionState |= inputAction.value() > 0;
		}

		return actionState;
	}

	public boolean getCurrentControlActionStateTimed(int actionUid, IInputProcessor inputProcessor) {
		return getCurrentControlActionStateTimed(PLAYER_INDEX_ALL, actionUid, inputProcessor);
	}

	public boolean getCurrentControlActionStateTimed(int playerIndex, int actionUid, IInputProcessor inputProcessor) {

		if (!inputProcessor.isCoolDownElapsed())
			return false;

		final var actionSet = getInputActionSet(playerIndex);
		if (actionSet == null)
			throw new RuntimeException("You have requested the action set for a player that has not been registered!");

		var actionStateReturnValue = false;
		final var inputAction = actionSet.getGameInputActionByUid(actionUid);
		if (inputAction != null) {

			if (inputProcessor.allowKeyboardInput()) {
				// TODO: Need to differentiate between input methods, and mask them out if disallowed.
			}

			if (inputProcessor.allowGamepadInput()) {
				// TODO: Need to differentiate between input methods, and mask them out if disallowed.
			}

			// The mapping from input device to LintfordInputCode is done inside the GameInputAction class.
			// Here we just record if the event is 'hit'.
			actionStateReturnValue |= inputAction.isDownTimed(inputProcessor);

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

		for (var keybindEntry : mGlobalInputActionsList) {
			setValue(KeyBindingSectionName, Integer.toString(keybindEntry.actionUid), Integer.toString(keybindEntry.getBoundKeyCode()));
			setValue(GamepadBindingSectionName, Integer.toString(keybindEntry.actionUid), Integer.toString(keybindEntry.getBoundGamepadCode()));
		}

		super.saveConfig();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		if (isEmpty())
			return;

		final var NO_BINDING_FOUND_VALUE = -1;

		for (var inputActionBindingEntry : mGlobalInputActionsList) {
			final var keyBindingValue = getInt(KeyBindingSectionName, Integer.toString(inputActionBindingEntry.actionUid), NO_BINDING_FOUND_VALUE);
			if (keyBindingValue > NO_BINDING_FOUND_VALUE)
				inputActionBindingEntry.boundKeyCode(keyBindingValue);

			final var gamepadValue = getInt(GamepadBindingSectionName, Integer.toString(inputActionBindingEntry.actionUid), NO_BINDING_FOUND_VALUE);
			if (gamepadValue > NO_BINDING_FOUND_VALUE)
				inputActionBindingEntry.boundGamepadCode(gamepadValue);
		}
	}
}
