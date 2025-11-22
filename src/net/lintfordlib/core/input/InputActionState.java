package net.lintfordlib.core.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.GamepadManager;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class InputActionState {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DOWN_TIMER_DELAY_MS = 200; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int actionUid;
	public final InputAction parent;

	private boolean mIsDown;
	private float mValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns true if the action state is triggered and the cooldown time has elapsed.
	 * If the return value is true, then the cooldown timer of the passed IInputProcessor will automatically be reset.  
	 */
	public boolean isDownTimed(IInputProcessor inputProcessor) {
		if (!mIsDown)
			return false;

		if (inputProcessor != null) {
			if (!inputProcessor.isCoolDownElapsed())
				return false;

			inputProcessor.resetCoolDownTimer();
		}

		return true;
	}

	public float value() {
		return mValue;
	}

	public boolean isDown() {
		return mIsDown;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public InputActionState(InputAction parentedAction) {
		parent = parentedAction;
		actionUid = parent.actionUid;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void reset() {
		mIsDown = false;
		mValue = 0;
	}

	public void update(LintfordCore core, InputActionSet actionSet) {

		reset();

		handleKeyboardInput(core, actionSet);

		handleGamepadInput(core, actionSet);

	}

	private void handleKeyboardInput(LintfordCore core, InputActionSet actionSet) {
		if (actionSet.isKeyboardEnabled()) {

			final var inputManager = core.input();

			final var lIsKeyDown = inputManager.keyboard().isKeyDown(parent.getBoundKeyCode());
			mIsDown |= lIsKeyDown;
		}
	}

	private void handleGamepadInput(LintfordCore core, InputActionSet actionSet) {

		if (actionSet.gamepadIndex() == GamepadManager.GAMEPAD_INDEX_NONE)
			return;

		final var inputManager = core.input();
		final var gamepadManager = inputManager.gamepads();

		if (gamepadManager.isSomeComponentCapturingInput())
			return;

		final var activeGamepads = gamepadManager.getActiveGamepads();
		if (activeGamepads.size() > 0) {

			final var isGamepadIndexSpecified = actionSet.gamepadIndex() != GamepadManager.GAMEPAD_INDEX_ANY;
			final var boundGamepadInputCode = parent.getBoundGamepadCode();

			if (isGamepadIndexSpecified) {
				final var gamepadIndex = actionSet.gamepadIndex();

				mIsDown |= gamepadManager.isGamepadButtonDown(gamepadIndex, boundGamepadInputCode, null);
				
				final var anyAxisValue = gamepadManager.getGamepadAxis(gamepadIndex, boundGamepadInputCode, null);
				mIsDown |= Math.abs(anyAxisValue) > 0.05f;

				final var result = anyAxisValue;
				mIsDown |= Math.abs(result) > 0.02f;

			} else {

				mIsDown |= gamepadManager.isAnyGamepadButtonDown(boundGamepadInputCode);
				final var anyAxisValue = gamepadManager.getAnyGamepadAxis(boundGamepadInputCode);
				mIsDown |= Math.abs(anyAxisValue) > 0.05f;

				final var result = anyAxisValue;
				mIsDown |= Math.abs(result) > 0.02f;

			}
		}
	}

}
