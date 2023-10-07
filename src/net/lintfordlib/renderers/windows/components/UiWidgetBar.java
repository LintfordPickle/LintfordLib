package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiWidgetBar extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5078960576301914891L;

	private static final float BAR_HEIGHT = 13.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelString;

	private final Color UiBarColor = new Color(1.f, 1.f, 1.f, 1.f);

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public UiWidgetBar(UiWindow pParentWindow, String pLabelString) {
		super(pParentWindow);

		mLabelString = pLabelString;

		mMinValue = 0;
		mMaxValue = 100;

		height(25 + BAR_HEIGHT);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		pTextFont.drawText(mLabelString, mX + lHorizontalPadding, mY + 1, pComponentZDepth, ColorConstants.WHITE, 1.f);

		final float lInnerBorderPadding = 1.f;

		float lBarWidth = MathHelper.scaleToRange(mCurValue, mMinValue, mMaxValue, 0, mW);
		lBarWidth = MathHelper.clamp(lBarWidth - lInnerBorderPadding * 2, 0, mW - lHorizontalPadding * 2.f);

		// Draw background bar
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX + lHorizontalPadding, mY + 25, mW - lHorizontalPadding * 2.f, BAR_HEIGHT, -0.1f, ColorConstants.BLACK);

		float xx = mX + lHorizontalPadding;
		float yy = mY + 25;
		float ww = lBarWidth;
		float hh = BAR_HEIGHT - lInnerBorderPadding * 2.f;

		// Draw the inner bar
		UiBarColor.setRGB(1.f, 0.f, 0.f);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xx + lInnerBorderPadding, yy + lInnerBorderPadding, ww, hh, -0.1f, UiBarColor);

	}

}
