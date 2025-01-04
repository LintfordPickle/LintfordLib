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

	private float mHorizontalMargins;

	private boolean mDrawLabel;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawLabel() {
		return mDrawLabel;
	}

	public void drawLabel(boolean drawLabel) {
		mDrawLabel = drawLabel;
	}

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
		mCurrentRelPosition = MathHelper.scaleToRange(mCurrentValue, mMinValue, mMaxValue, 0, mW - mHorizontalMargins * 2.f);
	}

	public float currentValue() {
		return mCurrentValue;
	}

	public String sliderLabel() {
		return mSliderLabel;
	}

	public void sliderLabel(final String newLabel) {
		mSliderLabel = newLabel;
		mDrawLabel = true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiFloatSlider(UiWindow parentWindow) {
		this(parentWindow, NO_LABEL_TEXT, -1);
	}

	public UiFloatSlider(UiWindow parentWindow, String label) {
		this(parentWindow, label, -1);
	}

	public UiFloatSlider(UiWindow parentWindow, String label, int entryUid) {
		super(parentWindow);

		mIsDoubleHeight = true;
		mSliderLabel = label;
		mW = 200;
		mH = 25;

		mHorizontalMargins = 5.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mIsEnabled)
			return false;

		var lDoWeOwnMouse = core.input().mouse().isMouseLeftClickOwnerAssigned(hashCode()) && core.input().mouse().isMouseLeftButtonDown();

		if (lDoWeOwnMouse || intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final float lMouseX = core.HUD().getMouseCameraSpace().x;
				updateValue(MathHelper.clamp(lMouseX - mX, 0, mW - mHorizontalMargins * 2.f));

				if (mUiWidgetListenerCallback != null)
					mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

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
		final var lHalfHeight = mH * .5f;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lBackgroundColor);

		// background bar
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mHorizontalMargins, lLowerYPosition - lRailHeight * .5f, mW - mHorizontalMargins * 2.f, lRailHeight, 0f);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);

		// position bar
		spriteBatch.setColor(lNubbinColor);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mHorizontalMargins + mCurrentRelPosition - lSliderWidth / 2, lLowerYPosition - lHalfHeight * .5f, lSliderWidth, lHalfHeight, 0f);
		spriteBatch.end();

		textFont.begin(core.HUD());
		textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		final var lAmtText = String.format("%.2f", mCurrentValue);
		if (mDrawLabel)
			textFont.drawText(mSliderLabel, mX, lUpperYPosition - textFont.fontHeight() * .5f, componentZDepth, 1f);
		textFont.drawText(lAmtText, mX + mW - textFont.getStringWidth(lAmtText), lUpperYPosition - textFont.fontHeight() * .5f, -0.01f, 1f);
		textFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateValue(float relPositionX) {
		mCurrentRelPosition = relPositionX;
		mCurrentValue = MathHelper.scaleToRange(mCurrentRelPosition, 0, mW - mHorizontalMargins * 2.f, mMinValue, mMaxValue);
	}
}
