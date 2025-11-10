package net.lintfordlib.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.options.reader.IniFile;

/**
 * Maps the raw input indices to LintfordInputCode for buttons and axes.
 * 
 * Input: Physical buttons and axes. Output: LintfordInputCode
 * 
 */
public class LintfordGamepadState extends IniFile {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// @formatter:off
	public static final String SECTION_NAME_META 			= "Info";
	public static final String SECTION_NAME_BUTTONS 		= "Buttons";
	public static final String SECTION_NAME_AXES 			= "Axes";
	
	public static final int NO_MAPPING_ID 					= -1;
	// @formatter:on

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mName;
	private String mGuid;

	private final Map<Integer, GamepadInputMap> mGamepadInputs = new HashMap<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String guid() {
		return mGuid;
	}

	public GamepadInputMap getState(int key) {
		return mGamepadInputs.get(key);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LintfordGamepadState(String configFileName, String gamepadName, String gamepadGuid) {
		super(configFileName);

		mName = gamepadName; // just used for saving the name in the ini
		mGuid = gamepadGuid;

		setupStateObjects();

		loadConfig();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// @formatter:off
	public void setupStateObjects() {

		mGamepadInputs.clear();
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH, 			new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH, 			new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER, 	new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER, 	new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START, 			new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE, 			new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT, 				new GamepadInputMap());
		mGamepadInputs.put(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT, 			new GamepadInputMap());

	}
	// @formatter:on

	public void updateCustomState(ByteBuffer buttons, FloatBuffer axes) {
		for (var buttonValue : mGamepadInputs.values()) {
			if (buttonValue == null) {
				continue;
			}

			buttonValue.updateRaw(buttons, axes);
		}
	}

	public void updateSdlState(GLFWGamepadState glfwGamepadState) {
		for (var buttonValue : mGamepadInputs.values()) {
			if (buttonValue == null) {
				continue;
			}

			buttonValue.updateSdl(glfwGamepadState);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Maps a physical/sdl button code to the given gamepadInputCode.
	 */
	public void setButtonMapping(int rawButtonCode, int gamepadInputCode) {
		final var gamepadInputMap = getGamepadInputByCode(gamepadInputCode);

		if (gamepadInputMap == GamepadInputMap.empty) {
			Debug.debugManager().logger().e(LintfordGamepadState.class.getSimpleName(), "Could not set button mapping for " + gamepadInputCode + ". GamepadInputCode not recognized!");
			return;
		}

		gamepadInputMap.mapToButton(rawButtonCode);
	}

	/**
	 * Maps a physical/sdl axis code to the given gamepadInputCode.
	 */
	public void setAxisMapping(int rawAxisCode, float signum, int gamepadInputCode) {
		final var gamepadInputMap = getGamepadInputByCode(gamepadInputCode);

		if (gamepadInputMap == GamepadInputMap.empty) {
			Debug.debugManager().logger().e(LintfordGamepadState.class.getSimpleName(), "Could not set axis mapping for " + gamepadInputCode + ". GamepadInputCode not recognized!");
			return;
		}

		gamepadInputMap.mapToAxis(rawAxisCode, signum);
	}

	/**
	 * Returns the physical/sdl button index mapped to the given gamepadInputCode.
	 */
	public GamepadInputMap getInputMapping(int gamepadInputCode) {
		final var gamepadInputMap = getGamepadInputByCode(gamepadInputCode);

		if (gamepadInputMap == GamepadInputMap.empty)
			Debug.debugManager().logger().e(LintfordGamepadState.class.getSimpleName(), "Could not get button mapping for " + gamepadInputCode + ". GamepadInputCode not recognized!");

		return gamepadInputMap;
	}

	/**
	 * Returns the GamepadButtonInput with the given 'gamepadInputCode'. If a matching GamepadButtonInput is not found, then GamepadButtonInput.empty is returned.
	 */
	public GamepadInputMap getGamepadInputByCode(int gamepadInputCode) {
		final var buttonInput = mGamepadInputs.get(gamepadInputCode);

		if (buttonInput == null) {
			// Debug.debugManager().logger().e(LintfordGamepadState.class.getSimpleName(), "GamepadInputMap with InputCode '" + gamepadInputCode + "' not present!");
			return GamepadInputMap.empty;
		}

		return buttonInput;
	}

	public void createSdlMapping() {
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_A);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_B);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_X);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_Y);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_BACK);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_START);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_GUIDE);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
		mGamepadInputs.get(GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT).mapToButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);

		saveConfig();
	}

	// CONFIG ---

	public static final int INI_SAVE_TYPE_PREFIX_BUTTON = 0;
	public static final int INI_SAVE_TYPE_PREFIX_AXIS = 1;

	public static final String INI_TYPE_POSTFIX_TYPE = "t";
	public static final String INI_TYPE_POSTFIX_INDEX = "i";
	public static final String INI_TYPE_POSTFIX_SIGNUM = "s";

	@Override
	public void loadConfig() {
		super.loadConfig();

		if (isEmpty()) {

			// We use isEmpty from the GamepadManager to map default keys (depending on whether sdl-mapping is available or not).
			// saveConfig();

		} else {

			mName = getString(SECTION_NAME_META, "NAME", "No Name");
			mGuid = getString(SECTION_NAME_META, "GUID", "invalid guid");

			for (var set : mGamepadInputs.entrySet()) {
				final var key = set.getKey();
				var state = set.getValue();

				if (state == null)
					set.setValue(state = new GamepadInputMap());

				final var mappedToType = getInt(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_TYPE, -2);
				final var mappedToIndex = getInt(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_INDEX, -2);
				final var mappedToSignum = getFloat(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_SIGNUM, -2);

				if (mappedToType == -2 || mappedToIndex == -2 || mappedToSignum == -2)
					continue;

				switch (mappedToType) {
				case 0: // button
					state.mapToButton(mappedToIndex);
					break;

				case 1: // axis
					state.mapToAxis(mappedToIndex, mappedToSignum);
					break;
				default:

					break;
				}

			}
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		setValue(SECTION_NAME_META, "GUID", mGuid);
		setValue(SECTION_NAME_META, "NAME", mName);

		// Iterate the hashmaps
		for (var set : mGamepadInputs.entrySet()) {
			final var key = set.getKey();
			var state = set.getValue();

			if (state == null)
				set.setValue(state = new GamepadInputMap());

			var mapType = 0;
			switch (state.mappedToType()) {
			default:
			case button: // button
				mapType = 0;
				break;

			case axis: // axis
				mapType = 1;
				break;

			}

			final var mapValue = state.mappedTo();
			final var mapSignum = state.mappedToSignum();

			setValue(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_TYPE, mapType);
			setValue(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_INDEX, mapValue);
			setValue(SECTION_NAME_BUTTONS, String.valueOf(key) + INI_TYPE_POSTFIX_SIGNUM, mapSignum);
		}

		super.saveConfig();
	}
}
