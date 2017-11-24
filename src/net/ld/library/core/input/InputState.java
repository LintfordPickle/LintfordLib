package net.ld.library.core.input;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.maths.Vector2f;
import net.ld.library.core.time.GameTime;

/** The {@link InputState} sets up callback listeners */
public class InputState {

	/**
	 * {@link KeyCallback} handles the keyboard input events received from the
	 * LWJGL/GLFW window
	 */
	public class KeyCallback extends GLFWKeyCallback {

		// ---------------------------------------------
		// Variables
		// ---------------------------------------------

		/**
		 * Reference to the InputState to be called on {@link KeyCallback}
		 * events,
		 */
		private InputState mInputState;

		// ---------------------------------------------
		// Constructor
		// ---------------------------------------------

		/** ctor. */
		private KeyCallback(InputState pInputState) {
			mInputState = pInputState;
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		/**
		 * Overridden callback method, called when a key is pressed, repeated or
		 * released.
		 */
		@Override
		public void invoke(long pWindow, int pKey, int pScanCode, int pAction, int pMods) {
			mLastInputActive = INPUT_TYPES.Keyboard;

			// We need to handle keypressed differently depending on whether or
			// not some UI component is
			// using 'buffered' input.
			if (mCaptureKeyboardInput) {
				// Buffered input (here we just listen for special keys
				// (backspace, return etc.)
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
								mIBufferedInputCallback.getStringBuilder().delete(
										mIBufferedInputCallback.getStringBuilder().length() - 1,
										mIBufferedInputCallback.getStringBuilder().length());
								mIBufferedInputCallback.onKeyPressed((char) pKey);
							}
						}

						// Treat some keys as unbuffered
						else if (pKey == GLFW.GLFW_KEY_LEFT || pKey == GLFW.GLFW_KEY_UP || pKey == GLFW.GLFW_KEY_RIGHT
								|| pKey == GLFW.GLFW_KEY_DOWN) {
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

			// however, if this was a key release, then at least set the array
			// to 0
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

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public enum INPUT_TYPES {
		Mouse, Keyboard,
	}

	private static final float TIMED_CLICK_DELAY = 250f; // seconds
	private static final float TIMED_KEY_DELAY = 200f; // seconds

	final static int KEY_LIMIT = 512;
	final static int MOUSE_BUTTONS_LIMIT = 3;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private GameTime mGameTime;
	boolean[] mKeyButtonStates;
	boolean[] mMouseButtonStates;

	private float mMouseWheelXOffset;
	private float mMouseWheelYOffset;

	private boolean mLeftClickHandled;
	private boolean mRightClickHandled;
	private int mLeftClickOwner;
	private int mRightClickOwner;

	private Vector2f mMouseWindowCoords;

	public KeyCallback mKeyCallback;
	public TextCallback mTextCallback;
	public MouseButtonCallback mMouseButtonCallback;
	public MousePositionCallback mMousePositionCallback;
	public MouseScrollCallback mMouseScrollCallback;

	/**
	 * We store the last input type so we can hide the mouse pointer when the
	 * user decides to use the keyboard when navigating the menus.
	 */
	private INPUT_TYPES mLastInputActive = INPUT_TYPES.Keyboard;

	private float mMenuClickTimer;
	private float mKeyTimer;

	private Camera mCamera;
	private HUD mHUD;

	private boolean mCaptureKeyboardInput;
	private IBufferedInputCallback mIBufferedInputCallback;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

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

	public Camera camera() {
		return mCamera;
	}

	public HUD HUD() {
		return mHUD;
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

	public boolean mouseTimedLeftClick() {
		if (!mLeftClickHandled && mouseLeftClick() && mMenuClickTimer > TIMED_CLICK_DELAY) {
			mLeftClickHandled = true;
			mMenuClickTimer = 0;
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
		if (!mRightClickHandled && mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT]
				&& mMenuClickTimer > TIMED_CLICK_DELAY) {
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

	/** Callback which sets the position of the mouse in window space. */
	void setMousePosition(double pXPos, double pYPos) {
		mMouseWindowCoords.x = (float) pXPos;
		mMouseWindowCoords.y = (float) pYPos;

		mLastInputActive = INPUT_TYPES.Mouse;

	}

	/**
	 * Returns true if the given keyboard key is currently pressed. false
	 * otherwise. See GLFW.GLFW_KEYS
	 */
	public boolean keyDown(int pKeyCode) {
		if (pKeyCode >= KEY_LIMIT) {
			System.err.println("Key " + pKeyCode + " out of range! ");
			return false;
		}

		return mKeyButtonStates[pKeyCode];
	}

	/**
	 * Returns true if the given key is currently pressed, and this method has
	 * not previously been called this frame (first come, first server timed
	 * keyboard input). false is returned otherwise.
	 */
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

	/**
	 * captures keyboard input and directs it towards the
	 * {@link IBufferedInputCallback} given.
	 */
	public void startCapture(IBufferedInputCallback pCallbackFunction) {
		mIBufferedInputCallback = pCallbackFunction;
		mCaptureKeyboardInput = true;
	}

	/** stops any buffered keyboard input capturing. */
	public void stopCapture() {
		mIBufferedInputCallback = null;
		mCaptureKeyboardInput = false;
	}

	/**
	 * Returns the {@link GameTime} object associated with the InputState. This
	 * allows you to use time to manipulate input.
	 */
	public GameTime gameTime() {
		return mGameTime;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	/** ctor. */
	public InputState(DisplayConfig pDisplayConfig, Camera pCamera, HUD pHUD, GameTime pGameTime) {
		mKeyButtonStates = new boolean[KEY_LIMIT];
		mMouseButtonStates = new boolean[MOUSE_BUTTONS_LIMIT];

		mKeyCallback = new KeyCallback(this);
		mTextCallback = new TextCallback(this);
		mMouseButtonCallback = new MouseButtonCallback(this);
		mMousePositionCallback = new MousePositionCallback(this);
		mMouseScrollCallback = new MouseScrollCallback(this);

		mMouseWindowCoords = new Vector2f();

		mGameTime = pGameTime;
		mCamera = pCamera;
		mHUD = pHUD;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * update called once per frame. handles timer info for tracking long clicks
	 * etc.
	 */
	public void update(GameTime pGameTime) {
		final double lElapsed = pGameTime.elapseGameTimeMilli();

		mKeyTimer += lElapsed;
		mMenuClickTimer += lElapsed;

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

	/** Clears the key flag buffer to zero (as if no keys have been pressed). */
	public void resetKeyFlags() {
		Arrays.fill(mKeyButtonStates, false);

	}

	/** resets an state flags which should not persist across update frames. */
	public void resetFlags() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	/**
	 * Helper method, calling objects can check a soft lock on the current left
	 * mouse click ownership. If the calling object wins the lock, true is
	 * returned and the calling object's hash is stored. false is returned if
	 * another object owns the current left click.
	 */
	public boolean tryAquireLeftClickOwnership(int pHash) {
		if (!mouseLeftClick())
			return false;
		if (mLeftClickOwner == -1 || mLeftClickOwner == pHash) {
			mLeftClickOwner = pHash;
			return true;

		}
		return false;
	}

	/**
	 * releases a lock on the left mouse click if the calling object's hash
	 * matches the current left click owner's hash.
	 */
	public void tryReleaseLeftLock(int pHash) {
		if (!mouseLeftClick())
			return;

		if (mLeftClickOwner == pHash) {
			mLeftClickOwner = -1;

		}

		return;
	}

	/**
	 * Helper method, calling objects can check a soft lock on the current right
	 * mouse click ownership. If the calling object wins the lock, true is
	 * returned and the calling object's hash is stored. false is returned if
	 * another object owns the current right click.
	 */
	public boolean tryAquireRightClickOwnership(int pHash) {
		if (!mouseRightClick())
			return false;
		if (mRightClickOwner == -1 || mRightClickOwner == pHash) {
			mRightClickOwner = pHash;
			return true;

		}
		return false;
	}

	/**
	 * releases a lock on the right mouse click if the calling object's hash
	 * matches the current left click owner's hash.
	 */
	public void tryReleaseRightLock(int pHash) {
		if (!mouseRightClick())
			return;

		if (mRightClickOwner == pHash) {
			mRightClickOwner = -1;

		}

		return;
	}

}