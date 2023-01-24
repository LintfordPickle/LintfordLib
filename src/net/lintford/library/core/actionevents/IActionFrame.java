package net.lintford.library.core.actionevents;

public interface IActionFrame {

	public abstract void copy(IActionFrame other);

	public abstract void setChangeFlags(IActionFrame last);

	// @formatter:off
	/* Note:
	 * it is important to flag changes as both key/button down as well as released.
	 * The input states are not automatically reset each frame, and are only reset when the
	 * corresponding key/button up is received.
	 * */
	// @formatter:on
	public abstract boolean hasChanges();

	public abstract void reset();

	int tickNumber();

	void tickNumber(int tickNumber);
}
