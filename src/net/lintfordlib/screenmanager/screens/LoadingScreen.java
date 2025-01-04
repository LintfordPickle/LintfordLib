package net.lintfordlib.screenmanager.screens;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
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
		if ((mScreenState == ScreenState.ACTIVE) && (mScreenManager.screens().size() == 1)) {

			final var lCore = screenManager.core();

			final var lScreenCount = mScreensToLoad.length;
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

					if (i < lScreenCount - 1)
						lScreen.forceScreenState(ScreenState.HIDDEN);
					else
						lScreen.forceScreenState(ScreenState.ACTIVE);

					mScreenManager.addScreenToStack(lScreen);

					// All screens have the ability to override either the game resolution and/or the ui resolution, to use something other than was specified in the GameInfo.
					// So that the transition between soft-resolutions is not jarring, we do this now at the end of the loading screen.
					final var lConfig = lCore.config();
					if (lScreen.overrideUiStretch()) {
						lConfig.display().stretchUiScreen(mStretchUiResolution);
					} else if (!lScreen.showBackgroundScreens()) {
						lConfig.display().restoreUiStretch();
					}

					if (lScreen.overrideGameStretch()) {
						lConfig.display().stretchGameScreen(mStretchGameResolution);
					} else if (!lScreen.showBackgroundScreens()) {
						lConfig.display().restoreGameStretch();
					}
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
			mFontUnit.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			mFontUnit.drawText(lLoadingText, lTextPositionX, lTextPositionY, -0.1f, 1f, -1);
			mFontUnit.end();
		}
	}
}