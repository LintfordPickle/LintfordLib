package net.lintford.library.screenmanager.screens;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.Screen.ScreenState;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public class LoadingScreen extends Screen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float BLINK_TIMER = 500;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManager mScreenManager;
	private Screen[] mScreensToLoad;
	private boolean mDisplayLoadingText;
	private float mBlinkTimer;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	private LoadingScreen(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen[] pScreensToLoad) {
		super(pScreenManager);

		mScreenManager = pScreenManager;
		mScreensToLoad = pScreensToLoad;

		mDisplayLoadingText = true;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(500));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(500));

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public static void load(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen... pScreensToLoad) {

		// transitiion off ALL current screens
		List<Screen> temp = new ArrayList<>();
		temp.addAll(pScreenManager.screens());

		int lScreenCount = temp.size();
		for (int i = 0; i < lScreenCount; i++) {
			temp.get(i).exitScreen();

		}

		temp.clear();
		temp = null;

		// create and activate the loading screen
		LoadingScreen lLoadingScreen = new LoadingScreen(pScreenManager, pLoadingIsSlow, pScreensToLoad);
		pScreenManager.addScreen(lLoadingScreen);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {

	}

	@Override
	public void unloadGLContent() {

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		final double lDeltaTime = pCore.time().elapseGameTimeMilli() / 1000f;
		
		mBlinkTimer += lDeltaTime;

		if (mBlinkTimer > BLINK_TIMER) {
			mBlinkTimer = 0;
			mDisplayLoadingText = !mDisplayLoadingText;
		}

		if ((mScreenState == ScreenState.Active) && (mScreenManager.screens().size() == 1)) {

			// And then continue loading on the main context
			int lCount = mScreensToLoad.length;
			for (int i = 0; i < lCount; i++) {
				Screen lScreen = mScreensToLoad[i];

				if (lScreen != null && !lScreen.isInitialised()) {
					lScreen.initialise();

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
		if (mDisplayLoadingText) {

			// TODO(John): Implement the pixel font here

			/*
				FontSpriteBatch lSpriteBatch = BitmapFontManager.bitmapFontManager().getBitmapFont("Main").mFontSpriteBatch;
				lSpriteBatch.begin(mScreenManager.HUD());
				final String lLoadingString = "Loading ...";
				lSpriteBatch.drawText(lLoadingString, 0, 0, 0f);
				lSpriteBatch.end();
			*/
		}
	}

	
}
