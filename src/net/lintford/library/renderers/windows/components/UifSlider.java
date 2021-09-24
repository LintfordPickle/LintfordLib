package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
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
	private int mClickID;
	private String mSliderLabel;

	private float mMinValue;
	private float mMaxValue;

	private float mCurrentRelPosition;
	private float mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setMinMax(float pMinValue, float pMaxValue) {
		if (pMaxValue < pMinValue)
			pMaxValue = pMinValue;
		if (pMinValue > pMaxValue)
			pMinValue = pMaxValue;

		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

		updateValue(mCurrentRelPosition);

	}

	public float minValue() {
		return mMinValue;
	}

	public float maxValue() {
		return mMaxValue;
	}

	public void currentValue(float pNewValue) {
		mCurrentValue = MathHelper.clamp(pNewValue, mMinValue, mMaxValue);
		mCurrentRelPosition = MathHelper.scaleToRange(mCurrentValue, mMinValue, mMaxValue, 0, w);

	}

	public float currentValue() {
		return mCurrentValue;
	}

	public String sliderLabel() {
		return mSliderLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mSliderLabel = pNewLabel;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UifSlider(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UifSlider(final UiWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mSliderLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mIsEnabled) {
			return false;

		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final float lMouseX = pCore.HUD().getMouseCameraSpace().x;
				updateValue(MathHelper.clamp(lMouseX - x, 0, w));

				if (mCallback != null) {
					// Notify subscribers that something changes
					mCallback.menuEntryOnClick(pCore.input(), mClickID);
				}

				return true;

			}

		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lRailHeight = 4;
		final float lSliderWidth = 10;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y + h / 2 - lRailHeight / 2, w, lRailHeight, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x + mCurrentRelPosition - lSliderWidth / 2, y + h / 4, lSliderWidth, h / 2, 0f, lNubbinColor);

		// Render Slider label
		final var lAmtText = String.format("%.2f", mCurrentValue);
		pTextFont.drawText(mSliderLabel, x, y - h / 2, pComponentZDepth, ColorConstants.WHITE, 1f);
		pTextFont.drawText(lAmtText, x + w - pTextFont.getStringWidth(lAmtText), y - h / 2, pComponentZDepth, ColorConstants.WHITE, 1f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateValue(float pRelPositionX) {
		mCurrentRelPosition = pRelPositionX;
		mCurrentValue = MathHelper.scaleToRange(mCurrentRelPosition, 0, w, mMinValue, mMaxValue);
	}

	public void setClickListener(final EntryInteractions pCallbackObject, final int pNewLIstenerID) {
		mCallback = pCallbackObject;
		mClickID = pNewLIstenerID;

	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

}
