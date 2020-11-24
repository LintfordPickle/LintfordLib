package net.lintford.library.core.input;

import org.lwjgl.glfw.GLFW;

//@formatter:off
public class InputHelper {

	/** returns false if a key is on the 'Binding' blacklist. */
	public static boolean isKeyAllowedAsBinding(int pKey) {
		switch(pKey) {
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
	
	public static String getGlfwPrintableKeyFromKeyCode(int pKeyCode) {
		final String lPlatformCharacter = GLFW.glfwGetKeyName(pKeyCode, GLFW.glfwGetKeyScancode(pKeyCode));
		if(lPlatformCharacter != null) {
			return lPlatformCharacter;

		}

		switch (pKeyCode) {
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

		default:
			return "unknown";
		}
	}
	
	
}
