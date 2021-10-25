package net.lintford.library.screenmanager.screens;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public class LoadingScreen extends Screen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String LOADING_BACKGROUND_TEXTURE_NAME = "LoadingScreen";
	public static final String LOADING_TEXT_TEXTURE_NAME = "LoadingTextScreen";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected Screen[] mScreensToLoad;
	protected final boolean mLoadingIsSlow;
	protected FontUnit mFontUnit;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public LoadingScreen(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen... pScreensToLoad) {
		super(pScreenManager);

		mScreenManager = pScreenManager;
		mScreensToLoad = pScreensToLoad;

		mLoadingIsSlow = pLoadingIsSlow;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(500));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(0));

		mFontUnit = pScreenManager.core().resources().fontManager().getCoreFont();

		mIsPopup = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		// Wait until all the other screens have exited
		if ((mScreenState == ScreenState.Active) && (mScreenManager.screens().size() == 1)) {

			// And then continue loading on the main context
			int lCount = mScreensToLoad.length;
			for (int i = 0; i < lCount; i++) {
				Screen lScreen = mScreensToLoad[i];

				if (lScreen != null && !lScreen.isinitialized()) {
					lScreen.initialize();

				}

				if (lScreen != null && !lScreen.isLoaded()) {
					lScreen.loadGLContent(mScreenManager.resources());

				}

			}

			// screens have been loaded on the other thread, so now lets add them to
			// the screen manager
			lCount = mScreensToLoad.length;
			for (int i = 0; i < lCount; i++) {
				Screen lScreen = mScreensToLoad[i];
				if (lScreen != null) {
					mScreenManager.addScreen(lScreen);

				}

			}

			mScreenManager.removeScreen(this);
		}
	}

	@Override
	public void draw(LintfordCore pCore) {

		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		if (mLoadingIsSlow && mFontUnit != null) {
			final var lWindowWidth = pCore.config().display().windowWidth();
			final var lWindowHeight = pCore.config().display().windowHeight();
			final var lTextPadding = 5.0f;

			final String lLoadingText = "Loading ...";
			final var lTextWidth = mFontUnit.getStringWidth(lLoadingText);
			final var lTextHeight = mFontUnit.getStringHeight(lLoadingText);

			final float lTextPositionX = lWindowWidth * 0.5f - lTextWidth - lTextPadding;
			final float lTextPositionY = lWindowHeight * 0.5f - lTextHeight - lTextPadding;

			mFontUnit.begin(pCore.HUD());
			mFontUnit.drawText(lLoadingText, lTextPositionX, lTextPositionY, -0.1f, ColorConstants.WHITE, 1f, -1);
			mFontUnit.end();
		}
	}

}
