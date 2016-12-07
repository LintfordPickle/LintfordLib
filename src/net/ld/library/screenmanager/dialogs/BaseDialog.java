package net.ld.library.screenmanager.dialogs;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.spritebatch.SpriteBatch9Patch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
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
	private SpriteBatch9Patch m9Patch;

	// ===========================================================
	// Constructor
	// ===========================================================

	public BaseDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogTitle, String pDialogMessage) {
		super(pScreenManager, "");

		mSpriteBatch = new SpriteBatch();
		m9Patch = new SpriteBatch9Patch();

		mTitleString = pDialogTitle;
		mMessageString = pDialogMessage;

		mIsPopup = true; // don't hide underlying screens
	}

	// =============================================
	// Core-Methods
	// =============================================

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);
		m9Patch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		m9Patch.unloadGLContent();

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
		final float lScreenWidthHalf = mDisplayConfig.windowWidth() * 0.5f;
		final float lScreenHeightHalf = mDisplayConfig.windowHeight() * 0.5f;

		final float lDialogWidth = 400f;
		final float lDialogHeight = 199f;

		/*
		 * position the buttons at the bottom of the dynamically sized dialog ...
		 */
		mEntryOffsetFromTop = lScreenHeightHalf + lDialogHeight * 0.5f - 120;

		/* Render background panel */
		m9Patch.begin(mScreenManager.HUD());
		m9Patch.draw9Patch(lScreenWidthHalf - lDialogWidth * 0.5f, lScreenHeightHalf - lDialogHeight * 0.5f, -.4f, lDialogWidth, lDialogHeight, 1f, TextureManager.textureManager().getTexture(ScreenManager.SCREEN_MANAGER_PATCH_TEXTURE_NAME));
		m9Patch.end();

		/* Render title and message */

		if (mTitleString != null) {
			mSpriteBatch.begin(mScreenManager.HUD());
			// mSpriteBatch.draw(mTitleString, lScreenWidthHalf - lDialogWidth * 0.5f + 2, lScreenHeightHalf - lDialogHeight * 0.5f - 4, -0.5f, 0.9f, TextureManager.textureManager().getTexture("Font"));
			mSpriteBatch.end();
		}

		if (mMessageString != null) {
			mSpriteBatch.begin(mScreenManager.HUD());
			// mSpriteBatch.draw(mMessageString, lScreenWidthHalf - lDialogWidth * 0.5f + 15, lScreenHeightHalf - lDialogHeight * 0.5f + 25, -0.5f, 0.85f, TextureManager.textureManager().getTexture("Font"));
			mSpriteBatch.end();
		}

		/* Render buttons */
		super.draw(pRenderState);
	}

}
