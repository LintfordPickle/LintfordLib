package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list (or lists) of keyboard and gamepad bindings that are expected for a game. Each KeyAction is assigned a 'game specific' unique integer id which will be referred to during game updates to see if key bound to that event has been activated.
 */
public abstract class BindableInputActionMap {

	// When a game is started, correspoonding KeyEventActions objects are created for
	// each of these to actually do the in-game polling.
	public class InputActionBinding {

		public static final int NO_DEFAULT_BOUND_KEY_CODE = -1;

		public final String eventActionName; // The name of the action to display ('fire', 'jump' etc.)
		public final int eventActionUid; // A unique event action uid to identify the event.

		public int defaultBoundKeyboardCode; // The keybaord input code bound to this event by default.
		public int defaultBoundGamepadCode; // The gamepad input code bound to this event by default.

		private InputActionBinding(String name, int eventActionUid) {
			this(name, eventActionUid, NO_DEFAULT_BOUND_KEY_CODE, NO_DEFAULT_BOUND_KEY_CODE);
		}

		private InputActionBinding(String name, int eventActionUid, int defaultBoundKeyboardCode) {
			this(name, eventActionUid, defaultBoundKeyboardCode, NO_DEFAULT_BOUND_KEY_CODE);
		}

		private InputActionBinding(String name, int eventActionUid, int defaultBoundKeyboardCode, int defaultBoundGamepadCode) {
			this.eventActionName = name;
			this.eventActionUid = eventActionUid;
			this.defaultBoundKeyboardCode = defaultBoundKeyboardCode;
			this.defaultBoundGamepadCode = defaultBoundGamepadCode;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<InputActionBinding> mBindableInputActions = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<InputActionBinding> bindableEventActions() {
		return mBindableInputActions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BindableInputActionMap() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// called from internal KeyEventActionManager when consuming/setting up the key events.
	void registerEventActions(GameInputActionManager keyEventActionManager) {
		final int numInputActions = mBindableInputActions.size();
		for (int i = 0; i < numInputActions; i++) {
			final var eventActionToCreate = mBindableInputActions.get(i);
			final var gameInputAction = keyEventActionManager.getOrCreateGameEventAction(eventActionToCreate.eventActionUid);

			if (eventActionToCreate.defaultBoundKeyboardCode != -1)
				gameInputAction.addKeyboardBinding(eventActionToCreate.defaultBoundKeyboardCode);

			if (eventActionToCreate.defaultBoundGamepadCode != -1)
				gameInputAction.addGamepadBinding(eventActionToCreate.defaultBoundGamepadCode);

		}
	}

	protected void addNewEventAction(String name, int eventActionUid, int defaultBoundKeyboardCode, int defaultGamepadInputCode) {
		final var newInputActionBinding = new InputActionBinding(name, eventActionUid, defaultBoundKeyboardCode, defaultGamepadInputCode);
		mBindableInputActions.add(newInputActionBinding);
	}
}
