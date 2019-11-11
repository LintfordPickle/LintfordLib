package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UISlider extends UIWidget {

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
	private float mR, mG, mB;

	public float mMinValue;
	public float mMaxValue;

	private float mCurrentPosition;
	public float mCurrentValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public UISlider(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UISlider(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mSliderLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

		mR = 0.19f;
		mG = 0.13f;
		mB = 0.3f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {

				float lWindowX = x;
				float lMouseX = pCore.HUD().getMouseCameraSpace().x;
				mCurrentPosition = MathHelper.clamp(lMouseX - lWindowX, 0, w);
				mCurrentValue = MathHelper.scaleToRange(mCurrentPosition, 0, w, mMinValue, mMaxValue);

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
	public void update(LintfordCore pCore) {
		super.update(pCore);

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {

		// Seconds
		mMinValue = 0;
		mMaxValue = 86400;

		final float SLIDER_RAIL_HEIGHT = 4;
		final float SLIDER_WIDTH = 10;

		// Draw the button background
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y + h / 2 - SLIDER_RAIL_HEIGHT / 2, w, SLIDER_RAIL_HEIGHT, 0f, mR, mG, mB, 1f);
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x + mCurrentPosition - SLIDER_WIDTH / 2, y, SLIDER_WIDTH, h, 0f, mB, mG, mR, 1f);
		pTextureBatch.end();

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
