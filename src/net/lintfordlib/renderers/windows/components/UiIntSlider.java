package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

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
		mH = 50;
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
		final var lSliderRailHeight = 4.f;
		final var lSliderWidth = 10.f;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX, mY + textFont.fontHeight() + mH / 2 - lSliderRailHeight / 2, mW, lSliderRailHeight, 0f, lBackgroundColor);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mCurrentRelPosition - lSliderWidth / 2, mY + +textFont.fontHeight() + mH / 4, lSliderWidth, mH / 2, 0f, lNubbinColor);
		spriteBatch.end();

		final var lQtyLabel = Integer.toString(mCurrentValue) + ((mQtyPostFix != null && mQtyPostFix.length() > 0) ? mQtyPostFix : "");
		final var lQuantyTextWidth = textFont.getStringWidth(lQtyLabel);

		textFont.begin(core.HUD());
		textFont.drawText(mSliderLabel, mX, mY, componentZDepth, ColorConstants.WHITE, 1.f);
		textFont.drawText(lQtyLabel, mX + mW - lQuantyTextWidth, mY, componentZDepth, ColorConstants.WHITE, 1.f);
		textFont.end();
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
