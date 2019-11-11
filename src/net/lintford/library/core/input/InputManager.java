package net.lintford.library.core.input;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.gamepad.GamepadManager;
import net.lintford.library.core.input.keyboard.KeyboardManager;
import net.lintford.library.core.input.mouse.MouseManager;

public class InputManager {

	public enum INPUT_TYPES {
		Mouse, Keyboard,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MouseManager mMouseManager;
	private KeyboardManager mKeyboardManager;
	private GamepadManager mGamepadManager;

	private INPUT_TYPES mLastInputActive = INPUT_TYPES.Keyboard; // we use this because sometimes the user is locked to a text input

	// --------------------------------------
	// Properties
	// --------------------------------------

	public INPUT_TYPES lastInputActive() {
		return mLastInputActive;
	}

	public KeyboardManager keyboard() {
		return mKeyboardManager;
	}

	public MouseManager mouse() {
		return mMouseManager;
	}

	public GamepadManager gamepad() {
		return mGamepadManager;
	}

	// --------------------------------------
	// Constructor(s)
	// --------------------------------------

	public InputManager() {
		mMouseManager = new MouseManager();
		mKeyboardManager = new KeyboardManager();
		mGamepadManager = new GamepadManager();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		mMouseManager.update(pCore);
		mKeyboardManager.update(pCore);

	}

	public void endUpdate() {
		mMouseManager.endUpdate();
		mKeyboardManager.endUpdate();

	}

	public void resetKeyFlags() {
		mKeyboardManager.resetKeyFlags();

	}

	/** Resets state variables of the {@link InputManager} such as the mouse scroll wheel (which should be consumed and reset). */
	public void resetFlags() {
		mMouseManager.resetFlags();
		mKeyboardManager.resetFlags();

	}

}