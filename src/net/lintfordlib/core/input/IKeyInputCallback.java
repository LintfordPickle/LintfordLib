package net.lintfordlib.core.input;

public interface IKeyInputCallback {

	/** Return true to end key input capture */
	public boolean keyInput(int key, int scanCode, int action, int mods);

}
