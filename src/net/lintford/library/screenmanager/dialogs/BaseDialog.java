package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class BaseDialog extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DIALOG_WIDTH = 600;
	public static final int DIALOG_HEIGHT = 250;

	public static final float TEXT_HORIZONTAL_PADDING = 20;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mMessageString;
	protected MenuScreen mParentScreen;
	protected boolean mDrawBackground;
	protected boolean mDarkenBackground;

	protected Texture mUITexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawBackground() {
		return mDrawBackground;
	}

	public void drawBackground(boolean pNewValue) {
		mDrawBackground = pNewValue;
	}

	public boolean darkenBackground() {
		return mDarkenBackground;
	}

	public void darkenBackground(boolean pNewValue) {
		mDarkenBackground = pNewValue;
	}

	public String dialogTitle() {
		return mMenuTitle;
	}

	public void dialogTitle(String pNewMenuTitle) {
		mMenuTitle = pNewMenuTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogMessage) {
		super(pScreenManager, "");

		mParentScreen = pParentScreen;

		mDrawBackground = true;
		mDarkenBackground = true;

		mMessageString = pDialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		mA = 1f;

		mPaddingTop = 0;

		mIsPopup = true; // don't hide underlying screens
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mUITexture = null;

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

	}

	@Override
	public void updateLayoutSize(LintfordCore pCore) {
		super.updateLayoutSize(pCore);

		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = layouts().get(i);

			lBaseLayout.set(-DIALOG_WIDTH * 0.4f, mPaddingTop, DIALOG_WIDTH * 0.8f, DIALOG_HEIGHT);
			lBaseLayout.updateStructure();

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		final float ZDEPTH = ZLayers.LAYER_SCREENMANAGER + 0.05f;

		final float lWindowWidth = pCore.HUD().boundingRectangle().w();
		final float lWindowHeight = pCore.HUD().boundingRectangle().h();

		final TextureBatchPCT lTextureBatch = mParentScreen.rendererManager().uiTextureBatch();

		// This is the full screen darken effect
		if (mDarkenBackground) {
			final float lAlphaAmt = 0.70f;
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, lWindowWidth, lWindowHeight, ZLayers.LAYER_SCREENMANAGER - 0.1f, 0f, 0f, 0f, lAlphaAmt);
			lTextureBatch.end();

		}

		if (mDrawBackground) {
			final float a = 1f;

			final float TILE_SIZE = 32f;
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;
			final float w = DIALOG_WIDTH;
			final float h = DIALOG_HEIGHT;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 256, 0, TILE_SIZE, TILE_SIZE, x, y, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 288, 0, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y, w - 64, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 320, 0, TILE_SIZE, TILE_SIZE, x + w - 32, y, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);

			lTextureBatch.draw(mUITexture, 256, 32, TILE_SIZE, TILE_SIZE, x, y + 32, TILE_SIZE, h - 64, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 288, 32, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + 32, w - 64, h - 64, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 320, 32, TILE_SIZE, TILE_SIZE, x + w - 32, y + 32, TILE_SIZE, h - 64, ZDEPTH, 1, 1, 1, a);

			lTextureBatch.draw(mUITexture, 256, 64, TILE_SIZE, TILE_SIZE, x, y + h - 32, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 288, 64, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.draw(mUITexture, 320, 64, TILE_SIZE, TILE_SIZE, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			lTextureBatch.end();

		}

		final float lHeaderFontHeight = mMenuHeaderFont.bitmap().fontHeight();

		/* Render title and message */
		font().begin(pCore.HUD());
		font().draw(mMessageString, -DIALOG_WIDTH * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + lHeaderFontHeight + 15f, ZDEPTH, 1f, DIALOG_WIDTH - 70f);
		font().end();

		// Render the menu title if there is one
		if (mMenuTitle != null && mMenuTitle.length() > 0) {
			mMenuHeaderFont.begin(pCore.HUD());
			mMenuHeaderFont.draw(mMenuTitle, -DIALOG_WIDTH / 2f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT / 2f + TEXT_HORIZONTAL_PADDING, ZDEPTH, mR, mG, mB, mA, 0.65f);
			mMenuHeaderFont.end();

		}

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, ZDEPTH + (i * 0.001f));

		}

	}

}
