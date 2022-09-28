package net.lintford.library.core.input;

public interface IProcessMouseInput {

	public static final int MOUSE_COOL_TIME_TIME = 200;

	boolean isCoolDownElapsed();

	void resetCoolDownTimer();

}
