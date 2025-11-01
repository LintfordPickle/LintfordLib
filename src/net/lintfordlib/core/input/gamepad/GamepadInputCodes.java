package net.lintfordlib.core.input.gamepad;

// @formatter:off
// Provides unique ids for gamepad input that the game will use.
// These are input type (button, axis, hat etc.) agnostic - we can map any physical input to each one of these.
// These are bound to the event actions (e.g. menu_move_next or game_shoot etc.)

public class GamepadInputCodes {

	public static final int LINTFORD_GAMEPAD_NO_MAPPING_UID = -1;
	
	public static final int NUM_BUTTONS 					= 15;

	public static final int
		LINTFORD_GAMEPAD_BUTTON_SOUTH        				= 100,
		LINTFORD_GAMEPAD_BUTTON_EAST         				= 101,
		LINTFORD_GAMEPAD_BUTTON_WEST         				= 102,
		LINTFORD_GAMEPAD_BUTTON_NORTH        				= 103,
		LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER  				= 104,
		LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER 				= 105,
		LINTFORD_GAMEPAD_BUTTON_BACK         				= 106,
		LINTFORD_GAMEPAD_BUTTON_START        				= 107,
		LINTFORD_GAMEPAD_BUTTON_GUIDE        				= 108,
		LINTFORD_GAMEPAD_BUTTON_UP    						= 109,
		LINTFORD_GAMEPAD_BUTTON_LEFT      					= 110,
		LINTFORD_GAMEPAD_BUTTON_RIGHT   					= 111,
		LINTFORD_GAMEPAD_BUTTON_DOWN    					= 112,
		LINTFORD_GAMEPAD_BUTTON_LAST         				= LINTFORD_GAMEPAD_BUTTON_DOWN;


	
	public static String getLintfordCodeName(int glfwInputCode) {

		switch(glfwInputCode) {
			case LINTFORD_GAMEPAD_BUTTON_SOUTH:				return "South Button";
			case LINTFORD_GAMEPAD_BUTTON_EAST: 				return "East Button";
			case LINTFORD_GAMEPAD_BUTTON_WEST: 				return "West Button";
			case LINTFORD_GAMEPAD_BUTTON_NORTH:				return "North Button";
			case LINTFORD_GAMEPAD_BUTTON_LEFT_SHOULDER: 		return "Left Bumper";
			case LINTFORD_GAMEPAD_BUTTON_RIGHT_SHOULDER: 		return "Right Bumper";
			case LINTFORD_GAMEPAD_BUTTON_BACK: 				return "Select";
			case LINTFORD_GAMEPAD_BUTTON_START: 			return "Start";
			case LINTFORD_GAMEPAD_BUTTON_GUIDE: 			return "Home";
			case LINTFORD_GAMEPAD_BUTTON_UP: 				return "Up";
			case LINTFORD_GAMEPAD_BUTTON_DOWN: 				return "Down";
			case LINTFORD_GAMEPAD_BUTTON_LEFT: 				return "Left";
			case LINTFORD_GAMEPAD_BUTTON_RIGHT: 			return "Right";
		
			default:
				return "unknown";
		}
	}

}
