package net.lintfordlib.core.input.mouse;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.maths.Vector2f;

public class MouseManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class MouseButtonCallback extends GLFWMouseButtonCallback {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		private MouseButtonCallback() {

		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long windowUid, int button, int action, int mods) {
			if (button < 0 || button >= mMouseButtonStates.length)
				return; // OOB

			if (action == GLFW.GLFW_PRESS) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					mLogicalLeftClickTimer++;
				} else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					mLogicalRightClickTimer++;
				}
			}

			mMouseButtonStates[button] = !(action == GLFW.GLFW_RELEASE);
		}
	}

	public class MousePositionCallback extends GLFWCursorPosCallback {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		private MousePositionCallback() {

		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long windowUid, double positionX, double positionY) {
			setMousePosition(positionX, positionY);
		}
	}

	public class MouseScrollCallback extends GLFWScrollCallback {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		private MouseScrollCallback() {

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void invoke(long windowUid, double offsetX, double offsetY) {
			mMouseWheelXOffset = (float) offsetX;
			mMouseWheelYOffset = (float) offsetY;
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final static int MOUSE_BUTTONS_LIMIT = 3;
	private static final int MOUSE_NO_OWNER = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	boolean[] mMouseButtonStates;

	private float mMouseWheelXOffset;
	private float mMouseWheelYOffset;

	private boolean mLeftMouseClickHandled;
	private boolean mRightMouseClickHandled;

	private int mMouseLeftClickOwnerHashCode;
	private int mMouseMiddleOwnerHashCode;
	private int mMouseRightClickOwnerHashCode;

	private int mMouseHoverOwnerHashCode; // stores the top-most window registered as being hovered-over

	/** This is filled by a LWJGL callback with the mouse position */
	private Vector2f mMouseWindowCoords;

	private int mLogicalLeftClickTimer;
	private int mLogicalRightClickTimer;

	public MouseButtonCallback mMouseButtonCallback;
	public MousePositionCallback mMousePositionCallback;
	public MouseScrollCallback mMouseScrollCallback;

	private boolean mMouseMenuSelectionEnabled;
	private float mLastMouseX;
	private float mLastMouseY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isMouseMenuSelectionEnabled() {
		return mMouseMenuSelectionEnabled;
	}

	public void isMouseMenuSelectionEnabled(boolean newValue) {
		mMouseMenuSelectionEnabled = newValue;
	}

	public int mouseLeftButtonLogicalTimer() {
		return mLogicalLeftClickTimer;
	}

	public int mouseRightButtonLogicalTimer() {
		return mLogicalRightClickTimer;
	}

	public boolean isMouseLeftButtonDown() {
		return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT];
	}

	public boolean isMouseRightButtonDown() {
		return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT];
	}

	public boolean isMouseLeftButtonDownTimed(IInputProcessor mouseProcessor) {
		if (isMouseLeftButtonDown() && mouseProcessor.isCoolDownElapsed()) {
			mouseProcessor.resetCoolDownTimer();
			return true;
		}

		return false;
	}

	public boolean isMouseRightButtonDownTimed(IInputProcessor mouseProcessor) {
		if (isMouseRightButtonDown() && mouseProcessor.isCoolDownElapsed()) {
			mouseProcessor.resetCoolDownTimer();
			return true;
		}

		return false;
	}

	public boolean isMouseLeftClick(int hashCode) {
		if (isMouseLeftClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT];
		}

		return false;
	}

	public boolean isMouseRightClick(int hashCode) {
		if (isMouseRightClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT];
		}

		return false;
	}

	public boolean tryAcquireMouseLeftClick(int hashCode) {
		if (isMouseLeftClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			if (mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT]) {
				mLeftMouseClickHandled = true;
				assignLeftClickOwnerToHashCode(hashCode);
				return true;

			}
		}

		return false;

	}

	public boolean tryAcquireMouseRightClick(int hashCode) {
		if (isMouseRightClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			if (mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT]) {
				mRightMouseClickHandled = true;
				assignRightClickOwnerToHashCode(hashCode);
				return true;

			}
		}

		return false;

	}

	public boolean tryAcquireMouseLeftClickTimed(int hashCode, IInputProcessor mouseProcessor) {
		if (isMouseLeftClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			if (!mLeftMouseClickHandled && isMouseLeftButtonDown() && mouseProcessor.isCoolDownElapsed()) {
				mLeftMouseClickHandled = true;
				assignLeftClickOwnerToHashCode(hashCode);
				mouseProcessor.resetCoolDownTimer();

				return true;
			}
		}

		return false;
	}

	public boolean tryAcquireMouseRightClickTimed(int hashCode, IInputProcessor mouseProcessor) {
		if (isMouseRightClickOwnerAssignedToUsOrNotAssigned(hashCode)) {
			if (!mRightMouseClickHandled && isMouseRightButtonDown() && mouseProcessor.isCoolDownElapsed()) {

				mRightMouseClickHandled = true;
				assignRightClickOwnerToHashCode(hashCode);
				mouseProcessor.resetCoolDownTimer();

				return true;
			}
		}

		return false;
	}

	public boolean tryAcquireMouseMiddle(int hashCode) {
		final var lOwnerNotAssigned = mMouseMiddleOwnerHashCode == MOUSE_NO_OWNER;
		final var lOwnerIsUs = mMouseMiddleOwnerHashCode == hashCode;

		if (lOwnerNotAssigned || lOwnerIsUs) {
			mMouseMiddleOwnerHashCode = hashCode;
			return true;
		}

		return false;
	}

	// OWNER

	private void assignLeftClickOwnerToHashCode(int hashCode) {
		mMouseLeftClickOwnerHashCode = hashCode;
	}

	private void assignRightClickOwnerToHashCode(int hashCode) {
		mMouseLeftClickOwnerHashCode = hashCode;
		mouseHoverOverHash(hashCode);
	}

	public boolean isMouseLeftClickOwnerAssignedToUsOrNotAssigned(int hashCode) {
		return isLeftClickOwnerNotAssigned() || isMouseLeftClickOwnerAssigned(hashCode);
	}

	public boolean isMouseRightClickOwnerAssignedToUsOrNotAssigned(int hashCode) {
		return isRightClickOwnerNotAssigned() || isMouseRightClickOwnerAssigned(hashCode);
	}

	public boolean isMouseLeftClickOwnerAssigned(int hashCode) {
		return mMouseLeftClickOwnerHashCode == hashCode;
	}

	public boolean isMouseRightClickOwnerAssigned(int hashCode) {
		return mMouseRightClickOwnerHashCode == hashCode;
	}

	public boolean isLeftClickOwnerNotAssigned() {
		return mMouseLeftClickOwnerHashCode == MOUSE_NO_OWNER;
	}

	public boolean isRightClickOwnerNotAssigned() {
		return mMouseRightClickOwnerHashCode == MOUSE_NO_OWNER;
	}

	public boolean isMiddleOwnerNotAssigned() {
		return mMouseMiddleOwnerHashCode == MOUSE_NO_OWNER;
	}

	public void tryMouseLeftClickReleaseLockOwnership(int hashCode) {
		if (mMouseLeftClickOwnerHashCode == hashCode) {
			mMouseLeftClickOwnerHashCode = -1;
		}

	}

	public void tryMouseRightClickReleaseLockOwnership(int hashCode) {
		if (mMouseRightClickOwnerHashCode == hashCode) {
			mMouseRightClickOwnerHashCode = -1;
		}
	}

	/// HOVER

	public boolean tryAcquireMouseOverThisComponent(int hashCode) {
		if ((mMouseHoverOwnerHashCode == hashCode) || mMouseHoverOwnerHashCode == MOUSE_NO_OWNER) {
			mMouseHoverOwnerHashCode = hashCode;
			return true;
		}

		return false;
	}

	public boolean isMouseOverThisComponent(int hashCode) {
		return (mMouseHoverOwnerHashCode == hashCode) || !isMouseOverAComponent();
	}

	public boolean isMouseOverAComponent() {
		return mMouseHoverOwnerHashCode != MOUSE_NO_OWNER;
	}

	public int mouseHoverOverHash() {
		return mMouseHoverOwnerHashCode;
	}

	public void mouseHoverOverHash(int hashOfOwner) {
		mMouseHoverOwnerHashCode = hashOfOwner;
	}

	///

	public Vector2f mouseWindowCoords() {
		return mMouseWindowCoords;
	}

	public float mouseWheelXOffset() {
		return mMouseWheelXOffset;
	}

	public float mouseWheelYOffset() {
		return mMouseWheelYOffset;
	}

	void setMousePosition(double positionX, double positionY) {
		mMouseWindowCoords.x = (float) positionX;
		mMouseWindowCoords.y = (float) positionY;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MouseManager() {
		mMouseButtonStates = new boolean[MOUSE_BUTTONS_LIMIT];
		mMouseButtonCallback = new MouseButtonCallback();
		mMousePositionCallback = new MousePositionCallback();
		mMouseScrollCallback = new MouseScrollCallback();
		mMouseWindowCoords = new Vector2f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		if (!isMouseLeftButtonDown()) {
			mLeftMouseClickHandled = false;
		}

		if (!isMouseRightButtonDown()) {
			mRightMouseClickHandled = false;
		}

		if (!mLeftMouseClickHandled) {
			mMouseLeftClickOwnerHashCode = MOUSE_NO_OWNER;
		}

		if (!mRightMouseClickHandled) {
			mMouseRightClickOwnerHashCode = MOUSE_NO_OWNER;
		}

		mMouseMiddleOwnerHashCode = MOUSE_NO_OWNER;

		updateMouseMenuSelectionEnabled(core);
	}

	private void updateMouseMenuSelectionEnabled(LintfordCore core) {
		final var lMouseX = core.HUD().getMouseWorldSpaceX();
		final var lMouseY = core.HUD().getMouseWorldSpaceY();

		if (lMouseX != mLastMouseX || lMouseY != mLastMouseY)
			mMouseMenuSelectionEnabled = true;

		mLastMouseX = lMouseX;
		mLastMouseY = lMouseY;

	}

	public void endUpdate() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;
		mMouseHoverOwnerHashCode = MOUSE_NO_OWNER;
	}

	public void resetFlags() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

		Arrays.fill(mMouseButtonStates, false);
	}
}
