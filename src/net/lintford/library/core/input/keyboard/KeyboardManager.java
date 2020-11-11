package net.lintford.library.core.input.keyboard;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.input.IBufferedTextInputCallback;
import net.lintford.library.core.input.IKeyInputCallback;

public class KeyboardManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class KeyCallback extends GLFWKeyCallback {
		@Override
		public void invoke(long pWindow, int pKey, int pScanCode, int pAction, int pMods) {
			System.out.println("pKey: " + pKey + "  pScaneCode: " + pScanCode + "  pAction " + pAction + "  pMods: " + pMods);
			
			if (mKeyInputCallback != null) {
				mKeyInputCallback.keyInput(pKey, pScanCode, pAction, pMods);
				stopKeyInputCapture();

			}

			// We need to handle keypresses differently depending on whether or not some UI component is
			// using 'buffered' input.
			if (mBufferedTextInputCallback != null) {
				// Buffered input (here we just listen for special keys (backspace, return etc.) used to stop the input capture,
				// but otherwise, we don't register or process the keys
				if (pAction == GLFW.GLFW_PRESS) {
					if (pKey == GLFW.GLFW_KEY_ENTER) {
						if (mBufferedTextInputCallback.onEnterPressed()) {
							stopBufferedTextCapture();
							return;
						}
						if (mBufferedTextInputCallback.getEnterFinishesInput()) {
							stopBufferedTextCapture();
						}
					} else if (pKey == GLFW.GLFW_KEY_ESCAPE) {
						if (mBufferedTextInputCallback.onEscapePressed()) {
							stopBufferedTextCapture();
							return;
						}
						if (mBufferedTextInputCallback.getEscapeFinishesInput()) {
							stopBufferedTextCapture();
						}
					}

					else if (pKey == GLFW.GLFW_KEY_BACKSPACE) {
						if (mBufferedTextInputCallback.getStringBuilder().length() > 0) {
							mBufferedTextInputCallback.getStringBuilder().delete(mBufferedTextInputCallback.getStringBuilder().length() - 1, mBufferedTextInputCallback.getStringBuilder().length());
							mBufferedTextInputCallback.onKeyPressed((char) pKey);
						}
					}

					// Treat some keys as unbuffered
					else if (pKey == GLFW.GLFW_KEY_LEFT || pKey == GLFW.GLFW_KEY_UP || pKey == GLFW.GLFW_KEY_RIGHT || pKey == GLFW.GLFW_KEY_DOWN) {
						if (pKey < KEY_LIMIT) {
							if (pKey != -1)
								mKeyButtonStates[pKey] = !(pAction == GLFW.GLFW_RELEASE);
						}
					}
				}

			} else {
				// normal Keyboad events
				if (pKey < KEY_LIMIT) {
					if (pKey != -1)
						mKeyButtonStates[pKey] = !(pAction == GLFW.GLFW_RELEASE);
				}
			}

			// however, if this was a key release, then at least set the array to 0
			if (pAction == GLFW.GLFW_RELEASE) {
				if (pKey != -1)
					mKeyButtonStates[pKey] = false;
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
					mBufferedTextInputCallback.getStringBuilder().append((char) codepoint);
					mBufferedTextInputCallback.onKeyPressed(codepoint);

				}

			}
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float TIMED_KEY_DELAY = 200f; // ms
	private final static int KEY_LIMIT = 512;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean[] mKeyButtonStates;
	public KeyCallback mKeyCallback;
	public TextCallback mTextCallback;
	private float mKeyTimer;

	private IBufferedTextInputCallback mBufferedTextInputCallback;
	private IKeyInputCallback mKeyInputCallback;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isKeyDown(int pKeyCode) {
		if (pKeyCode >= KEY_LIMIT) {
			System.err.println("Key " + pKeyCode + " out of range! ");
			return false;
		}

		return mKeyButtonStates[pKeyCode];
	}

	public boolean isKeyDownTimed(int pKeyCode) {
		if (mKeyTimer < TIMED_KEY_DELAY)
			return false;

		if (pKeyCode >= KEY_LIMIT) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), String.format("Key %d out of range!", pKeyCode));
			return false;

		}

		if (mKeyButtonStates[pKeyCode]) {
			mKeyTimer = 0;
			return true;
		}

		return false;
	}

	public void handleKeyTimed() {
		mKeyTimer = 0;
	}

	public void simulateMenuKeyPress() {
		mKeyTimer = 0;
	}

	public void startBufferedTextCapture(IBufferedTextInputCallback pCallbackFunction) {
		if (mBufferedTextInputCallback != null) {
			mBufferedTextInputCallback.captureStopped();

		}

		mBufferedTextInputCallback = pCallbackFunction;

	}

	public boolean isSomeComponentCapturingKeyboardText() {
		return mBufferedTextInputCallback != null;
	}

	public void stopBufferedTextCapture() {
		if (mBufferedTextInputCallback != null) {
			mBufferedTextInputCallback.captureStopped();
		}

		mBufferedTextInputCallback = null;

	}

	public void StartKeyInputCapture(IKeyInputCallback pKeyInputCallback) {
		if (pKeyInputCallback != null) {

		}

		mKeyInputCallback = pKeyInputCallback;

	}

	public boolean isSomeComponentCapturingInputKeys() {
		return mKeyInputCallback != null;
	}

	public void stopKeyInputCapture() {
		if (mKeyInputCallback != null) {

		}

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

	public void update(LintfordCore pCore) {
		final double lDeltaTime = pCore.appTime().elapsedTimeMilli();

		mKeyTimer += lDeltaTime;
	}

	public void endUpdate() {

	}

	public void resetKeyFlags() {
		Arrays.fill(mKeyButtonStates, false);
	}

	public void resetFlags() {
		// This is needed in the case of toggling the GLFW window
		Arrays.fill(mKeyButtonStates, false);

	}

}
