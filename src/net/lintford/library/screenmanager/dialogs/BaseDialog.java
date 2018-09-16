package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public abstract class BaseDialog extends MenuScreen {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int DIALOG_WIDTH = 600;
	public static final int DIALOG_HEIGHT = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mMessageString;
	protected boolean mDrawBackground;

	protected TextureBatch mTextureBatch;

	// --------------------------------------
	// constructor
	// --------------------------------------

	public BaseDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogMessage) {
		super(pScreenManager, "");

		mTextureBatch = new TextureBatch();

		mDrawBackground = true;

		mMessageString = pDialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		mA = 1f;

		mPaddingTop = 400f;

		mIsPopup = true; // don't hide underlying screens
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

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
	}

	@Override
	public void updateStructureDimensions(LintfordCore pCore) {
		super.updateStructureDimensions(pCore);

	}

	@Override
	public void updateStructurePositions(LintfordCore pCore) {
		float lTextHeight = font().bitmap().getStringHeight(mMessageString);

		// Get the Y Start position of the menu entries
		float lYPos = -DIALOG_HEIGHT * 0.5f + font().bitmap().getStringHeight(mMessageString) + lTextHeight / 2;

		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			// TODO: Ignore floating layouts
			BaseLayout lLayout = layouts().get(i);

			lYPos += 0;//lLayout.paddingTop();

			switch (mChildAlignment) {
			case left:
				lLayout.x = 0;//lLayout.paddingLeft();
				break;
			case center:
				lLayout.x = -lLayout.w / 2;
				break;
			case right:
				lLayout.x = pCore.config().display().windowSize().x - lLayout.w;// - lLayout.paddingRight();
				break;
			}

			lLayout.y = lYPos;
			lYPos += lLayout.h;// + lLayout.paddingBottom();
			lLayout.w = DIALOG_WIDTH - 32;

			layouts().get(i).updateStructurePositions();

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

		final float TEXT_HORIZONTAL_PADDING = 20;

		if (mDrawBackground) {
			final float a = 1f;

			final float TILE_SIZE = 32f;
			final float x = -DIALOG_WIDTH / 2;
			final float y = -DIALOG_HEIGHT / 2;
			final float w = DIALOG_WIDTH;
			final float h = DIALOG_HEIGHT;

			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 64, TILE_SIZE, TILE_SIZE, x, y, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 64, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y, w - 64, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 64, TILE_SIZE, TILE_SIZE, x + w - 32, y, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);

			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 96, TILE_SIZE, TILE_SIZE, x, y + 32, TILE_SIZE, h - 64, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 96, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + 32, w - 64, h - 64, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 96, TILE_SIZE, TILE_SIZE, x + w - 32, y + 32, TILE_SIZE, h - 64, ZDEPTH, 1, 1, 1, a);

			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 128, TILE_SIZE, TILE_SIZE, x, y + h - 32, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 128, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 128, TILE_SIZE, TILE_SIZE, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, ZDEPTH, 1, 1, 1, a);
			mTextureBatch.end();

		}

		font().begin(pCore.HUD());
		font().draw(mMessageString, -DIALOG_WIDTH * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + 30, ZDEPTH, 1f, DIALOG_WIDTH);
		font().end();

		Rectangle lHUDRect = pCore.HUD().boundingRectangle();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left() + TITLE_PADDING_X, lHUDRect.top(), ZDEPTH, mR, mG, mB, mA, 1f);
		mMenuHeaderFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, ZDEPTH + (i * 0.001f));

		}

	}

}
