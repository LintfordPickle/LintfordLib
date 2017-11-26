package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LWJGLCore;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.MasterConfig;
import net.lintford.library.screenmanager.Screen.ScreenState;

public class ScreenManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// TODO: MAX_TOAST_AGE belongs in the toast package
	private static final float MAX_TOAST_AGE = 5000f; // ms

	public static final String SCREENMANAGER_TEXTURE_NAME = "ScreenManagerTexture";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LWJGLCore mLWJGLCore;
	private MasterConfig mMasterConfig;
	private ArrayList<Screen> mScreens;
	private ArrayList<Screen> mScreensToUpdate;
	// Needs to be passed in by some abstract class
	// private GameSettings mGameSettings;
	private ToolTip mToolTip;
	private ResourceManager mResourceManager;
	private List<ToastMessage> mToastMessages;
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

	public MasterConfig masterConfig() {
		return mMasterConfig;
	}

	public ToolTip toolTip() {
		return mToolTip;
	}

	public ArrayList<Screen> screens() {
		return mScreens;
	}

	public InputState inputState() {
		return mLWJGLCore.inputState();
	}

	public GameTime gameTime() {
		return mLWJGLCore.gameTime();
	}

	public HUD HUD() {
		return mLWJGLCore.HUD();
	}

	public String fontPathname() {
		return mFontPathname;
	}

	public LWJGLCore core() {
		return mLWJGLCore;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScreenManager(LWJGLCore pCore) {
		mLWJGLCore = pCore;
		mMasterConfig = pCore.configuration();
		// mGameSettings = new GameSettings();

		mToastMessages = new ArrayList<>();
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
		TextureManager.textureManager().loadTexture(SCREENMANAGER_TEXTURE_NAME, "/res/textures/core/screenmanager.png");

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreens.get(i).unloadGLContent();

		}

		mToolTip.unloadGLContent();

		mIsLoaded = false;

	}

	public void handleInput(final InputState pInputState) {
		if (mScreens == null || mScreens.size() == 0)
			return;

		// Maybe we are missing out on the 'pOtherScreenHasFocus'
		Screen lScreen = mScreens.get(mScreens.size() - 1);
		if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
			lScreen.handleInput(mLWJGLCore.gameTime(), mLWJGLCore.inputState(), true, true);

		}

	}

	public void update(GameTime pGameTime) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		mScreensToUpdate.clear();

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreensToUpdate.add(mScreens.get(i));

		}

		if (mToastMessages.size() > 0) {
			mToastMessages.get(0).update(pGameTime);
			if (mToastMessages.get(0).timeLeft() <= 0) {
				// FIXME: Reuse the toast messages!
				mToastMessages.get(0).unloadGLContent();
				mToastMessages.remove(0);
			}
		}

		boolean lOtherScreenHasFocus = false;
		boolean lCoveredByOtherScreen = false;

		while (mScreensToUpdate.size() > 0) {
			Screen lScreen = mScreensToUpdate.get(mScreensToUpdate.size() - 1);

			mScreensToUpdate.remove(mScreensToUpdate.size() - 1);

			// update the screen positioning (recursive, children too).
			lScreen.updateStructure();

			// Update the screen
			lScreen.update(pGameTime, lOtherScreenHasFocus, lCoveredByOtherScreen);

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
				lOtherScreenHasFocus = true;

				if (!lScreen.isPopup()) {
					lCoveredByOtherScreen = true;

				}

			}

		}

	}

	public void draw(RenderState pRenderState) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			if (mScreens.get(i).screenState() == ScreenState.Hidden && !mScreens.get(i).showInBackground())
				continue;

			mScreens.get(i).draw(pRenderState);

		}

		if (mToastMessages.size() > 0) {
			mToastMessages.get(0).draw();
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setScreenManagerTexture(String pTexturePath) {
		if (pTexturePath == null || pTexturePath.length() == 0)
			return;

		TextureManager.textureManager().loadTexture(SCREENMANAGER_TEXTURE_NAME, pTexturePath, GL11.GL_NEAREST, true);

	}

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

			pScreen.updateStructure();

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

	public void displayToast(String pTitle, String pMessage, float pTimeToDisplay) {
		// FIXME: GC Add a toast message and pool these resources!
		ToastMessage lToast = new ToastMessage(this);
		lToast.loadGLContent(mResourceManager);
		if (pTimeToDisplay > MAX_TOAST_AGE)
			pTimeToDisplay = MAX_TOAST_AGE;

		lToast.setupToast(pTitle, pMessage, pTimeToDisplay);

		mToastMessages.add(lToast);
	}
}
