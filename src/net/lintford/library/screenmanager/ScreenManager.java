package net.lintford.library.screenmanager;

import java.util.ArrayList;

import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.fonts.FontManager;
import net.lintford.library.screenmanager.Screen.ScreenState;
import net.lintford.library.screenmanager.toast.ToastManager;

public class ScreenManager {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mLWJGLCore;
	private ArrayList<Screen> mScreens;
	private ArrayList<Screen> mScreensToUpdate;
	private ToolTip mToolTip;
	private ResourceManager mResourceManager;
	private ToastManager mToastManager;
	private String mFontPathname;
	private boolean mIsInitialised;
	private boolean mIsLoaded;
	private int mScreenCounter;
	private UIHUDStructureController mUIHUDController;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public UIHUDStructureController UIHUDController() {
		return mUIHUDController;
	}

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

	/** Returns a new ID for a screen. */
	public int getNewUUID() {
		return mScreenCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScreenManager(LintfordCore pCore) {
		mLWJGLCore = pCore;
		mScreenCounter = 100;
		// mGameSettings = new GameSettings();

		mToastManager = new ToastManager();
		mScreens = new ArrayList<Screen>();
		mScreensToUpdate = new ArrayList<Screen>();

		// This can and probably should be overriden with a game specific font
		mFontPathname = FontManager.SYSTEM_FONT_PATH;

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

		mUIHUDController = (UIHUDStructureController) mLWJGLCore.controllerManager().getControllerByNameRequired(UIHUDStructureController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

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

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Finished loadingGLContent");
		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreens.get(i).unloadGLContent();

		}

		mToolTip.unloadGLContent();
		mToastManager.unloadGLContent();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Finished ScreenManager.unloadGLContent");
		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore) {
		if (mScreens == null || mScreens.size() == 0)
			return;

		final int lScreenCount = mScreens.size() - 1;
		for (int i = lScreenCount; i >= 0; i--) {
			Screen lScreen = mScreens.get(i);

			// Only allow keyboard and mouse input if we are on the top screen

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
				lScreen.handleInput(pCore, true, i == lScreenCount);

			}

		}

	}

	public void update(LintfordCore pCore) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		boolean lOtherScreenHasFocus = false;
		boolean lCoveredByOtherScreen = false;

		mScreensToUpdate.clear();

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreensToUpdate.add(mScreens.get(i));

		}

		while (mScreensToUpdate.size() > 0) {
			Screen lScreen = mScreensToUpdate.get(mScreensToUpdate.size() - 1);

			mScreensToUpdate.remove(mScreensToUpdate.size() - 1);

			// Update the screen
			lScreen.update(pCore, lOtherScreenHasFocus, lCoveredByOtherScreen);

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
				lOtherScreenHasFocus = true;

				if (!lScreen.isPopup()) {
					lCoveredByOtherScreen = true;

				}

			}

		}

		mToastManager.update(pCore);

	}

	public void draw(LintfordCore pCore) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			if (mScreens.get(i).screenState() == ScreenState.Hidden && !mScreens.get(i).showInBackground())
				continue;

			mScreens.get(i).draw(pCore);

			GLDebug.checkGLErrorsException(getClass().getSimpleName());

		}

//		if (mToolTip.active)
//			mToolTip.draw(pCore);

		mToastManager.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addScreen(Screen pScreen) {
		if (pScreen.singletonScreen()) {
			final int lScreenCount = mScreens.size();
			for (int i = 0; i < lScreenCount; i++) {
				Screen lScreen = mScreens.get(i);
				if (lScreen.getClass().getSimpleName().equals(pScreen.getClass().getSimpleName())) {
					Debug.debugManager().logger().e(this.getClass().getSimpleName(), "Cannot add second SingletonScreen instance: " + pScreen.getClass().getSimpleName());
					return;

				}

			}

		}

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

		int lInsertIndex = 0;
		if (mScreens.size() > 0) {
			mScreens.get(mScreens.size() - 1).onLostFocus();

			lInsertIndex = mScreens.size();
		}
		for (int i = mScreens.size() - 1; i > 0; i--) {
			lInsertIndex = i + 1;
			if (!mScreens.get(i).alwaysOnTop()) {
				break;
			}
		}

		mScreens.add(lInsertIndex, pScreen);

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Added screen ", pScreen.getClass().getSimpleName()));

	}

	public void removeScreen(Screen pScreen) {

		if (mIsInitialised) {
			pScreen.unloadGLContent();

		}

		pScreen.onScreenRemovedFromScreenManager();

		if (mScreens.contains(pScreen)) {
			// if this screen was the top screen, then the screen below gains focus
			if (mScreens.size() > 1 && mScreens.get(mScreens.size() - 1) == pScreen) {
				mScreens.get(mScreens.size() - 2).onGainedFocus();
			}

			mScreens.remove(pScreen);
		}

		if (mScreensToUpdate.contains(pScreen))
			mScreensToUpdate.remove(pScreen);

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Removed screen ", pScreen.getClass().getSimpleName()));

	}

	public void fadeBackBufferToBlack(float pAlpha) {

		// TODO: Render a full screen black quad ...

	}

	public void exitGame() {
		mLWJGLCore.closeApp();

	}

}
