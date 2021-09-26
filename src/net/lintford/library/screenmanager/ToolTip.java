package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.fonts.FontUnit.WrapType;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;

public class ToolTip {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FontUnit mMenuFont;
	protected SpriteBatch mSpriteBatch;
	protected SpriteSheetDefinition mCoreSpritesheet;
	private IToolTipProvider mToolTipProvider;
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
		mSpriteBatch = new SpriteBatch();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();
		mMenuFont = pResourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_TOOLTIP_NAME);
	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

		mCoreSpritesheet = null;
		mMenuFont = null;
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
		mSpriteBatch.begin(pCore.HUD());
		draw9Patch(mSpriteBatch, mCoreSpritesheet, 32.f, lPositionX - lTextPadding, lPositionY - lTextPadding, TOOLTIP_PANEL_WIDTH + lTextPadding * 2, lToolTipTextHeight + lTextPadding * 3, -0.1f, lColor);
		mSpriteBatch.end();

		mMenuFont.begin(pCore.HUD());
		mMenuFont.drawText(lToolTipText, lPositionX + lTextPadding, lPositionY, -0.01f, ColorConstants.WHITE, 1.f, 350.f);
		mMenuFont.end();
	}
	
	private void draw9Patch(SpriteBatch ppSpriteBatch, SpriteSheetDefinition pSpritesheetDefinition, float pTileSize, float x, float y, float w, float h, float z, Color pTint) {
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, x, y, pTileSize, pTileSize, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, x + pTileSize, y, w - 64, pTileSize, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, x + w - 32, y, pTileSize, pTileSize, z, pTint);

		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, x, y + 32, pTileSize, h - 64, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, x + pTileSize, y + 32, w - 64, h - 64, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, x + w - 32, y + 32, pTileSize, h - 64, z, pTint);

		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, x, y + h - 32, pTileSize, pTileSize, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, x + pTileSize, y + h - 32, w - 64, pTileSize, z, pTint);
		ppSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - 32, y + h - 32, pTileSize, pTileSize, z, pTint);
	}
}
