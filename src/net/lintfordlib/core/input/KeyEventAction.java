package net.lintfordlib.core.input;

import java.io.Serializable;

public class KeyEventAction implements Serializable {

	// TODO: Move these somewhere else
	public static final int INPUT_DEVICE_NOTHING = 0;
	public static final int INPUT_DEVICE_KEYBOARD = 1;
	public static final int INPUT_DEVICE_GAMEPAD = 2;
	public static final int INPUT_DEVICE_MOUSE = 3;

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3787420369463342303L;

	public static final int UNASSIGNED_KEY_CODE = -1;
	public static final int DOWN_TIMER_DELAY_MS = 200; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mDefaultBoundKeyCode;
	private final int mEventActionUid;

	private int mInputDeviceType; // keyboard, gamepad, mouse
	private int mBoundKeyCode;

	private float mDownTimer;
	private boolean mIsDown;
	private boolean mIsDownTimed;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int boundToInputDevice() {
		return mInputDeviceType;
	}

	public int defaultBoundKeyCode() {
		return mDefaultBoundKeyCode;
	}

	public int eventActionUid() {
		return mEventActionUid;
	}

	public void boundKeyCode(int inputDeviceType, int newKeyCode) {
		mInputDeviceType = inputDeviceType;
		mBoundKeyCode = newKeyCode;
	}

	public int getBoundKeyCode() {
		return mBoundKeyCode > UNASSIGNED_KEY_CODE ? mBoundKeyCode : mDefaultBoundKeyCode;
	}

	public void isDown(boolean isDown) {
		this.mIsDown = isDown;

		if (isDown && mDownTimer > DOWN_TIMER_DELAY_MS) {
			mIsDownTimed = true;
			mDownTimer = 0;
		} else {
			mIsDownTimed = false;
		}
	}

	public boolean isDown() {
		return mIsDown;
	}

	public boolean isDownTimed() {
		return mIsDownTimed;
	}

	public void incDownTimer(float amt) {
		this.mDownTimer += amt;
	}

	public float resetDownTimer() {
		return mDownTimer;
	}

	public float downTimer() {
		return mDownTimer;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public KeyEventAction(int eventActionUid, int inputDeviceType, int defaultBoundKeyCode) {
		mEventActionUid = eventActionUid;
		mDefaultBoundKeyCode = defaultBoundKeyCode;
		mBoundKeyCode = UNASSIGNED_KEY_CODE;
		mInputDeviceType = inputDeviceType;
	}

	public KeyEventAction(int eventActionUid, int inputDeviceType, int defaultBoundKeyCode, int initialBoundKeyCode) {
		this(eventActionUid, inputDeviceType, defaultBoundKeyCode);

		mBoundKeyCode = initialBoundKeyCode;
	}
}
