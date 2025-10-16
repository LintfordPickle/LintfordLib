package net.lintfordlib.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;

public class InputGamepad {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mJoystickIndex;
	private String mJoystickName;
	public final String GUID;
	private boolean mIsActive;
	private boolean mIsInitialized;

	private ByteBuffer mJoystickButtons;
	private ByteBuffer mJoystickHats;
	private FloatBuffer mJoystickAxes;

	private int mNumButtonsRaw;
	private int mNumHatsRaw;
	private int mNumAxisRaw;

	private boolean mIsGamepadMappingAvailable;
	private int mNumButtonsMapped;
	private int mNumAxesMapped;
	GLFWGamepadState mGamepadMappingState;
	protected final ByteBuffer mDataByteBuffer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int index() {
		return mJoystickIndex;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public int numMappedButtons() {
		if (!mIsGamepadMappingAvailable)
			return 0;

		return mNumButtonsMapped;
	}

	public int numAxisMapped() {
		if (!mIsGamepadMappingAvailable)
			return 0;

		return mNumAxesMapped;
	}

	public int numButtonsRaw() {
		return mNumButtonsRaw;
	}

	public int numButtonsMapped() {
		if (!mIsGamepadMappingAvailable)
			return 0;

		return mGamepadMappingState.buttons().limit();
	}

	public int numAxisRaw() {
		return mNumAxisRaw;
	}

	public int numHatsRaw() {
		return mNumHatsRaw;
	}

	public boolean isGamepadMappingAvailable() {
		return mIsGamepadMappingAvailable;
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

	public InputGamepad(String guid, int joystickIndex) {
		mJoystickIndex = joystickIndex;
		GUID = guid;

		// TODO: This needs to be cleaned up after finished
		mDataByteBuffer = MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF);
		mGamepadMappingState = new GLFWGamepadState(mDataByteBuffer);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		isActive(true);

		final var controllerName = GLFW.glfwGetJoystickName(mJoystickIndex);
		final var mappingAvailable = GLFW.glfwJoystickIsGamepad(mJoystickIndex);

		mJoystickName = controllerName;
		mIsGamepadMappingAvailable = mappingAvailable;
		if (mIsGamepadMappingAvailable) {
			GLFW.glfwGetGamepadState(mJoystickIndex, mGamepadMappingState);

			mNumButtonsMapped = mGamepadMappingState.buttons().limit();
			mNumAxesMapped = mGamepadMappingState.axes().limit();

		}

		mNumButtonsRaw = GLFW.glfwGetJoystickButtons(mJoystickIndex).limit();
		mNumAxisRaw = GLFW.glfwGetJoystickAxes(mJoystickIndex).limit();

		final var hatReturn = GLFW.glfwGetJoystickHats(mJoystickIndex);
		if (hatReturn != null) {
			mNumHatsRaw = hatReturn.limit();
		}

		mIsInitialized = true;
	}

	public void reset() {
		mIsActive = false;
		mIsGamepadMappingAvailable = false;
		mIsInitialized = false;

		mJoystickName = null;
		mNumButtonsRaw = 0;
		mNumAxisRaw = 0;
		mNumHatsRaw = 0;
	}

	public void update(LintfordCore core) {
		if (!GLFW.glfwJoystickPresent(mJoystickIndex)) {
			reset();
			return;
		}

		if (mIsGamepadMappingAvailable)
			GLFW.glfwGetGamepadState(mJoystickIndex, mGamepadMappingState);

		mJoystickButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
		mJoystickAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);
		mJoystickHats = GLFW.glfwGetJoystickHats(mJoystickIndex);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float getAxisValueMapped(int glfwAxisIndex) {
		assert mIsGamepadMappingAvailable : "Cannot query mapped controler state if mIsGamepadMappingAvailable is false.";

		if (!mIsGamepadMappingAvailable)
			return 0;

		if (glfwAxisIndex < 0 || glfwAxisIndex >= mGamepadMappingState.axes().limit())
			return 0; // OoB

		return mGamepadMappingState.axes(glfwAxisIndex);
	}

	/**
	 * Gets the raw state of the axis.
	 * 
	 * @param glfwButtonIndex The index of the axis to check. Must be 0 > glfwAxisIndex >= mNumButtons.
	 */
	public float getAxisValueRaw(int glfwAxisIndex) {
		if (mJoystickAxes == null)
			return 0.f;

		if (glfwAxisIndex >= mNumAxisRaw)
			return 0.f;

		return mJoystickAxes.get(glfwAxisIndex);
	}

	/**
	 * Gets the current state of the button.
	 * 
	 * @param glfwButtonIndex The index of the button to check. Must be 0 > glfwButtonIndex >= mNumButtons.
	 * @param glfwButtonState GLFW.GLFW_PRESS | GLFW.GLFW_RELEASE | GLFW.GLFW_REPEAT
	 */
	public boolean getButtonStateMapped(int glfwButtonIndex, int glfwButtonState) {
		if (!mIsGamepadMappingAvailable)
			return false;

		if (glfwButtonIndex < 0)
			return false;

		if (mJoystickButtons == null)
			return false;

		final var mappedButtons = mGamepadMappingState.buttons();
		final var numMappedButtons = mappedButtons.limit();
		if (glfwButtonIndex >= numMappedButtons)
			return false;

		return mappedButtons.get(glfwButtonIndex) == glfwButtonState;
	}

	public boolean getButtonStateRaw(int glfwButtonIndex, int glfwButtonState) {
		if (glfwButtonIndex < 0)
			return false;

		if (mJoystickButtons == null)
			return false;

		if (glfwButtonIndex >= mNumButtonsRaw)
			return false;

		return mJoystickButtons.get(glfwButtonIndex) == glfwButtonState;
	}

	public boolean getIsButtonDown(int glfwGamepadButtonIndex) {
		if (mIsGamepadMappingAvailable) {
			return getButtonStateMapped(glfwGamepadButtonIndex, GLFW.GLFW_PRESS);
		}

		return getButtonStateRaw(glfwGamepadButtonIndex, GLFW.GLFW_PRESS);
	}

	public float getAxisValue(int glfwGamepadButtonIndex) {
		if (mIsGamepadMappingAvailable) {
			return getAxisValueMapped(glfwGamepadButtonIndex);
		}

		return getAxisValueRaw(glfwGamepadButtonIndex);
	}

	public boolean getIsHatDown(int hatBitIndex) {
		if (mJoystickHats == null || mJoystickHats.capacity() == 0)
			return false;

		if (hatBitIndex < 0 || hatBitIndex >= mNumHatsRaw)
			return false;

		final var lHatState = mJoystickHats.get(0);
		return (lHatState & (hatBitIndex)) == hatBitIndex;
	}

	boolean mIsCheckedForInputCode;
	final List<Boolean> tempCheckButtonList = new ArrayList<>();
	final List<Float> tempCheckAxisList = new ArrayList<>();

	/**
	 * Checks for any gamepad input, and returns the LINTFORD_GAMEPAD code for the button/axis/hat.
	 * 
	 * @return The LintfordGamepadCode for the selected gamepad input.
	 */
	public int checkForInputCode() {

		// This needs to be captured after a small pause
		fillTempCheckLists();

		if (mIsGamepadMappingAvailable) {
			for (int i = 0; i < mNumButtonsMapped; i++) {
				final var currentState = getButtonStateMapped(i, GLFW.GLFW_PRESS);
				final var benchmarkState = tempCheckButtonList.get(i);
				if (currentState != benchmarkState) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Button Pressed detected: Button index : " + i);

					return GamepadInputMap.mapGLFWButtonToLintfordButton(i);
				}
			}

			for (int i = 0; i < mNumAxesMapped; i++) {
				final var currentState = getAxisValueMapped(i);
				final var benchmarkState = tempCheckAxisList.get(i);

				if (Math.abs(currentState - benchmarkState) > 0.05f) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Axis movement detected: Axis index : " + i + "  Value: " + (currentState - benchmarkState));

					return GamepadInputMap.mapGLFWAxisToLintfordAxis(i, currentState - benchmarkState);
				}
			}
		} else {
			for (int i = 0; i < mNumButtonsRaw; i++) {
				final var currentState = getButtonStateRaw(i, GLFW.GLFW_PRESS);
				final var benchmarkState = tempCheckButtonList.get(i);
				if (currentState != benchmarkState) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Raw Button Pressed detected: Button index : " + i);

					return GamepadInputMap.mapGLFWButtonToLintfordButton(i);
				}
			}

			for (int i = 0; i < mNumAxisRaw; i++) {
				final var currentState = getAxisValueRaw(i);
				final var benchmarkState = tempCheckAxisList.get(i);

				if (Math.abs(currentState - benchmarkState) > 0.05f) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Raw Axis movement detected: Axis index : " + i + "  Value: " + (currentState - benchmarkState));

					return GamepadInputMap.mapGLFWAxisToLintfordAxis(i, currentState - benchmarkState);
				}
			}
		}

		return -1;
	}

	// We need a benchmark to test against
	private void fillTempCheckLists() {
		if (mIsCheckedForInputCode)
			return;

		tempCheckButtonList.clear();
		tempCheckAxisList.clear();

		if (mIsGamepadMappingAvailable) {
			for (int i = 0; i < mNumButtonsMapped; i++) {
				// button states default to false?
				tempCheckButtonList.add( false /* getButtonStateMapped(i, GLFW.GLFW_PRESS) */);
			}

			for (int i = 0; i < mNumAxesMapped; i++) {
				tempCheckAxisList.add(getAxisValueMapped(i));
			}

		} else {
			for (int i = 0; i < mNumButtonsRaw; i++) {
				// button states default to false?
				tempCheckButtonList.add( false /* getButtonStateRaw(i, GLFW.GLFW_PRESS) */);
			}

			for (int i = 0; i < mNumAxisRaw; i++) {
				tempCheckAxisList.add(getAxisValueRaw(i));
			}

		}

		mIsCheckedForInputCode = true;
	}

}