package net.lintfordlib.core.graphics.fonts;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PCT;

//TODO: The FontUnit instances don't need a dedicated SpriteBatch per FontUnit!

public class FontUnit {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String BREAK_CHARS = " ";
	public static final String WRAP_ON_CHARS = "-";

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	public enum WrapType {
		WORD_WRAP, WORD_WRAP_TRIM, LETTER_COUNT_TRIM
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFontDefinition mFontDefinition;
	private SpriteBatch mSpriteBatch;
	private WrapType mWrapType = WrapType.WORD_WRAP;

	private final Color mTextColor;
	private final Color mShadowColor;

	private float mLineSpacingInPx = 3;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float lineSpacingInPx() {
		return mLineSpacingInPx;
	}

	public void lineSpacingInPx(float newLineSpacing) {
		mLineSpacingInPx = newLineSpacing;
	}

	public void setTextColorWhite() {
		mTextColor.r = 1.f;
		mTextColor.g = 1.f;
		mTextColor.b = 1.f;
		mTextColor.a = 1.f;
	}

	public void setTextColorBlack() {
		mTextColor.r = 0.f;
		mTextColor.g = 0.f;
		mTextColor.b = 0.f;
		mTextColor.a = 1.f;
	}

	public void setTextColor(Color color) {
		mTextColor.setFromColor(color);
	}

	public void setTextColorA(float a) {
		mTextColor.a = a;
	}

	public void setTextColorRGB(float r, float g, float b) {
		mTextColor.setRGB(r, g, b);
	}

	public void setTextColorRGBA(float r, float g, float b, float a) {
		mTextColor.setRGBA(r, g, b, a);
	}

	public void setShadowColor(Color color) {
		mShadowColor.setFromColor(color);
	}

	public void setShadowColorRGB(float r, float g, float b) {
		mShadowColor.setRGB(r, g, b);
	}

	public void setShadowColorA(float a) {
		mShadowColor.a = a;
	}

	public void setShadowColorRGBA(float r, float g, float b, float a) {
		mShadowColor.setRGBA(r, g, b, a);
	}

	public WrapType getWrapType() {
		return mWrapType;
	}

	public void setWrapType(WrapType wrapType) {
		mWrapType = wrapType;
	}

	public boolean isLoaded() {
		return mSpriteBatch != null && mSpriteBatch.isLoaded();
	}

	public float getStringHeight(String text) {
		return getStringHeightWrapping(text, NO_WORD_WRAP);
	}

	public float getStringHeight(String text, float scale) {
		return getStringHeightWrapping(text, NO_WORD_WRAP) * scale;
	}

	public float getStringHeightWrapping(String text, float wrapWidth) {
		if (mWrapType == WrapType.LETTER_COUNT_TRIM || wrapWidth == NO_WORD_WRAP)
			return mFontDefinition.getFontHeight();

		if (text == null)
			return mFontDefinition.getFontHeight();

		float lReturnHeight = fontHeight();
		float lX = 0;

		float lWrapWidth = 0;
		boolean lJustWrapped = false;
		float lWordWidth;
		boolean lClearedWord = false;
		boolean lBreakCharFitsOnThisLine = false;

		final float lSpaceBetweenLines = 0f;

		final float lScaledLineHeight = (mFontDefinition.getFontHeight() + lSpaceBetweenLines);
		final int lLineSpacing = BitmapFontDefinition.LINE_SPACING;
		int lCharacterLength = text.length();
		for (int i = 0; i < lCharacterLength; i++) {
			char lCurrentChar = text.charAt(i);
			final var lCurrentGlyph = mFontDefinition.getGlyphFrame(lCurrentChar);

			// Special characters
			if (lCurrentChar == '\n' || lCurrentChar == '\r') {
				lX = 0;
				lReturnHeight += mFontDefinition.getFontHeight() + lLineSpacing;
				lWrapWidth = 0.f;
				lClearedWord = false;
				continue;
			}

			if (BREAK_CHARS.indexOf(lCurrentChar) >= 0) {
				lClearedWord = false;
			}

			if (wrapWidth != NO_WORD_WRAP) {
				if (mWrapType == WrapType.WORD_WRAP || mWrapType == WrapType.WORD_WRAP_TRIM && !lClearedWord) {

					if (mWrapType == WrapType.WORD_WRAP_TRIM && lJustWrapped)
						break;

					lWordWidth = lCurrentGlyph.width();
					lBreakCharFitsOnThisLine = lWrapWidth + lCurrentGlyph.width() <= wrapWidth;
					if ((lX == 0) || BREAK_CHARS.indexOf(lCurrentChar) >= 0) {
						for (int j = i + 1; j < text.length(); j++) {
							var lNextChar = text.charAt(j);
							var lCharGlyph = mFontDefinition.getGlyphFrame(lNextChar);

							if (lCharGlyph == null)
								continue;

							if (BREAK_CHARS.indexOf(lNextChar) >= 0) {
								lClearedWord = true;
								lWrapWidth += lWordWidth;
								break;
							}

							if (lWrapWidth + lWordWidth + lCharGlyph.width() > wrapWidth) {
								lWrapWidth = 0;
								lJustWrapped = true;
								break;
							}

							lWordWidth += lCharGlyph.width();
						}
					}
				}
			}

			if (lCurrentChar == ' ' && lX == 0)
				continue;

			if (lCurrentGlyph == null)
				continue;

			if (lJustWrapped && !lBreakCharFitsOnThisLine)
				lReturnHeight += lScaledLineHeight;

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lReturnHeight += lScaledLineHeight;
				lX = 0;
			} else
				lX += lCurrentGlyph.width();

			lJustWrapped = false;
			lBreakCharFitsOnThisLine = true;
		}

		return lReturnHeight;
	}

	public float getStringWidth(String text, float scale) {
		return mFontDefinition.getStringWidth(text, scale);
	}

	public float getStringWidth(String text) {
		return mFontDefinition.getStringWidth(text);
	}

	public float fontHeight() {
		return mFontDefinition.getFontHeight();
	}

	public boolean isDrawing() {
		return mSpriteBatch != null && mSpriteBatch.isDrawing();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontUnit(BitmapFontDefinition bitmapFontDefinition) {
		mFontDefinition = bitmapFontDefinition;
		mSpriteBatch = new SpriteBatch();

		mTextColor = new Color(1.f, 1.f, 1.f, 1.f);
		mShadowColor = new Color(0.f, 0.f, 0.f, 1.f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResources(ResourceManager resouceManager) {
		mSpriteBatch.loadResources(resouceManager);
		mFontDefinition.loadResources(resouceManager);
	}

	public void unloadResources() {
		mSpriteBatch.unloadResources();
		mFontDefinition.unloadResources();
	}

	public void begin(ICamera camera) {
		mSpriteBatch.begin(camera);
	}

	public void begin(ICamera camera, ShaderMVP_PCT customShader) {
		mSpriteBatch.begin(camera, customShader);
	}

	public void drawText(String text, float positionX, float positionY, float zDepth, float scale) {
		if (text == null)
			return;

		mSpriteBatch.setColor(mTextColor);
		_drawText(text, positionX, positionY, zDepth, scale, -1);
	}

	public void drawText(String text, float positionX, float positionY, float zDepth, float scale, float wrapWidth) {
		if (text == null)
			return;

		mSpriteBatch.setColor(mTextColor);
		_drawText(text, positionX, positionY, zDepth, scale, wrapWidth);
	}

	public void drawShadowedText(String text, float positionX, float positionY, float zDepth, float shadowXOffset, float shadowYOffset, float scale) {
		drawShadowedText(text, positionX, positionY, zDepth, shadowXOffset, shadowYOffset, scale, -1);
	}

	public void drawShadowedText(String text, float positionX, float positionY, float zDepth, float shadowXOffset, float shadowYOffset, float scale, float wordWrap) {
		mSpriteBatch.setColor(mShadowColor);
		_drawText(text, positionX + shadowXOffset, positionY + shadowYOffset, zDepth, scale, wordWrap);

		mSpriteBatch.setColor(mTextColor);
		_drawText(text, positionX, positionY, zDepth, scale, wordWrap);
	}

	private void _drawText(String text, float positionX, float positionY, float zDepth, float scale, float wrapWidthCap) {
		if (text == null)
			return;

		var xx = positionX;
		var yy = positionY;

		var wrapWidth = 0;
		var wordWidth = 0;
		var justWrapped = false;

		boolean clearedWord = false;
		boolean breakCharFitsOnThisLine = false;

		final var scaledLineHeight = (mFontDefinition.getFontHeight() + mLineSpacingInPx) * scale;
		final var lineSpacing = BitmapFontDefinition.LINE_SPACING;

		int characterLength = text.length();
		for (int i = 0; i < characterLength; i++) {
			var currentCharacter = text.charAt(i);
			var currentGlyph = mFontDefinition.getGlyphFrame(currentCharacter);

			// Special characters
			if (currentCharacter == '\n' || currentCharacter == '\r') {
				xx = (int) positionX;
				yy += mFontDefinition.getFontHeight() + lineSpacing;
				wrapWidth = 0;
				clearedWord = false;
				continue;
			}

			if (BREAK_CHARS.indexOf(currentCharacter) >= 0)
				clearedWord = false;

			if (wrapWidthCap != NO_WORD_WRAP) {
				if (mWrapType == WrapType.WORD_WRAP || mWrapType == WrapType.WORD_WRAP_TRIM && !clearedWord) {
					if (mWrapType == WrapType.WORD_WRAP_TRIM && justWrapped)
						break;

					wordWidth = (int) Math.ceil(currentGlyph.width() * scale);
					breakCharFitsOnThisLine = wrapWidth + currentGlyph.width() <= wrapWidthCap;
					if ((xx == positionX) || BREAK_CHARS.indexOf(currentCharacter) >= 0) {
						for (int j = i + 1; j < text.length(); j++) {
							char lCharcterNext = text.charAt(j);
							var lNextGlyph = mFontDefinition.getGlyphFrame(lCharcterNext);

							if (lNextGlyph == null)
								continue;

							if (BREAK_CHARS.indexOf(lCharcterNext) >= 0) {
								clearedWord = true;
								wrapWidth += wordWidth;
								break;
							}

							float lOverflowSpaceForElipsis = mWrapType == WrapType.WORD_WRAP_TRIM ? 20.f : 0.f;
							if (wrapWidth + wordWidth + lNextGlyph.width() * scale + lOverflowSpaceForElipsis * scale > wrapWidthCap) {
								wrapWidth = 0;
								justWrapped = true;

								break;
							}

							wordWidth += lNextGlyph.width() * scale;
						}
					}
				} else if (mWrapType == WrapType.LETTER_COUNT_TRIM) {
					final int lNumElpsis = 3;
					if (i >= wrapWidthCap - lNumElpsis) {
						final var lDotGlyph = mFontDefinition.getGlyphFrame('.');
						if (lDotGlyph != null) {
							for (int j = 0; j < lNumElpsis; j++) {
								mSpriteBatch.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), lDotGlyph.width(), lDotGlyph.height(), xx + j * lDotGlyph.width() * scale, yy, currentGlyph.width() * scale, currentGlyph.height() * scale, zDepth);
							}
						}

						break;
					}
				}
			}

			if (currentGlyph == null) {
				currentGlyph = mFontDefinition.getGlyphFrame('?');
			}

			if (justWrapped && mWrapType == WrapType.WORD_WRAP_TRIM) {
				final int lNumElpsis = 3;

				final var lDotGlyph = mFontDefinition.getGlyphFrame('.');
				if (lDotGlyph != null) {
					for (int j = 0; j < lNumElpsis; j++) {
						mSpriteBatch.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), lDotGlyph.width(), lDotGlyph.height(), xx + j * lDotGlyph.width() * scale, yy, currentGlyph.width() * scale, currentGlyph.height() * scale, zDepth);
					}
				}

				return;
			}

			if (justWrapped) {
				yy += scaledLineHeight;
				xx = (int) positionX;
			}

			if (currentCharacter == ' ' && xx == positionX && yy > positionY) {
				justWrapped = false;
				continue;
			}

			if (currentGlyph != null)
				mSpriteBatch.draw(mFontDefinition.texture(), currentGlyph.x(), currentGlyph.y(), currentGlyph.width(), currentGlyph.height(), xx, yy, currentGlyph.width() * scale, currentGlyph.height() * scale, zDepth);

			if (justWrapped && breakCharFitsOnThisLine) {
				yy += scaledLineHeight;
				xx = (int) positionX;
			} else {
				xx += currentGlyph.width() * scale;
			}

			justWrapped = false;
			breakCharFitsOnThisLine = true;
		}
	}

	public void end() {
		mSpriteBatch.end();
	}
}
