package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.ZLayers;

public class ToolTip {

	public static final String TOOLTIP_FONT_NAME = "ToolTipFont";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManager mScreenManager;
	private TextureBatch mSpriteBatch;
	protected FontUnit mMenuFont;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToolTip(ScreenManager pScreenManager) {
		mScreenManager = pScreenManager;
		mSpriteBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

		final String lFontPathname = mScreenManager.fontPathname();
		mMenuFont = pResourceManager.fontManager().loadNewFont(TOOLTIP_FONT_NAME, lFontPathname, 16, true);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	public void draw(LintfordCore pCore, String pTipText, float pX, float pY) {
		// Draw the tool tip on top

		final float lTextPadding = 5f;

		float lToolTipTextWidth = mMenuFont.bitmap().getStringWidth(pTipText);
		float lToolTipTextHeight = mMenuFont.bitmap().getStringHeight(pTipText);

		pX += 15;
		pY -= 64 - lToolTipTextHeight;
		
		// Check for tooltips overlapping the edge of the screen (x axis)
		if (pX + lToolTipTextWidth + lTextPadding > pCore.config().display().windowSize().x) {
			pX -= lToolTipTextWidth + 30;
		}

		// Check for tooltips overlapping the edge of the screen (y axis)
		if (pY + lToolTipTextHeight + lTextPadding > pCore.config().display().windowSize().y) {
			pY -= lToolTipTextHeight + 30;
		}

		// Render the background
		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 96, 0, 32, 32, pX, pY, lToolTipTextWidth + lTextPadding * 2f, lToolTipTextHeight + lTextPadding * 2f, ZLayers.LAYER_SCREENMANAGER + 0.1f, 1, 1, 1, 1);
		mSpriteBatch.end();

		mMenuFont.begin(pCore.HUD());
		mMenuFont.draw(pTipText, pX + lTextPadding, pY + lTextPadding, ZLayers.LAYER_SCREENMANAGER + 0.1f, 1f);
		mMenuFont.end();

	}

}
