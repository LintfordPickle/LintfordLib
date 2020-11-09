package net.lintford.library.core.input;

import org.lwjgl.glfw.GLFW;

public class GLFWKeyMap {

	//@formatter:off
	public static String GetGlfwPrintableKeyFromKeyCode(int pKeyCode) {
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
		
		case GLFW.GLFW_KEY_LEFT_CONTROL: return "L-CONTROL";
		case GLFW.GLFW_KEY_RIGHT_CONTROL: return "R-CONTROL";
		
		case GLFW.GLFW_KEY_LEFT_BRACKET: return "[";
		case GLFW.GLFW_KEY_RIGHT_BRACKET: return "]";
		
		default:
			return "unknown";
		}
	}
	
}
