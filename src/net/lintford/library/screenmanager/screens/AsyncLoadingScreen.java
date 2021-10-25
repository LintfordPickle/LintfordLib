package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public abstract class AsyncLoadingScreen extends Screen {

	public class ScreenManagerScreenLoader extends Thread {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private long mOffscreenBufferId;

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ScreenManagerScreenLoader(long pOffscreenBufferId) {
			super("Background Content Loader");
			mOffscreenBufferId = pOffscreenBufferId;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void run() {
			display.makeContextCurrent(mOffscreenBufferId);
			display.createGlCompatiblities();

			// And then continue loading on the main context
			int lCount = mScreensToLoad.length;
			for (int i = 0; i < lCount; i++) {
				final var lScreen = mScreensToLoad[i];

				if (lScreen != null && !lScreen.isinitialized()) {
					lScreen.initialize();
				}

				if (lScreen != null && !lScreen.isLoaded()) {
					lScreen.loadGLContent(mScreenManager.resources());
				}
			}

			Debug.debugManager().logger().i("ScreenManager", "Finished loading GL Content on the background thread");
		};
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String LOADING_BACKGROUND_TEXTURE_NAME = "LoadingScreen";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManagerScreenLoader mBackgroundThread;
	private boolean loadingThreadStarted;
	private ScreenManager mScreenManager;
	private Screen[] mScreensToLoad;
	protected final boolean mLoadingIsSlow;
	private DisplayManager display;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean hasLoadingStarted() {
		return loadingThreadStarted;
	}

	public boolean hasLoadingFinished() {
		return loadingThreadStarted && (mBackgroundThread != null && mBackgroundThread.isAlive() == false);
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	protected AsyncLoadingScreen(DisplayManager pDisplay, ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen[] pScreensToLoad) {
		super(pScreenManager);

		mScreenManager = pScreenManager;
		mScreensToLoad = pScreensToLoad;

		mLoadingIsSlow = pLoadingIsSlow;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(1));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(1));

		mIsPopup = true;
		display = pDisplay;

		loadingThreadStarted = false;
	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if ((mScreenState == ScreenState.Active) && (mScreenManager.screens().size() == 1)) {
			if (hasLoadingStarted() == false) {
				loadingThreadStarted = true;

				long lOffscreenBuffer = display.createSharedContext();
				mBackgroundThread = new ScreenManagerScreenLoader(lOffscreenBuffer);

				Debug.debugManager().logger().i("screenmanager", "Starting background thread");
				mBackgroundThread.start();

			} else if (hasLoadingFinished()) {
				exitScreen();
				display.destroySharedContext();

				// Once the background thread has finished,
				// take the new 'loaded' screens from it and add them to the screen manager
				// I *think* this avoids us needing to 'synchronized' anything
				final var lNumScreensToAdd = mScreensToLoad.length;
				for (int i = 0; i < lNumScreensToAdd; i++) {
					if (mScreensToLoad[i] != null) {
						mScreenManager.addScreen(mScreensToLoad[i]);

					}
				}

			}
		}
	}
}
