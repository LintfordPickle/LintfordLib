package net.ld.library.core.graphics.fonts;

import net.ld.library.core.graphics.fonts.BitmapFont.Glyph;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;

// TODO: Need to implement the sprite batch like TileSetRendererVBO, i.e. with separate shaders and 
public class AWTBitmapFontSpriteBatch extends SpriteBatch {

	public static final int NO_WORD_WRAP = -1;

	// =============================================
	// Variables
	// =============================================

	private BitmapFont mBitmapFont;

	// =============================================
	// Properties
	// =============================================

	public BitmapFont bitmapFont() {
		return mBitmapFont;
	}

	// =============================================
	// Constructor
	// =============================================

	public AWTBitmapFontSpriteBatch(BitmapFont pBitmapFont) {
		super();

		mBitmapFont = pBitmapFont;

	}

	// =============================================
	// Methods
	// =============================================

	public void draw(String pText, float pX, float pY, float pScale) {
		draw(pText, pX, pY, .5f, 1f, 1f, 1f, 1f, pScale, NO_WORD_WRAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pScale, float pWordWrapWidth) {
		draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, pWordWrapWidth);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale) {
		draw(pText, pX, pY, pZ, 1f, 1f, 1f, 1f, pScale, NO_WORD_WRAP);
	}

	public void draw(String pText, float pX, float pY, float pZ, float pR, float pG, float pB, float pA, float pScale, float pWordWrapWidth) {
		if(pText == null) return;
		
		float lPosX = pX;
		float lPosY = pY;
		float lWrapWidth = 0;

		for (int i = 0; i < pText.length(); i++) {
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
						
						if(lCharGlyph == null) continue;
						lWrapWidth += lCharGlyph.width * pScale * pScale;

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

			Glyph lCharGlyph = mBitmapFont.glyphs().get(ch);

			if (lCharGlyph != null) {
				// Draw a shadow
				// draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX - 2, lPosY + 2, pZ, lCharGlyph.width * pScale, lCharGlyph.height * pScale, pScale, 0f, 0f, 0f, 1f, mBitmapFont.fontTexture());
				
				// Draw the colored text
				draw(lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, pZ, lCharGlyph.width * pScale, lCharGlyph.height * pScale, pScale, pR, pG, pB, pA, mBitmapFont.fontTexture());
				lPosX += lCharGlyph.width * pScale * pScale;
			} else {
				lPosX += mBitmapFont.pointSize() * pScale * pScale;
			}

		}

	}

}
