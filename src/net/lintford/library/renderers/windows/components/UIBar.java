package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.maths.MathHelper;

public class UIBar {

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

	public void innerBorderPadding(float pValue) {
		if (pValue < 0f)
			pValue = 0f;
		if (pValue > h)
			pValue = h;

		mInnerBorderPadding = pValue;
	}

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

		final var lCoreTexture = pCore.resources().spriteSheetManager().coreSpritesheet();

		float lBarWidth = MathHelper.scaleToRange(mCurValue, mMinValue, mMaxValue, 0, mIsVertical ? h : w);

		pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, pComponentZDepth, UiBarOuterColor);

		if (mIsVertical) {
			lBarWidth = MathHelper.clamp(lBarWidth - mInnerBorderPadding * 2, 0, h);
			float lWidth = w - mInnerBorderPadding * 2;
			float lHeight = lBarWidth;

			float xx = x;
			float yy = !mIsInverted ? y + h - mInnerBorderPadding * 2 - lHeight : y;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : lHeight;

			pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + mInnerBorderPadding, yy + mInnerBorderPadding, ww, hh, pComponentZDepth, UiBarInnerColor);

		} else {
			lBarWidth = MathHelper.clamp(lBarWidth - mInnerBorderPadding * 2, 0, w);
			float lWidth = lBarWidth;
			float lHeight = h - mInnerBorderPadding * 2;

			float xx = !mIsInverted ? x : x + w - mInnerBorderPadding * 2 - lWidth;
			float yy = y;
			float ww = !mIsInverted ? lWidth : lWidth;
			float hh = !mIsInverted ? lHeight : lHeight;

			pSpriteBatch.draw(lCoreTexture, CoreTextureNames.TEXTURE_WHITE, xx + mInnerBorderPadding, yy + mInnerBorderPadding, ww, hh, pComponentZDepth, UiBarInnerColor);
		}
	}
}
