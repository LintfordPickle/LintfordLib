package net.lintford.library.core.graphics.fonts;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PCT;
import net.lintford.library.core.graphics.shaders.ShaderSubPixel;
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
		WordWrap, WordWrapTrim, LetterCountTrim
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final int id;
	public BitmapFontDefinition mFontDefinition;
	public SpriteBatch mFontRenderer;
	public WrapType mWrapType = WrapType.WordWrap;
	private ShaderSubPixel mShaderSubPixel;

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

	public float getStringHeight(String pText) {
		return getStringHeightWrapping(pText, NO_WORD_WRAP);
	}

	public float getStringHeight(String pText, float pScale) {
		return getStringHeightWrapping(pText, NO_WORD_WRAP) * pScale;
	}

	public float getStringHeightWrapping(String pText, float pWrapWidth) {
		if (mWrapType == WrapType.LetterCountTrim || pWrapWidth == NO_WORD_WRAP) {
			return mFontDefinition.getFontHeight();
		}

		float lReturnHeight = fontHeight();
		float lX = 0;

		float lWrapWidth = 0;
		boolean lJustWrapped = false;
		float lWordWidth = 0.f;
		boolean lClearedWord = false;
		boolean lBreakCharFitsOnThisLine = false;

		final float lSpaceBetweenLines = 0f;

		final float lScaledLineHeight = (mFontDefinition.getFontHeight() + lSpaceBetweenLines);
		final int lLineSpacing = BitmapFontDefinition.LINE_SPACING;
		int lCharacterLength = pText.length();
		for (int i = 0; i < lCharacterLength; i++) {
			char ch_c = pText.charAt(i);
			SpriteFrame glyph_c = mFontDefinition.getGlyphFrame((int) ch_c);

			// Special characters
			if (ch_c == '\n' || ch_c == '\r') {
				lX = 0;
				lReturnHeight += mFontDefinition.getFontHeight() + lLineSpacing;
				lWrapWidth = 0.f;
				lWordWidth = 0.f;
				lClearedWord = false;
				continue;
			}

			if (BREAK_CHARS.indexOf(ch_c) >= 0) {
				lClearedWord = false;
			}

			if (pWrapWidth != NO_WORD_WRAP) {
				if (mWrapType == WrapType.WordWrap || mWrapType == WrapType.WordWrapTrim && !lClearedWord) {

					if (mWrapType == WrapType.WordWrapTrim && lJustWrapped) {
						break;
					}

					lWordWidth = glyph_c.width();
					lBreakCharFitsOnThisLine = lWrapWidth + glyph_c.width() <= pWrapWidth;
					if ((lX == 0) || BREAK_CHARS.indexOf(ch_c) >= 0) {
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

							if (lWrapWidth + lWordWidth + lCharGlyph.width() > pWrapWidth) {
								lWrapWidth = 0;
								lJustWrapped = true;
								break;
							}

							lWordWidth += lCharGlyph.width();
						}
					}
				}
			}

			if (ch_c == ' ' && lX == 0) {
				continue;
			}

			if (glyph_c == null) {
				continue;
			}

			if (lJustWrapped && lBreakCharFitsOnThisLine == false) {
				lReturnHeight += lScaledLineHeight;
			}

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lReturnHeight += lScaledLineHeight;
				lX = 0;
			} else {
				if (!lJustWrapped)
					lX += glyph_c.width();
				else {
					lX += glyph_c.width();
				}
			}

			lJustWrapped = false;
			lBreakCharFitsOnThisLine = true;
		}

		return lReturnHeight;
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

	public FontUnit(int pId, BitmapFontDefinition pBitmapFontDefinition) {
		id = pId;
		mFontDefinition = pBitmapFontDefinition;
		mFontRenderer = new SpriteBatch();
		// TODO: Cache Shaders (and retrieve from here the SubPixelShader instance)
		mShaderSubPixel = new ShaderSubPixel("SubPixelShader", ShaderSubPixel.VERT_FILENAME, ShaderSubPixel.FRAG_FILENAME);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResouceManager) {
		mFontRenderer.loadResources(pResouceManager);
		mFontDefinition.loadResources(pResouceManager);
		mShaderSubPixel.loadResources(pResouceManager);
	}

	public void unloadResources() {
		mFontRenderer.unloadResources();
		mFontDefinition.unloadResources();
		mShaderSubPixel.unloadResources();
	}

	public void begin(ICamera pCamera) {
		if (mFontDefinition.useSubPixelRendering)
			mFontRenderer.begin(pCamera, mShaderSubPixel);
		else
			mFontRenderer.begin(pCamera);
	}

	public void begin(ICamera pCamera, ShaderMVP_PCT pCustomShader) {
		mFontRenderer.begin(pCamera, pCustomShader);
	}

	public void drawText(String pText, float pX, float pY, float pZ, float pScale) {
		drawText(pText, pX, pY, pZ, ColorConstants.WHITE, pScale, -1);
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
			SpriteFrame glyph_c = mFontDefinition.getGlyphFrame((int) ch_c);

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
				if (mWrapType == WrapType.WordWrap || mWrapType == WrapType.WordWrapTrim && !lClearedWord) {

					if (mWrapType == WrapType.WordWrapTrim && lJustWrapped) {
						break;
					}

					lWordWidth = glyph_c.width() * pScale;
					lBreakCharFitsOnThisLine = lWrapWidth + glyph_c.width() <= pWrapWidth;
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
								mFontRenderer.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), lDotGlyph.w(), lDotGlyph.h(), lX + j * lDotGlyph.width(), lY, glyph_c.width() * pScale,
										glyph_c.height() * pScale, pZ, pTextColor);
							}

						}

						break;
					}
				}
			}

			if (ch_c == ' ' && lX == pX && lY > pY) {
				continue;
			}

			if (glyph_c == null) {
				continue;
			}

			if (lJustWrapped && lBreakCharFitsOnThisLine == false) {
				lY += lScaledLineHeight;
				lX = pX;
			}

			mFontRenderer.draw(mFontDefinition.texture(), glyph_c.x(), glyph_c.y(), glyph_c.w(), glyph_c.h(), (int)lX, (int)lY, glyph_c.width() * pScale, glyph_c.height() * pScale, pZ, pTextColor);

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lY += lScaledLineHeight;
				lX = pX;
			} else {
				if (!lJustWrapped)
					lX += glyph_c.width() * pScale;
				else {
					lX += glyph_c.width() * pScale;
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
