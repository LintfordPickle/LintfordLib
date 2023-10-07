package net.lintfordlib.screenmanager.screens;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.transitions.TransitionFadeIn;
import net.lintfordlib.screenmanager.transitions.TransitionFadeOut;

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

	public LoadingScreen(ScreenManager screenManager, boolean loadingIsSlow, Screen... screensToLoad) {
		super(screenManager);

		mScreenManager = screenManager;
		mScreensToLoad = screensToLoad;

		mLoadingIsSlow = loadingIsSlow;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(500));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(0));

		mFontUnit = screenManager.core().resources().fontManager().getCoreFont();

		mIsPopup = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		// Wait until all the other screens have exited
		if ((mScreenState == ScreenState.Active) && (mScreenManager.screens().size() == 1)) {
			final int lScreenCount = mScreensToLoad.length;
			for (int i = 0; i < lScreenCount; i++) {
				final var lScreen = mScreensToLoad[i];

				if (lScreen != null && !lScreen.isinitialized())
					lScreen.initialize();

				if (lScreen != null && !lScreen.isResourcesLoaded())
					lScreen.loadResources(mScreenManager.resources());

			}

			for (int i = 0; i < lScreenCount; i++) {
				final var lScreen = mScreensToLoad[i];
				if (lScreen != null) {
					mScreenManager.addScreen(lScreen);
				}
			}

			mScreenManager.removeScreen(this);
		}
	}

	@Override
	public void draw(LintfordCore core) {

		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		if (mLoadingIsSlow && mFontUnit != null) {
			final var lWindowWidth = core.config().display().windowWidth();
			final var lWindowHeight = core.config().display().windowHeight();
			final var lTextPadding = 5.0f;

			final String lLoadingText = "Loading ...";
			final var lTextWidth = mFontUnit.getStringWidth(lLoadingText);
			final var lTextHeight = mFontUnit.getStringHeight(lLoadingText);

			final float lTextPositionX = lWindowWidth * 0.5f - lTextWidth - lTextPadding;
			final float lTextPositionY = lWindowHeight * 0.5f - lTextHeight - lTextPadding;

			mFontUnit.begin(core.HUD());
			mFontUnit.drawText(lLoadingText, lTextPositionX, lTextPositionY, -0.1f, ColorConstants.WHITE, 1f, -1);
			mFontUnit.end();
		}
	}
}