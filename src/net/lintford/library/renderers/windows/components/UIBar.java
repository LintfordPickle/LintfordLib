package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.maths.MathHelper;

public class UIBar {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float x, y, w, h;
	private final Color UiBarColor = new Color(1.f, 1.f, 1.f, 1.f);

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	private boolean mIsVertical;
	private boolean mIsInverted;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void isInverted(boolean pIsVertical) {
		mIsInverted = pIsVertical;
	}

	public boolean isInverted() {
		return mIsInverted;
	}

	public void isVertical(boolean pIsVertical) {
		mIsVertical = pIsVertical;
	}

	public boolean isVertical() {
		return mIsVertical;
	}

	public void setDestRectangle(Rectangle pRect) {
		setDestRectangle(pRect.x(), pRect.y(), pRect.w(), pRect.h());

	}

	public void setDestRectangle(float pX, float pY, float pW, float pH) {
		x = pX;
		y = pY;
		w = pW;
		h = pH;

	}

	public void setColor(Color pColor) {
		setColor(pColor.r, pColor.g, pColor.b, pColor.a);

	}

	public void setColor(float pR, float pG, float pB, float pA) {
		UiBarColor.setRGBA(pR, pG, pB, pA);

	}

	public void setCurrentValue(float pValue) {
		mCurValue = MathHelper.clamp(pValue, mMinValue, mMaxValue);

	}

	public void setMinMax(float pMinValue, float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIBar(float pMinValue, float pMaxValue) {
		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, FontUnit pTextFont, float pComponentZDepth) {
		if (pSpriteBatch == null || !pSpriteBatch.isDrawing())
			return;

		final float INNER_BORDER_PADDING = 2.f;

		final var lCoreTexture = pCore.resources().spriteSheetManager().coreSpritesheet();

		float lBarWidth = MathHelper.scaleToRange(mCurValue, mMinValue, mMaxValue, 0, mIsVertical ? h : w);
		lBarWidth = MathHelper.clamp(lBarWidth - INNER_BORDER_PADDING * 2, 0, w);

		final var lOutlineColor = ColorConstants.getColor(.1f, .1f, .1f);
		pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, pComponentZDepth, lOutlineColor);

		if (mIsVertical) {
			float lWidth = w - INNER_BORDER_PADDING * 2;
			float lHeight = lBarWidth;

			float xx = x;
			float yy = !mIsInverted ? y + h - INNER_BORDER_PADDING * 2 - lHeight : y + h - INNER_BORDER_PADDING * 2;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : -lHeight;

			pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + INNER_BORDER_PADDING, yy + INNER_BORDER_PADDING, ww, hh, pComponentZDepth, UiBarColor);

		} else {
			float lWidth = lBarWidth;
			float lHeight = h - INNER_BORDER_PADDING * 2;

			float xx = !mIsInverted ? x : x + w - INNER_BORDER_PADDING * 2 - lWidth;
			float yy = y;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : lHeight;

			pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + INNER_BORDER_PADDING, yy + INNER_BORDER_PADDING, ww, hh, pComponentZDepth, UiBarColor);
		}
	}

}
