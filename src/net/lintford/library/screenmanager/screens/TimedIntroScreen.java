package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.screenmanager.IMenuAction;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class TimedIntroScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatchPCT mTextureBatch;
	private Texture mBackgroundTexture;

	private String mImageLocation;
	private float mShowImageTime;
	private float mShowImageTimer;

	private boolean mUserRequestSkip;
	private boolean mTimedActionPerformed;

	private Rectangle mSrcTextureRect;

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

	public void setTextureSrcRectangle(float pX, float pY, float pW, float pH) {
		mSrcTextureRect.set(pX, pY, pW, pH);

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

		mTextureBatch = new TextureBatchPCT();
		mSrcTextureRect = new Rectangle(0, 0, 800, 600);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundTexture = pResourceManager.textureManager().loadTexture(mImageLocation, mImageLocation, entityGroupID());

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
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				mUserRequestSkip = true;

			}

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!mTimedActionPerformed) {
			final float deltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;

			mShowImageTime += deltaTime;

			if (mShowImageTime >= mShowImageTimer || mUserRequestSkip) {
				if (mActionCallback != null) {
					mActionCallback.TimerFinished(this);

				}

				exitScreen();
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
			DisplayManager lDisplay = pCore.config().display();
			lLeft = -lDisplay.windowWidth() / 2;
			lRight = lDisplay.windowWidth();
			lTop = -lDisplay.windowHeight() / 2;
			lBottom = lDisplay.windowHeight();
		}

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mBackgroundTexture, mSrcTextureRect, lLeft, lTop, lRight, lBottom, -1f, 1f, 1f, 1f, mA);
		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction pCallback) {
		mActionCallback = pCallback;

	}

}
