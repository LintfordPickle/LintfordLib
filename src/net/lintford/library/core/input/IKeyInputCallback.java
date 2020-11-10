package net.lintford.library.core.input;

public interface IKeyInputCallback {

	/** Return true to end key input capture */
	public void keyInput(int pKey, int pScanCode, int pAction, int pMods);

}
