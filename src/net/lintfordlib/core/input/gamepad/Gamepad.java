package net.lintfordlib.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.storage.AppStorage;
import net.lintfordlib.core.storage.FileUtils;

public class Gamepad {

	/**
	 * Setting this to true will skip the SDL gamepad mapping (if available) and use the physical mapping that the user provides via the controller option screen.
	 */
	public static final boolean FORCE_CUSTOM_MAPPING = false;

	public static final String FileExtension = ".ini";
	public static final String GamepadsSubDir = "gamepads";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final int mJoystickIndex;
	private String mControllerName;
	public final String GUID;
	private boolean mIsActive;
	private boolean mIsInitialized;
	private int mNumButtons;
	private int mNumAxis;

	// Inputs
	// 'Raw'
	private ByteBuffer mGamepadButtons;
	private FloatBuffer mGamepadAxes;

	// 'SDL'
	private final boolean mIsGamepadMappingAvailable;
	private GLFWGamepadState mGLFWGamepadState;

	public final LintfordGamepadState state;

	private boolean mIsCheckedForInputCode;
	private boolean mIsCheckedForBindCode;

	// TODO: extract the following 2 constructs and put them in their own thing

	private final List<Boolean> tempCheckButtonList = new ArrayList<>();
	private final List<Float> tempCheckAxisList = new ArrayList<>();

	private final Map<Integer, Boolean> tempStateMap = new HashMap<>();

	private ByteBuffer mGLFWGamepadStateBuffer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int index() {
		return mJoystickIndex;
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public int numButtons() {
		return mNumButtons;
	}

	public int numAxis() {
		return mNumAxis;
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
		return mControllerName;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Gamepad(String name, String guid, int joystickIndex) {
		mJoystickIndex = joystickIndex;
		GUID = guid;

		final var gamepadMappingFileName = AppStorage.getGameConfigDirectory() + FileUtils.FILE_SEPERATOR + GamepadsSubDir + FileUtils.FILE_SEPERATOR + guid + FileExtension;
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading gamepad maping from '" + gamepadMappingFileName + "'");

		mControllerName = name;

		mIsGamepadMappingAvailable = !FORCE_CUSTOM_MAPPING && GLFW.glfwJoystickIsGamepad(mJoystickIndex);
		state = new LintfordGamepadState(gamepadMappingFileName, name, guid);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		isActive(true);

		if (mIsGamepadMappingAvailable) {
			mGLFWGamepadStateBuffer = MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF);
			mGLFWGamepadState = new GLFWGamepadState(mGLFWGamepadStateBuffer);

			GLFW.glfwGetGamepadState(mJoystickIndex, mGLFWGamepadState);

			mNumButtons = mGLFWGamepadState.buttons().limit();
			mNumAxis = mGLFWGamepadState.axes().limit();

		} else {
			mNumButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex).limit();
			mNumAxis = GLFW.glfwGetJoystickAxes(mJoystickIndex).limit();

			// poll once just so they are not empty in the update loop
			mGamepadButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
			mGamepadAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);
		}

		mIsInitialized = true;
	}

	public void reset() {
		mIsActive = false;
	}

	public void update(LintfordCore core) {
		if (!mIsInitialized)
			return;

		if (!GLFW.glfwJoystickPresent(mJoystickIndex)) {
			reset();
			return;
		}

		// update the state of the gamepad depending on whether there is an SDL available or 'custom' mappings
		if (mIsGamepadMappingAvailable) {
			GLFW.glfwGetGamepadState(mJoystickIndex, mGLFWGamepadState);

			state.updateSdlState(mGLFWGamepadState);

		} else {
			mGamepadButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
			mGamepadAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);

			state.updateCustomState(mGamepadButtons, mGamepadAxes);
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public float getSdlAxisValue(int glfwAxisIndex) {
		if (mGLFWGamepadState == null)
			throw new RuntimeException("Error polling sdl axis state (GLFWGamepadState is null)!");

		if (glfwAxisIndex < 0 || glfwAxisIndex >= mGLFWGamepadState.axes().limit())
			return 0; // OoB

		return mGLFWGamepadState.axes(glfwAxisIndex);
	}

	/**
	 * Gets the raw state of the axis.
	 * 
	 * @param glfwButtonIndex The index of the axis to check. Must be 0 > glfwAxisIndex >= mNumButtons.
	 */
	public float getPhysicalAxisValue(int axisIndex) {
		if (axisIndex < 0)
			return 0.f;

		if (mGamepadAxes == null)
			throw new RuntimeException("Error polling raw axis state (mGamepadAxes is null)!");

		if (axisIndex >= mNumAxis)
			return 0.f;

		return mGamepadAxes.get(axisIndex);
	}

	public boolean getSdlButtonState(int glfwButtonIndex, int glfwButtonState) {
		if (glfwButtonIndex < 0)
			return false;

		if (mGLFWGamepadState == null)
			throw new RuntimeException("Error polling sdl button state (GLFWGamepadState is null)!");

		final var mappedButtons = mGLFWGamepadState.buttons();
		final var numMappedButtons = mappedButtons.limit();
		if (glfwButtonIndex >= numMappedButtons)
			return false;

		return mappedButtons.get(glfwButtonIndex) == glfwButtonState;
	}

	public boolean getPhysicalButtonState(int rawButtonIndex, int rawStateToCheckAgainst) {
		if (rawButtonIndex < 0)
			return false;

		if (mGamepadButtons == null)
			throw new RuntimeException("Error polling raw button state (mGamepadButton is null)!");

		if (rawButtonIndex >= mNumButtons)
			return false;

		return mGamepadButtons.get(rawButtonIndex) == rawStateToCheckAgainst;
	}

	// These are called for checking for raw/sdl input changes on the gamepad

	/**
	 * Checks for any gamepad input, and returns the LINTFORD_GAMEPAD code for the button/axis/hat.
	 * 
	 * @return The button/axis index pressed.
	 */
	public int checkForRawButtonInput() {
		ensureTempCheckLists();

		if (mIsGamepadMappingAvailable) {
			final var numButtons = numButtons();
			for (int i = 0; i < numButtons; i++) {
				final var currentState = getSdlButtonState(i, GLFW.GLFW_PRESS);
				final var benchmarkState = tempCheckButtonList.get(i);
				if (currentState != benchmarkState) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Button Pressed detected: Button index : " + i);

					clearTempCheckList();

					return i;
				}
			}

		} else {
			final var numButtons = numButtons();
			for (int i = 0; i < numButtons; i++) {
				final var currentState = getPhysicalButtonState(i, GLFW.GLFW_PRESS);
				final var benchmarkState = tempCheckButtonList.get(i);
				if (currentState != benchmarkState) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Button Pressed detected: Button index : " + i);

					clearTempCheckList();

					return i;
				}
			}
		}

		return -1; // nothing yet
	}

	public RawAxisInput checkForAxisInput() {

		ensureTempCheckLists();

		if (mIsGamepadMappingAvailable) {
			final var numAxes = numAxis();
			for (int i = 0; i < numAxes; i++) {
				final var currentState = getSdlAxisValue(i);
				final var benchmarkState = tempCheckAxisList.get(i);

				final var value = currentState - benchmarkState;
				if (Math.abs(value) > 0.1f) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Axis movement detected: Axis index : " + i);

					clearTempCheckList();

					return new RawAxisInput(i, value);
				}
			}
		} else {
			final var numAxes = numAxis();
			for (int i = 0; i < numAxes; i++) {
				final var currentState = getPhysicalAxisValue(i);
				final var benchmarkState = tempCheckAxisList.get(i);

				final var value = currentState - benchmarkState;
				if (Math.abs(value) > 0.1f) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Raw Axis movement detected: Axis index : " + i);
					System.out.println("Raw Axis movement detected: Axis index : " + i + " (+)");

					clearTempCheckList();

					return new RawAxisInput(i, value);
				}
			}
		}

		return null;
	}

	// This method is called for checking for changes in button states (GamepadInputCode) on this gamepad

	/**
	 * This is called when we want to know which LintfordInputCode was last pressed on the controller, either button or axis. This method returns input code based on the mapping (either SDL or a custom mapping table).
	 * 
	 * @return The GamepadInputcode of the changed gamepad input.
	 */
	public int checkForBoundButtonInput() {

		ensureTempStateList();

		final var numStates = GamepadInputCodes.NUM_BUTTONS;
		for (int i = 0; i < numStates; i++) {
			final var codeToCheck = 100 + i;
						
			if (isStateChanged(codeToCheck)) {
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Mapped Button Pressed detected: Button index : " + i);

				clearTempStateList();

				return codeToCheck;
			}
		}

		return -1; // nothing yet
	}

	private boolean isStateChanged(int gamepadInputcode) {
		final var result = state.getState(gamepadInputcode);
		if (result == null)
			return false;

		final var benchmark = tempStateMap.get(gamepadInputcode);
		return result.isDown() != benchmark;
	}

	// misc.

	// We need a benchmark to test against. This is needed because some buttons default to 1 and not 0 (trigger buttons)
	private void ensureTempCheckLists() {
		if (mIsCheckedForInputCode)
			return;

		tempCheckButtonList.clear();
		tempCheckAxisList.clear();

		if (mIsGamepadMappingAvailable) {
			final var numButtons = numButtons();
			for (int i = 0; i < numButtons; i++) {
				// button states default to false?
				tempCheckButtonList.add(false /* getButtonStateMapped(i, GLFW.GLFW_PRESS) */);
			}

			final var numAxes = numAxis();
			for (int i = 0; i < numAxes; i++) {
				tempCheckAxisList.add(getSdlAxisValue(i));
			}

		} else {
			final var numButtons = numButtons();
			for (int i = 0; i < numButtons; i++) {
				// button states default to false?
				tempCheckButtonList.add(false /* getButtonStateRaw(i, GLFW.GLFW_PRESS) */);
			}

			final var numAxes = numAxis();
			for (int i = 0; i < numAxes; i++) {
				tempCheckAxisList.add(getPhysicalAxisValue(i));
			}

		}

		mIsCheckedForInputCode = true;
	}

	private void clearTempCheckList() {
		tempCheckButtonList.clear();
		tempCheckAxisList.clear();
		mIsCheckedForInputCode = false;
	}

	private void ensureTempStateList() {
		if (mIsCheckedForBindCode)
			return;

		tempStateMap.clear();

		// state.getState())
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT).isDown());
		tempStateMap.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN, state.getState(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN).isDown());

		mIsCheckedForBindCode = true;
	}

	private void clearTempStateList() {
		tempStateMap.clear();
		mIsCheckedForBindCode = false;
	}

}