package net.lintfordlib.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.core.LintfordCore;

public class InputGamepad {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mJoystickIndex;

	private ByteBuffer mJoystickButtons;
	private ByteBuffer mJoystickHats;
	private FloatBuffer mJoystickAxes;

	private int mNumButtons;
	private int mNumHats;
	private int mNumAxis;

	private boolean mHasHats;

	private String mJoystickName;
	private boolean mIsActive;
	private boolean mIsGamepadMappingAvailable;

	GLFWGamepadState mGamepadMappingState;
	protected final ByteBuffer mDataByteBuffer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int index() {
		return mJoystickIndex;
	}

	public int numButtons() {
		return mNumButtons;
	}

	public int numAxis() {
		return mNumAxis;
	}

	public int numHats() {
		return mNumHats;
	}

	public boolean isGamepadMappingAvailable() {
		return mIsGamepadMappingAvailable;
	}

	public void isGamepadMappingAvailable(boolean isGamepadMappingAvailable) {
		mIsGamepadMappingAvailable = isGamepadMappingAvailable;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean isActive) {
		mIsActive = isActive;
	}

	public String name() {
		return mJoystickName;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public InputGamepad(int joystickIndex) {
		mJoystickIndex = joystickIndex;

		mDataByteBuffer = MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF);
		mGamepadMappingState = new GLFWGamepadState(mDataByteBuffer);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		isActive(true);

		final var lControllerName = GLFW.glfwGetJoystickName(mJoystickIndex);
		final var lMappingAvailable = GLFW.glfwJoystickIsGamepad(mJoystickIndex);

		mJoystickName = lControllerName;
		mIsGamepadMappingAvailable = lMappingAvailable;

		mNumButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex).limit();
		mNumAxis = GLFW.glfwGetJoystickAxes(mJoystickIndex).limit();

		final var lHatReturn = GLFW.glfwGetJoystickHats(mJoystickIndex);
		if (lHatReturn != null) {
			mNumHats = lHatReturn.limit();
		} else {
			mHasHats = false;
		}
	}

	public void reset() {
		mIsActive = false;
		mIsGamepadMappingAvailable = false;

		mJoystickName = null;
		mNumButtons = 0;
		mNumAxis = 0;
		mNumHats = 0;
	}

	public void update(LintfordCore core) {
		if (GLFW.glfwJoystickPresent(mJoystickIndex) == false) {
			reset();
			return;
		}

		if (mIsGamepadMappingAvailable) {
			GLFW.glfwGetGamepadState(mJoystickIndex, mGamepadMappingState);
		}

		mJoystickButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
		mJoystickAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);
		mJoystickHats = GLFW.glfwGetJoystickHats(mJoystickIndex);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float getLeftTriggerAxis() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_LEFT_X)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER);
	}

	public float getLeftAxisX() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_LEFT_X)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X);
	}

	public float getLeftAxisY() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y);

	}

	public float getRightTriggerAxis() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
	}

	public float getRightAxisX() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X);
	}

	public float getRightAxisY() {
		if (mNumAxis <= GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y)
			return 0.f;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.axes(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y);

		return mJoystickAxes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y);
	}

	public boolean getIsButtonDown(int buttonIndex) {
		if (mDataByteBuffer == null || mDataByteBuffer.capacity() == 0)
			return false;

		if (buttonIndex < 0 || buttonIndex > mNumButtons)
			return false;

		if (mIsGamepadMappingAvailable)
			return mGamepadMappingState.buttons(buttonIndex) == GLFW.GLFW_PRESS;

		return mJoystickButtons.get(buttonIndex) == GLFW.GLFW_PRESS;
	}

	public boolean getIsHatDown(int hatBitIndex) {
		if (mJoystickHats == null || mJoystickHats.capacity() == 0)
			return false;

		if (hatBitIndex < 0 || hatBitIndex >= mNumHats)
			return false;

		final var lHatState = mJoystickHats.get(0);
		return (lHatState & (hatBitIndex)) == hatBitIndex;
	}

}
