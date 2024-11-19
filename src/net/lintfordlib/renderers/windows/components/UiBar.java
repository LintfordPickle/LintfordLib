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
	private final Color mUiBarOuterColor = new Color(1.f, 1.f, 1.f, 1.f);
	private final Color mUiBarInnerColor = new Color(1.f, 1.f, 1.f, 1.f);
	private final Color mUiBarTextColor = new Color(1.f, 1.f, 1.f, 1.f);
	private float mInnerBorderPadding = 1.f;

	private String mLabel;

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	private boolean mIsInverted;
	private float mBarSizeAsPercentageOfWidth; // [0,1]

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the normalized factor representing the point along the width that the bar starts (left is label, right is bar). */
	public float barAsPercentageOfWidth() {
		return mBarSizeAsPercentageOfWidth;
	}

	public void barAsPercentageOfWidth(float newValue) {
		mBarSizeAsPercentageOfWidth = MathHelper.clamp(newValue, 0, 1);
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

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
		mUiBarOuterColor.setRGBA(pR, pG, pB, pA);
	}

	public void setInnerColor(Color pColor) {
		setInnerColor(pColor.r, pColor.g, pColor.b, pColor.a);
	}

	public void setInnerColor(float pR, float pG, float pB, float pA) {
		mUiBarInnerColor.setRGBA(pR, pG, pB, pA);
	}

	public void setTextColor(Color pColor) {
		setTextColor(pColor.r, pColor.g, pColor.b, pColor.a);
	}

	public void setTextColor(float pR, float pG, float pB, float pA) {
		mUiBarTextColor.setRGBA(pR, pG, pB, pA);
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

	public UiBar() {
		mBarSizeAsPercentageOfWidth = .5f;
	}

	public UiBar(float minValue, float maxValue) {
		mMinValue = minValue;
		mMaxValue = maxValue;

		mBarSizeAsPercentageOfWidth = .5f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core, SpriteBatch spriteBatch, FontUnit textFont, float componentZDepth) {
		final var lCoreTexture = core.resources().spriteSheetManager().coreSpritesheet();

		final var lFullBarWidth = w * mBarSizeAsPercentageOfWidth;
		final var lFullBarPosX = x + (w * (1 - mBarSizeAsPercentageOfWidth));
		final var lInnerBarWidth = MathHelper.scaleToRange(mCurValue, mMinValue, mMaxValue, 0, lFullBarWidth);

		textFont.drawText(mLabel, x, y, -0.01f, mUiBarTextColor, 1.f);

		// Outer
		spriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, lFullBarPosX, y, lFullBarWidth, h, componentZDepth, mUiBarOuterColor);

		// Inner
		mInnerBorderPadding = 2;
		final var lHeight = h - mInnerBorderPadding * 2;

		final var xx = !mIsInverted ? lFullBarPosX : x + w - mInnerBorderPadding * 2 - lInnerBarWidth;
		final var yy = y;
		final var ww = !mIsInverted ? lInnerBarWidth : -lInnerBarWidth;
		final var hh = !mIsInverted ? lHeight : -lHeight;
		spriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + mInnerBorderPadding, yy + mInnerBorderPadding, ww, hh, componentZDepth, mUiBarInnerColor);
	}
}