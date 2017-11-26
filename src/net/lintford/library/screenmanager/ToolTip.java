package net.lintford.library.screenmanager;

import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class ToolTip {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatch mSpriteBatch;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToolTip(ScreenManager pScreenManager) {
		mSpriteBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();

	}

	public void draw(String pTipText, float pX, float pY) {
		// Draw the tool tip on top
		pX += 15;
		pY += 15;

		// SpriteSheet lSpriteSheet = mScreenManager.resources().spriteSheetManager().getSpriteSheet("MenuTextures");
		// FontSpriteBatch lSpriteBatch = BitmapFontManager.bitmapFontManager().getBitmapFont("Main").mFontSpriteBatch;
		// final float lOldScale = lSpriteBatch.getScale();
		// lSpriteBatch.setScale(0.5f);
		//
		// float lToolTipTextWidth = lSpriteBatch.getWidth(pTipText);
		// float lToolTipTextHeight = lSpriteBatch.getHeight(pTipText);
		//
		// if (pX + lToolTipTextWidth + 5 > mDisplayConfig.windowWidth()) {
		// pX -= lToolTipTextWidth + 30;
		// }
		//
		// if (pY + lToolTipTextHeight + 5 > mDisplayConfig.windowHeight()) {
		// pY -= lToolTipTextHeight + 30;
		// }
		//
		// m9Patch.begin(mScreenManager.HUD());
		// m9Patch.draw9Patch(pX, pY, -0.1f, lToolTipTextWidth + 10, lToolTipTextHeight + 10, 0.5f, lSpriteSheet, "panelFancy");
		// m9Patch.end();
		//
		// lSpriteBatch.begin(mScreenManager.HUD());
		// lSpriteBatch.drawText(pTipText, pX + 5, pY + 5, -0.2f);
		// lSpriteBatch.end();
		//
		// lSpriteBatch.setScale(lOldScale);
	}

}
