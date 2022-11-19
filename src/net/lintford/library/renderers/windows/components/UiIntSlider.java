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

public class UiIntSlider extends UIWidget {

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
	private String mQtyPostFix;

	private int mMinValue;
	private int mMaxValue;

	private float mCurrentRelPosition;
	private int mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelPostfix(String newLabelPostfix) {
		mQtyPostFix = newLabelPostfix;
	}

	public void setMinMax(int minValue, int maxValue) {
		if (maxValue < minValue)
			maxValue = minValue;
		if (minValue > maxValue)
			minValue = maxValue;

		mMinValue = minValue;
		mMaxValue = maxValue;

		updateValue(mCurrentRelPosition);
	}

	public int minValue() {
		return mMinValue;
	}

	public int maxValue() {
		return mMaxValue;
	}

	public void currentValue(int newValue) {
		mCurrentValue = MathHelper.clampi(newValue, mMinValue, mMaxValue);
		mCurrentRelPosition = (int) MathHelper.scaleToRange(mCurrentValue, mMinValue, mMaxValue, 0, mW);
	}

	public int currentValue() {
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

	public UiIntSlider(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UiIntSlider(final UiWindow parentWindow, final int entryUid) {
		super(parentWindow);

		mEntryUid = entryUid;

		mSliderLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	private void updateValue(float relPositionX) {
		mCurrentRelPosition = relPositionX;
		mCurrentValue = (int) MathHelper.scaleToRange(mCurrentRelPosition, 0, mW, mMinValue, mMaxValue);

		if (mMinValue == mMaxValue) {
			mCurrentValue = mMinValue;
			mCurrentRelPosition = 0;
		}
	}

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
		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY + mH / 2 - SLIDER_RAIL_HEIGHT / 2, mW, SLIDER_RAIL_HEIGHT, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mCurrentRelPosition - SLIDER_WIDTH / 2, mY + mH / 4, SLIDER_WIDTH, mH / 2, 0f, lNubbinColor);

		textFont.drawText(mSliderLabel, mX, mY - textFont.fontHeight(), componentZDepth, ColorConstants.WHITE, 1.f);
		final String lQtyLabel = Integer.toString(mCurrentValue) + ((mQtyPostFix != null && mQtyPostFix.length() > 0) ? mQtyPostFix : "");
		final float lQuantyTextWidth = textFont.getStringWidth(lQtyLabel);
		textFont.drawText(lQtyLabel, mX + mW - lQuantyTextWidth, mY - textFont.fontHeight(), componentZDepth, ColorConstants.WHITE, 1.f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions callbackObject, final int entryUid) {
		mCallback = callbackObject;
		mEntryUid = entryUid;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
		mCallback = null;
	}

}
