package net.lintford.library.core.graphics.fonts;

import net.lintford.library.core.graphics.fonts.BitmapFont.Glyph;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
// TODO: Add option for shadows
// TODO: Text scaling (global and local) needs implementing (it was removed from the TextureBatch, so the destRect needs to be adapted).
public class AWTBitmapFontSpriteBatch extends TextureBatch {

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFont mBitmapFont;
	private boolean mDrawShadow; 

	// --------------------------------------
	// Properties
	// --------------------------------------

	public BitmapFont bitmapFont() {
		return mBitmapFont;
	}

	public boolean shadowEnabled() {
		return mDrawShadow;
	}
	
	public void shadowEnabled(boolean pNewValue) {
		mDrawShadow = pNewValue;
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AWTBitmapFontSpriteBatch(BitmapFont pBitmapFont) {
		super();

		mBitmapFont = pBitmapFont;
		mDrawShadow = true;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(String pText, float pX, float pY, float pScale) {
		draw(pText, pX, pY, -0.1f, 1f, 1f, 1f, 1f, pScale, NO_WORD_WRAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pScale, float pWordWrapWidth) {
		draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, pWordWrapWidth);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale) {
		draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, NO_WORD_WRAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth) {
		draw(pText, pX, pY, pZ, pR, pG, pB, pA, pScale, pWordWrapWidth, NO_WIDTH_CAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth, int pCapWidth) {
		if (pText == null)
			return;
		
		final float lSpaceBetweenLines = 3f;

		float lPosX = pX;
		float lPosY = pY;
		float lWrapWidth = 0;

		final int lTextLength = pCapWidth == NO_WIDTH_CAP ? pText.length() : Math.min(pCapWidth, pText.length());

		for (int i = 0; i < lTextLength; i++) {
			char ch = pText.charAt(i);

			if (ch == '\n') {
				/* Line feed, set x and y to draw at the next line */
				lPosY += (mBitmapFont.fontHeight() + lSpaceBetweenLines) * pScale;
				lPosX = pX;
				lWrapWidth = 0;
				continue;
			}
			if (ch == '\r') {
				/* Carriage return, set x and y to draw at the next line */			
				lPosY += (mBitmapFont.fontHeight() + lSpaceBetweenLines) * pScale;
				lPosX = pX;
				lWrapWidth = 0;
				continue;
			}

			// word wrapping works on words
			if (pWordWrapWidth != NO_WORD_WRAP) {
				if (ch == ' ') {
					for (int j = i + 1; j < pText.length(); j++) {
						char ch_m = pText.charAt(j);

						Glyph lCharGlyph = mBitmapFont.glyphs().get(ch_m);

						if (lCharGlyph == null)
							continue;
						lWrapWidth += lCharGlyph.width * pScale;

						if (ch_m == ' ') {
							break;
						}

						if (lWrapWidth >= pWordWrapWidth) {
							lPosY += (mBitmapFont.fontHeight() + lSpaceBetweenLines) * pScale;
							lPosX = pX;
							lWrapWidth = 0;
						}
					}
				}
			}

			// don't render the first space after a word wrap
			if (ch == ' ' && lPosX == pX)
				continue;

			Glyph lCharGlyph = mBitmapFont.glyphs().get(ch);

			if (lCharGlyph != null) {
				if(mDrawShadow)
					draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX - 1f*pScale, lPosY + 1f*pScale, lCharGlyph.width*pScale, lCharGlyph.height*pScale, pZ, 0f, 0f, 0f, pA);
				draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, lCharGlyph.width*pScale, lCharGlyph.height*pScale, pZ, pR, pG, pB, pA);
				lPosX += lCharGlyph.width * pScale;

			} else {
				lPosX += mBitmapFont.pointSize() * pScale;

			}

		}

		// Draw the elipses
		if (lTextLength != pText.length()) {
			Glyph lCharGlyph = mBitmapFont.glyphs().get('.');

			for (int i = 0; i < 3; i++) {
				if (lCharGlyph != null) {
					if(mDrawShadow)
						draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX - 2, lPosY + 2, lCharGlyph.width*pScale, lCharGlyph.height*pScale, pZ, 0f, 0f, 0f, pA);
					draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, lCharGlyph.width*pScale, lCharGlyph.height*pScale, pZ, pR, pG, pB, pA);
					lPosX += lCharGlyph.width * pScale;

				} else {
					lPosX += mBitmapFont.pointSize() * pScale;

				}
			}

		}

	}

}
