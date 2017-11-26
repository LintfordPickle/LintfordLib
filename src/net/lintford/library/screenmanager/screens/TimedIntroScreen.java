package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.screenmanager.IMenuAction;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class TimedIntroScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatch mTextureBatch;
	private Texture mBackgroundTexture;

	private String mImageLocation;
	private float mShowImageTime;
	private float mShowImageTimer;

	private boolean mUserRequestSkip;
	private boolean mTimedActionPerformed;

	private IMenuAction mActionCallback;

	private boolean mStretchBackgroundToFit;

	// --------------------------------------
	// Properites
	// --------------------------------------

	public boolean stretchBackgroundToFit() {
		return mStretchBackgroundToFit;
	}

	public void stretchBackgroundToFit(boolean pNewValue) {
		mStretchBackgroundToFit = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TimedIntroScreen(ScreenManager pScreenManager, String pImageLocation) {
		this(pScreenManager, pImageLocation, 3.0f);

	}

	public TimedIntroScreen(ScreenManager pScreenManager, String pImageLocation, float pTimer) {
		super(pScreenManager);

		mImageLocation = pImageLocation;

		mShowImageTime = 0;
		mShowImageTimer = pTimer;

		mTextureBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		mBackgroundTexture = TextureManager.textureManager().loadTexture(mImageLocation, mImageLocation);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);

		if (!mTimedActionPerformed) {
			if (pInputState.isMouseTimedLeftClickAvailable()) {
				pInputState.setLeftMouseClickHandled();
				mUserRequestSkip = true;

			}

		}

	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!mTimedActionPerformed) {
			final float deltaTime = (float) pGameTime.elapseGameTimeMilli() / 1000f;

			mShowImageTime += deltaTime;

			if (mShowImageTime >= mShowImageTimer || mUserRequestSkip) {
				if (mActionCallback != null) {
					mActionCallback.TimerFinished(this);

				}

				mTimedActionPerformed = true;

			}

		}

	}

	@Override
	public void draw(RenderState pRenderState) {
		super.draw(pRenderState);

		float lLeft = -mBackgroundTexture.getTextureWidth() / 2;
		float lRight = mBackgroundTexture.getTextureWidth();
		float lTop = -mBackgroundTexture.getTextureHeight() / 2;
		float lBottom = mBackgroundTexture.getTextureHeight();

		if (mStretchBackgroundToFit) {
			lLeft = -pRenderState.displayConfig().windowSize().x / 2;
			lRight = pRenderState.displayConfig().windowSize().x;
			lTop = -pRenderState.displayConfig().windowSize().y / 2;
			lBottom = pRenderState.displayConfig().windowSize().y;
		}

		mTextureBatch.begin(pRenderState.HUDCamera());
		mTextureBatch.draw(0, 0, 800, 600, lLeft, lTop, -1f, lRight, lBottom, 1f, mA, mBackgroundTexture);
		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction pCallback) {
		mActionCallback = pCallback;

	}

}
