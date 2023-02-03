package net.lintford.library.core.input.gamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;

@SuppressWarnings("unused")
public class GamepadManager extends GLFWJoystickCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int MAX_NUM_CONTROLLERS = GLFW.GLFW_JOYSTICK_LAST;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Map<Integer, InputGamepad> mJoysticks = new HashMap<>();
	private final List<InputGamepad> mControllerUpdateList = new ArrayList<>();

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
		final int lNumConnectedJoysticks = mControllerUpdateList.size();
		for (int i = 0; i < lNumConnectedJoysticks; i++) {
			final var lJoystick = mControllerUpdateList.get(i);
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
			initializeController(i);
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
		mControllerUpdateList.add(lNewJoystick);

		return lNewJoystick;
	}

	// --------------------------------------
	// Callback-Methods
	// --------------------------------------

	@Override
	public void invoke(int joystickIndex, int event) {
		if (event == GLFW.GLFW_CONNECTED) {
			initializeController(joystickIndex);
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			resetController(joystickIndex);
		}
	}

	private void initializeController(int controllerIndex) {
		var present = GLFW.glfwJoystickPresent(controllerIndex);
		if (present) {
			final var lJoystick = getInputGamepad(controllerIndex);
			lJoystick.initialize();

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

	private void resetController(int controllerIndex) {
		getInputGamepad(controllerIndex).reset();
	}

}
