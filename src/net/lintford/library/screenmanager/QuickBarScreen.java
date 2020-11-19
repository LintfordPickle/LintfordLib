package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;

public abstract class QuickBarScreen extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float lQuickBarHeight = 90f;

	/**
	 * Space around the content and between the border
	 * */
	public static final float lQuickBarPadding = 8.f;

	/**
	 * Space on the outside of the border and determines proximity to other elements.
	 * */
	public static float lQuickBarMargin = 16.f;

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
		mBlockInputInBackground = false;

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