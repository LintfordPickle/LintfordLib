package net.ld.library.core.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

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

			// We need to handle keypressed differently depending on whether or not some UI component is
			// using 'buffered' input.
			if (mCaptureKeyboardInput) {
				// Buffered input (here we just listen for special keys (backspace, return etc.)
				if (pAction == GLFW.GLFW_PRESS) {
					if (mIBufferedInputCallback != null) {

						if (pKey == GLFW.GLFW_KEY_ENTER) {
							mIBufferedInputCallback.onEnterPressed();
							if (mIBufferedInputCallback.getEnterFinishesInput()) {
								stopCapture();
							}
						} else if (pKey == GLFW.GLFW_KEY_ESCAPE) {
							mIBufferedInputCallback.onEscapePressed();
							if (mIBufferedInputCallback.getEscapeFinishesInput()) {
								stopCapture();
							}
						}

						else if (pKey == GLFW.GLFW_KEY_BACKSPACE) {
							if (mIBufferedInputCallback.getStringBuilder().length() > 0) {
								mIBufferedInputCallback.getStringBuilder().delete(mIBufferedInputCallback.getStringBuilder().length() - 1, mIBufferedInputCallback.getStringBuilder().length());
							}
						}
					}
				}

			} else {
				// normal Keyboad events
				if (pKey < InputState.KEY_LIMIT) {
					if(pKey != -1)
						mInputState.mKeyButtonStates[pKey] = !(pAction == GLFW.GLFW_RELEASE);
				}
			}

			// however, if this was a key release, then at least set the array to 0
			if (pAction == GLFW.GLFW_RELEASE) {
				if(pKey != -1)
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
			if(pButton >= 0 && pButton < mInputState.mMouseButtonStates.length){
				mInputState.mMouseButtonStates[pButton] = !(pAction == GLFW.GLFW_RELEASE);
			}
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

	// =============================================
	// Constants
	// =============================================

	private static final float TIMED_CLICK_DELAY = 250f; // seconds
	private static final float TIMED_KEY_DELAY = 500f; // seconds

	final static int KEY_LIMIT = 512;
	final static int MOUSE_BUTTONS_LIMIT = 3;

	// =============================================
	// Variables
	// =============================================

	private DisplayConfig mDisplayConfig;
	private GameTime mGameTime;
	boolean[] mKeyButtonStates;
	boolean[] mMouseButtonStates;
	double mMouseXPosition;
	double mMouseYPosition;

	private float mMouseWheelXOffset;
	private float mMouseWheelYOffset;

	private boolean mLeftClickHandled;
	private boolean mRightClickHandled;

	private Vector2f mMouseScreenCoord;

	public KeyCallback mKeyCallback;
	public TextCallback mTextCallback;
	public MouseButtonCallback mMouseButtonCallback;
	public MousePositionCallback mMousePositionCallback;
	public MouseScrollCallback mMouseScrollCallback;

	private float mMenuClickTimer;
	private float mKeyTimer;

	private boolean mCaptureKeyboardInput;
	private IBufferedInputCallback mIBufferedInputCallback;

	// =============================================
	// Properties
	// =============================================

	public float mouseWheelXOffset() {
		return mMouseWheelXOffset;
	}

	public float mouseWheelYOffset() {
		return mMouseWheelYOffset;
	}

	public boolean mouseTimedLeftClick() {
		if (!mLeftClickHandled && mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT] && mMenuClickTimer > TIMED_CLICK_DELAY) {
			mLeftClickHandled = true;
			mMenuClickTimer = 0;
			return true;
		}
		return false;
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

	public double getMouseX() {
		return mMouseXPosition;
	}

	public double getMouseY() {
		return mMouseYPosition;
	}

	public Vector2f mouseScreenCoords() {
		return mMouseScreenCoord;
	}

	void setMousePosition(double pXPos, double pYPos) {
		mMouseXPosition = pXPos - 1;
		mMouseYPosition = mDisplayConfig.windowHeight() - pYPos - 1;
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

	public void simulateMenuKeyPress() {
		mKeyTimer = 0;
	}

	public void startCapture(IBufferedInputCallback pCallbackFunction) {
		mIBufferedInputCallback = pCallbackFunction;
		mCaptureKeyboardInput = true;
	}

	public void stopCapture() {
		mIBufferedInputCallback = null;
		mCaptureKeyboardInput = false;
	}

	public GameTime gameTime() {
		return mGameTime;
	}

	// =============================================
	// Constructor(s)
	// =============================================

	public InputState(DisplayConfig pDisplayConfig, GameTime pGameTime) {
		mDisplayConfig = pDisplayConfig;
		mGameTime = pGameTime;
		mKeyButtonStates = new boolean[KEY_LIMIT];
		mMouseButtonStates = new boolean[MOUSE_BUTTONS_LIMIT];

		mKeyCallback = new KeyCallback(this);
		mTextCallback = new TextCallback(this);
		mMouseButtonCallback = new MouseButtonCallback(this);
		mMousePositionCallback = new MousePositionCallback(this);
		mMouseScrollCallback = new MouseScrollCallback(this);

		mMouseScreenCoord = new Vector2f();
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void update(GameTime pGameTime) {
		final double lElapsed = pGameTime.elapseGameTime();

		mKeyTimer += lElapsed;
		mMenuClickTimer += lElapsed;

		mLeftClickHandled = false;
		mRightClickHandled = false;

		mMouseScreenCoord.x = (float) (mMouseXPosition - 1);
		mMouseScreenCoord.y = (float) (mDisplayConfig.windowHeight() - mMouseYPosition - 1);
	}

	public void resetFlags() {

		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

	}

	// =============================================
	// Methods
	// =============================================

}