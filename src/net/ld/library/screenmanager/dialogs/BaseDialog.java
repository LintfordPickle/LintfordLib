package net.ld.library.screenmanager.dialogs;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.ScreenManager;

public abstract class BaseDialog extends MenuScreen {

	// ===========================================================
	// Variables
	// ===========================================================

	protected String mTitleString;
	protected String mMessageString;
	protected float mDialogWidth;
	protected float mDialogHeight;

	private SpriteBatch mSpriteBatch;

	// ===========================================================
	// Constructor
	// ===========================================================

	public BaseDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogTitle, String pDialogMessage) {
		super(pScreenManager, "");

		mSpriteBatch = new SpriteBatch();

		mTitleString = pDialogTitle;
		mMessageString = pDialogMessage;

		mZ = 3f; // default to 3 for dialogs
		mIsPopup = true; // don't hide underlying screens
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);
	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);
	}

	@Override
	public void draw(RenderState pRenderState) {
		final float lDialogWidth = 400f;
		final float lDialogHeight = 200f;

		/*
		 * position the buttons at the bottom of the dynamically sized dialog ...
		 */
		mEntryOffsetFromTop = 220;

		Rectangle lHUDRect = pRenderState.hudCamera().boundingRectangle();

		/* Render background panel */
		mScreenManager.spriteBatch().begin(pRenderState.hudCamera());
		mScreenManager.spriteBatch().draw(0, 0, 16, 16, lHUDRect.centerX() - lDialogWidth / 2, lHUDRect.centerY() - lDialogHeight / 2, mZ, lDialogWidth, lDialogHeight, 1f,
				TextureManager.textureManager().getTexture(ScreenManager.SCREEN_MANAGER_TEXTURE_NAME));
		mScreenManager.spriteBatch().end();

		/* Render title and message */

		if (mTitleString != null) {
			mMenuTitleFont.begin(pRenderState.hudCamera());
			mMenuTitleFont.draw(mTitleString, lHUDRect.centerX() - lDialogWidth / 2 + 5, lHUDRect.centerY() - lDialogHeight / 2 + 5, mZ + 0.1f, 1.0f);
			mMenuTitleFont.end();
		}

		if (mMessageString != null) {
			mMenuFont.begin(pRenderState.hudCamera());
			mMenuFont.draw(mMessageString, lHUDRect.centerX() - lDialogWidth / 2 + 5, lHUDRect.centerY() - lDialogHeight / 2 + 40, mZ + 0.1f, 1f);
			mMenuFont.end();
		}

		/* Render buttons */
		super.draw(pRenderState);
	}

}
