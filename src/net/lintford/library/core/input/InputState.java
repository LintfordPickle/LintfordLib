package net.lintford.library.core.input;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.Vector2f;

public class InputState {

	public class KeyCallback extends GLFWKeyCallback {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		private InputState mInputState;

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		private KeyCallback(InputState pInputState) {
			mInputState = pInputState;
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, int pKey, int pScanCode, int pAction, int pMods) {
			mLastInputActive = INPUT_TYPES.Keyboard;

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
							if (pKey < InputState.KEY_LIMIT) {
								if (pKey != -1)
									mInputState.mKeyButtonStates[pKey] = !(pAction == GLFW.GLFW_RELEASE);
							}
						}
					}
				}

			} else {
				// normal Keyboad events
				if (pKey < InputState.KEY_LIMIT) {
					if (pKey != -1)
						mInputState.mKeyButtonStates[pKey] = !(pAction == GLFW.GLFW_RELEASE);
				}
			}

			// however, if this was a key release, then at least set the array to 0
			if (pAction == GLFW.GLFW_RELEASE) {
				if (pKey != -1)
					mInputState.mKeyButtonStates[pKey] = false;
			}

		}

	}

	public class MouseButtonCallback extends GLFWMouseButtonCallback {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		private InputState mInputState;

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		private MouseButtonCallback(InputState pInputState) {
			mInputState = pInputState;
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, int pButton, int pAction, int pMods) {
			if (pButton < 0 || pButton >= mInputState.mMouseButtonStates.length)
				return; // OOB
			mInputState.mMouseButtonStates[pButton] = !(pAction == GLFW.GLFW_RELEASE);
		}

	}

	public class MousePositionCallback extends GLFWCursorPosCallback {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		private InputState mInputState;

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		private MousePositionCallback(InputState pInputState) {
			mInputState = pInputState;
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, double pXPos, double pYPos) {
			mInputState.setMousePosition(pXPos, pYPos);

		}

	}

	public class TextCallback extends GLFWCharModsCallback {

		InputState mInputState;

		public TextCallback(InputState pInputState) {
			mInputState = pInputState;
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

	public class MouseScrollCallback extends GLFWScrollCallback {

		InputState mInputState;

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		private MouseScrollCallback(InputState pInputState) {
			mInputState = pInputState;
		}

		@Override
		public void invoke(long pWindow, double pXOffset, double pYOffset) {

			mMouseWheelXOffset = (float) pXOffset;
			mMouseWheelYOffset = (float) pYOffset;

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum INPUT_TYPES {
		Mouse, Keyboard,
	}

	private static final float TIMED_CLICK_DELAY = 250f; // ms
	private static final float TIMED_KEY_DELAY = 200f; // ms

	final static int KEY_LIMIT = 512;
	final static int MOUSE_BUTTONS_LIMIT = 3;

	// --------------------------------------
	// Variables
	// --------------------------------------

	boolean[] mKeyButtonStates;
	boolean[] mMouseButtonStates;

	private float mMouseWheelXOffset;
	private float mMouseWheelYOffset;

	private boolean mLeftClickHandled;
	private boolean mRightClickHandled;
	private int mLeftClickOwner;
	private int mRightClickOwner;

	/** This is filled by a LWJGL callback with the mouse position */
	private Vector2f mMouseWindowCoords;

	public KeyCallback mKeyCallback;
	public TextCallback mTextCallback;
	public MouseButtonCallback mMouseButtonCallback;
	public MousePositionCallback mMousePositionCallback;
	public MouseScrollCallback mMouseScrollCallback;
	private INPUT_TYPES mLastInputActive = INPUT_TYPES.Keyboard; // we use this because sometimes the user is locked to a text input

	private float mMenuClickTimer;
	private float mKeyTimer;

	private boolean mCaptureKeyboardInput;
	private IBufferedInputCallback mIBufferedInputCallback;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void leftClickOwner(int pOwnerHash) {
		mLeftClickOwner = pOwnerHash;
	}

	public int leftClickOwner() {
		return mLeftClickOwner;
	}

	public void rightClickOwner(int pOwnerHash) {
		mRightClickOwner = pOwnerHash;
	}

	public int rightClickOwner() {
		return mRightClickOwner;
	}

	public INPUT_TYPES lastInputActive() {
		return mLastInputActive;
	}

	public float mouseWheelXOffset() {
		return mMouseWheelXOffset;
	}

	public float mouseWheelYOffset() {
		return mMouseWheelYOffset;
	}

	public boolean isMouseTimedLeftClickAvailable() {
		if (!mLeftClickHandled && mouseLeftClick() && mMenuClickTimer > TIMED_CLICK_DELAY) {
			return true;

		}

		return false;

	}

	public boolean isMouseTimedRightClickAvailable() {
		if (!mRightClickHandled && mouseRightClick() && mMenuClickTimer > TIMED_CLICK_DELAY) {
			return true;
		}
		return false;
	}

	public void setLeftMouseClickHandled() {
		mLeftClickHandled = true;
		mMenuClickTimer = 0;
	}

	public void setRightMouseClickHandled() {
		mRightClickHandled = true;
		mMenuClickTimer = 0;
	}

	public boolean mouseLeftClick() {
		return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT];
	}

	public boolean mouseTimedRightClick() {
		if (!mRightClickHandled && mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT] && mMenuClickTimer > TIMED_CLICK_DELAY) {
			mRightClickHandled = true;
			return true;
		}
		return false;
	}

	public boolean mouseRightClick() {
		return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT];
	}

	public Vector2f mouseWindowCoords() {
		return mMouseWindowCoords;
	}

	void setMousePosition(double pXPos, double pYPos) {
		mMouseWindowCoords.x = (float) pXPos;
		mMouseWindowCoords.y = (float) pYPos;

		mLastInputActive = INPUT_TYPES.Mouse;

	}

	public boolean keyDown(int pKeyCode) {
		if (pKeyCode >= KEY_LIMIT) {
			System.err.println("Key " + pKeyCode + " out of range! ");
			return false;
		}

		return mKeyButtonStates[pKeyCode];
	}

	public boolean keyDownTimed(int pKeyCode) {
		if (mKeyTimer < TIMED_KEY_DELAY)
			return false;

		if (pKeyCode >= KEY_LIMIT) {
			System.err.println("Key " + pKeyCode + " out of range! ");
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
	// Constructor(s)
	// --------------------------------------

	public InputState() {
		mKeyButtonStates = new boolean[KEY_LIMIT];
		mMouseButtonStates = new boolean[MOUSE_BUTTONS_LIMIT];

		mKeyCallback = new KeyCallback(this);
		mTextCallback = new TextCallback(this);
		mMouseButtonCallback = new MouseButtonCallback(this);
		mMousePositionCallback = new MousePositionCallback(this);
		mMouseScrollCallback = new MouseScrollCallback(this);

		mMouseWindowCoords = new Vector2f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		final double lDeltaTime = pCore.time().elapseGameTimeMilli();

		mKeyTimer += lDeltaTime;
		mMenuClickTimer += lDeltaTime;

		if (isMouseTimedLeftClickAvailable() && mCaptureKeyboardInput) {
			stopCapture();

		}

		// Releasing the left click will automatically reset the owner
		if (!mouseLeftClick()) {
			mLeftClickHandled = false;
			mLeftClickOwner = -1;
		}

		// Releasing the right click will automatically reset the owner
		if (!mouseRightClick()) {
			mRightClickHandled = false;
			mRightClickOwner = -1;
		}

	}

	public void endUpdate() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

	}

	public void resetKeyFlags() {
		Arrays.fill(mKeyButtonStates, false);
	}

	/** Resets state variables of the {@link InputState} such as the mouse scroll wheel (which should be consumed and reset). */
	public void resetFlags() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

		// This is needed in the case of toggling the GLFW window
		Arrays.fill(mKeyButtonStates, false);
		Arrays.fill(mMouseButtonStates, false);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean tryAquireLeftClickOwnership(int pHash) {
		if (!mouseLeftClick())
			return false;
		if (mLeftClickOwner == -1 || mLeftClickOwner == pHash) {
			mLeftClickOwner = pHash;
			return true;

		}
		return false;
	}

	public void tryReleaseLeftLock(int pHash) {
		if (!mouseLeftClick())
			return;

		if (mLeftClickOwner == pHash) {
			mLeftClickOwner = -1;

		}

		return;
	}

	public boolean tryAquireRightClickOwnership(int pHash) {
		if (!mouseRightClick())
			return false;
		if (mRightClickOwner == -1 || mRightClickOwner == pHash) {
			mRightClickOwner = pHash;
			return true;

		}
		return false;
	}

}