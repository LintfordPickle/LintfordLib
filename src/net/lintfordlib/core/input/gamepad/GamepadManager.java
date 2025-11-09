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
import net.lintfordlib.core.input.IGamepadInputBindingCallback;
import net.lintfordlib.core.input.IGamepadInputMappingCallback;
import net.lintfordlib.core.input.mouse.IInputProcessor;

public class GamepadManager extends GLFWJoystickCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_NUM_CONTROLLERS = GLFW.GLFW_JOYSTICK_LAST;
	public static final int NO_GAMEPAD_MAPPING = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Map<Integer, Gamepad> mGamepads = new HashMap<>();
	private final List<Gamepad> mUpdateGamepadList = new ArrayList<>();
	private final List<Gamepad> mActiveGamepads = Collections.unmodifiableList(mUpdateGamepadList);

	/**
	 * These listeners are called whenever a gamepad is connected or disconnected. They are called after the controller event has been processed by the GamepadManager (added or removed).
	 */
	private final List<IGamepadListener> mGamepadListeners = new ArrayList<>();

	// If either of these are set, then there is something waiting on the gamepad for input - so don't process other gamepad input ?
	private int mMappingGamepadIndex;
	private IGamepadInputMappingCallback mGamepadInputMappingCallback;
	private IGamepadInputBindingCallback mGamepadInputBindingCallback;

	private float mGamepadCaptureCooldownMs;

	// calling out to GLFW.glfwIsJoystickPresent(index) can trigger the connect/disconnect for other pads.
	// During the frames where a gamepad is connected or removed, we need to rebuild the gamepad lists.
	private boolean mGamepadManagerDirty;
	private boolean mIsGamepadAvailable;

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

	public Gamepad getGamepad(int gamepadId) {
		if (gamepadId < 0 || gamepadId >= MAX_NUM_CONTROLLERS)
			return null;

		return mGamepads.get(gamepadId);
	}

	public List<Gamepad> getActiveGamepads() {
		return mActiveGamepads;
	}

	public void startGamepadMappingCapture(IGamepadInputMappingCallback gamepadMappingCallback, int gamepadIndex) {
		if (mGamepadInputMappingCallback != null || mGamepadInputBindingCallback != null)
			return; // cannot be capturing two things at a time.

		mMappingGamepadIndex = gamepadIndex;
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Starting gamepad mapping capture.");

		mGamepadInputMappingCallback = gamepadMappingCallback;
		mGamepadCaptureCooldownMs = 300;
	}

	public void startGamepadBindingCapture(IGamepadInputBindingCallback gamepadBindingCallback) {
		if (mGamepadInputMappingCallback != null || mGamepadInputBindingCallback != null)
			return; // cannot be capturing two things at a time.

		Debug.debugManager().logger().v(getClass().getSimpleName(), "Starting gamepad binding capture.");

		mGamepadInputBindingCallback = gamepadBindingCallback;
		mGamepadCaptureCooldownMs = 300;
	}

	/**
	 * @return true if waiting for either a mapping or binding input from a controller.
	 */
	public boolean isSomeComponentCapturingInput() {
		return mGamepadInputMappingCallback != null || mGamepadInputBindingCallback != null || mGamepadCaptureCooldownMs > 0;
	}

	public void stopGamepadCapture() {

		Debug.debugManager().logger().v(getClass().getSimpleName(), "Stopping gamepad capture");

		mMappingGamepadIndex = NO_GAMEPAD_MAPPING;

		mGamepadInputMappingCallback = null;
		mGamepadInputBindingCallback = null;
		mGamepadCaptureCooldownMs = 300;
	}

	public boolean isGamepadAvailable() {
		return mIsGamepadAvailable;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GamepadManager() {
		mMappingGamepadIndex = NO_GAMEPAD_MAPPING;
		mIsGamepadAvailable = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		detectControllers();
	}

	public void update(LintfordCore core) {

		if (mGamepadCaptureCooldownMs > 0)
			mGamepadCaptureCooldownMs -= core.gameTime().elapsedTimeMilli();

		final var numConnectedGamepads = mUpdateGamepadList.size();
		for (int i = 0; i < numConnectedGamepads; i++) {

			if (mGamepadManagerDirty) {
				mGamepadManagerDirty = false;
				return; // cannot continue this frame because the update list is old
			}

			final var gamepad = mUpdateGamepadList.get(i);
			if (!gamepad.isActive())
				continue;

			gamepad.update(core);
		}

		if (mGamepadCaptureCooldownMs > 0)
			return;

		if (mGamepadInputMappingCallback != null)
			WaitForRawGamepadInput(core);
		else if (mGamepadInputBindingCallback != null)
			WaitForMappedGamepadInput(core);

	}

	private void WaitForRawGamepadInput(LintfordCore core) { // Mapping logic

		final var gamepad = mGamepads.get(mMappingGamepadIndex);

		if (gamepad == null || !gamepad.isActive()) {
			stopGamepadCapture();
			return;
		}

		final var buttonResult = gamepad.checkForRawButtonInput();
		if (buttonResult != -1) {
			final var accepted = mGamepadInputMappingCallback.gamepadButtonInput(buttonResult);
			if (!accepted)
				return;

			stopGamepadCapture();
			return;
		}

		final var axisResult = gamepad.checkForAxisInput();
		if (axisResult != null) {
			final var accepted = mGamepadInputMappingCallback.gamepadAxisInput(axisResult.rawAxisId, Math.signum(axisResult.value));
			if (!accepted)
				return;

			stopGamepadCapture();
			return;
		}
	}

	// These get bound to GamepadInputCodes - not to the raw/sdl input
	private void WaitForMappedGamepadInput(LintfordCore core) { // TODO: binding logic

		final var numConnectedGamepads = mUpdateGamepadList.size();
		for (int i = 0; i < numConnectedGamepads; i++) {
			final var gamepad = mUpdateGamepadList.get(i);
			if (gamepad.isActive() == false)
				continue;

			final var buttonResult = gamepad.checkForBoundButtonInput();
			if (buttonResult != -1) {

				final var accepted = mGamepadInputBindingCallback.gamepadButtonBindingInput(buttonResult);
				if (!accepted)
					continue;

				stopGamepadCapture();
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

	private Gamepad getInputGamepad(int controllerIndex) {
		if (mGamepads.containsKey(controllerIndex)) {
			return mGamepads.get(controllerIndex);
		}

		return null;
	}

	private Gamepad createNewInputGamepad(int controllerIndex, String guid, String name) {
		final var newController = new Gamepad(name, guid, controllerIndex);

		mGamepads.put(controllerIndex, newController);
		mUpdateGamepadList.add(newController);

		return newController;
	}

	private void removeInputGampad(int controllerIndex) {
		final var gamepad = mGamepads.remove(controllerIndex);
		if (gamepad != null)
			mUpdateGamepadList.remove(gamepad);

	}

	// Input Methods
	// --------------------------------------

	// --- Buttons

	public boolean isGamepadButtonDown(int gamepadInputCode) {
		return isGamepadButtonDown(gamepadInputCode, null);
	}

	public boolean isGamepadButtonDown(int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (mGamepadCaptureCooldownMs > 0)
			return false;

		final var numConnectGamepads = mActiveGamepads.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			if (isGamepadButtonDown(i, gamepadInputCode, processor)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGamepadButtonDown(int controllerIndex, int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (mGamepadCaptureCooldownMs > 0)
			return false;

		if (controllerIndex < 0 || controllerIndex >= mActiveGamepads.size())
			return false;

		if (processor != null) {
			if (!processor.allowGamepadInput())
				return false;

			if (!processor.isCoolDownElapsed())
				return false;
		}

		final var controller = mActiveGamepads.get(controllerIndex);
		final var buttonState = controller.state.getGamepadInputByCode(gamepadInputCode);
		if (buttonState.isDown()) {
			if (processor != null) {
				processor.resetCoolDownTimer();
			}

			return true;
		}

		return false;
	}

	// --- Timed

	public boolean isGamepadButtonDownTimed(int gamepadInputCode) {
		return isGamepadButtonDownTimed(gamepadInputCode, null);
	}

	public boolean isGamepadButtonDownTimed(int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (processor != null && !processor.allowGamepadInput())
			return false;

		if (mGamepadCaptureCooldownMs > 0)
			return false;

		final var numConnectGamepads = mActiveGamepads.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			if (isGamepadButtonDown(i, gamepadInputCode, processor)) {
				return true;
			}
		}

		return false;
	}

	public boolean isGamepadButtonDownTimed(int controllerIndex, int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return false;

		if (mGamepadCaptureCooldownMs > 0)
			return false;

		if (controllerIndex < 0 || controllerIndex >= mActiveGamepads.size())
			return false;

		if (processor != null) {
			if (!processor.allowGamepadInput())
				return false;

			if (!processor.isCoolDownElapsed())
				return false;
		}

		final var gamepad = mActiveGamepads.get(controllerIndex);
		final var buttonState = gamepad.state.getGamepadInputByCode(gamepadInputCode);
		if (buttonState.isDown()) {
			if (processor != null) {
				processor.resetCoolDownTimer();
			}

			return true;
		}

		return false;
	}

	// --- Axis

	public float getGamepadAxis(int gamepadInputCode) {
		return getGamepadAxis(gamepadInputCode, null);
	}

	public float getGamepadAxis(int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return 0;

		if (mGamepadCaptureCooldownMs > 0)
			return 0;

		final var numConnectGamepads = mActiveGamepads.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			final var controllerAxisValue = getGamepadAxis(i, gamepadInputCode, processor);

			if (Math.abs(controllerAxisValue) > 0.02f) {
				return controllerAxisValue;
			}
		}

		return 0;
	}

	public float getGamepadAxis(int controllerIndex, int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return 0.f;

		if (mGamepadCaptureCooldownMs > 0)
			return 0.f;

		if (controllerIndex < 0 || controllerIndex >= mActiveGamepads.size())
			return 0.f;

		if (processor != null) {
			if (!processor.allowGamepadInput())
				return 0.f;

			if (!processor.isCoolDownElapsed())
				return 0.f;
		}

		final var gamepad = mActiveGamepads.get(controllerIndex);
		final var axisValue = gamepad.state.getGamepadInputByCode(gamepadInputCode);

		// TODO: Axis interpretation needs fixing
		if (axisValue.isValueSet() && processor != null) {
			processor.resetCoolDownTimer();
		}

		return axisValue.valueAdjusted();
	}

	// --- Timed

	public float getGamepadAxisTimed(int gamepadInputCode) {
		return getGamepadAxisTimed(gamepadInputCode, null);
	}

	public float getGamepadAxisTimed(int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return 0;

		if (mGamepadCaptureCooldownMs > 0)
			return 0;

		final var numConnectGamepads = mActiveGamepads.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			final var gamepadAxisValue = getGamepadAxisTimed(i, gamepadInputCode, processor);

			if (gamepadAxisValue > 0.f) {
				return gamepadAxisValue;
			}
		}

		return 0;
	}

	public float getGamepadAxisTimed(int controllerIndex, int gamepadInputCode, IInputProcessor processor) {
		if (isSomeComponentCapturingInput())
			return 0.f;

		if (mGamepadCaptureCooldownMs > 0)
			return 0.f;

		if (controllerIndex < 0 || controllerIndex >= mActiveGamepads.size())
			return 0.f;

		if (processor != null) {
			if (!processor.allowGamepadInput())
				return 0.f;

			if (!processor.isCoolDownElapsed())
				return 0.f;
		}

		final var gamepad = mActiveGamepads.get(controllerIndex);
		final var axisValue = gamepad.state.getGamepadInputByCode(gamepadInputCode);

		// TODO: Axis interpretation needs fixing
		if (axisValue.isValueSet() && processor != null) {
			processor.resetCoolDownTimer();
		}

		return axisValue.value();
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

		// Some controllers are invoked twice. If the same guid has already been added, then don't re-add this controller

		if (gamepadPresent) {
			var controllerGuid = GLFW.glfwGetJoystickGUID(controllerIndex);

			var gamepad = getInputGamepad(controllerIndex);

			if (gamepad == null) {
				var controllerName = GLFW.glfwGetJoystickName(controllerIndex);
				var numGamepadGuids = gamepadGuidPresent(controllerGuid);
				if (numGamepadGuids > 0) {
					Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller with guid already added " + controllerGuid + "!");
					controllerName += " (" + numGamepadGuids + ")";
				}

				gamepad = createNewInputGamepad(controllerIndex, controllerGuid, controllerName);
			}
			gamepad.initialize();

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " is present (" + gamepad.name() + ")");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller guid:" + controllerGuid);
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Buttons: " + gamepad.numButtons());
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Num Axis: " + gamepad.numAxis());

			if (gamepad.isGamepadMappingAvailable())
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has gamepad mappings available");
			else
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + controllerIndex + " has no gamepad mappings available");

			final var lNumListeners = mGamepadListeners.size();
			for (var i = 0; i < lNumListeners; i++) {
				mGamepadListeners.get(i).onGamepadConnected(gamepad);
			}
		}

		mIsGamepadAvailable = mActiveGamepads.size() > 0;
		mGamepadManagerDirty = true;
		
	}

	private int gamepadGuidPresent(String guid) {
		int counter = 0;
		final var numConnectGamepads = mActiveGamepads.size();
		for (int i = 0; i < numConnectGamepads; i++) {
			final var gamepad = mActiveGamepads.get(i);
			if (gamepad == null)
				continue;

			if (gamepad.GUID == null || gamepad.GUID.length() == 0)
				continue;

			if (gamepad.GUID.equals(guid))
				counter++;
		}

		return counter;
	}

	private void disconnectController(int controllerIndex) {
		final var disconnectedGamepad = getInputGamepad(controllerIndex);
		if (disconnectedGamepad == null || disconnectedGamepad.isActive() == false)
			return;

		removeInputGampad(controllerIndex);

		final var numListeners = mGamepadListeners.size();
		for (var i = 0; i < numListeners; i++) {
			mGamepadListeners.get(i).onGamepadDisconnected(disconnectedGamepad);

		}

		disconnectedGamepad.reset();
		mIsGamepadAvailable = mActiveGamepads.size() > 0;
		mGamepadManagerDirty = true;
	}

}
