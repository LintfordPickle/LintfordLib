package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;

		final var lBackgroundColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y + h / 2 - SLIDER_RAIL_HEIGHT / 2, w, SLIDER_RAIL_HEIGHT, 0f, lBackgroundColor);
		final var lNubbinColor = mIsEnabled ? ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, 1.f) : ColorConstants.getBlackWithAlpha(.4f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + mCurrentRelPosition - SLIDER_WIDTH / 2, y + h / 4, SLIDER_WIDTH, h / 2, 0f, lNubbinColor);

		// Render Slider label
		pTextFont.draw(mSliderLabel, x, y - h / 2, 1.f);
		pTextFont.draw(String.format("%.2f", mCurrentValue), x + w - 30 - SLIDER_WIDTH / 2, y - h / 2, 1.f);

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
