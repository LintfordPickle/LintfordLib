package net.lintford.library.core.input.gamepad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;

public class GamepadManager extends GLFWJoystickCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_NUM_CONTROLLERS = GLFW.GLFW_JOYSTICK_LAST;

	private static final int BUTTON_COOLDOWN_MS = 200;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Map<Integer, InputGamepad> mJoysticks = new HashMap<>();
	private final List<InputGamepad> mUpdateControllerList = new ArrayList<>();
	private final List<InputGamepad> mActiveControllers = Collections.unmodifiableList(mUpdateControllerList);

	private IGamepadListener mGamepadListener;

	private float mButtonDownTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void gamepadListener(IGamepadListener newGamepadListener) {
		mGamepadListener = newGamepadListener;
	}

	public IGamepadListener gamepadListener() {
		return mGamepadListener;
	}

	public InputGamepad getGamepad(int gamepadId) {
		if (gamepadId < 0 || gamepadId >= MAX_NUM_CONTROLLERS)
			return null;

		return mJoysticks.get(gamepadId);
	}

	public List<InputGamepad> getActiveGamepads() {
		return mActiveControllers;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GamepadManager() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		detectControllers();
	}

	public void update(LintfordCore core) {
		if (mButtonDownTimer > 0) {
			mButtonDownTimer -= core.gameTime().elapsedTimeMilli();
		}

		final int lNumConnectedJoysticks = mUpdateControllerList.size();
		for (int i = 0; i < lNumConnectedJoysticks; i++) {
			final var lJoystick = mUpdateControllerList.get(i);
			if (lJoystick.isActive() == false)
				continue;

			// Custom
			lJoystick.update(core);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void detectControllers() {
		for (int i = 0; i < MAX_NUM_CONTROLLERS; i++) {
			connectController(i);
		}
	}

	private InputGamepad getInputGamepad(int joystickIndex) {
		if (mJoysticks.containsKey(joystickIndex)) {
			return mJoysticks.get(joystickIndex);
		}

		return createNewInputGamepad(joystickIndex);
	}

	private InputGamepad createNewInputGamepad(int joystickIndex) {
		final var lNewJoystick = new InputGamepad(joystickIndex);
		mJoysticks.put(joystickIndex, lNewJoystick);
		mUpdateControllerList.add(lNewJoystick);

		return lNewJoystick;
	}

	// --------------------------------------

	public boolean isGamepadButtonDown(int glfwGamepadButtonIndex) {
		if (mButtonDownTimer > 0)
			return false;

		final var lNumConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < lNumConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {
				mButtonDownTimer = BUTTON_COOLDOWN_MS;
				return true;
			}
		}

		return false;
	}

	// --------------------------------------
	// Callback-Methods
	// --------------------------------------

	@Override
	public void invoke(int joystickIndex, int event) {
		if (event == GLFW.GLFW_CONNECTED) {
			connectController(joystickIndex);
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			disconnectController(joystickIndex);
		}
	}

	private void connectController(int controllerIndex) {
		var lGamepadPresent = GLFW.glfwJoystickPresent(controllerIndex);
		if (lGamepadPresent) {
			final var lJoystick = getInputGamepad(controllerIndex);
			lJoystick.initialize();

			if (mGamepadListener != null)
				mGamepadListener.onGamepadConnected(lJoystick);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " is present (" + lJoystick.name() + ")");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Buttons: " + lJoystick.numButtons());
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Axis: " + lJoystick.numAxis());
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Hats: " + lJoystick.numHats());

			if (lJoystick.isGamepadMappingAvailable())
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has gamepad mappings available");
			else
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has no gamepad mappings available");

		}
	}

	private void disconnectController(int controllerIndex) {
		final var lDisconnectedGamepad = getInputGamepad(controllerIndex);
		if (lDisconnectedGamepad == null || lDisconnectedGamepad.isActive() == false)
			return;

		if (mGamepadListener != null)
			mGamepadListener.onGamepadDisconnected(lDisconnectedGamepad);

		lDisconnectedGamepad.reset();
	}

}
