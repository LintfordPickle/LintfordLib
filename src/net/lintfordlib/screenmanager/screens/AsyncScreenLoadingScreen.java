package net.lintfordlib.screenmanager.screens;

import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.renderers.SimpleRendererManager;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.transitions.TransitionFadeIn;
import net.lintfordlib.screenmanager.transitions.TransitionFadeOut;

/** Initializes and loads the GL content for a list of screens in a background thread. */
public abstract class AsyncScreenLoadingScreen extends Screen {

	public class ScreenManagerScreenLoader extends Thread {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ScreenManagerScreenLoader() {
			super("Background Content Loader");
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void run() {
			mDisplayManager.makeOffscreenContextCurrentOnThread();

			final int lScreenCount = screensToLoad.length;
			for (int i = 0; i < lScreenCount; i++) {
				final var lScreen = screensToLoad[i];

				if (lScreen != null && !lScreen.isinitialized())
					lScreen.initialize();

				if (lScreen != null && !lScreen.isResourcesLoaded())
					lScreen.loadResources(screenManager.resources());

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
	protected Screen[] screensToLoad;
	private final DisplayManager mDisplayManager;
	protected boolean mActivateLoadedScreens;

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

	protected AsyncScreenLoadingScreen(ScreenManager screenManager) {
		this(screenManager, new SimpleRendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber()));
	}

	protected AsyncScreenLoadingScreen(ScreenManager screenManager, SimpleRendererManager rendererManager) {
		super(screenManager, rendererManager);

		mDisplayManager = screenManager.core().config().display();

		mTransitionOn = new TransitionFadeIn(new TimeSpan(1));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(1));

		mIsPopup = true;

		loadingThreadStarted = false;
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if ((mScreenState == ScreenState.ACTIVE) && (screenManager.screens().size() == 1)) {
			if (!hasLoadingStarted()) {
				loadingThreadStarted = true;

				mBackgroundThread = new ScreenManagerScreenLoader();

				Debug.debugManager().logger().i(getClass().getSimpleName(), "Starting background thread");
				mBackgroundThread.start();

			} else if (!mActivateLoadedScreens && hasLoadingFinished()) {
				onAfterAssetsLoaded();

			} else {
				if (mActivateLoadedScreens && !isExiting()) {
					exitScreen();

					final var lNumScreensToAdd = screensToLoad.length;
					for (int i = 0; i < lNumScreensToAdd; i++) {
						if (screensToLoad[i] != null) {
							screenManager.addScreen(screensToLoad[i]);
						}
					}
				}
			}
		}
	}

	protected abstract void onAfterAssetsLoaded();
}
