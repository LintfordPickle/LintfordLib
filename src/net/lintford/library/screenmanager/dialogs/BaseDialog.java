package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
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

	protected TextureBatch mSpriteBatch;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// constructor
	// --------------------------------------

	public BaseDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogMessage) {
		super(pScreenManager, "");

		mSpriteBatch = new TextureBatch();

		mDrawBackground = true;

		mMessageString = pDialogMessage;

		mTransitionOn = null;
		mTransitionOff = null;
		mA = 1f;

		mTopMargin = 400f;

		mIsPopup = true; // don't hide underlying screens
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mSpriteBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mSpriteBatch.unloadGLContent();

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);
	}

	@Override
	public void updateStructure(LintfordCore pCore) {
		float lTextHeight = font().bitmap().getStringHeight(mMessageString);

		// Get the Y Start position of the menu entries
		float lYPos = -DIALOG_HEIGHT * 0.5f + font().bitmap().getStringHeight(mMessageString) + lTextHeight / 2;

		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			// TODO: Ignore floating layouts
			BaseLayout lLayout = layouts().get(i);

			lYPos += lLayout.paddingTop();

			switch (mChildAlignment) {
			case left:
				lLayout.x = lLayout.paddingLeft();
				break;
			case center:
				lLayout.x = -lLayout.w / 2;
				break;
			case right:
				lLayout.x = pCore.config().display().windowSize().x - lLayout.w - lLayout.paddingRight();
				break;
			}

			lLayout.y = lYPos;
			lYPos += lLayout.h + lLayout.paddingBottom();

			layouts().get(i).updateStructure();

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
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 64, 0, 32, 32, -DIALOG_WIDTH * 0.5f, -DIALOG_HEIGHT * 0.5f, DIALOG_WIDTH, DIALOG_HEIGHT, ZDEPTH, mR, mG, mB, mA);
			mSpriteBatch.end();
		}

		font().begin(pCore.HUD());

		/* Render title and message */
		font().draw(mMessageString, -DIALOG_WIDTH * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + 30, ZDEPTH, 1f, DIALOG_WIDTH);

		font().end();

		AARectangle lHUDRect = pCore.HUD().boundingRectangle();

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
