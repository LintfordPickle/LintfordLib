package net.lintfordlib.core.input.mouse;

public interface IInputProcessor {

	public static final int INPUT_COOLDOWN_TIME = 200;

	boolean isCoolDownElapsed();

	void resetCoolDownTimer();

	boolean allowKeyboardInput();

	boolean allowGamepadInput();

	boolean allowMouseInput();

}
