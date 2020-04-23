package net.lintford.library.core.input.keyboard;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.input.IBufferedInputCallback;

public class KeyboardManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class KeyCallback extends GLFWKeyCallback {

		private KeyCallback() {
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, int pKey, int pScanCode, int pAction, int pMods) {
			// We need to handle keypressed differently depending on whether or not some UI component is
			// using 'buffered' input.
			if (mCaptureKeyboardInput) {
				// Buffered input (here we just listen for special keys (backspace, return etc.)
				if (pAction == GLFW.GLFW_PRESS) {
					if (mIBufferedInputCallback != null) {
						if (pKey == GLFW.GLFW_KEY_ENTER) {
							if (mIBufferedInputCallback.onEnterPressed()) {
								stopCapture();
								return;
							}
							if (mIBufferedInputCallback.getEnterFinishesInput()) {
								stopCapture();
							}
						} else if (pKey == GLFW.GLFW_KEY_ESCAPE) {
							if (mIBufferedInputCallback.onEscapePressed()) {
								stopCapture();
								return;
							}
							if (mIBufferedInputCallback.getEscapeFinishesInput()) {
								stopCapture();
							}
						}

						else if (pKey == GLFW.GLFW_KEY_BACKSPACE) {
							if (mIBufferedInputCallback.getStringBuilder().length() > 0) {
								mIBufferedInputCallback.getStringBuilder().delete(mIBufferedInputCallback.getStringBuilder().length() - 1, mIBufferedInputCallback.getStringBuilder().length());
								mIBufferedInputCallback.onKeyPressed((char) pKey);
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

		public TextCallback() {
		}

		@Override
		public void invoke(long window, int codepoint, int mods) {
			if (mCaptureKeyboardInput) {
				// Buffered input
				if (mIBufferedInputCallback != null) {
					mIBufferedInputCallback.getStringBuilder().append((char) codepoint);
					mIBufferedInputCallback.onKeyPressed((char) codepoint);
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
	private boolean mCaptureKeyboardInput;
	private IBufferedInputCallback mIBufferedInputCallback;

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

	public void startCapture(IBufferedInputCallback pCallbackFunction) {
		if (mIBufferedInputCallback != null) {
			mIBufferedInputCallback.captureStopped();

		}

		mIBufferedInputCallback = pCallbackFunction;
		mCaptureKeyboardInput = true;
	}

	public void stopCapture() {
		if (mIBufferedInputCallback != null) {
			mIBufferedInputCallback.captureStopped();
		}
		mIBufferedInputCallback = null;
		mCaptureKeyboardInput = false;
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
		final double lDeltaTime = pCore.time().elapseAppTimeMilli();

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
