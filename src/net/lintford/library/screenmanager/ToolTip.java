package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.fonts.FontUnit.WrapType;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;

public class ToolTip {

	// --------------------------------------
	// Constants
	// --------------------------------------

	final float TOOLTIP_PANEL_WIDTH = 400;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mMenuFont;
	private SpriteBatch mSpriteBatch;
	private SpriteSheetDefinition mCoreSpritesheet;
	private IToolTipProvider mToolTipProvider;
	private boolean mTopOfScreen;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean topOfScreen() {
		return mTopOfScreen;
	}

	public void topOfScreen(boolean topOfScreen) {
		mTopOfScreen = topOfScreen;
	}

	public void toolTipProvider(IToolTipProvider toolTipProvider) {
		if (toolTipProvider != null && toolTipProvider.isParentActive())
			mToolTipProvider = toolTipProvider;
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
		mTopOfScreen = true;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mSpriteBatch.loadResources(resourceManager);

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mMenuFont = resourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_TOOLTIP_NAME);
	}

	public void unloadResources() {
		mSpriteBatch.unloadResources();

		mCoreSpritesheet = null;
		mMenuFont = null;
	}

	public void update(LintfordCore core) {
		if (mToolTipProvider != null) {
			if (!mToolTipProvider.isMouseOver() && !mToolTipProvider.hasFocus()) {
				mToolTipProvider = null;
			}
		}
	}

	public void draw(LintfordCore core) {
		if (mToolTipProvider == null)
			return;

		final var lHudBoundingBox = core.HUD().boundingRectangle();

		final var lTextPadding = 5f;
		final var lToolTipText = mToolTipProvider.toolTipText();

		mMenuFont.setWrapType(WrapType.WordWrap);
		float lToolTipTextHeight = mMenuFont.getStringHeightWrapping(lToolTipText, 350.f);

		mTopOfScreen = !mToolTipProvider.isTopHalfOfScreen();

		final var lPositionX = -TOOLTIP_PANEL_WIDTH * .5f;
		final var lPositionY = mTopOfScreen ? lHudBoundingBox.top() + lHudBoundingBox.height() / 7 : lHudBoundingBox.centerY() + lHudBoundingBox.height() / 7;

		final var lColor = ColorConstants.getColor(.21f, .11f, .13f, 1.f);

		// Render the background
		mSpriteBatch.begin(core.HUD());
		draw9Patch(mSpriteBatch, mCoreSpritesheet, 32.f, lPositionX - lTextPadding, lPositionY - lTextPadding, TOOLTIP_PANEL_WIDTH + lTextPadding * 2, lToolTipTextHeight + lTextPadding * 3, -0.1f, lColor);
		mSpriteBatch.end();

		mMenuFont.begin(core.HUD());
		mMenuFont.drawText(lToolTipText, lPositionX + lTextPadding, lPositionY, -0.01f, ColorConstants.WHITE, 1.f, 350.f);
		mMenuFont.end();
	}

	private void draw9Patch(SpriteBatch spriteBatch, SpriteSheetDefinition spritesheetDefinition, float tileSize, float x, float y, float w, float h, float z, Color colorTint) {
		// @formatter:off
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     x,            y, tileSize+1, tileSize+1, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      x + tileSize, y, w - (tileSize-1)*2, tileSize+1, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    x + w - 32,   y, tileSize+1, tileSize+1, z, colorTint);

		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT,     x,            y + 32, tileSize+1, h - 64, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER,   x + tileSize, y + 32, w - (tileSize-1)*2, h - 64, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT,    x + w - 32,   y + 32, tileSize+1, h - 64, z, colorTint);

		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  x,            y + h - 32, tileSize+1, tileSize+1, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   x + tileSize, y + h - 32, w - (tileSize-1)*2, tileSize+1, z, colorTint);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, x + w - 32,   y + h - 32, tileSize+1, tileSize+1, z, colorTint);
		//@formatter:on
	}
}
