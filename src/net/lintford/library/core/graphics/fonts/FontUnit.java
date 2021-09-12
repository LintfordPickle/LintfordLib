package net.lintford.library.core.graphics.fonts;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PCT;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;

public class FontUnit {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String BREAK_CHARS = " -";

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	public enum WrapType {
		WordWrap, LetterCountTrim
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int id;
	public final int mEntityGroupId;
	public BitmapFontDefinition mFontDefinition;
	public SpriteBatch mFontRenderer;
	public WrapType mWrapType = WrapType.WordWrap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public WrapType getWrapType() {
		return mWrapType;
	}

	public void setWrapType(WrapType pWrapType) {
		mWrapType = pWrapType;
	}

	public boolean isLoaded() {
		return mFontRenderer != null && mFontRenderer.isLoaded();
	}

	public float getStringHeight(String pText, float pScale) {
		return getStringHeight(pText) * pScale;
	}

	public float getStringHeight(String pText) {
		// TODO: Don't forget - count CR / LF
		return mFontDefinition.getFontHeight();
	}

	public float getStringWidth(String pText, float pScale) {
		return mFontDefinition.getStringWidth(pText, pScale);
	}

	public float getStringWidth(String pText) {
		return mFontDefinition.getStringWidth(pText);
	}

	public float fontHeight() {
		return mFontDefinition.getFontHeight();
	}

	public boolean isDrawing() {
		return mFontRenderer != null && mFontRenderer.isDrawing();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontUnit(int pId, BitmapFontDefinition pBitmapFontDefinition, int pEntityGroupId) {
		id = pId;
		mEntityGroupId = pEntityGroupId;
		mFontDefinition = pBitmapFontDefinition;
		mFontRenderer = new SpriteBatch();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void onLoadGlContent(ResourceManager pResouceManager) {
		mFontRenderer.loadGLContent(pResouceManager);
		mFontDefinition.loadGLContent(pResouceManager, mEntityGroupId);
	}

	public void onUnloadGlContent() {
		mFontRenderer.unloadGLContent();
		mFontDefinition.unloadGLContent();
	}

	public void begin(ICamera pCamera) {
		mFontRenderer.begin(pCamera);
	}

	public void begin(ICamera pCamera, ShaderMVP_PCT pCustomShader) {
		mFontRenderer.begin(pCamera, pCustomShader);
	}

	public void drawText(String pText, float pX, float pY, float pZ, Color pTextColor, float pScale) {
		drawText(pText, pX, pY, pZ, pTextColor, pScale, -1);
	}

	public void drawText(String pText, float pX, float pY, float pZ, Color pTextColor, float pScale, float pWrapWidth) {
		float lX = pX;
		float lY = pY;

		float lWrapWidth = 0;
		boolean lJustWrapped = false;
		float lWordWidth = 0.f;
		boolean lClearedWord = false;
		boolean lBreakCharFitsOnThisLine = false;

		final float lSpaceBetweenLines = 0f;

		final float lScaledLineHeight = (mFontDefinition.getFontHeight() + lSpaceBetweenLines) * pScale;
		final int lLineSpacing = BitmapFontDefinition.LINE_SPACING;
		int lCharacterLength = pText.length();
		for (int i = 0; i < lCharacterLength; i++) {
			char ch_c = pText.charAt(i);
			SpriteFrame lGlyphFrame = mFontDefinition.getGlyphFrame((int) ch_c);

			// Special characters
			if (ch_c == '\n' || ch_c == '\r') {
				lX = pX;
				lY += mFontDefinition.getFontHeight() + lLineSpacing;
				lWrapWidth = 0.f;
				lWordWidth = 0.f;
				lClearedWord = false;
				continue;
			}

			if (BREAK_CHARS.indexOf(ch_c) >= 0) {
				lClearedWord = false;
			}

			if (pWrapWidth != NO_WORD_WRAP) {

				if (mWrapType == WrapType.WordWrap && !lClearedWord) {
					lWordWidth = lGlyphFrame.width() * pScale;
					lBreakCharFitsOnThisLine = lWrapWidth + lGlyphFrame.width() <= pWrapWidth;
					if ((lX == pX) || BREAK_CHARS.indexOf(ch_c) >= 0) {
						for (int j = i + 1; j < pText.length(); j++) {
							char ch_n = pText.charAt(j);
							var lCharGlyph = mFontDefinition.getGlyphFrame((int) ch_n);

							if (lCharGlyph == null)
								continue;

							if (BREAK_CHARS.indexOf(ch_n) >= 0) {
								lClearedWord = true;
								lWrapWidth += lWordWidth;
								break;
							}

							if (lWrapWidth + lWordWidth + lCharGlyph.width() * pScale > pWrapWidth) {
								lWrapWidth = 0;
								lJustWrapped = true;
								break;
							}

							lWordWidth += lCharGlyph.width() * pScale;
						}
					}
				} else if (mWrapType == WrapType.LetterCountTrim) {
					final int lNumElpsis = 3;
					if (i >= pWrapWidth - lNumElpsis) {

						SpriteFrame lDotGlyph = mFontDefinition.getGlyphFrame((int) '.');
						if (lDotGlyph != null) {
							for (int j = 0; j < lNumElpsis; j++) {
								mFontRenderer.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), lDotGlyph.w(), lDotGlyph.h(), lX + j * lDotGlyph.width(), lY, lGlyphFrame.width() * pScale,
										lGlyphFrame.height() * pScale, pZ, pTextColor);
							}

						}

						break;
					}
				}
			}

			if (ch_c == ' ' && lX == pX && lY > pY) {
				continue;
			}

			if (lGlyphFrame == null) {
				continue;
			}

			if (lJustWrapped && lBreakCharFitsOnThisLine == false) {
				lY += lScaledLineHeight;
				lX = pX;
			}

			mFontRenderer.draw(mFontDefinition.texture(), lGlyphFrame.x(), lGlyphFrame.y(), lGlyphFrame.w(), lGlyphFrame.h(), lX, lY, lGlyphFrame.width() * pScale, lGlyphFrame.height() * pScale, pZ, pTextColor);

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lY += lScaledLineHeight;
				lX = pX;
			} else {
				if (!lJustWrapped)
					lX += lGlyphFrame.width() * pScale;
				else {
					lX += lGlyphFrame.width() * pScale;
					lX -= lGlyphFrame.width() * pScale;
				}
			}

			lJustWrapped = false;
			lBreakCharFitsOnThisLine = true;
		}

	}

	public void end() {
		mFontRenderer.end();
	}

}
