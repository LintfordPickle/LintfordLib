package net.lintford.library.core.graphics.fonts;

import net.lintford.library.core.graphics.fonts.BitmapFont.Glyph;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
// TODO: Add option for shadows
public class AWTBitmapFontSpriteBatch extends TextureBatch {

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFont mBitmapFont;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public BitmapFont bitmapFont() {
		return mBitmapFont;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AWTBitmapFontSpriteBatch(BitmapFont pBitmapFont) {
		super();

		mBitmapFont = pBitmapFont;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void draw(String pText, float pX, float pY, float pScale) {
		draw(pText, pX, pY, 0f, 1f, 1f, 1f, 1f, pScale, NO_WORD_WRAP);
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

		float lPosX = pX;
		float lPosY = pY;
		float lWrapWidth = 0;

		final int lTextLength = pCapWidth == NO_WIDTH_CAP ? pText.length() : Math.min(pCapWidth, pText.length());

		for (int i = 0; i < lTextLength; i++) {
			char ch = pText.charAt(i);

			if (ch == '\n') {
				/* Line feed, set x and y to draw at the next line */
				lPosY += mBitmapFont.fontHeight() * pScale;
				lPosX = pX;
				lWrapWidth = 0;
				continue;
			}
			if (ch == '\r') {
				/* Carriage return, just skip it */
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
							lPosY += mBitmapFont.fontHeight() * pScale;
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
				draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX - 2, lPosY + 2, pZ, lCharGlyph.width, lCharGlyph.height, pScale, 0f, 0f, 0f, pA, mBitmapFont.fontTexture());
				draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, pZ, lCharGlyph.width, lCharGlyph.height, pScale, pR, pG, pB, pA, mBitmapFont.fontTexture());
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
					draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX - 2, lPosY + 2, pZ, lCharGlyph.width, lCharGlyph.height, pScale, 0f, 0f, 0f, pA, mBitmapFont.fontTexture());
					draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, pZ, lCharGlyph.width, lCharGlyph.height, pScale, pR, pG, pB, pA, mBitmapFont.fontTexture());
					lPosX += lCharGlyph.width * pScale;

				} else {
					lPosX += mBitmapFont.pointSize() * pScale;

				}
			}

		}

	}

}
