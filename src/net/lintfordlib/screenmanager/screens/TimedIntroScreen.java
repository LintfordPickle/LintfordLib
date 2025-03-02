package net.lintfordlib.screenmanager.screens;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.screenmanager.IMenuAction;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class TimedIntroScreen extends Screen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatchPCT mTextureBatch;
	private Texture mBackgroundTexture;
	private String mImageLocation;
	private float mShowImageTime;
	private float mMaxShowImageTimerInMs;
	private float mMinShowImageTimerInMs;
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
		this(screenManager, imageLocation, 1.f, 4.f);
	}

	public TimedIntroScreen(ScreenManager screenManager, String imageLocation, float minTimeInMs, float maxTimeInMs) {
		super(screenManager);

		mImageLocation = imageLocation;

		mShowImageTime = 0;
		mMinShowImageTimerInMs = minTimeInMs;
		mMaxShowImageTimerInMs = maxTimeInMs;

		mTextureBatch = new TextureBatchPCT();
		mSrcTextureRect = new Rectangle(0, 0, 960, 540);
	}

	// --------------------------------------f
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
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode()) || core.input().mouse().tryAcquireMouseRightClick(hashCode()))
				mUserRequestSkip = true;

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE, this) || core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_SPACE, this))
				mUserRequestSkip = true;

		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (!mTimedActionPerformed) {
			final float deltaTime = (float) core.appTime().elapsedTimeMilli();

			mShowImageTime += deltaTime;

			final var lMinTimeElapsed = mShowImageTime >= mMinShowImageTimerInMs;
			final var lMaxTimeElapsed = mShowImageTime >= mMaxShowImageTimerInMs;

			if ((lMinTimeElapsed && mUserRequestSkip) || lMaxTimeElapsed) {
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
			final var lHudBoundingRectangle = core.HUD().boundingRectangle();
			lLeft = -lHudBoundingRectangle.width() / 2;
			lRight = lHudBoundingRectangle.width();
			lTop = -lHudBoundingRectangle.height() / 2;
			lBottom = lHudBoundingRectangle.height();
		}

		mTextureBatch.begin(core.HUD());
		mTextureBatch.setColor(screenColor);
		mTextureBatch.draw(mBackgroundTexture, mSrcTextureRect, lLeft, lTop, lRight, lBottom, 1f);
		mTextureBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction callback) {
		mActionCallback = callback;
	}
}
