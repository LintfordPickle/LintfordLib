package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.options.DisplayConfig;
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
	// Properties
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
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = TextureManager.textureManager().loadTexture(mImageLocation, mImageLocation);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (!mTimedActionPerformed) {
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				pCore.input().setLeftMouseClickHandled();
				mUserRequestSkip = true;

			}

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!mTimedActionPerformed) {
			final float deltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;

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
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		float lLeft = -mBackgroundTexture.getTextureWidth() / 2;
		float lRight = mBackgroundTexture.getTextureWidth();
		float lTop = -mBackgroundTexture.getTextureHeight() / 2;
		float lBottom = mBackgroundTexture.getTextureHeight();

		if (mStretchBackgroundToFit) {
			DisplayConfig lDisplay = pCore.config().display();
			lLeft = -lDisplay.windowSize().x / 2;
			lRight = lDisplay.windowSize().x;
			lTop = -lDisplay.windowSize().y / 2;
			lBottom = lDisplay.windowSize().y;
		}

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mBackgroundTexture, 0, 0, 800, 600, lLeft, lTop, lRight, lBottom, -1f, 1f, 1f, 1f, mA);
		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction pCallback) {
		mActionCallback = pCallback;

	}

}
