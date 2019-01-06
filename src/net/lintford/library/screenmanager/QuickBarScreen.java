package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;

public abstract class QuickBarScreen extends MenuScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Screen mParentScreen;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public QuickBarScreen(ScreenManager pScreenManager, Screen pParentScreen) {
		super(pScreenManager, "");

		mParentScreen = pParentScreen;

		mIsPopup = true;
		mAlwaysOnTop = true;
		mESCBackEnabled = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (mParentScreen == null) {
			exitScreen();

		}

	}

}