package net.lintfordlib.core.input;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.gamepad.GamepadManager;
import net.lintfordlib.core.input.keyboard.KeyboardManager;
import net.lintfordlib.core.input.mouse.MouseManager;
import net.lintfordlib.core.storage.AppStorage;

public class InputManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum INPUT_TYPES {
		Mouse, Keyboard, Gamepad,
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

	public void initialize(LintfordCore core) {
		mGamepadManager.initialize();
	}

	public void update(LintfordCore core) {
		mMouseManager.update(core);
		mKeyboardManager.update(core);
		mGamepadManager.update(core);
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