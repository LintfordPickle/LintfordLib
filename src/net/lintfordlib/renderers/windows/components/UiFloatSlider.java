package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiFloatSlider extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5982888162840234990L;

	public static final String NO_LABEL_TEXT = "Slider";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mSliderLabel;

	private float mMinValue;
	private float mMaxValue;

	private float mCurrentRelPosition;
	private float mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMinMax(float minValue, float maxValue) {
		if (maxValue < minValue)
			maxValue = minValue;
		if (minValue > maxValue)
			minValue = maxValue;

		mMinValue = minValue;
		mMaxValue = maxValue;

		updateValue(mCurrentRelPosition);
	}

	public float minValue() {
		return mMinValue;
	}

	public float maxValue() {
		return mMaxValue;
	}

	public void currentValue(float newValue) {
		mCurrentValue = MathHelper.clamp(newValue, mMinValue, mMaxValue);
		mCurrentRelPosition = MathHelper.scaleToRange(mCurrentValue, mMinValue, mMaxValue, 0, mW);
	}

	public float currentValue() {
		return mCurrentValue;
	}

	public String sliderLabel() {
		return mSliderLabel;
	}

	public void sliderLabel(final String newLabel) {
		mSliderLabel = newLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiFloatSlider(UiWindow parentWindow) {
		this(parentWindow, -1);
	}

	public UiFloatSlider(UiWindow parentWindow, int entryUid) {
		super(parentWindow);

		mIsDoubleHeight = true;
		mSliderLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mIsEnabled)
			return false;

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final float lMouseX = core.HUD().getMouseCameraSpace().x;
				updateValue(MathHelper.clamp(lMouseX - mX, 0, mW));

				if (mCallback != null)
					mCallback.widgetOnDataChanged(core.input(), mUiWidgetUid);

				return true;
			}
		}

		return false;
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lRailHeight = 4;
		final float lSliderWidth = 10;

		final var lUpperYPosition = mY + mH * .25f;
		final var lLowerYPosition = mY + mH * .75f;
		final var lQHeight = mH * .25f;
		final var lHalfHeight = mH * .5f;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, lLowerYPosition - lRailHeight * .5f, mW, lRailHeight, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mCurrentRelPosition - lSliderWidth / 2, lLowerYPosition - lHalfHeight * .5f, lSliderWidth, lHalfHeight, 0f, lNubbinColor);

		// Render Slider label
		final var lAmtText = String.format("%.2f", mCurrentValue);
		textFont.drawText(mSliderLabel, mX, lUpperYPosition - textFont.fontHeight() * .5f, componentZDepth, ColorConstants.WHITE, 1f);
		textFont.drawText(lAmtText, mX + mW - textFont.getStringWidth(lAmtText), lUpperYPosition - textFont.fontHeight() * .5f, -0.01f, ColorConstants.WHITE, 1f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateValue(float relPositionX) {
		mCurrentRelPosition = relPositionX;
		mCurrentValue = MathHelper.scaleToRange(mCurrentRelPosition, 0, mW, mMinValue, mMaxValue);
	}
}