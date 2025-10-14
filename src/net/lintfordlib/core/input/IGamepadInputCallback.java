package net.lintfordlib.core.input;

public interface IGamepadInputCallback {

	/**
	 * Return true to end key input capture. See: GamepadInputMap for gamepad inputs.
	 */
	public boolean gamepadInput(int lintfordGamepadButtonId);

}
