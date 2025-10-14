package net.lintfordlib.core.input;

import java.io.Serializable;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.GamepadInputMap;

// This actually contains the new bindings!!
public class GameInputAction implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3787420369463342303L;

	public static final int UNASSIGNED_KEY_CODE = -1;
	public static final int DOWN_TIMER_DELAY_MS = 200; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mEventActionUid;

	// Keyboard binding
	private int mDefaultKeyCode; // to reset back to
	private int mBoundKeyCode;

	// Gamepad binding
	private int mDefaultGamepadCode; // to reset back to
	private int mBoundGamepadCode;

	// Mouse binding
	private int mDefaultMouseCode; // to reset back to
	private int mBoundMouseCode;

	private float mDownTimerMs;
	private boolean mIsDown;
	private boolean mIsDownTimed;

	private float mValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int defaultBoundKeyCode() {
		return mDefaultKeyCode;
	}

	public int eventActionUid() {
		return mEventActionUid;
	}

	public void boundKeyCode(int newKeyCode) {
		mBoundKeyCode = newKeyCode;
	}

	public int getBoundKeyCode() {
		return mBoundKeyCode > UNASSIGNED_KEY_CODE ? mBoundKeyCode : mDefaultKeyCode;
	}

	public void boundGamepadCode(int newGamepadCode) {
		mBoundGamepadCode = newGamepadCode;
	}

	public int getBoundGamepadCode() {
		return mBoundGamepadCode > UNASSIGNED_KEY_CODE ? mBoundGamepadCode : mDefaultGamepadCode;
	}

	public void boundMouseCode(int newMouseCode) {
		mBoundMouseCode = newMouseCode;
	}

	public int getBoundMouseCode() {
		return mBoundMouseCode > UNASSIGNED_KEY_CODE ? mBoundMouseCode : mDefaultMouseCode;
	}

	public float value() {
		return mValue;
	}

	public boolean isDown() {
		return mIsDown;
	}

	public boolean isDownTimed() {
		return mIsDownTimed;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public GameInputAction(int eventActionUid) {
		mEventActionUid = eventActionUid;
		mBoundKeyCode = UNASSIGNED_KEY_CODE;

		mDefaultKeyCode = UNASSIGNED_KEY_CODE;
		mBoundKeyCode = UNASSIGNED_KEY_CODE;
		mDefaultGamepadCode = UNASSIGNED_KEY_CODE;
		mBoundGamepadCode = UNASSIGNED_KEY_CODE;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void reset() {
		mIsDown = false;
		mIsDownTimed = false;
		mValue = 0;
	}

	public void update(LintfordCore core) {

		if (mDownTimerMs > 0)
			mDownTimerMs -= core.gameTime().elapsedTimeMilli();

		final var inputManager = core.input();

		// 1) Check if the keyboard has activated this input action
		final var lIsKeyDown = inputManager.keyboard().isKeyDown(getBoundKeyCode());
		mIsDown = lIsKeyDown;

		// 2) Check if a gamepad has activated this input action
		final var gamepadManager = inputManager.gamepads();
		if (gamepadManager.isSomeComponentCapturingInput())
			return; //

		final var activeGamepads = gamepadManager.getActiveGamepads();
		if (activeGamepads.size() == 0)
			return;

		// TODO: move the mapping to its own class
		// TODO: Use the GamepadInputMap class to reduce the size of this switch.
		switch (getBoundGamepadCode()) {
		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_A: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A);
			mIsDown |= result;
			break;

		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_B: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_B);
			mIsDown |= result;
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_X: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_X);
			mIsDown |= result;
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_Y: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_Y);
			mIsDown |= result;
			break;
		}

		// --

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_DPAD_UP: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
			mIsDown |= result;
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_DPAD_DOWN: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
			mIsDown |= result;
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_DPAD_LEFT: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
			mIsDown |= result;
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_BUTTON_DPAD_RIGHT: {
			final var result = gamepadManager.isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
			mIsDown |= result;
			break;
		}

		// TODO: For the following axis binds, take prev value if higher and multibound
		// also need to adjust for inverted axes.

		case GamepadInputMap.LINTFORD_GAMEPAD_AXIS_LEFT_X_LEFT: {
			final var result = gamepadManager.getGamepadAxisValue(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
			mValue = result <= -0.01f ? result : 0; // only sets mValue to a non-zerop value if the axis matches
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_AXIS_LEFT_X_RIGHT: {
			final var result = gamepadManager.getGamepadAxisValue(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
			mValue = result >= 0.01f ? result : 0; // only sets mValue to a non-zerop value if the axis matches
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_AXIS_LEFT_Y_UP: {
			final var result = gamepadManager.getGamepadAxisValue(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
			mValue = result <= -0.01f ? result : 0; // only sets mValue to a non-zerop value if the axis matches
			break;
		}

		case GamepadInputMap.LINTFORD_GAMEPAD_AXIS_LEFT_Y_DOWN: {
			final var result = gamepadManager.getGamepadAxisValue(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);
			mValue = result >= 0.01f ? result : 0; // only sets mValue to a non-zerop value if the axis matches
			break;
		}

		}

		// any non-zero value (in the correct axis direction) will count as a down | downTimed
		mIsDown |= Math.abs(mValue) >= 0.01f;

		// When we've figured out if this event is active, then do the shit with the timed...
		if (mIsDown)

		{
			if (mDownTimerMs <= 0) {
				mIsDownTimed = true;
				mDownTimerMs = DOWN_TIMER_DELAY_MS;
			}
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addKeyboardBinding(int newKeyboardBinding) {
		mBoundKeyCode = newKeyboardBinding;
	}

	public void addGamepadBinding(int newGamepadBinding) {
		mBoundGamepadCode = newGamepadBinding;
	}

}
