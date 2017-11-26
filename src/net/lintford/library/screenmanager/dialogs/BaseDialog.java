package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public abstract class BaseDialog extends MenuScreen {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String mMessageString;
	protected float mDialogWidth;
	protected float mDialogHeight;
	protected boolean mDrawBackground;

	private TextureBatch mSpriteBatch;

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

		mDialogWidth = 400f;
		mDialogHeight = 200f;

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
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pGameTime, pInputState, pAcceptMouse, pAcceptKeyboard);
	}

	@Override
	public void updateStructure() {
		float lTextHeight = font().bitmap().getStringHeight(mMessageString);

		mDialogHeight = 100 + lTextHeight;

		// Get the Y Start position of the menu entries
		float lYPos = -mDialogHeight * 0.5f + font().bitmap().getStringHeight(mMessageString);

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
				lLayout.x = -lLayout.width / 2;
				break;
			case right:
				lLayout.x = mScreenManager.masterConfig().displayConfig().windowSize().x - lLayout.width - lLayout.paddingRight();
				break;
			}

			lLayout.y = lYPos;
			lYPos += lLayout.height + lLayout.paddingBottom();

			layouts().get(i).updateStructure();

		}

	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

	}

	@Override
	public void draw(RenderState pRenderState) {
		final float TEXT_HORIZONTAL_PADDING = 20;
		mDialogWidth = font().bitmap().getStringWidth(mMessageString) + TEXT_HORIZONTAL_PADDING * 2;

		final float lDialogWidth = mDialogWidth;
		final float lDialogHeight = 150 + font().bitmap().getStringHeight(mMessageString);

		if (mDrawBackground) {
			mSpriteBatch.begin(pRenderState.HUDCamera());
			final float SCALE = 1f;
			mSpriteBatch.draw(64, 64, 32, 32, -lDialogWidth * 0.5f, -lDialogHeight * 0.5f, -1.5f, lDialogWidth, lDialogHeight, SCALE, mR, mG, mB, mA, TextureManager.TEXTURE_CORE_UI);
			mSpriteBatch.end();
		}

		font().begin(mScreenManager.HUD());

		/* Render title and message */
		font().draw(mMessageString, -lDialogWidth * 0.5f + TEXT_HORIZONTAL_PADDING, -lDialogHeight * 0.5f + 30, -1.5f, 1f, lDialogWidth);

		font().end();

		super.draw(pRenderState);
	}

}
