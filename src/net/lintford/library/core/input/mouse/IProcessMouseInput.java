package net.lintford.library.core.input.mouse;

public interface IProcessMouseInput {

	public static final int MOUSE_COOL_TIME_TIME = 200;

	boolean isCoolDownElapsed();

	void resetCoolDownTimer();

}
