package net.lintford.library.core.input;

public interface IBufferedInputCallback {

	/** @return true to end capture, false to continue with keyboard capture. */
	public abstract boolean onEscapePressed();

	public abstract void onKeyPressed(char pCh);

	/** @return true to end capture, false to continue with keyboard capture. */
	public abstract boolean onEnterPressed();

	public abstract StringBuilder getStringBuilder();

	public abstract boolean getEnterFinishesInput();

	public abstract boolean getEscapeFinishesInput();

	public default void captureStopped() {

	}

}
