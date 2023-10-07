package net.lintfordlib.core.actionevents;

public interface IActionFrame {

	public abstract void copy(IActionFrame other);

	public abstract void setChangeFlags(IActionFrame last);

	// @formatter:off
	/* Note:
	 * it is important to flag changes as both key/button down as well as released.
	 * The input states are not automatically reset each frame, and are only reset when the
	 * corresponding key/button up is received.
	 * 
	 * Equally important is the distinction between input devices and the input events that drive the game.
	 * Here we are only interested in preserving the input events - the source should be irrelevant.
	 * */
	// @formatter:on
	public abstract boolean hasChanges();

	public abstract void reset();

	int tickNumber();

	void tickNumber(int tickNumber);
}
