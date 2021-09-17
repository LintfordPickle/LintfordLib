package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.fonts.FontUnit.WrapType;
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
		if (pToolTipProvider.isParentActive())
			mToolTipProvider = pToolTipProvider;
		else
			mToolTipProvider = null;
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
		mMenuFont = pResourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_TOOLTIP_NAME);
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

		final float TOOLTIP_PANEL_WIDTH = 350;

		final float lTextPadding = 5f;
		final String lToolTipText = mToolTipProvider.toolTipText();

		mMenuFont.setWrapType(WrapType.WordWrap);
		float lToolTipTextHeight = mMenuFont.getStringHeightWrapping(lToolTipText, 350.f);

		float lPositionX = mPositionX;
		float lPositionY = mPositionY;

		lPositionX += 15;
		lPositionY -= 64;// - lToolTipTextHeight;

		// Check for tooltip overlapping the edge of the screen (x axis)
		if (lPositionX + TOOLTIP_PANEL_WIDTH + 10 > pCore.config().display().windowWidth() * .5f) {
			lPositionX -= TOOLTIP_PANEL_WIDTH + 30;
		}

		// Check for tooltip overlapping the edge of the screen (y axis)
		if (lPositionY + lToolTipTextHeight + lTextPadding > pCore.config().display().windowHeight() * .5f) {
			lPositionY -= lToolTipTextHeight + 30;
		}

		final var lColor = ColorConstants.getColor(.21f, .11f, .13f, 1.f);

		// Render the background
		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(mUiPanelTexture, 0, 0, 32, 32, lPositionX - lTextPadding, lPositionY - lTextPadding, TOOLTIP_PANEL_WIDTH + lTextPadding * 2, lToolTipTextHeight + lTextPadding * 2, -0.1f, lColor);
		mTextureBatch.end();

		mMenuFont.begin(pCore.HUD());
		mMenuFont.drawText(lToolTipText, lPositionX + lTextPadding, lPositionY, -0.01f, ColorConstants.WHITE, 1.f, 350.f);
		mMenuFont.end();
	}
}
