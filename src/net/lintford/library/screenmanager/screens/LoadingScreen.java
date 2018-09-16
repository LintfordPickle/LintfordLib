package net.lintford.library.screenmanager.screens;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public class LoadingScreen extends Screen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float BLINK_TIMER = 500;

	public static final String LOADING_BACKGROUND_TEXTURE_NAME = "LoadingScreen";
	public static final String LOADING_TEXT_TEXTURE_NAME = "LoadingTextScreen";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManager mScreenManager;
	private Screen[] mScreensToLoad;
	private boolean mDisplayLoadingText;
	private final boolean mLoadingIsSlow;
	private float mBlinkTimer;

	private TextureBatch mTextureBatch;
	private Texture mLoadingTexture;
	private Texture mLoadingTextTexture;

	// --------------------------------------
	// Constructors
	// --------------------------------------

	private LoadingScreen(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen[] pScreensToLoad) {
		super(pScreenManager);

		mScreenManager = pScreenManager;
		mScreensToLoad = pScreensToLoad;

		mLoadingIsSlow = pLoadingIsSlow;

		mDisplayLoadingText = true;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(500));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(500));

		mIsPopup = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public static void load(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen... pScreensToLoad) {

		// transitiion off ALL current screens
		List<Screen> lScreenList = new ArrayList<>();
		lScreenList.addAll(pScreenManager.screens());

		int lScreenCount = lScreenList.size();
		for (int i = 0; i < lScreenCount; i++) {
			if (!lScreenList.get(i).isExiting())
				lScreenList.get(i).exitScreen();

		}

		lScreenList.clear();
		lScreenList = null;

		// create and activate the loading screen
		LoadingScreen lLoadingScreen = new LoadingScreen(pScreenManager, pLoadingIsSlow, pScreensToLoad);
		pScreenManager.addScreen(lLoadingScreen);

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch = new TextureBatch();
		mTextureBatch.loadGLContent(pResourceManager);

		mLoadingTexture = TextureManager.textureManager().loadTexture(LOADING_BACKGROUND_TEXTURE_NAME, "/res/textures/core/loadingScreen.png");
		mLoadingTextTexture = TextureManager.textureManager().loadTexture(LOADING_TEXT_TEXTURE_NAME, "/res/textures/core/loadingScreenText.png");

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		if (mTextureBatch != null) {
			mTextureBatch.unloadGLContent();

		}

	}

	@Override
	public void updateStructureDimensions(LintfordCore pCore) {

	}

	@Override
	public void updateStructurePositions(LintfordCore pCore) {

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		final double lDeltaTime = pCore.time().elapseGameTimeMilli();

		mBlinkTimer += lDeltaTime;

		if (mBlinkTimer > BLINK_TIMER) {
			mBlinkTimer = 0;
			mDisplayLoadingText = !mDisplayLoadingText;
		}

		// Wait until all the other screens have exited
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

		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		if (mLoadingIsSlow) {
			final float textureWidth = mLoadingTexture.getTextureWidth();
			final float textureHeight = mLoadingTexture.getTextureHeight();

			final float textTextureWidth = mLoadingTextTexture.getTextureWidth();
			final float textTextureHeight = mLoadingTextTexture.getTextureHeight();

			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(mLoadingTexture, 0, 0, textureWidth, textureHeight, -textureWidth / 2, -textureHeight / 2, textureWidth, textureHeight, -0.1f, 1f, 1f, 1f, 1f);
			if (mDisplayLoadingText)
				mTextureBatch.draw(mLoadingTextTexture, 0, 0, textTextureWidth, textTextureHeight, textureWidth / 2 - textTextureWidth - 10, textureHeight / 2 - textTextureHeight - 10, textTextureWidth,
						textTextureHeight, -0.1f, 1f, 1f, 1f, 1f);
			mTextureBatch.end();

		}

	}

}
