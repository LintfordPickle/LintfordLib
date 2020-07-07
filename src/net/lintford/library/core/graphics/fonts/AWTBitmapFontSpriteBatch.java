package net.lintford.library.core.graphics.fonts;

import net.lintford.library.core.graphics.fonts.BitmapFont.Glyph;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

// TODO: Need to implement the spritebatch like TileSetRendererVBO, i.e. with separate shaders and 
// TODO: Add option for shadows
// TODO: Text scaling (global and local) needs implementing (it was removed from the TextureBatch, so the destRect needs to be adapted).
public class AWTBitmapFontSpriteBatch extends TextureBatchPCT {

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFont mBitmapFont;
	private boolean mDrawShadow;
	private boolean mTrimText;

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

	public boolean trimText() {
		return mTrimText;
	}

	public void trimText(boolean pNewValue) {
		mTrimText = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public AWTBitmapFontSpriteBatch(BitmapFont pBitmapFont) {
		super();

		mBitmapFont = pBitmapFont;
		mDrawShadow = true;
		mTrimText = true;

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

		final float lSpaceBetweenLines = 0f;

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

			// word wrapping works on words, so check the next word to see if it can fit on the line ...
			if (pWordWrapWidth != NO_WORD_WRAP) {
				if ((lPosX == pX) || ch == ' ') {
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
							float temp = (mBitmapFont.fontHeight() + lSpaceBetweenLines) * pScale;
							lPosY += temp;
							lPosX = pX;
							lWrapWidth = 0;
						}
					}
				}
			}

			// don't render the first space after a word wrap
			if (ch == ' ' && mTrimText && lPosX == pX && lPosY > pY) {
				// Glyph lCharGlyph = mBitmapFont.glyphs().get(ch);
				// lPosX += lCharGlyph.width * pScale;
				continue;
			}

			Glyph lCharGlyph = mBitmapFont.glyphs().get(ch);

			if (lCharGlyph != null) {
				if (mDrawShadow)
					draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, (lPosX - 1 - mBitmapFont.fontHeight() * 0.05f), (lPosY + 1 + mBitmapFont.fontHeight() * 0.05f),
							lCharGlyph.width * pScale, lCharGlyph.height * pScale, pZ, 0f, 0f, 0f, pA);
				draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, lCharGlyph.width * pScale, lCharGlyph.height * pScale, pZ, pR, pG, pB, pA);
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
					if (mDrawShadow)
						draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, (lPosX - 1 - mBitmapFont.fontHeight()), lPosY + 2, lCharGlyph.width * pScale,
								lCharGlyph.height * pScale, pZ, 0f, 0f, 0f, pA);
					draw(mBitmapFont.fontTexture(), lCharGlyph.x, lCharGlyph.y, lCharGlyph.width, lCharGlyph.height, lPosX, lPosY, lCharGlyph.width * pScale, lCharGlyph.height * pScale, pZ, pR, pG, pB, pA);
					lPosX += lCharGlyph.width * pScale;

				} else {
					lPosX += mBitmapFont.pointSize() * pScale;

				}
			}

		}

	}

}
