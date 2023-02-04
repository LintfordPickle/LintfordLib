package net.lintford.library.core.input.gamepad;

public interface IGamepadListener {

	void onGamepadConnected(InputGamepad gamepad);

	void onGamepadDisconnected(InputGamepad gamepad);

}
