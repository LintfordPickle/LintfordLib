package net.lintford.library.core.input.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import net.lintford.library.core.LintfordCore;

public class GamepadManager {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class LintfordJoystick extends GLFWJoystickCallback {
		private int mJoystickIndex;
		private ByteBuffer mJoystickButtons;
		private FloatBuffer mJoystickAxes;
		private String mJoystickName;
		private boolean mIsActive;

		public boolean isActive() {
			return mIsActive;
		}

		public String name() {
			return mJoystickName;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public LintfordJoystick(final int pIndex) {
			mJoystickIndex = pIndex;

			GLFW.glfwSetJoystickCallback(this);

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void update(LintfordCore pCore) {
			mJoystickButtons = GLFW.glfwGetJoystickButtons(mJoystickIndex);
			mJoystickAxes = GLFW.glfwGetJoystickAxes(mJoystickIndex);

		}

		public void reset() {

		}

		void joystick_callback(int joy, int event) {
			if (event == GLFW.GLFW_CONNECTED) {
				mIsActive = true;

			} else if (event == GLFW.GLFW_DISCONNECTED) {
				mIsActive = false;

			}
		}

		@Override
		public void invoke(int arg0, int arg1) {

		}

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GamepadManager() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void handleJoysticks() {
		ByteBuffer d = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1);
		FloatBuffer lAxes = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1);
		String lJoystickName = GLFW.glfwGetJoystickName(GLFW.GLFW_JOYSTICK_1);

	}

}
