package net.lintford.library.screenmanager;

import java.util.ArrayList;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.screenmanager.Screen.ScreenState;
import net.lintford.library.screenmanager.toast.ToastManager;

public class ScreenManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SCREENMANAGER_TEXTURE_NAME = "ScreenManagerTexture";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mLWJGLCore;
	private ArrayList<Screen> mScreens;
	private ArrayList<Screen> mScreensToUpdate;
	// Needs to be passed in by some abstract class
	// private GameSettings mGameSettings;
	private ToolTip mToolTip;
	private ResourceManager mResourceManager;
	private ToastManager mToastManager;
	private String mFontPathname;

	private boolean mIsInitialised;
	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// public GameSettings gameSettings() {
	// return mGameSettings;
	// }

	public ResourceManager resources() {
		return mResourceManager;
	}

	public ToolTip toolTip() {
		return mToolTip;
	}

	public ArrayList<Screen> screens() {
		return mScreens;
	}

	public String fontPathname() {
		return mFontPathname;
	}

	public LintfordCore core() {
		return mLWJGLCore;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScreenManager(LintfordCore pCore) {
		mLWJGLCore = pCore;
		// mGameSettings = new GameSettings();

		mToastManager = new ToastManager();
		mScreens = new ArrayList<Screen>();
		mScreensToUpdate = new ArrayList<Screen>();

		mToolTip = new ToolTip(this);

		mIsInitialised = false;
		mIsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise(String pFontPathname) {
		mFontPathname = pFontPathname;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).initialise();
		}

		mIsInitialised = true;

	}

	public void loadGLContent(final ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).loadGLContent(pResourceManager);
		}

		mToolTip.loadGLContent(pResourceManager);
		mToastManager.loadGLContent(pResourceManager);
		TextureManager.textureManager().loadTexture(SCREENMANAGER_TEXTURE_NAME, "/res/textures/core/core_ui.png");

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreens.get(i).unloadGLContent();

		}

		mToolTip.unloadGLContent();
		mToastManager.unloadGLContent();

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		if (mScreens == null || mScreens.size() == 0)
			return;

		// Maybe we are missing out on the 'pOtherScreenHasFocus'
		Screen lScreen = mScreens.get(mScreens.size() - 1);
		if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
			lScreen.handleInput(pCore, true, true);

		}

	}

	public void update(LintfordCore pCore) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		mScreensToUpdate.clear();

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreensToUpdate.add(mScreens.get(i));

		}

		mToastManager.update(pCore);

		boolean lOtherScreenHasFocus = false;
		boolean lCoveredByOtherScreen = false;

		while (mScreensToUpdate.size() > 0) {
			Screen lScreen = mScreensToUpdate.get(mScreensToUpdate.size() - 1);

			mScreensToUpdate.remove(mScreensToUpdate.size() - 1);

			// update the screen positioning (recursive, children too).
			lScreen.updateStructure(pCore);

			// Update the screen
			lScreen.update(pCore, lOtherScreenHasFocus, lCoveredByOtherScreen);

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
				lOtherScreenHasFocus = true;

				if (!lScreen.isPopup()) {
					lCoveredByOtherScreen = true;

				}

			}

		}

	}

	public void draw(LintfordCore pCore) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			if (mScreens.get(i).screenState() == ScreenState.Hidden && !mScreens.get(i).showInBackground())
				continue;

			mScreens.get(i).draw(pCore);

		}

		mToastManager.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addScreen(Screen pScreen) {
		if (!pScreen.isLoaded()) {
			pScreen.screenManager(this);
			pScreen.isExiting(false);

			if (mIsInitialised && !pScreen.isInitialised()) {// screen manager already initialized? then load this screen manually
				pScreen.initialise();
			}

			if (mIsLoaded) { // screen manager already loaded? then load this screen manually
				pScreen.loadGLContent(mResourceManager);
			}

		}

		if (mScreens.size() > 0) {
			mScreens.get(mScreens.size() - 1).onLostFocus();
		}

		mScreens.add(pScreen);

	}

	public void removeScreen(Screen pScreen) {

		if (mIsInitialised) {
			pScreen.unloadGLContent();
		}

		if (mScreens.contains(pScreen)) {
			// if this screen was the top screen, then the screen below gains focus
			if (mScreens.size() > 1 && mScreens.get(mScreens.size() - 1) == pScreen) {
				mScreens.get(mScreens.size() - 2).onGainedFocus();
			}

			mScreens.remove(pScreen);
		}

		if (mScreensToUpdate.contains(pScreen))
			mScreensToUpdate.remove(pScreen);

	}

	public void fadeBackBufferToBlack(float pAlpha) {

		// TODO: Render a full screen black quad ...

	}

	/** When called, notifies the LWJGLCore instance to close application. */
	public void exitGame() {
		mLWJGLCore.closeApp();

	}

}
