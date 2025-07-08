package net.lintfordlib.core.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.InputGamepad;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class InputTypeMapper implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int NOT_ASSIGNED = 0;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final InputType inputTypeIndex;

	public int assignedToPlayerIndex;
	private float mCooldownTimer;

	private InputGamepad gamepad;
	private boolean mIsLockedToPlayer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLockedToPlayer() {
		return mIsLockedToPlayer;
	}

	public void isLockedToPlayer(boolean newvalue) {
		mIsLockedToPlayer = newvalue;
	}

	public InputGamepad gamepad() {
		return gamepad;
	}

	public void gamepad(InputGamepad gamepad) {
		this.gamepad = gamepad;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mCooldownTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mCooldownTimer = cooldownInMs;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public InputTypeMapper(InputType typeIndex) {
		if (typeIndex == null) {
			typeIndex = InputType.None;
		}

		inputTypeIndex = typeIndex;
		assignedToPlayerIndex = NOT_ASSIGNED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		if (mCooldownTimer > 0)
			mCooldownTimer -= core.gameTime().elapsedTimeMilli();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public boolean allowKeyboardInput() {
		return inputTypeIndex == InputType.Keyboard;
	}

	@Override
	public boolean allowGamepadInput() {
		return inputTypeIndex == InputType.Gamepad;
	}

	@Override
	public boolean allowMouseInput() {
		return inputTypeIndex == InputType.Mouse;
	}
}
