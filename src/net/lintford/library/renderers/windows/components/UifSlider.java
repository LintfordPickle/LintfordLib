package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.windows.UiWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UifSlider extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5982888162840234990L;

	public static final String NO_LABEL_TEXT = "Slider";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EntryInteractions mCallback;
	private int mEntryUid;
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

	public UifSlider(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UifSlider(final UiWindow parentWindow, final int entryUid) {
		super(parentWindow);

		mEntryUid = entryUid;

		mSliderLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mIsEnabled) {
			return false;
		}

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final float lMouseX = core.HUD().getMouseCameraSpace().x;
				updateValue(MathHelper.clamp(lMouseX - mX, 0, mW));

				if (mCallback != null) {
					mCallback.menuEntryOnClick(core.input(), mEntryUid);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lRailHeight = 4;
		final float lSliderWidth = 10;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY + mH / 2 - lRailHeight / 2, mW, lRailHeight, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mCurrentRelPosition - lSliderWidth / 2, mY + mH / 4, lSliderWidth, mH / 2, 0f, lNubbinColor);

		// Render Slider label
		final var lAmtText = String.format("%.2f", mCurrentValue);
		textFont.drawText(mSliderLabel, mX, mY - mH / 2, componentZDepth, ColorConstants.WHITE, 1f);
		textFont.drawText(lAmtText, mX + mW - textFont.getStringWidth(lAmtText), mY - mH / 2, -0.01f, ColorConstants.WHITE, 1f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateValue(float relPositionX) {
		mCurrentRelPosition = relPositionX;
		mCurrentValue = MathHelper.scaleToRange(mCurrentRelPosition, 0, mW, mMinValue, mMaxValue);
	}

	public void setClickListener(final EntryInteractions callbackObject, final int entryUid) {
		mCallback = callbackObject;
		mEntryUid = entryUid;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
		mCallback = null;
	}
}
