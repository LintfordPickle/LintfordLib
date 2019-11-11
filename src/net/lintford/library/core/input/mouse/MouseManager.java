package net.lintford.library.core.input.mouse;

import java.util.Arrays;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.Vector2f;

public class MouseManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class MouseButtonCallback extends GLFWMouseButtonCallback {

		private MouseButtonCallback() {
		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, int pButton, int pAction, int pMods) {
			if (pButton < 0 || pButton >= mMouseButtonStates.length)
				return; // OOB

			if (pAction == GLFW.GLFW_PRESS) {
				if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					mLogicalLeftClickTimer++;

				} else if (pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					mLogicalRightClickTimer++;

				}

			}

			mMouseButtonStates[pButton] = !(pAction == GLFW.GLFW_RELEASE);
		}

	}

	public class MousePositionCallback extends GLFWCursorPosCallback {

		private MousePositionCallback() {

		}

		// ---------------------------------------------
		// Methods
		// ---------------------------------------------

		@Override
		public void invoke(long pWindow, double pXPos, double pYPos) {
			setMousePosition(pXPos, pYPos);

		}

	}

	public class MouseScrollCallback extends GLFWScrollCallback {

		private MouseScrollCallback() {
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

	private int mMouseOwnerHashCode;
	private int mMouseHoverOverHashCode; // stores the top-most window registered as being hovered-over

	/** This is filled by a LWJGL callback with the mouse position */
	// FIXME: This doesn't belong here - it is display-centeric (maybe the controller)
	private Vector2f mMouseWindowCoords;

	private int mLogicalLeftClickTimer;
	private int mLogicalRightClickTimer;

	public MouseButtonCallback mMouseButtonCallback;
	public MousePositionCallback mMousePositionCallback;
	public MouseScrollCallback mMouseScrollCallback;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public boolean isMouseLeftButtonDownTimed(IProcessMouseInput pObject) {
		if (isMouseLeftButtonDown() && pObject.isCoolDownElapsed()) {
			pObject.resetCoolDownTimer();
			return true;
		}

		return false;
	}

	public boolean isMouseRightButtonDownTimed(IProcessMouseInput pObject) {
		if (isMouseRightButtonDown() && pObject.isCoolDownElapsed()) {
			pObject.resetCoolDownTimer();
			return true;
		}

		return false;
	}

	public boolean isMouseLeftClick(int pHashCode) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT];

		}

		return false;

	}

	public boolean tryAcquireMouseOwnership(int pHashCode) {
		if (isOwnerNotAssigned()) {
			assignOwnerToHashCode(pHashCode);
			return true;
		}

		return false;
	}

	public boolean tryAcquireMouseLeftClick(int pHashCode) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			if (mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_LEFT]) {
				mLeftMouseClickHandled = true;
				assignOwnerToHashCode(pHashCode);
				return true;

			}

		}

		return false;

	}

	public boolean isMouseRightClick(int pHashCode) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			return mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT];

		}

		return false;

	}

	public boolean tryAcquireMouseRightClick(int pHashCode) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			if (mMouseButtonStates[GLFW.GLFW_MOUSE_BUTTON_RIGHT]) {
				mRightMouseClickHandled = true;
				assignOwnerToHashCode(pHashCode);
				return true;

			}

		}

		return false;

	}

	public boolean tryAcquireMouseLeftClickTimed(int pHashCode, IProcessMouseInput pObject) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			if (!mLeftMouseClickHandled && isMouseLeftButtonDown() && pObject.isCoolDownElapsed()) {

				mLeftMouseClickHandled = true;
				assignOwnerToHashCode(pHashCode);
				pObject.resetCoolDownTimer();

				return true;
			}

		}

		return false;
	}

	public boolean tryAcquireMouseRightClickTimed(int pHashCode, IProcessMouseInput pObject) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			if (!mRightMouseClickHandled && isMouseRightButtonDown() && pObject.isCoolDownElapsed()) {

				mRightMouseClickHandled = true;
				assignOwnerToHashCode(pHashCode);
				pObject.resetCoolDownTimer();

				return true;
			}

		}

		return false;
	}

	public boolean tryAcquireMouseMiddle(int pHashCode) {
		if (isOwnerAssignedToUsOrNotAssigned(pHashCode)) {
			assignOwnerToHashCode(pHashCode);

			return true;

		}

		return false;

	}

	private void assignOwnerToHashCode(int pHashCode) {
		mMouseOwnerHashCode = pHashCode;
		mouseHoverOverHash(pHashCode);
	}

	public boolean isOwnerAssignedToUsOrNotAssigned(int pHashCode) {
		return isOwnerNotAssigned() || isOwnerAssigned(pHashCode);
	}

	public boolean isOwnerAssigned(int pHashCode) {
		return mMouseOwnerHashCode == pHashCode;
	}

	public boolean isOwnerNotAssigned() {
		return mMouseOwnerHashCode == MOUSE_NO_OWNER;
	}

	public void tryReleaseMouseLock(int pHash) {
		if (mMouseOwnerHashCode == pHash) {
			mMouseOwnerHashCode = -1;

		}

		return;
	}

	/// HOVER

	public boolean tryAcquireMouseOverThisComponent(int pHashCode) {
		if ((mMouseHoverOverHashCode == pHashCode) || mMouseHoverOverHashCode == MOUSE_NO_OWNER) {
			mMouseHoverOverHashCode = pHashCode;
			return true;
		}

		return false;

	}

	public boolean isMouseOverThisComponent(int pHashCode) {
		return (mMouseHoverOverHashCode == pHashCode) || !isMouseOverAComponent();
	}

	public boolean isMouseOverAComponent() {
		return mMouseHoverOverHashCode != MOUSE_NO_OWNER;
	}

	public int mouseHoverOverHash() {
		return mMouseHoverOverHashCode;
	}

	public void mouseHoverOverHash(int pHashOfOwner) {
		mMouseHoverOverHashCode = pHashOfOwner;
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

	void setMousePosition(double pXPos, double pYPos) {
		mMouseWindowCoords.x = (float) pXPos;
		mMouseWindowCoords.y = (float) pYPos;
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

	public void update(LintfordCore pCore) {
		if (!isMouseLeftButtonDown()) {
			mLeftMouseClickHandled = false;
		}

		if (!isMouseRightButtonDown()) {
			mRightMouseClickHandled = false;
		}

		if (!mLeftMouseClickHandled && !mRightMouseClickHandled) {
			mMouseOwnerHashCode = -1;
		}

	}

	public void endUpdate() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;
		mMouseHoverOverHashCode = MOUSE_NO_OWNER;

	}

	public void resetFlags() {
		mMouseWheelXOffset = 0;
		mMouseWheelYOffset = 0;

		Arrays.fill(mMouseButtonStates, false);
	}

}
