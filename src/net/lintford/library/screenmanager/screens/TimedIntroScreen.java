package net.lintford.library.screenmanager.screens;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.batching.TextureBatchPCT;
import net.lintford.library.core.graphics.textures.Texture;
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

	public void stretchBackgroundToFit(boolean newValue) {
		mStretchBackgroundToFit = newValue;
	}

	public void setTextureSrcRectangle(float x, float y, float w, float h) {
		mSrcTextureRect.set(x, y, w, h);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public TimedIntroScreen(ScreenManager screenManager, String imageLocation) {
		this(screenManager, imageLocation, 3.0f);
	}

	public TimedIntroScreen(ScreenManager screenManager, String imageLocation, float timer) {
		super(screenManager);

		mImageLocation = imageLocation;

		mShowImageTime = 0;
		mShowImageTimer = timer;

		mTextureBatch = new TextureBatchPCT();
		mSrcTextureRect = new Rectangle(0, 0, 800, 600);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mBackgroundTexture = resourceManager.textureManager().loadTexture(mImageLocation, mImageLocation, entityGroupUid());

		mTextureBatch.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mTextureBatch.unloadResources();
	}

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);

		if (!mTimedActionPerformed) {
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode()))
				mUserRequestSkip = true;

		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (!mTimedActionPerformed) {
			final float deltaTime = (float) core.appTime().elapsedTimeMilli() / 1000f;

			mShowImageTime += deltaTime;

			if (mShowImageTime >= mShowImageTimer || mUserRequestSkip) {
				if (mActionCallback != null)
					mActionCallback.TimerFinished(this);

				exitScreen();
				mTimedActionPerformed = true;
			}
		}
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		float lLeft = -mBackgroundTexture.getTextureWidth() / 2;
		float lRight = mBackgroundTexture.getTextureWidth();
		float lTop = -mBackgroundTexture.getTextureHeight() / 2;
		float lBottom = mBackgroundTexture.getTextureHeight();

		if (mStretchBackgroundToFit) {
			final var lDisplay = core.config().display();
			lLeft = -lDisplay.windowWidth() / 2;
			lRight = lDisplay.windowWidth();
			lTop = -lDisplay.windowHeight() / 2;
			lBottom = lDisplay.windowHeight();
		}

		mTextureBatch.begin(core.HUD());
		mTextureBatch.draw(mBackgroundTexture, mSrcTextureRect, lLeft, lTop, lRight, lBottom, -1f, screenColor);
		mTextureBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction callback) {
		mActionCallback = callback;
	}
}
