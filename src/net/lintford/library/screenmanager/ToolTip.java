package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ToolTip {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String TOOLTIP_FONT_NAME = "ToolTipFont";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ScreenManager mScreenManager;
	private TextureBatch mSpriteBatch;
	private Texture mUITexture;
	protected FontUnit mMenuFont;

	private String mToolTipText;
	private float x, y, z;

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

		mUITexture = pResourceManager.textureManager().textureCore();

		final String lFontPathname = mScreenManager.fontPathname();
		mMenuFont = pResourceManager.fontManager().loadNewFont(TOOLTIP_FONT_NAME, lFontPathname, 16, true, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		mMenuFont = null;

	}

	public void setToolTipActive(String pTipText, float pX, float pY, float pZ) {
		mToolTipText = pTipText;
		x = pX;
		y = pY;
		z = pZ;

	}

	public void draw(LintfordCore pCore) {
		final float lTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final float lTextPadding = 5f;

		float lToolTipTextWidth = mMenuFont.bitmap().getStringWidth(mToolTipText, lTextScale);
		float lToolTipTextHeight = mMenuFont.bitmap().getStringHeight(mToolTipText, lTextScale);

		x += 15;
		y -= 64 - lToolTipTextHeight;

		// Check for tooltips overlapping the edge of the screen (x axis)
		if (x + lToolTipTextWidth + lTextPadding > pCore.config().display().windowWidth()) {
			x -= lToolTipTextWidth + 30;
		}

		// Check for tooltips overlapping the edge of the screen (y axis)
		if (y + lToolTipTextHeight + lTextPadding > pCore.config().display().windowHeight()) {
			y -= lToolTipTextHeight + 30;
		}

		// Render the background
		mSpriteBatch.begin(pCore.HUD());
		mSpriteBatch.draw(mUITexture, 96, 0, 32, 32, x, y, lToolTipTextWidth + lTextPadding * 2f, lToolTipTextHeight + lTextPadding * 2f, z, 1, 1, 1, 1);
		mSpriteBatch.end();

		mMenuFont.begin(pCore.HUD());
		mMenuFont.draw(mToolTipText, x + lTextPadding, y + lTextPadding, z, lTextScale);
		mMenuFont.end();

	}

}
