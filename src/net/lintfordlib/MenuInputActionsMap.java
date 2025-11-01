package net.lintfordlib;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.core.input.BindableInputActionMap;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;

//@formatter:off

// TODO: Rename this class - its used a lot for getting the nav ids
public class MenuInputActionsMap extends BindableInputActionMap {

	public static final int MENU_KEY_BINDING_NAV_UP			= 500;
	public static final int MENU_KEY_BINDING_NAV_DOWN 		= 501;
	public static final int MENU_KEY_BINDING_NAV_LEFT 		= 502;
	public static final int MENU_KEY_BINDING_NAV_RIGHT 		= 503;
	public static final int MENU_KEY_BINDING_NAV_CONFIRM 	= 504;
	public static final int MENU_KEY_BINDING_NAV_CANCEL 	= 505;
	public static final int MENU_KEY_BINDING_NAV_BACK 		= 506;
	public static final int MENU_KEY_BINDING_NAV_PAUSE 		= 507;

	public MenuInputActionsMap() {

		// These will appear on the keybinds screen.

		addNewEventAction("Up", 		MENU_KEY_BINDING_NAV_UP, 		GLFW.GLFW_KEY_UP, 		GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_UP);
		addNewEventAction("Down", 		MENU_KEY_BINDING_NAV_DOWN, 		GLFW.GLFW_KEY_DOWN, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_DOWN);
		addNewEventAction("Left", 		MENU_KEY_BINDING_NAV_LEFT, 		GLFW.GLFW_KEY_LEFT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_LEFT);
		addNewEventAction("Right", 		MENU_KEY_BINDING_NAV_RIGHT, 	GLFW.GLFW_KEY_RIGHT, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_RIGHT);
		addNewEventAction("Confirm", 	MENU_KEY_BINDING_NAV_CONFIRM, 	GLFW.GLFW_KEY_ENTER, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH);
		addNewEventAction("Cancel", 	MENU_KEY_BINDING_NAV_CANCEL, 	GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);
		addNewEventAction("Back", 		MENU_KEY_BINDING_NAV_BACK, 		GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST);
		addNewEventAction("Pause", 		MENU_KEY_BINDING_NAV_PAUSE, 	GLFW.GLFW_KEY_ESCAPE, 	GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_START);

	}
}
