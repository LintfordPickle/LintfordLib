package net.lintfordlib.core.input.gamepad;

public interface IGamepadListener {

	void onGamepadConnected(InputGamepad gamepad);

	void onGamepadDisconnected(InputGamepad gamepad);

}
