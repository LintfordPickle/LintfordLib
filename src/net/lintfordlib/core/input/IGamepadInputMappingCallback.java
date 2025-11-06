package net.lintfordlib.core.input;

public interface IGamepadInputMappingCallback {

	public boolean gamepadButtonInput(int rawButtonId);

	/**
	 * 
	 * @param rawAxisId The raw axis id (either index into AxisBuffer, or Sdl state index).
	 * @param value     The signum of the value read when a change was detected.
	 * @return Input was accepted by the listener.
	 */

	public boolean gamepadAxisInput(int rawAxisId, float value);

	public void mappingCancelled();

}
