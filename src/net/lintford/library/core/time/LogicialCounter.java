package net.lintford.library.core.time;

public class LogicialCounter {

	private int mLogicalCounter;

	public LogicialCounter() {
		mLogicalCounter = 0;
	}

	public void reset() {
		mLogicalCounter = 0;
	}

	public int getCounter() {
		return mLogicalCounter;
	}

	public void incrementCounter() {
		mLogicalCounter++;
	}

	public void decrementCounter() {
		mLogicalCounter--;
	}

}
