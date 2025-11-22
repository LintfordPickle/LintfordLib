package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list (or lists) of keyboard and gamepad bindings that are expected for a game. Each KeyAction is assigned a 'game specific' unique integer id which will be referred to during game updates to see if key bound to that event has been activated.
 */
public abstract class BindableActionMap {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<InputAction> mBindableInputActions = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<InputAction> bindableActions() {
		return mBindableInputActions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BindableActionMap() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void addNewEventAction(String name, int eventActionUid, int defaultBoundKeyboardCode, int defaultGamepadInputCode) {
		final var newInputActionBinding = new InputAction(name, eventActionUid, defaultBoundKeyboardCode, defaultGamepadInputCode);
		mBindableInputActions.add(newInputActionBinding);
	}
}
