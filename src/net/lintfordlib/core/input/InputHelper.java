package net.lintfordlib.core.input;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.input.gamepad.GamepadInputCodes;

//@formatter:off
public class InputHelper {

	/** returns false if a key is on the 'Binding' blacklist. */
	public static boolean isKeyAllowedAsBinding(int key) {
		switch(key) {
		case GLFW.GLFW_KEY_F1:
		case GLFW.GLFW_KEY_F2:
		case GLFW.GLFW_KEY_F3:
		case GLFW.GLFW_KEY_F4:
		case GLFW.GLFW_KEY_F5:
		case GLFW.GLFW_KEY_F6:
		case GLFW.GLFW_KEY_F7:
		case GLFW.GLFW_KEY_F8:
		case GLFW.GLFW_KEY_F9:
		case GLFW.GLFW_KEY_F10:
		case GLFW.GLFW_KEY_F11:
		case GLFW.GLFW_KEY_F12:

		case GLFW.GLFW_KEY_PRINT_SCREEN:
		case GLFW.GLFW_KEY_SCROLL_LOCK:
		case GLFW.GLFW_KEY_PAUSE:
			return false;
		default:
			return true;
		}
	}
	
	public static String getGlfwPrintableKeyFromKeyCode(int keyCode) {
		final String lPlatformCharacter = GLFW.glfwGetKeyName(keyCode, GLFW.glfwGetKeyScancode(keyCode));
		if(lPlatformCharacter != null) {
			return lPlatformCharacter;
		}

		switch (keyCode) {
		case GLFW.GLFW_KEY_A: return "A";
		case GLFW.GLFW_KEY_B: return "B";
		case GLFW.GLFW_KEY_C: return "C";
		case GLFW.GLFW_KEY_D: return "D";
		case GLFW.GLFW_KEY_E: return "E";
		case GLFW.GLFW_KEY_F: return "F";
		case GLFW.GLFW_KEY_G: return "G";
		case GLFW.GLFW_KEY_H: return "H";
		case GLFW.GLFW_KEY_I: return "I";
		case GLFW.GLFW_KEY_J: return "J";
		case GLFW.GLFW_KEY_K: return "K";
		case GLFW.GLFW_KEY_L: return "L";
		case GLFW.GLFW_KEY_M: return "M";
		case GLFW.GLFW_KEY_N: return "N";
		case GLFW.GLFW_KEY_O: return "O";
		case GLFW.GLFW_KEY_P: return "P";
		case GLFW.GLFW_KEY_Q: return "Q";
		case GLFW.GLFW_KEY_R: return "R";
		case GLFW.GLFW_KEY_S: return "S";
		case GLFW.GLFW_KEY_T: return "T";
		case GLFW.GLFW_KEY_U: return "U";
		case GLFW.GLFW_KEY_V: return "V";
		case GLFW.GLFW_KEY_W: return "W";
		case GLFW.GLFW_KEY_X: return "X";
		case GLFW.GLFW_KEY_Y: return "Y";
		case GLFW.GLFW_KEY_Z: return "Z";

		case GLFW.GLFW_KEY_SPACE: return "SPACE";
		case GLFW.GLFW_KEY_ENTER: return "ENTER";

		case GLFW.GLFW_KEY_LEFT_SHIFT: return "L-SHIFT";
		case GLFW.GLFW_KEY_RIGHT_SHIFT: return "R-SHIFT";

		case GLFW.GLFW_KEY_LEFT_ALT: return "L-ALT";
		case GLFW.GLFW_KEY_RIGHT_ALT: return "R-ALT";

		case GLFW.GLFW_KEY_TAB: return "TAB";
		case GLFW.GLFW_KEY_ESCAPE: return "ESCAPE";
		case GLFW.GLFW_KEY_BACKSPACE: return "BACKSPACE";
		case GLFW.GLFW_KEY_HOME: return "HOME";
		case GLFW.GLFW_KEY_END: return "END";
		case GLFW.GLFW_KEY_PAGE_UP: return "PAGE UP";
		case GLFW.GLFW_KEY_PAGE_DOWN: return "PAGE DOWN";
		case GLFW.GLFW_KEY_INSERT: return "INSERT";
		case GLFW.GLFW_KEY_DELETE: return "DELETE";

		case GLFW.GLFW_KEY_PERIOD: return ".";
		case GLFW.GLFW_KEY_COMMA: return ",";

		case GLFW.GLFW_KEY_LEFT_CONTROL: return "L-CONTROL";
		case GLFW.GLFW_KEY_RIGHT_CONTROL: return "R-CONTROL";

		case GLFW.GLFW_KEY_LEFT_BRACKET: return "[";
		case GLFW.GLFW_KEY_RIGHT_BRACKET: return "]";

		case GLFW.GLFW_KEY_LEFT: return "LEFT";
		case GLFW.GLFW_KEY_RIGHT: return "RIGHT";
		case GLFW.GLFW_KEY_UP: return "UP";
		case GLFW.GLFW_KEY_DOWN: return "DOWN";

		default:
			return "unknown";
		}
	}
	
	/** Joystick buttons are raw and assume no phyiscal location on the hardward. */
	public static String getGlfwPrintableKeyForJoystickButtons(int glfwGamepadButtonIndex) {
		switch (glfwGamepadButtonIndex) {
		case GLFW.GLFW_GAMEPAD_BUTTON_A: return "Button 0";
		case GLFW.GLFW_GAMEPAD_BUTTON_B: return "Button 1";
		case GLFW.GLFW_GAMEPAD_BUTTON_X: return "Button 2";
		case GLFW.GLFW_GAMEPAD_BUTTON_Y: return "Button 3";
		
		case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER: return "Button 4";
		case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER: return "Button 5";
		case GLFW.GLFW_GAMEPAD_BUTTON_BACK: return "Button 6";
		case GLFW.GLFW_GAMEPAD_BUTTON_START: return "Button 7";
		case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE: return "Button 8";
		case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB: return "Button 9";
		case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB: return "Button 10";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP: return "Button 11";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT: return "Button 12";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN: return "Button 13";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT: return "Button 14";

		default:
			return "unknown";
		}
	}
	
	/** Gamepad mapped states attempt to model the layout of the Xbox controller layout. */
	public static String getGlfwPrintableKeyForGamepadButtons(int glfwGamepadButtonIndex) {
		switch (glfwGamepadButtonIndex) {
		case GLFW.GLFW_GAMEPAD_BUTTON_A: return "Button A";
		case GLFW.GLFW_GAMEPAD_BUTTON_B: return "Button B";
		case GLFW.GLFW_GAMEPAD_BUTTON_X: return "Button X";
		case GLFW.GLFW_GAMEPAD_BUTTON_Y: return "Button Y";
		
		case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER: return "Left Bumper";
		case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER: return "Right Bumper";
		case GLFW.GLFW_GAMEPAD_BUTTON_BACK: return "Back";
		case GLFW.GLFW_GAMEPAD_BUTTON_START: return "Start";
		case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE: return "Home";
		case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB: return "Left Thumb";
		case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB: return "Right Thumb";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP: return "DPAD Up";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT: return "DPAD Right";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN: return "DPAD Down";
		case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT: return "DPAD Left";

		default:
			return "unknown";
		}
	}
	
	public static String getGlfwPrintableKeyForJoystickAxis(int glfwGamepadAxisIndex) {
		switch (glfwGamepadAxisIndex) {
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X: return "Axis ";
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y: return "Axis 1";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X: return "Axis 2 ";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y: return "Axis 3";
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER: return "Axis 4";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER: return "Axis 5";

		default:
			return "unknown";
		}
	}
	
	public static String getGlfwPrintableKeyForGamepadAxis(int glfwGamepadAxisIndex) {
		switch (glfwGamepadAxisIndex) {
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X: return "GLFW_GAMEPAD_AXIS_LEFT_X";
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y: return "GLFW_GAMEPAD_AXIS_LEFT_Y";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X: return "GLFW_GAMEPAD_AXIS_RIGHT_X";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y: return "GLFW_GAMEPAD_AXIS_RIGHT_Y";
		case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER: return "GLFW_GAMEPAD_AXIS_LEFT_TRIGGER";
		case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER: return "GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER";

		default:
			return "unknown";
		}
	}
	
	/** Maps code from GamepadInputMap into printable text */
	public static String getPhysicalKeyNameForGamepadInputIndex(int lintfordGamepadInputIndex) {
		switch (lintfordGamepadInputIndex) {
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH: return "Button 0";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST: return "Button 1";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST: return "Button 2";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH: return "Button 3";
		
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER: return "Button 4";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER: return "Button 5";
		
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK: return "Button 6";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START: return "Button 7";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE: return "Button 8";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT: return "Button 9";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT: return "Button 10";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN: return "Button 11";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP: return "Button 12";

		default:
			return "unknown";
		}
	}
	
	public static String getFriendlyKeyNameForGamepadInputIndex(int lintfordGamepadInputIndex) {
		switch (lintfordGamepadInputIndex) {
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH: return "Button South";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST: return "Button East";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST: return "Button West";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH: return "Button North";
		
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER: return "L-Shoulder";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER: return "R-Shoulder";
		
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_BACK: return "Back";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START: return "Start";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_GUIDE: return "Guide";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT: return "Left";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT: return "Right";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN: return "Down";
		case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP: return "Up";

		default:
			return "unknown";
		}
	}
	
}
