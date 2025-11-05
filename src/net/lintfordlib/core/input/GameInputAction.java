package net.lintfordlib.core.input;

import java.io.Serializable;

import net.lintfordlib.core.LintfordCore;

// This actually contains the new bindings!!
public class GameInputAction implements Serializable {

	// TODO: This class needs to be further split down into keyboard/gamepad/mouse inputs.

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
	// TODO: Maybe extend this for analog / float values from axes
	private int mDefaultGamepadCode; // to reset back to
	private int mBoundLintfordInputCode;

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
		mBoundLintfordInputCode = newGamepadCode;
	}

	/**
	 * Returns the bound LintfordInputCode.
	 */
	public int getBoundGamepadCode() {
		return mBoundLintfordInputCode > UNASSIGNED_KEY_CODE ? mBoundLintfordInputCode : mDefaultGamepadCode;
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
		mBoundLintfordInputCode = UNASSIGNED_KEY_CODE;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void reset() {
		mIsDown = false;
		mIsDownTimed = false;
		mValue = 0;
	}

	// TODO: Update the logic to determine, if a InputAction is current on/off (based on the various input devices).

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
			return;

		final var activeGamepads = gamepadManager.getActiveGamepads();
		if (activeGamepads.size() > 0) {

			// This should not know about the type of the input mapped (whether button or axis) - we
			// just want to know, for each action code defined, whether the bound inputcode is 'down' or 'active'.

			// TODO: Seems to be working, but need to re-check this logic.

			final var boundGamepadInputCode = getBoundGamepadCode();
			mIsDown |= gamepadManager.isGamepadButtonDown(boundGamepadInputCode);

			// any non-zero value (in the correct axis direction) will count as a down | downTimed
			mIsDown |= Math.abs(gamepadManager.getGamepadAxis(boundGamepadInputCode)) > 0.05f;

			final var result = gamepadManager.getGamepadAxis(boundGamepadInputCode);
			mIsDown |= Math.abs(result) > 0.02f;

		}

		// When we've figured out if this event is active, then do the shit with the timed...
		if (mIsDown) {
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
		mBoundLintfordInputCode = newGamepadBinding;
	}

}
