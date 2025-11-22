package net.lintfordlib.core.input;

import java.io.Serializable;

public class InputAction implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3787420369463342303L;

	public static final int UNASSIGNED_KEY_CODE = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final String actionName; // The name of the action to display ('fire', 'jump' etc.)
	public final int actionUid;

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

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int defaultBoundKeyCode() {
		return mDefaultKeyCode;
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
	 * Returns the bound GamepadInputCode.
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

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public InputAction(String name, int eventActionUid) {
		this(name, eventActionUid, UNASSIGNED_KEY_CODE, UNASSIGNED_KEY_CODE);
	}

	public InputAction(String name, int eventActionUid, int defaultBoundKeyboardCode) {
		this(name, eventActionUid, defaultBoundKeyboardCode, UNASSIGNED_KEY_CODE);
	}

	public InputAction(String actionName, int actionUid, int defaultBoundKeyboardCode, int defaultBoundGamepadCode) {
		this.actionName = actionName;
		this.actionUid = actionUid;

		mDefaultKeyCode = defaultBoundKeyboardCode;
		mDefaultGamepadCode = defaultBoundGamepadCode;

		mBoundKeyCode = UNASSIGNED_KEY_CODE;
		mBoundLintfordInputCode = UNASSIGNED_KEY_CODE;
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
