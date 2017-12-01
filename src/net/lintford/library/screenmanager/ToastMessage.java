package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ToastMessage {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManager mScreenManager;
	private TextureBatch mSpriteBatch;
	private float mTimeToDisplay;
	private String mMessageText;
	private String mMessageTitle;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float timeLeft() {
		return mTimeToDisplay;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToastMessage(ScreenManager pScreenManager) {
		mScreenManager = pScreenManager;
		mSpriteBatch = new TextureBatch();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	public void update(LintfordCore pCore) {
		final float deltaTime = (float) pCore.time().elapseGameTimeSeconds();
		mTimeToDisplay -= deltaTime;
	}

	public void draw(LintfordCore pCore) {

		// float pX = mDisplayConfig.windowSize().x * 0.5f;
		// float pY = mDisplayConfig.windowSize().y * 0.75f;

		// TODO(John): Implement the pixel font here

		// SpriteSheet lSpriteSheet = mScreenManager.resources().spriteSheetManager().getSpriteSheet("MenuTextures");
		// FontSpriteBatch lSpriteBatch = BitmapFontManager.bitmapFontManager().getBitmapFont("Main").mFontSpriteBatch;
		// final float lOldScale = lSpriteBatch.getScale();
		//
		// float lToolTipTextWidth = lSpriteBatch.getWidth(mMessageText);
		// float lToolTipTextHeight = lSpriteBatch.getHeight(mMessageText);

		// pX -= (lToolTipTextWidth + 10) * 0.5f;
		// pY -= (lToolTipTextHeight + 10) * 0.5f;

		// float lHorizontalPadding = 55;
		// float lVerticalPadding = 15;

		// m9Patch.begin(mScreenManager.HUD());
		// m9Patch.draw9Patch(pX - lHorizontalPadding, pY - lVerticalPadding, -.1f, lToolTipTextWidth + 10 + lHorizontalPadding * 2f, lToolTipTextHeight + lVerticalPadding * 2f, 0.5f, lSpriteSheet, "panelFancy");
		// m9Patch.end();
		//
		// lSpriteBatch.setScale(0.5f);
		// lSpriteBatch.begin(mScreenManager.HUD());
		// lSpriteBatch.drawText(mMessageTitle, pX - 20, pY - 13, -0.2f);
		// lSpriteBatch.end();
		//
		// lSpriteBatch.setScale(0.65f);
		// lSpriteBatch.begin(mScreenManager.HUD());
		// lSpriteBatch.drawText(mMessageText, pX + 5, pY + 5, -0.2f);
		// lSpriteBatch.end();
		//
		// lSpriteBatch.setScale(lOldScale);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setupToast(String pTitle, String pMessage, float pTimeInMS) {
		mMessageTitle = pTitle;
		mMessageText = pMessage;
		mTimeToDisplay = pTimeInMS;
	}

}
