package net.lintford.library.core.input;

public interface IKeyInputCallback {

	/** Return true to end key input capture */
	public void keyInput(int key, int scanCode, int action, int mods);

}
