package net.lintfordlib.core.input.mouse;

public interface IInputProcessor {

	public static final int INPUT_COOLDOWN_TIME = 200;

	boolean isCoolDownElapsed();

	default void resetCoolDownTimer() {
		resetCoolDownTimer(INPUT_COOLDOWN_TIME);
	}

	void resetCoolDownTimer(float cooldownInMs);

	boolean allowKeyboardInput();

	boolean allowGamepadInput();

	boolean allowMouseInput();

}
