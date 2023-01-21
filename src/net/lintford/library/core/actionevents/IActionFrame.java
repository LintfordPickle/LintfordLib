package net.lintford.library.core.actionevents;

public interface IActionFrame {

	public abstract void copy(IActionFrame other);

	public abstract void setChangeFlags(IActionFrame last);

	public abstract boolean hasChanges();

	public abstract void reset();

	int tickNumber();

	void tickNumber(int tickNumber);
}
