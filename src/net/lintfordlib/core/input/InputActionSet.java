package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.GamepadManager;

public class InputActionSet {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int playerIndex;

	private boolean mKeyboardEnabled;
	private int mGamepadIndex;

	private final Map<Integer, InputActionState> actions = new HashMap<>(); // <ActionCode, InputActionState>
	private final List<InputActionState> actionUpdateList = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isKeyboardEnabled(boolean isKeyboardEnabled) {
		mKeyboardEnabled = isKeyboardEnabled;
	}

	public boolean isKeyboardEnabled() {
		return mKeyboardEnabled;
	}

	public void gamepadIndex(int gamepadIndex) {
		mGamepadIndex = gamepadIndex;
	}

	public int gamepadIndex() {
		return mGamepadIndex;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public InputActionSet(int playerIndex) {
		this.playerIndex = playerIndex;
		mGamepadIndex = GamepadManager.GAMEPAD_INDEX_NONE;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		final var numActionStates = actionUpdateList.size();
		for (int i = 0; i < numActionStates; i++) {
			final var actionState = actionUpdateList.get(i);

			actionState.update(core, this);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public InputActionState getGameInputActionByUid(int inputActionUid) {
		return actions.get(inputActionUid);
	}

	public void addActionToSet(InputActionState action) {
		if (action == null)
			return;

		if (actions.containsKey(action.actionUid))
			return;

		actions.put(action.actionUid, action);
		actionUpdateList.add(action);
	}

	public void cleanUp() {

	}

}
