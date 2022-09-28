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

public class GamepadManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class JoystickState {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private final int mJoystickIndex;
		private ByteBuffer mJoystickButtons;
		private FloatBuffer mJoystickAxes;
		private String mJoystickName;
		private boolean mIsActive;

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

			Debug.debugManager().logger().i("GAMEPAD", "Gamepad " + mJoystickName + " (" + mJoystickIndex + ") connected");
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void update(LintfordCore core) {
			mJoystickButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
			mJoystickAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);
		}
	}

	public class LintfordJoystick extends GLFWJoystickCallback {

		public final Map<Integer, JoystickState> mJoysticks = new HashMap<>();
		public final List<JoystickState> mJoystickStateList = new ArrayList<>();

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public LintfordJoystick() {
			// GLFW.glfwSetJoystickCallback(this);
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void update(LintfordCore core) {
			final int lNumConnectedJoysticks = mJoystickStateList.size();
			for (int i = 0; i < lNumConnectedJoysticks; i++) {
				final var lJoystick = mJoystickStateList.get(i);
				if (lJoystick.isActive() == false)
					continue;

				// Custom
				lJoystick.update(core);
			}
		}

		public void reset() {

		}

		void joystick_callback(int joy, int event) {
			if (event == GLFW.GLFW_CONNECTED) {

			}
		}

		@Override
		public void invoke(int joystickIndex, int event) {
			if (event == GLFW.GLFW_CONNECTED) {
				final var lJoystick = getJoystickState(joystickIndex);
				lJoystick.isActive(true);
			} else if (event == GLFW.GLFW_DISCONNECTED) {
				final var lJoystick = getJoystickState(joystickIndex);
				lJoystick.isActive(false);
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
			mJoystickStateList.add(lNewJoystick);
			return lNewJoystick;
		}

		@SuppressWarnings("unused")
		private void deleteJoystick(int joystickIndex) {
			if (mJoysticks.containsKey(joystickIndex)) {
				final var lJoystick = mJoysticks.remove(joystickIndex);
				mJoystickStateList.remove(lJoystick);
			}
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final LintfordJoystick lintfordJoystick = new LintfordJoystick();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GamepadManager() {

	}

}
