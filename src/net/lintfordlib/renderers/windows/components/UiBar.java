package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.MathHelper;

public class UiBar {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float x, y, w, h;
	private final Color UiBarOuterColor = new Color(1.f, 1.f, 1.f, 1.f);
	private final Color UiBarInnerColor = new Color(1.f, 1.f, 1.f, 1.f);
	private float mInnerBorderPadding = 1.f;

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	private boolean mIsVertical;
	private boolean mIsInverted;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void innerBorderPadding(float value) {
		if (value < 0f)
			value = 0f;
		if (value > h)
			value = h;

		mInnerBorderPadding = value;
	}

	public void isInverted(boolean isVertical) {
		mIsInverted = isVertical;
	}

	public boolean isInverted() {
		return mIsInverted;
	}

	public void isVertical(boolean isVertical) {
		mIsVertical = isVertical;
	}

	public boolean isVertical() {
		return mIsVertical;
	}

	public void setDestRectangle(Rectangle pRect) {
		setDestRectangle(pRect.x(), pRect.y(), pRect.width(), pRect.height());

	}

	public void setDestRectangle(float pX, float pY, float pW, float pH) {
		x = pX;
		y = pY;
		w = pW;
		h = pH;
	}

	public void setOuterColor(Color pColor) {
		setOuterColor(pColor.r, pColor.g, pColor.b, pColor.a);
	}

	public void setOuterColor(float pR, float pG, float pB, float pA) {
		UiBarOuterColor.setRGBA(pR, pG, pB, pA);
	}

	public void setInnerColor(Color pColor) {
		setInnerColor(pColor.r, pColor.g, pColor.b, pColor.a);
	}

	public void setInnerColor(float pR, float pG, float pB, float pA) {
		UiBarInnerColor.setRGBA(pR, pG, pB, pA);
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

	public UiBar(float minValue, float maxValue) {
		mMinValue = minValue;
		mMaxValue = maxValue;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core, SpriteBatch spriteBatch, FontUnit textFont, float componentZDepth) {

		final var lCoreTexture = core.resources().spriteSheetManager().coreSpritesheet();
		float lBarWidth = MathHelper.scaleToRange(mCurValue, mMinValue, mMaxValue, 0, mIsVertical ? h : w);

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, componentZDepth, UiBarOuterColor);

		if (mIsVertical) {
			lBarWidth = MathHelper.clamp(lBarWidth - mInnerBorderPadding * 2, 0, h);
			float lWidth = w - mInnerBorderPadding * 2;
			float lHeight = lBarWidth;

			float xx = x;
			float yy = !mIsInverted ? y + h - mInnerBorderPadding * 2 - lHeight : y;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : lHeight;

			spriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + mInnerBorderPadding, yy + mInnerBorderPadding, ww, hh, componentZDepth, UiBarInnerColor);

		} else {
			lBarWidth = MathHelper.clamp(lBarWidth - mInnerBorderPadding * 2, 0, w);
			float lWidth = lBarWidth;
			float lHeight = h - mInnerBorderPadding * 2;

			float xx = !mIsInverted ? x : x + w - mInnerBorderPadding * 2 - lWidth;
			float yy = y;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : lHeight;

			spriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + mInnerBorderPadding, yy + mInnerBorderPadding, ww, hh, componentZDepth, UiBarInnerColor);
		}
		spriteBatch.end();
	}
}
