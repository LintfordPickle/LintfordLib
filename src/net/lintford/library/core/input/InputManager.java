package net.lintford.library.core.input;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.gamepad.GamepadManager;
import net.lintford.library.core.input.keyboard.KeyboardManager;
import net.lintford.library.core.input.mouse.MouseManager;
import net.lintford.library.core.storage.AppStorage;

public class InputManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum INPUT_TYPES {
		Mouse, Keyboard,
	}

	public static final String InputConfigFilename = "keybindings.ini";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MouseManager mMouseManager;
	private KeyboardManager mKeyboardManager;
	private GamepadManager mGamepadManager;
	private KeyEventActionManager mEventActionManager;

	private INPUT_TYPES mLastInputActive = INPUT_TYPES.Keyboard;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public INPUT_TYPES lastInputActive() {
		return mLastInputActive;
	}

	public KeyEventActionManager eventActionManager() {
		return mEventActionManager;
	}

	public KeyboardManager keyboard() {
		return mKeyboardManager;
	}

	public MouseManager mouse() {
		return mMouseManager;
	}

	public GamepadManager gamepads() {
		return mGamepadManager;
	}

	// --------------------------------------
	// Constructor(s)
	// --------------------------------------

	public InputManager() {
		mMouseManager = new MouseManager();
		mKeyboardManager = new KeyboardManager();
		mGamepadManager = new GamepadManager();

		final String lInputConfigFilename = AppStorage.getGameDataDirectory() + InputConfigFilename;
		mEventActionManager = new KeyEventActionManager(this, lInputConfigFilename);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		mMouseManager.update(core);
		mKeyboardManager.update(core);
		mEventActionManager.update(core);
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