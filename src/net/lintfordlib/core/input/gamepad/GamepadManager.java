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
import net.lintfordlib.core.input.IGamepadInputCallback;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class GamepadManager extends GLFWJoystickCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_NUM_CONTROLLERS = GLFW.GLFW_JOYSTICK_LAST;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Map<Integer, InputGamepad> mGamepads = new HashMap<>();
	private final List<InputGamepad> mUpdateControllerList = new ArrayList<>();
	private final List<InputGamepad> mActiveControllers = Collections.unmodifiableList(mUpdateControllerList);

	private final List<IGamepadListener> mGamepadListeners = new ArrayList<>();

	// If this is set, then there is something waiting on the gamepad for input - so don't process other gamepad input ?
	private IGamepadInputCallback mGamepadInputCallback;
	private float mGamepadCooldownMs;

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

		return mGamepads.get(gamepadId);
	}

	public List<InputGamepad> getActiveGamepads() {
		return mActiveControllers;
	}

	public void StartGamepadInputCapture(IGamepadInputCallback gamepadInputCallback) {
		mGamepadInputCallback = gamepadInputCallback;
		mGamepadCooldownMs = 300;
	}

	public boolean isSomeComponentCapturingInput() {
		return mGamepadInputCallback != null || mGamepadCooldownMs > 0;
	}

	public void stopKeyInputCapture() {

		Debug.debugManager().logger().v(getClass().getSimpleName(), "stopKeyInputCapture");

		mGamepadInputCallback = null;
		mGamepadCooldownMs = 300;
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

		if (mGamepadCooldownMs > 0)
			mGamepadCooldownMs -= core.gameTime().elapsedTimeMilli();

		final var lNumConnectedJoysticks = mUpdateControllerList.size();
		for (int i = 0; i < lNumConnectedJoysticks; i++) {
			final var lJoystick = mUpdateControllerList.get(i);
			if (lJoystick.isActive() == false)
				continue;

			lJoystick.update(core);
		}

		// only start actually captuing the gamepad input after a short cooldown
		if (mGamepadInputCallback != null && mGamepadCooldownMs < 0) {
			WaitForGamepadInput(core);
		}
	}

	private void WaitForGamepadInput(LintfordCore core) {
		// waiting for input
		int result = -1;
		final var lNumConnectedJoysticks = mUpdateControllerList.size();
		for (int i = 0; i < lNumConnectedJoysticks; i++) {
			final var lJoystick = mUpdateControllerList.get(i);
			if (lJoystick.isActive() == false)
				continue;

			result = lJoystick.checkForInputCode();
			if (result != -1) {
				final var accepted = mGamepadInputCallback.gamepadInput(result);
				if (!accepted)
					continue;

				stopKeyInputCapture();
				return;

			}
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

	private InputGamepad getInputGamepad(int controllerIndex) {
		if (mGamepads.containsKey(controllerIndex)) {
			return mGamepads.get(controllerIndex);
		}

		return createNewInputGamepad(controllerIndex);
	}

	private InputGamepad createNewInputGamepad(int controllerIndex) {
		var controllerGuid = GLFW.glfwGetJoystickGUID(controllerIndex);
		final var newController = new InputGamepad(controllerGuid, controllerIndex);

		mGamepads.put(controllerIndex, newController);
		mUpdateControllerList.add(newController);

		return newController;
	}

	// --------------------------------------

	/** Checks if the requested button is pressed on any of the currently connected gamepads. */
	public boolean isGamepadButtonDown(int glfwGamepadButtonIndex) {
		if (isSomeComponentCapturingInput())
			return false;

		final var lNumConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < lNumConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {
				return true;
			}
		}

		return false;
	}

	/** Checks if the requested button is pressed on the connected gamepad with the given id. */
	public boolean isGamepadButtonDown(int gamepadIndex, int glfwGamepadButtonIndex) {
		if (isSomeComponentCapturingInput())
			return false;

		if (gamepadIndex < 0 || gamepadIndex >= mActiveControllers.size())
			return false;

		if (mActiveControllers.get(gamepadIndex).getIsButtonDown(glfwGamepadButtonIndex)) {
			return true;
		}

		return false;
	}

	/** Checks if the requested button is pressed on the connected gamepad with the given id. The state will be checked by the passed IInputProcessor. */
	public boolean isGamepadButtonDown(int gamepadIndex, int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		if (gamepadIndex < 0 || gamepadIndex >= mActiveControllers.size())
			return false;

		if (mActiveControllers.get(gamepadIndex).getIsButtonDown(glfwGamepadButtonIndex)) {
			return true;
		}

		return false;
	}

	/** Checks if the requested button is pressed on any of the currently connected gamepads. The state will be checked by the passed IInputProcessor. */
	public boolean isGamepadButtonDown(int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (isSomeComponentCapturingInput())
			return false;

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

	/** Checks if the requested button is pressed on the connected gamepad with the given id. The state will be checked by the passed IInputProcessor, and will be constrained by a cooldown timer. */
	public boolean isGamepadButtonDownTimed(int gamepadIndex, int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (mGamepadCooldownMs > 0)
			return false;

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

	/** Checks if the requested button is pressed on any of the currently connected gamepads. The state will be checked by the passed IInputProcessor, and will be constrained by a cooldown timer. */
	public boolean isGamepadButtonDownTimed(int glfwGamepadButtonIndex, IInputProcessor inputProcessor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (mGamepadCooldownMs > 0)
			return false;

		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return false;

		if (inputProcessor.isCoolDownElapsed() == false)
			return false;

		final var numConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			if (mActiveControllers.get(i).getIsButtonDown(glfwGamepadButtonIndex)) {
				inputProcessor.resetCoolDownTimer();
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the axis value of the first controller found where the absolute axis value is larger than threashold. The returned value retains the sign.
	 */
	public float getGamepadAxisValue(int glfwGamepadAxisIndex) {
		if (isSomeComponentCapturingInput())
			return 0;

		final var numConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			final var value = mActiveControllers.get(i).getAxisValue(glfwGamepadAxisIndex);
			if (Math.abs(value) > 0.01f) {
				return value;
			}
		}

		return 0;
	}

	/**
	 * Returns the axis value of the first controller found where the absolute axis value is larger than threashold. The returned value retains the sign.
	 */
	public float getGamepadAxisValueTimed(int glfwGamepadAxisIndex, IInputProcessor inputProcessor) {
		if (isSomeComponentCapturingInput())
			return 0;

		if (mGamepadCooldownMs > 0)
			return 0;

		if (inputProcessor != null && inputProcessor.allowGamepadInput() == false)
			return 0;

		if (inputProcessor.isCoolDownElapsed() == false)
			return 0;

		final var numConnectGamepads = mActiveControllers.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			final var value = mActiveControllers.get(i).getAxisValue(glfwGamepadAxisIndex);
			if (Math.abs(value) > 0.01f) {
				inputProcessor.resetCoolDownTimer();

				return value;
			}
		}

		return 0;
	}

	// --------------------------------------
	// Callback-Methods
	// --------------------------------------

	@Override
	public void invoke(int gamepadIndex, int event) {
		if (event == GLFW.GLFW_CONNECTED) {
			connectController(gamepadIndex);
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			disconnectController(gamepadIndex);
		}
	}

	private void connectController(int controllerIndex) {
		var gamepadPresent = GLFW.glfwJoystickPresent(controllerIndex);

		if (gamepadPresent) {
			final var gamepad = getInputGamepad(controllerIndex);
			gamepad.initialize();

			final var lNumListeners = mGamepadListeners.size();
			for (var i = 0; i < lNumListeners; i++) {
				mGamepadListeners.get(i).onGamepadConnected(gamepad);
			}

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " is present (" + gamepad.name() + ")");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Buttons: " + gamepad.numButtonsRaw());
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Axis: " + gamepad.numAxisRaw());
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Hats: " + gamepad.numHatsRaw());

			if (gamepad.isGamepadMappingAvailable())
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has gamepad mappings available");
			else
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has no gamepad mappings available");

		}
	}

	private void disconnectController(int controllerIndex) {
		final var disconnectedGamepad = getInputGamepad(controllerIndex);
		if (disconnectedGamepad == null || disconnectedGamepad.isActive() == false)
			return;

		final var numListeners = mGamepadListeners.size();
		for (var i = 0; i < numListeners; i++) {
			mGamepadListeners.get(i).onGamepadDisconnected(disconnectedGamepad);

		}

		disconnectedGamepad.reset();
	}

}
