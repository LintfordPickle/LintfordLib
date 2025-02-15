package net.lintfordlib.core.input.keyboard;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.IKeyInputCallback;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class KeyboardManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class KeyCallback extends GLFWKeyCallback {
		@Override
		public void invoke(long windowUid, int key, int scanCode, int action, int mods) {
			if (mKeyInputCallback != null) {
				if (mKeyInputCallback.keyInput(key, scanCode, action, mods)) {
					stopKeyInputCapture();
				}
			} else // If we are listening for a single key press, then don't process the buffered text input below (I'm not sure about this)

			// We need to handle keypresses differently depending on whether or not some UI component is
			// using 'buffered' input.
			if (mBufferedTextInputCallback != null) {
				// Buffered input (here we just listen for special keys (backspace, return etc.) used to stop the input capture,
				// but otherwise, we don't register or process the keys
				if (action == GLFW.GLFW_PRESS) {
					if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
						if (mBufferedTextInputCallback.onEnterPressed()) {
							stopBufferedTextCapture();
							return;
						}
						if (mBufferedTextInputCallback.getEnterFinishesInput()) {
							stopBufferedTextCapture();
						}
					} else if (key == GLFW.GLFW_KEY_ESCAPE) {
						if (mBufferedTextInputCallback.onEscapePressed()) {
							stopBufferedTextCapture();
							return;
						}
						if (mBufferedTextInputCallback.getEscapeFinishesInput()) {
							stopBufferedTextCapture();
						}
					} else if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_RIGHT || key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_HOME || key == GLFW.GLFW_KEY_END) {
						mBufferedTextInputCallback.onKeyPressed((char) key);
						if (key < KEY_LIMIT) {
							if (key != GLFW.GLFW_KEY_UNKNOWN)
								mKeyButtonStates[key] = !(action == GLFW.GLFW_RELEASE);
						}
					}
				}

			} else {
				// normal Keyboad events
				if (key < KEY_LIMIT) {
					if (key != GLFW.GLFW_KEY_UNKNOWN)
						mKeyButtonStates[key] = !(action == GLFW.GLFW_RELEASE);
				}
			}

			// however, if this was a key release, then at least set the array to 0
			if (action == GLFW.GLFW_RELEASE) {
				if (key != GLFW.GLFW_KEY_UNKNOWN)
					mKeyButtonStates[key] = false;
			}
		}
	}

	public class TextCallback extends GLFWCharModsCallback {
		/** Text input in the form of unicode code points, as produced by the OS. This text obeys keyboard layouts and modifier keys. */
		@Override
		public void invoke(long window, int codepoint, int mods) {
			if (mBufferedTextInputCallback != null) {
				if (mBufferedTextInputCallback.captureSingleKey()) {
					mBufferedTextInputCallback.onKeyPressed(codepoint);
					stopBufferedTextCapture();
				} else {
					mBufferedTextInputCallback.onKeyPressed(codepoint);
				}
			}
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final static int KEY_LIMIT = 512;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean[] mKeyButtonStates;
	public KeyCallback mKeyCallback;
	public TextCallback mTextCallback;

	private IBufferedTextInputCallback mBufferedTextInputCallback;
	private IKeyInputCallback mKeyInputCallback;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isAnyKeyDown() {
		for (int i = 0; i < KEY_LIMIT; i++) {
			if (mKeyButtonStates[i])
				return true;
		}
		return false;
	}

	public boolean isKeyDown(int keyCode, IInputProcessor inputProcessor) {
		if (inputProcessor != null && inputProcessor.allowKeyboardInput() == false)
			return false;

		return isKeyDown(keyCode);
	}

	public boolean isKeyDown(int keyCode) {
		if (keyCode >= KEY_LIMIT) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Key " + keyCode + " out of range! ");
			return false;
		}

		return mKeyButtonStates[keyCode];
	}

	public boolean isKeyDownTimed(int keyCode, IInputProcessor inputProcessor) {
		if (inputProcessor != null && !inputProcessor.allowKeyboardInput())
			return false;

		if (inputProcessor != null && !inputProcessor.isCoolDownElapsed())
			return false;

		if (keyCode >= KEY_LIMIT) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Key %d out of range!", keyCode));
			return false;
		}

		if (mKeyButtonStates[keyCode]) {
			if (inputProcessor != null)
				inputProcessor.resetCoolDownTimer();

			return true;
		}

		return false;
	}

	public void startBufferedTextCapture(IBufferedTextInputCallback callbackFunction) {
		if (mBufferedTextInputCallback != null) {
			mBufferedTextInputCallback.onCaptureStopped();
		}

		mBufferedTextInputCallback = callbackFunction;
		if (mBufferedTextInputCallback != null) {
			mBufferedTextInputCallback.onCaptureStarted();
		}

	}

	public boolean isSomeComponentCapturingKeyboardText() {
		return mBufferedTextInputCallback != null;
	}

	public void stopBufferedTextCapture() {
		if (mBufferedTextInputCallback != null) {
			mBufferedTextInputCallback.onCaptureStopped();
		}

		mBufferedTextInputCallback = null;
	}

	public void StartKeyInputCapture(IKeyInputCallback keyInputCallback) {
		mKeyInputCallback = keyInputCallback;
	}

	public boolean isSomeComponentCapturingInputKeys() {
		return mKeyInputCallback != null;
	}

	public void stopKeyInputCapture() {
		mKeyInputCallback = null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public KeyboardManager() {
		mKeyButtonStates = new boolean[KEY_LIMIT];
		mKeyCallback = new KeyCallback();
		mTextCallback = new TextCallback();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
	}

	public void endUpdate() {
	}

	public void resetKeyFlags() {
		Arrays.fill(mKeyButtonStates, false);
	}

	public void resetFlags() {
		Arrays.fill(mKeyButtonStates, false);
	}
}
