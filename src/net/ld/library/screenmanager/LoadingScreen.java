package net.ld.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.textures.Texture;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.core.time.TimeSpan;

public class LoadingScreen extends Screen {

	// =============================================
	// Variables
	// =============================================

	private Screen[] mScreensToLoad;
	private Texture mUITexture;

	private int mPartsLoaded;
	private SpriteBatch mTextureSpriteBatch;

	private boolean mLoadingStarted;
	private List<GameLoaderPart> mGameLoaderParts;
	private ResourceManager mResourceManager;

	// =============================================
	// Constructors
	// =============================================

	private LoadingScreen(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen[] pScreensToLoad) {
		super(pScreenManager);

		mScreensToLoad = pScreensToLoad;

		mTextureSpriteBatch = new SpriteBatch();
		mGameLoaderParts = new ArrayList<>();
		mLoadingStarted = false;
		mPartsLoaded = 0;

		mTransitionOnTime = new TimeSpan(0.5f);

	}

	// =============================================
	// Core-Methods
	// =============================================

	public static void load(ScreenManager pScreenManager, boolean pLoadingIsSlow, Screen... pScreensToLoad) {

		int lScreenCount = pScreenManager.screens().size();
		for (int i = 0; i < lScreenCount; i++) {
			pScreenManager.screens().get(i).exitScreen();

		}

		// create and activate the loading screen
		LoadingScreen lLoadingScreen = new LoadingScreen(pScreenManager, pLoadingIsSlow, pScreensToLoad);
		pScreenManager.addScreen(lLoadingScreen);

	}

	@Override
	public void loadContent(ResourceManager pResourceManager) {

		mResourceManager = pResourceManager;
		mTextureSpriteBatch.loadContent(pResourceManager);
		mUITexture = TextureManager.textureManager().loadTexture("UITexture", "bin/res/textures/ui.png");

	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if ((mScreenState == ScreenState.Active) && (mScreenManager.screens().size() == 1)) {

			if (!mLoadingStarted) {

				int lCount = mScreensToLoad.length;
				for (int i = 0; i < lCount; i++) {
					Screen lScreen = mScreensToLoad[i];

					if (lScreen != null) {
						lScreen.initialise();

						if (!lScreen.isLoaded()) {
							lScreen.loadContent(mScreenManager.resources());

							mGameLoaderParts.addAll(lScreen.mGameLoadableParts);

						}
					}
				}

				mLoadingStarted = true;
			} else {

				// Load the next part
				mGameLoaderParts.get(mPartsLoaded).loadContent(mResourceManager);
				mPartsLoaded++;
			}

			// Once we have loaded all the parts, then continue..
			if (mPartsLoaded >= mGameLoaderParts.size()) {
				// all screens havae been loaded, so now lets add them to the screen manager
				int lCount = mScreensToLoad.length;
				for (int i = 0; i < lCount; i++) {

					Screen lScreen = mScreensToLoad[i];
					if (lScreen != null) {

						mScreenManager.addScreen(lScreen);
					}

				}

				mScreenManager.removeScreen(this);
			}

		}
	}

	@Override
	public void draw(RenderState pRenderState) {
		if (!mLoadingStarted)
			return;

		final float lScreenWidthHalf = pRenderState.displayConfig().windowWidth() * 0.5f;
		final float lScreenHeightHalf = pRenderState.displayConfig().windowHeight() * 0.5f;

		final float lBarWidth = 480f / mGameLoaderParts.size() * mPartsLoaded;

		mTextureSpriteBatch.begin(mScreenManager.HUD());
		final String lLoadingString = mGameLoaderParts.get(mPartsLoaded).getTitle();
		mTextureSpriteBatch.draw(lLoadingString, lScreenWidthHalf - (lLoadingString.length() * 32f * .6f) * .5f, lScreenHeightHalf + 54, -0.5f, 0.6f, TextureManager.textureManager().getTexture("Font"));
		mTextureSpriteBatch.draw(0, 0, 480, 48, lScreenWidthHalf - 480f * .5f, lScreenHeightHalf, 0.5f, 480, 48, 1f, mUITexture);
		mTextureSpriteBatch.draw(0, 48, 480, 48, lScreenWidthHalf - lBarWidth * .5f, lScreenHeightHalf, 0.5f, lBarWidth, 48, 1f, mUITexture);
		mTextureSpriteBatch.end();

	}

}
