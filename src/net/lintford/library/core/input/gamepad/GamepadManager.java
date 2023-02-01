package net.lintford.library.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
	// Inner-Classes
	// --------------------------------------

	public class JoystickState {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private final int mJoystickIndex;

		private ByteBuffer mJoystickButtons;
		private ByteBuffer mJoystickHats;
		private FloatBuffer mJoystickAxes;
		private String mJoystickName;
		private boolean mIsActive;

		private static final int MAX_BUTTON_COUNT = 32;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public int index() {
			return mJoystickIndex;
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

		public JoystickState(int joystickIndex) {
			mJoystickIndex = joystickIndex;
			mJoystickName = GLFW.glfwGetJoystickName(mJoystickIndex);
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void update(LintfordCore core) {
			mJoystickButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
			mJoystickAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);
			mJoystickHats = GLFW.glfwGetJoystickHats(mJoystickIndex);

			int axisID = 1;
			while (mJoystickAxes.hasRemaining()) {
			    float state = mJoystickAxes.get();
			    if (state < -0.95f || state > 0.95f) {
			        System.out.println("Axis " + axisID + " is at full-range! " + state);
			    } else if (state < -0.5f || state > 0.5f) {
			        System.out.println("Axis " + axisID + " is at mid-range! " + state);
			    }
			    axisID++;
			}
			
			
			if (getIsButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A)) {
				System.out.println("button A down");
			}

			if (getIsButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_B)) {
				System.out.println("button B down");
			}

			if (getIsButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_X)) {
				System.out.println("button X down");
			}

			if (getIsButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_Y)) {
				System.out.println("button Y down");
			}

			if (getIsHatDown(GLFW.GLFW_HAT_LEFT)) {
				System.out.println("left");
			}

			if (getIsHatDown(GLFW.GLFW_HAT_RIGHT)) {
				System.out.println("right");
			}

			if (getIsHatDown(GLFW.GLFW_HAT_UP)) {
				System.out.println("up");
			}

			if (getIsHatDown(GLFW.GLFW_HAT_DOWN)) {
				System.out.println("down");
			}

		}

		public boolean getIsButtonDown(int buttonIndex) {
			if (mJoystickButtons == null || mJoystickButtons.capacity() == 0)
				return false;

			if (buttonIndex < 0 || buttonIndex > mJoystickButtons.limit())
				return false;

			return mJoystickButtons.get(buttonIndex) == (byte) 1;
		}

		public boolean getIsHatDown(int hatIndex) {
			if (mJoystickHats == null || mJoystickHats.capacity() == 0)
				return false;

			final var lHatState = mJoystickHats.get(0);
			return (lHatState & (hatIndex)) == hatIndex;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// Contains all the joysticks that are connected (indexed)
	public final Map<Integer, JoystickState> mJoysticks = new HashMap<>();
	private final List<JoystickState> mControllerUpdateList = new ArrayList<>();

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
			var present = GLFW.glfwJoystickPresent(i);
			if (present) {
				final var lControllerName = GLFW.glfwGetJoystickName(i);
				Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + i + " is present (" + lControllerName + ")");

				final var lJoystick = getJoystickState(i);
				lJoystick.isActive(true);
			}
		}
	}

	private JoystickState getJoystickState(int joystickIndex) {
		if (mJoysticks.containsKey(joystickIndex)) {
			return mJoysticks.get(joystickIndex);
		}

		return createNewJoystick(joystickIndex);
	}

	private JoystickState createNewJoystick(int joystickIndex) {
		final var lNewJoystick = new JoystickState(joystickIndex);
		mJoysticks.put(joystickIndex, lNewJoystick);
		mControllerUpdateList.add(lNewJoystick);

		return lNewJoystick;
	}

	@SuppressWarnings("unused")
	private void deleteJoystick(int joystickIndex) {
		if (mJoysticks.containsKey(joystickIndex)) {
			final var lJoystick = mJoysticks.remove(joystickIndex);
			mControllerUpdateList.remove(lJoystick);
		}
	}

	// --------------------------------------
	// Callback-Methods
	// --------------------------------------

	@Override
	public void invoke(int joystickIndex, int event) {
		if (event == GLFW.GLFW_CONNECTED) {
			final var lJoystick = getJoystickState(joystickIndex);
			lJoystick.isActive(true);

			Debug.debugManager().logger().i(getClass().getSimpleName(), "Controller " + lJoystick.index() + " is present (" + lJoystick.name() + ")");
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			final var lJoystick = getJoystickState(joystickIndex);
			lJoystick.isActive(false);
		}
	}

}
