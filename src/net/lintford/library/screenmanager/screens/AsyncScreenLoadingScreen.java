package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

/** Initializes and loads the GL content for a list of screens in a background thread. */
public abstract class AsyncScreenLoadingScreen extends Screen {

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
			mDisplayManager.makeContextCurrent(mOffscreenBufferId);
			mDisplayManager.createGlCompatiblities();

			// And then continue loading on the main context
			int lCount = screensToLoad.length;
			for (int i = 0; i < lCount; i++) {
				final var lScreen = screensToLoad[i];

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
	protected Screen[] screensToLoad;
	private final DisplayManager mDisplayManager;
	protected boolean mActivateLoadedScreens;
	private long mOffscreenBufferIndex;

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

	protected AsyncScreenLoadingScreen(ScreenManager pScreenManager) {
		this(pScreenManager, new RendererManager(pScreenManager.core(), BaseEntity.getEntityNumber()));
	}

	protected AsyncScreenLoadingScreen(ScreenManager pScreenManager, RendererManager pRendererManager) {
		super(pScreenManager, pRendererManager);

		mScreenManager = pScreenManager;
		mDisplayManager = mScreenManager.core().config().display();

		mTransitionOn = new TransitionFadeIn(new TimeSpan(1));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(1));

		mIsPopup = true;

		mOffscreenBufferIndex = -1;
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

				mOffscreenBufferIndex = mDisplayManager.createSharedContext();
				mBackgroundThread = new ScreenManagerScreenLoader(mOffscreenBufferIndex);

				Debug.debugManager().logger().i("ScreenManager", "Starting background thread");
				mBackgroundThread.start();

			} else if (mOffscreenBufferIndex > 0 && hasLoadingFinished()) {
				mOffscreenBufferIndex = -1;
				mDisplayManager.destroySharedContext();

				onAfterAssetsLoaded();

			} else {
				if (mActivateLoadedScreens && mIsExiting == false) {
					exitScreen();

					// Once the background thread has finished,
					// take the new 'loaded' screens from it and add them to the screen manager
					// I *think* this avoids us needing to 'synchronized' anything
					final var lNumScreensToAdd = screensToLoad.length;
					for (int i = 0; i < lNumScreensToAdd; i++) {
						if (screensToLoad[i] != null) {
							mScreenManager.addScreen(screensToLoad[i]);
						}
					}
				}
			}
		}
	}

	protected abstract void onAfterAssetsLoaded();
}
