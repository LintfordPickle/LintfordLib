package net.lintfordlib;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.input.BindableActionMap;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;

//@formatter:off

public class MenuActions extends BindableActionMap {

	public static final int NAV_UP			= 500;
	public static final int NAV_DOWN 		= 501;
	public static final int NAV_LEFT 		= 502;
	public static final int NAV_RIGHT 		= 503;
	public static final int NAV_CONFIRM 	= 504;
	public static final int NAV_CANCEL 		= 505;
	public static final int NAV_BACK 		= 506;
	public static final int NAV_PAUSE 		= 507;
	public static final int NAV_INFO 	    = 508;

	public MenuActions() {

		// These will appear on the keybinds screen.

		addNewEventAction("Up", 		NAV_UP, 		GLFW.GLFW_KEY_UP, 		GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP);
		addNewEventAction("Down", 		NAV_DOWN, 		GLFW.GLFW_KEY_DOWN, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN);
		addNewEventAction("Left", 		NAV_LEFT, 		GLFW.GLFW_KEY_LEFT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT);
		addNewEventAction("Right", 		NAV_RIGHT, 		GLFW.GLFW_KEY_RIGHT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT);
		addNewEventAction("Confirm", 	NAV_CONFIRM, 	GLFW.GLFW_KEY_ENTER, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH);
		addNewEventAction("Cancel", 	NAV_CANCEL, 	GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);
		addNewEventAction("Back", 		NAV_BACK, 		GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);
		addNewEventAction("Pause", 		NAV_PAUSE, 		GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START);
		addNewEventAction("Info", 		NAV_INFO, 		GLFW.GLFW_KEY_I, 		GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH);

	}
}
