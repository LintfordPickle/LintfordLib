package net.lintford.library.screenmanager.screens;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.screenmanager.IMenuAction;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class PressToContinueIntroScreen extends Screen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SOUND_ON_CLICK_NAME = "SOUND_MENU_PRESS_TO_CONTINUE";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected TextureBatch mTextureBatch;
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

	public void stretchBackgroundToFit(boolean pNewValue) {
		mStretchBackgroundToFit = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public PressToContinueIntroScreen(ScreenManager pScreenManager, String pImageLocation) {
		super(pScreenManager);

		mImageLocation = pImageLocation;

		mTextureBatch = new TextureBatch();

		mBackgroundZDepth = -1.0f;
		mContentZDepth = -0.5f;
		mFlashZDepth = -0.01f;

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

		if (!mActionPerformed) {
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode()) || pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_ESCAPE) || pCore.input().keyboard().isKeyDown(GLFW.GLFW_KEY_SPACE)) {
				mScreenManager.uiSounds().play("SOUND_MENU_PRESS_TO_CONTINUE");
				mUserRequestSkip = true;

			}

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!mActionPerformed) {
			if (mUserRequestSkip) {
				mTransitionTimer = 0f;
				mActionPerformed = true;

			}

			mTransitionTimer += pCore.time().elapseGameTimeMilli();
			fadeOutFromWhite(pCore, mTransitionTimer, mTimeToCompleteTransition);

		} else if (mTransitionTimer < mTimeToCompleteTransition) {
			mTransitionTimer += pCore.time().elapseGameTimeMilli();

			fadeOutFromWhite(pCore, mTransitionTimer, mTimeToCompleteTransition);

		} else {
			if (mActionCallback != null) {
				mActionCallback.TimerFinished(this);

			}

			exitScreen();
		}

	}

	private void fadeOutFromWhite(LintfordCore pCore, float pCurrentTimer, float pTotalTime) {
		float normalizedLifetime = pCurrentTimer / pTotalTime;

		mWhiteFlashAlphaAmt = (1 - normalizedLifetime);
	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		float lX = -mBackgroundTexture.getTextureWidth() / 2;
		float lWidth = mBackgroundTexture.getTextureWidth();
		float lY = -mBackgroundTexture.getTextureHeight() / 2;
		float lHeight = mBackgroundTexture.getTextureHeight();

		if (mStretchBackgroundToFit) {
			DisplayManager lDisplay = pCore.config().display();
			lX = -lDisplay.windowWidth() / 2;
			lWidth = lDisplay.windowWidth();
			lY = -lDisplay.windowHeight() / 2;
			lHeight = lDisplay.windowHeight();
		}

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mBackgroundTexture, 0, 0, mBackgroundTexture.getTextureWidth(), mBackgroundTexture.getTextureHeight(), lX, lY, lWidth, lHeight, mBackgroundZDepth, 1f, 1f, 1f, mA);
		mTextureBatch.end();

		drawScreenContents(pCore);

		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(pCore.resources().textureManager().textureWhite(), 0, 0, 2, 2, lX, lY, lWidth, lHeight, mFlashZDepth, 1f, 1f, 1f, mWhiteFlashAlphaAmt);
		mTextureBatch.end();

	}

	protected void drawScreenContents(LintfordCore pCore) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTimerFinishedCallback(IMenuAction pCallback) {
		mActionCallback = pCallback;

	}

}
