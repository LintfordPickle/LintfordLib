package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

public class ToolTip {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FontUnit mMenuFont;
	private IToolTipProvider mToolTipProvider;
	private TextureBatchPCT mTextureBatch;
	private Texture mUiPanelTexture;
	private float mPositionX;
	private float mPositionY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void toolTipProvider(IToolTipProvider pToolTipProvider) {
		mToolTipProvider = pToolTipProvider;

	}

	public boolean isActive() {
		return mToolTipProvider != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToolTip() {
		mTextureBatch = new TextureBatchPCT();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mMenuFont = pResourceManager.fontManager().getFontUnit("FONT_TOOLTIP", LintfordCore.CORE_ENTITY_GROUP_ID);
		mTextureBatch.loadGLContent(pResourceManager);
		mUiPanelTexture = pResourceManager.textureManager().loadTexture("TEXTURE_UI_PANEL", "/res/textures/core/system.png", LintfordCore.CORE_ENTITY_GROUP_ID);
	}

	public void unloadGLContent() {
		mMenuFont = null;
		mTextureBatch.unloadGLContent();
		mUiPanelTexture = null;

	}

	public void handleInput(LintfordCore pCore) {
		mPositionX = pCore.HUD().getMouseWorldSpaceX();
		mPositionY = pCore.HUD().getMouseWorldSpaceY();

	}

	public void update(LintfordCore pCore) {
		if (mToolTipProvider != null) {
			if (!mToolTipProvider.isMouseOver()) {
				mToolTipProvider = null;

			}

		}
	}

	public void draw(LintfordCore pCore) {
		if (mToolTipProvider == null)
			return;

		final float lTextScale = 1.0f;// mScreenManager.UIHUDController().uiTextScaleFactor();

		final float lTextPadding = 5f;
		final String lToolTipText = mToolTipProvider.toolTipText();

		float lToolTipTextWidth = mMenuFont.getStringWidth(lToolTipText, lTextScale);
		float lToolTipTextHeight = mMenuFont.getStringWidth(lToolTipText, lTextScale);

		float lPositionX = mPositionX;
		float lPositionY = mPositionY;

		lPositionX += 15;
		lPositionY -= 64 - lToolTipTextHeight;

		// Check for tooltip overlapping the edge of the screen (x axis)
		if (lPositionX + lToolTipTextWidth + lTextPadding > pCore.config().display().windowWidth()) {
			lPositionX -= lToolTipTextWidth + 30;
		}

		// Check for tooltip overlapping the edge of the screen (y axis)
		if (lPositionY + lToolTipTextHeight + lTextPadding > pCore.config().display().windowHeight()) {
			lPositionY -= lToolTipTextHeight + 30;
		}

		final var lColor = ColorConstants.getColor(.21f, .11f, .13f, 1.f);

		// Render the background
		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mUiPanelTexture, 0, 0, 32, 32, lPositionX, lPositionY + 4f, lToolTipTextWidth + lTextPadding * 2.f, lToolTipTextHeight + lTextPadding * 2.f, -0.1f, lColor);
		mTextureBatch.end();

		mMenuFont.begin(pCore.HUD());
		mMenuFont.drawText(lToolTipText, lPositionX + lTextPadding, lPositionY + lTextPadding, -0.01f, ColorConstants.WHITE, lTextScale);
		mMenuFont.end();
	}
}
