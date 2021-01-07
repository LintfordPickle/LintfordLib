package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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
	private int mClickID;
	private String mSliderLabel;
	private String mQtyPostFix;

	private int mMinValue;
	private int mMaxValue;

	private float mCurrentRelPosition;
	private int mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void amountValuePostFix(String pNewPostFix) {
		mQtyPostFix = pNewPostFix;
	}

	public void setMinMax(int pMinValue, int pMaxValue) {
		if (pMaxValue < pMinValue)
			pMaxValue = pMinValue;
		if (pMinValue > pMaxValue)
			pMinValue = pMaxValue;

		mMinValue = pMinValue;
		mMaxValue = pMaxValue;

		updateValue(mCurrentRelPosition);

	}

	public int minValue() {
		return mMinValue;
	}

	public int maxValue() {
		return mMaxValue;
	}

	public void currentValue(int pNewValue) {
		mCurrentValue = MathHelper.clampi(pNewValue, mMinValue, mMaxValue);
		mCurrentRelPosition = (int) MathHelper.scaleToRange(mCurrentValue, mMinValue, mMaxValue, 0, w);

	}

	public int currentValue() {
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

	public UiIntSlider(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UiIntSlider(final UiWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mSliderLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	private void updateValue(float pRelPositionX) {
		mCurrentRelPosition = pRelPositionX;
		mCurrentValue = (int) MathHelper.scaleToRange(mCurrentRelPosition, 0, w, mMinValue, mMaxValue);

		if (mMinValue == mMaxValue) {
			mCurrentValue = mMinValue;
			mCurrentRelPosition = 0;

		}

	}

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
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y + h / 2 - SLIDER_RAIL_HEIGHT / 2, w, SLIDER_RAIL_HEIGHT, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + mCurrentRelPosition - SLIDER_WIDTH / 2, y + h / 4, SLIDER_WIDTH, h / 2, 0f, lNubbinColor);

		pTextFont.draw(mSliderLabel, x, y - h / 2, pComponentZDepth, ColorConstants.WHITE, 1.f);
		final String lQtyLabel = Integer.toString(mCurrentValue) + ((mQtyPostFix != null && mQtyPostFix.length() > 0) ? mQtyPostFix : "");
		pTextFont.draw(lQtyLabel, x + w - 32f - SLIDER_WIDTH / 2, y - h / 2, 1.f);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions pCallbackObject, final int pNewLIstenerID) {
		mCallback = pCallbackObject;
		mClickID = pNewLIstenerID;

	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

}
