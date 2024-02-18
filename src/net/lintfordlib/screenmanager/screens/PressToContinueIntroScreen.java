package net.lintfordlib.screenmanager.screens;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;
import net.lintfordlib.core.graphics.textures.Texture;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.screenmanager.IMenuAction;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class PressToContinueIntroScreen extends Screen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SOUND_ON_CLICK_NAME = "SOUND_MENU_PRESS_TO_CONTINUE";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected TextureBatchPCT mTextureBatch;
	private Texture mBackgroundTexture;
	private String mImageLocation;
	protected boolean mUserRequestSkip;
	private boolean mActionPerformed;
	private float mTimeToCompleteTransition = 400f;
	private float mTransitionTimer;
	private float mWhiteFlashAlphaAmt = 1f;
	protected float mBackgroundZDepth;
	protected float mContentZDepth;
	protected float mFlashZDepth;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PressToContinueIntroScreen(ScreenManager screenManager, String imageLocation) {
		super(screenManager);

		mImageLocation = imageLocation;

		mTextureBatch = new TextureBatchPCT();

		mBackgroundZDepth = -1.0f;
		mContentZDepth = -0.5f;
		mFlashZDepth = -0.01f;
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

		if (!mActionPerformed) {
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode()) || core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_ESCAPE) || core.input().keyboard().isKeyDown(GLFW.GLFW_KEY_SPACE)) {
				mScreenManager.uiSounds().play("SOUND_MENU_PRESS_TO_CONTINUE");
				mUserRequestSkip = true;
			}
		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (!mActionPerformed) {
			if (mUserRequestSkip) {
				mTransitionTimer = 0f;
				mActionPerformed = true;
			}

			mTransitionTimer += core.appTime().elapsedTimeMilli();
			fadeOutFromWhite(core, mTransitionTimer, mTimeToCompleteTransition);
		} else if (mTransitionTimer < mTimeToCompleteTransition) {
			mTransitionTimer += core.appTime().elapsedTimeMilli();

			fadeOutFromWhite(core, mTransitionTimer, mTimeToCompleteTransition);
		} else {
			if (mActionCallback != null) {
				mActionCallback.TimerFinished(this);
			}

			exitScreen();
		}
	}

	private void fadeOutFromWhite(LintfordCore core, float currentTimer, float totalTime) {
		float normalizedLifetime = currentTimer / totalTime;

		mWhiteFlashAlphaAmt = (1 - normalizedLifetime);
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);

		float lX = -mBackgroundTexture.getTextureWidth() / 2;
		float lWidth = mBackgroundTexture.getTextureWidth();
		float lY = -mBackgroundTexture.getTextureHeight() / 2;
		float lHeight = mBackgroundTexture.getTextureHeight();

		if (mStretchBackgroundToFit) {
			DisplayManager lDisplay = core.config().display();
			lX = -lDisplay.windowWidth() / 2;
			lWidth = lDisplay.windowWidth();
			lY = -lDisplay.windowHeight() / 2;
			lHeight = lDisplay.windowHeight();
		}

		final var lColor = ColorConstants.getWhiteWithAlpha(mWhiteFlashAlphaAmt);

		mTextureBatch.begin(core.HUD());
		mTextureBatch.draw(mBackgroundTexture, 0, 0, mBackgroundTexture.getTextureWidth(), mBackgroundTexture.getTextureHeight(), lX, lY, lWidth, lHeight, mBackgroundZDepth, lColor);
		mTextureBatch.end();

		drawScreenContents(core);

		mTextureBatch.begin(core.HUD());
		mTextureBatch.draw(core.resources().textureManager().textureWhite(), 0, 0, 2, 2, lX, lY, lWidth, lHeight, mFlashZDepth, lColor);
		mTextureBatch.end();
	}

	protected void drawScreenContents(LintfordCore core) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction callback) {
		mActionCallback = callback;
	}
}