package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.MathHelper;

public class UIBar {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float x, y, w, h;
	private float r, g, b, a;

	private float mMinValue;
	private float mMaxValue;
	private float mCurValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setDestRectangle(float pX, float pY, float pW, float pH) {
		x = pX;
		y = pY;
		w = pW;
		h = pH;
	}

	public void setColor(float pR, float pG, float pB, float pA) {
		r = pR;
		g = pG;
		b = pB;
		a = pA;
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

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, FontUnit pTextFont, float pComponentZDepth) {
		if (pTextureBatch == null || !pTextureBatch.isDrawing())
			return;

		float lBarWidth = MathHelper.scaleToRange(mCurValue, 0, 100, 0, w);
		lBarWidth = MathHelper.clamp(lBarWidth, 0, w);

		// Draw outer bar
		// pTextureBatch.draw(0, 96, 32, 32, x - 2, y - 2, -0.1f, w + 4, h + 4, 1f, 0f, 0f, 0f, 0.7f, RendererManager.GAME_UI);
		// pTextureBatch.draw(0, 96, 32, 32, x, y, -0.1f, lBarWidth, h, 1f, r, g, b, a, RendererManager.GAME_UI);

	}

}
