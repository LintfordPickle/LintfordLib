package net.lintfordlib.core.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.InputGamepad;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class InputTypeMapper implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int INPUT_TYPE_INDEX_KEYBOARD = 0;
	public static final int INPUT_TYPE_INDEX_GAMEPAD = 1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int inputTypeIndex;

	public int assignedToPlayerIndex;
	private float mCooldownTimer;

	private InputGamepad gamepad;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public InputTypeMapper(int typeIndex) {
		inputTypeIndex = typeIndex;
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
		return inputTypeIndex == INPUT_TYPE_INDEX_KEYBOARD;
	}

	@Override
	public boolean allowGamepadInput() {
		return inputTypeIndex == INPUT_TYPE_INDEX_GAMEPAD;
	}

	@Override
	public boolean allowMouseInput() {
		return false;
	}
}
