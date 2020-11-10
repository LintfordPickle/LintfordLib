package net.lintford.library.core.input;

public interface IBufferedTextInputCallback {

	/** @return true to end capture, false to continue with keyboard capture. */
	public abstract boolean onEscapePressed();

	public abstract void onKeyPressed(int pCodePoint);

	/** @return true to end capture, false to continue with keyboard capture. */
	public abstract boolean onEnterPressed();

	public abstract StringBuilder getStringBuilder();

	public abstract boolean getEnterFinishesInput();

	public abstract boolean getEscapeFinishesInput();

	/** If true, keyboard capture will end after a single key is input */
	public default boolean captureSingleKey() {
		return false;
	}

	public default void captureStopped() {

	}

}
