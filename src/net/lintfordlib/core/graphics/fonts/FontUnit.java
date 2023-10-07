package net.lintfordlib.core.graphics.fonts;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.GLDebug;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PCT;
import net.lintfordlib.core.graphics.shaders.ShaderSubPixel;

public class FontUnit {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String BREAK_CHARS = " ";
	public static final String WRAP_ON_CHARS = "-";

	public static final int NO_WORD_WRAP = -1;
	public static final int NO_WIDTH_CAP = -1;

	public enum WrapType {
		WordWrap, WordWrapTrim, LetterCountTrim
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFontDefinition mFontDefinition;
	private SpriteBatch mFontRenderer;
	private ShaderSubPixel mShaderSubPixel;
	private WrapType mWrapType = WrapType.WordWrap;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void _countDebugStats(boolean enableDebugStats) {
		mFontRenderer._countDebugStats(enableDebugStats);
	}

	public WrapType getWrapType() {
		return mWrapType;
	}

	public void setWrapType(WrapType wrapType) {
		mWrapType = wrapType;
	}

	public boolean isLoaded() {
		return mFontRenderer != null && mFontRenderer.isLoaded();
	}

	public float getStringHeight(String text) {
		return getStringHeightWrapping(text, NO_WORD_WRAP);
	}

	public float getStringHeight(String text, float scale) {
		return getStringHeightWrapping(text, NO_WORD_WRAP) * scale;
	}

	public float getStringHeightWrapping(String text, float wrapWidth) {
		if (mWrapType == WrapType.LetterCountTrim || wrapWidth == NO_WORD_WRAP)
			return mFontDefinition.getFontHeight();

		if (text == null)
			return mFontDefinition.getFontHeight();

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
		int lCharacterLength = text.length();
		for (int i = 0; i < lCharacterLength; i++) {
			char ch_c = text.charAt(i);
			final var glyph_c = mFontDefinition.getGlyphFrame((int) ch_c);

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

			if (wrapWidth != NO_WORD_WRAP) {
				if (mWrapType == WrapType.WordWrap || mWrapType == WrapType.WordWrapTrim && !lClearedWord) {

					if (mWrapType == WrapType.WordWrapTrim && lJustWrapped) {
						break;
					}

					lWordWidth = glyph_c.width();
					lBreakCharFitsOnThisLine = lWrapWidth + glyph_c.width() <= wrapWidth;
					if ((lX == 0) || BREAK_CHARS.indexOf(ch_c) >= 0) {
						for (int j = i + 1; j < text.length(); j++) {
							char ch_n = text.charAt(j);
							var lCharGlyph = mFontDefinition.getGlyphFrame((int) ch_n);

							if (lCharGlyph == null)
								continue;

							if (BREAK_CHARS.indexOf(ch_n) >= 0) {
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

			if (ch_c == ' ' && lX == 0)
				continue;

			if (glyph_c == null)
				continue;

			if (lJustWrapped && lBreakCharFitsOnThisLine == false)
				lReturnHeight += lScaledLineHeight;

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lReturnHeight += lScaledLineHeight;
				lX = 0;
			} else {
				if (!lJustWrapped)
					lX += glyph_c.width();
				else
					lX += glyph_c.width();
			}

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
		return mFontRenderer != null && mFontRenderer.isDrawing();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FontUnit(BitmapFontDefinition bitmapFontDefinition) {
		mFontDefinition = bitmapFontDefinition;
		mFontRenderer = new SpriteBatch();
		mShaderSubPixel = new ShaderSubPixel("SubPixelShader", ShaderSubPixel.VERT_FILENAME, ShaderSubPixel.FRAG_FILENAME);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadResources(ResourceManager resouceManager) {
		mFontRenderer.loadResources(resouceManager);
		mFontDefinition.loadResources(resouceManager);
		mShaderSubPixel.loadResources(resouceManager);
		GLDebug.checkGLErrorsException();
	}

	public void unloadResources() {
		mFontRenderer.unloadResources();
		mFontDefinition.unloadResources();
		mShaderSubPixel.unloadResources();
	}

	public void begin(ICamera camera) {
		if (mFontDefinition.mUseSubPixelRendering)
			mFontRenderer.begin(camera, mShaderSubPixel);
		else
			mFontRenderer.begin(camera);
	}

	public void begin(ICamera camera, ShaderMVP_PCT customShader) {
		mFontRenderer.begin(camera, customShader);
	}

	public void drawText(String text, float positionX, float positionY, float zDepth, float scale) {
		if (text == null)
			return;

		drawText(text, positionX, positionY, zDepth, ColorConstants.WHITE, scale, -1);
	}

	public void drawText(String text, float positionX, float positionY, float zDepth, Color textColor, float scale) {
		drawText(text, positionX, positionY, zDepth, textColor, scale, -1);
	}

	public void drawText(String text, float positionX, float positionY, float zDepth, Color textColor, float scale, float wrapWidth) {
		if (text == null)
			return;

		int lX = (int) positionX;
		int lY = (int) positionY;

		int lWrapWidth = 0;
		boolean lJustWrapped = false;
		int lWordWidth = 0;
		boolean lClearedWord = false;
		boolean lBreakCharFitsOnThisLine = false;

		final float lSpaceBetweenLines = 0f;

		final float lScaledLineHeight = (mFontDefinition.getFontHeight() + lSpaceBetweenLines) * scale;
		final int lLineSpacing = BitmapFontDefinition.LINE_SPACING;
		int lCharacterLength = text.length();
		for (int i = 0; i < lCharacterLength; i++) {
			char lCurrentCharacter = text.charAt(i);
			final var lCurrentGlyph = mFontDefinition.getGlyphFrame((int) lCurrentCharacter);

			// Special characters
			if (lCurrentCharacter == '\n' || lCurrentCharacter == '\r') {
				lX = (int) positionX;
				lY += mFontDefinition.getFontHeight() + lLineSpacing;
				lWrapWidth = 0;
				lWordWidth = 0;
				lClearedWord = false;
				continue;
			}

			if (BREAK_CHARS.indexOf(lCurrentCharacter) >= 0)
				lClearedWord = false;

			if (wrapWidth != NO_WORD_WRAP) {
				if (mWrapType == WrapType.WordWrap || mWrapType == WrapType.WordWrapTrim && !lClearedWord) {
					// TODO: the line breaks in here are broken (floating point error?)

					if (mWrapType == WrapType.WordWrapTrim && lJustWrapped)
						break;

					lWordWidth = (int) Math.ceil(lCurrentGlyph.width() * scale);
					lBreakCharFitsOnThisLine = lWrapWidth + lCurrentGlyph.width() <= wrapWidth;
					if ((lX == positionX) || BREAK_CHARS.indexOf(lCurrentCharacter) >= 0) {
						for (int j = i + 1; j < text.length(); j++) {
							char lCharcterNext = text.charAt(j);
							var lNextGlyph = mFontDefinition.getGlyphFrame((int) lCharcterNext);

							if (lNextGlyph == null)
								continue;

							if (BREAK_CHARS.indexOf(lCharcterNext) >= 0) {
								lClearedWord = true;
								lWrapWidth += lWordWidth;
								break;
							}

							float lOverflowSpaceForElipsis = mWrapType == WrapType.WordWrapTrim ? 20.f : 0.f;
							if (lWrapWidth + lWordWidth + lNextGlyph.width() * scale + lOverflowSpaceForElipsis > wrapWidth) {
								lWrapWidth = 0;
								lJustWrapped = true;

								break;
							}

							lWordWidth += lNextGlyph.width() * scale;
						}
					}
				} else if (mWrapType == WrapType.LetterCountTrim) {
					final int lNumElpsis = 3;
					if (i >= wrapWidth - lNumElpsis) {

						final var lDotGlyph = mFontDefinition.getGlyphFrame((int) '.');
						if (lDotGlyph != null) {
							for (int j = 0; j < lNumElpsis; j++) {
								mFontRenderer.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), lDotGlyph.width(), lDotGlyph.height(), (int) (lX) + j * lDotGlyph.width(), (int) lY,
										lCurrentGlyph.width() * scale, lCurrentGlyph.height() * scale, zDepth, textColor);
							}
						}

						break;
					}
				}
			}

			if (lCurrentGlyph == null)
				continue;

			if (lJustWrapped && mWrapType == WrapType.WordWrapTrim) {
				final int lNumElpsis = 3;

				final var lDotGlyph = mFontDefinition.getGlyphFrame((int) '.');
				if (lDotGlyph != null) {
					for (int j = 0; j < lNumElpsis; j++) {
						mFontRenderer.draw(mFontDefinition.texture(), lDotGlyph.x(), lDotGlyph.y(), (int) lDotGlyph.width(), lDotGlyph.height(), (int) (lX) + j * lDotGlyph.width(), (int) lY,
								lCurrentGlyph.width() * scale, lCurrentGlyph.height() * scale, zDepth, textColor);
					}
				}

				return;
			}

			if (lJustWrapped) {
				lY += lScaledLineHeight;
				lX = (int) positionX;
			}

			if (lCurrentCharacter == ' ' && lX == positionX && lY > positionY) {
				lJustWrapped = false;
				continue;
			}

			mFontRenderer.draw(mFontDefinition.texture(), lCurrentGlyph.x(), lCurrentGlyph.y(), (int) lCurrentGlyph.width(), lCurrentGlyph.height(), (int) lX, (int) lY, lCurrentGlyph.width() * scale,
					lCurrentGlyph.height() * scale, zDepth, textColor);

			if (lJustWrapped && lBreakCharFitsOnThisLine) {
				lY += lScaledLineHeight;
				lX = (int) positionX;
			} else {
				lX += lCurrentGlyph.width() * scale;
			}

			lJustWrapped = false;
			lBreakCharFitsOnThisLine = true;
		}
	}

	public void end() {
		mFontRenderer.end();
	}
}
