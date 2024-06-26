package net.lintfordlib.core.input.gamepad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class GamepadManager extends GLFWJoystickCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_NUM_CONTROLLERS = GLFW.GLFW_JOYSTICK_LAST;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Map<Integer, InputGamepad> mJoysticks = new HashMap<>();
	private final List<InputGamepad> mUpdateControllerList = new ArrayList<>();
	private final List<InputGamepad> mActiveControllers = Collections.unmodifiableList(mUpdateControllerList);

	private final List<IGamepadListener> mGamepadListeners = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void addGamepadListener(IGamepadListener newGamepadListener) {
		if (mGamepadListeners.contains(newGamepadListener) == false)
			mGamepadListeners.add(newGamepadListener);

	}

	public void removeGamepadListener(IGamepadListener newGamepadListener) {
		if (mGamepadListeners.contains(newGamepadListener))
			mGamepadListeners.remove(newGamepadListener);

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
		final var lNumConnectedJoysticks = mUpdateControllerList.size();
		for (int i = 0; i < lNumConnectedJoysticks; i++) {
			final var lJoystick = mUpdateControllerList.get(i);
			if (lJoystick.isActive() == false)
				continue;

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
		final var lNumConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < lNumConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGamepadButtonDown(int gamepadIndex, int glfwGamepadButtonIndex) {
		if (gamepadIndex < 0 || gamepadIndex >= mActiveControllers.size())
			return false;

		if (mActiveControllers.get(gamepadIndex).getIsButtonDown(glfwGamepadButtonIndex)) {
			return true;
		}

		return false;
	}

	public boolean isGamepadButtonDown(int gamepadIndex, int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		if (gamepadIndex < 0 || gamepadIndex >= mActiveControllers.size())
			return false;

		if (mActiveControllers.get(gamepadIndex).getIsButtonDown(glfwGamepadButtonIndex)) {
			return true;
		}

		return false;
	}

	public boolean isGamepadButtonDown(int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		final var lNumConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < lNumConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGamepadButtonDownTimed(int gamepadIndex, int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		if (inputProcessor.isCoolDownElapsed() == false)
			return false;

		if (gamepadIndex < 0 || gamepadIndex >= mActiveControllers.size())
			return false;

		if (mActiveControllers.get(gamepadIndex).getIsButtonDown(glfwGamepadButtonIndex)) {

			inputProcessor.resetCoolDownTimer();
			return true;
		}

		return false;
	}

	public boolean isGamepadButtonDownTimed(int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		if (inputProcessor.isCoolDownElapsed() == false)
			return false;

		final var lNumConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < lNumConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {

				inputProcessor.resetCoolDownTimer();
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

			final int lNumListeners = mGamepadListeners.size();
			for (int i = 0; i < lNumListeners; i++) {
				mGamepadListeners.get(i).onGamepadConnected(lJoystick);
			}

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

		final int lNumListeners = mGamepadListeners.size();
		for (int i = 0; i < lNumListeners; i++) {
			mGamepadListeners.get(i).onGamepadDisconnected(lDisconnectedGamepad);

		}

		lDisconnectedGamepad.reset();
	}

}
