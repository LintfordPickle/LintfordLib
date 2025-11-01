package net.lintfordlib.core.input;

// The binding callback is used for waiting for an LintfordInputCode value from a gamepad.
// That is, you will receive the mapped LintfordInputCode for the next button/axis pressed on the gamepad.
public interface IGamepadInputBindingCallback {

	public boolean gamepadButtonBindingInput(int gamepadInputCode);

	public boolean gamepadAxisBindingInput(int gamepadInputCode);

}
